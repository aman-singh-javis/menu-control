package ai.javis.menucontrol.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.javis.menucontrol.dto.ApiResponse;
import ai.javis.menucontrol.dto.CompanyDTO;
import ai.javis.menucontrol.dto.ConfirmAccountRequest;
import ai.javis.menucontrol.dto.LoginRequest;
import ai.javis.menucontrol.dto.LoginResponse;
import ai.javis.menucontrol.dto.RegisterRequest;
import ai.javis.menucontrol.dto.UserDTO;
import ai.javis.menucontrol.exception.CompanyAlreadyExists;
import ai.javis.menucontrol.exception.UserAlreadyExists;
import ai.javis.menucontrol.exception.UserNotFound;
import ai.javis.menucontrol.jwt.JwtUtils;
import ai.javis.menucontrol.model.User;
import ai.javis.menucontrol.service.CompanyService;
import ai.javis.menucontrol.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest body)
            throws UserAlreadyExists, CompanyAlreadyExists {

        CompanyDTO compDTO = body.getCompanyInfo();

        UserDTO userDTO = body.getUserInfo();
        userDTO.setEmail(body.getEmail());

        // Assert Company Domain is same as user email domain
        String emailArr[] = body.getEmail().split("@");
        if (emailArr.length != 2) {
            return new ResponseEntity<>("invalid email", HttpStatus.BAD_REQUEST);
        } else if (!compDTO.getDomain().equalsIgnoreCase(emailArr[1])) {
            return new ResponseEntity<>("please register using company email", HttpStatus.BAD_REQUEST);
        }

        companyService.saveCompany(compDTO);
        userService.saveUser(userDTO, body.getPassword());

        ApiResponse<?> resp = new ApiResponse<>("Verify email by the link sent on your email address", null);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/confirm-account")
    public ResponseEntity<?> confirmUserAccount(
            @Valid @RequestBody ConfirmAccountRequest body)
            throws UserNotFound {
        String username = userService.confirmEmail(body.getToken());
        return authenticatedResponse(username, "email verified successfully");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) throws UserNotFound {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            throw new UserNotFound("bad credentials");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return authenticatedResponse(userDetails.getUsername(), "signed in successfully");
    }

    private ResponseEntity<?> authenticatedResponse(String username, String message) throws UserNotFound {
        String jwtToken = jwtUtils.generateTokenFromUsername(username);
        User user = userService.getUserByUsername(username);
        LoginResponse response = new LoginResponse(jwtToken, user);

        ApiResponse<LoginResponse> resp = new ApiResponse<LoginResponse>(message, response);
        return ResponseEntity.ok(resp);
    }
}
