package ai.javis.menucontrol.controller;

import java.util.List;

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
import ai.javis.menucontrol.exception.MenuAlreadyExists;
import ai.javis.menucontrol.exception.TeamAlreadyExists;
import ai.javis.menucontrol.exception.UserAlreadyExists;
import ai.javis.menucontrol.exception.UserNotFound;
import ai.javis.menucontrol.jwt.JwtUtils;
import ai.javis.menucontrol.model.Company;
import ai.javis.menucontrol.model.Menu;
import ai.javis.menucontrol.model.Team;
import ai.javis.menucontrol.model.User;
import ai.javis.menucontrol.service.CompanyService;
import ai.javis.menucontrol.service.MenuService;
import ai.javis.menucontrol.service.TeamService;
import ai.javis.menucontrol.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest body)
            throws UserAlreadyExists, CompanyAlreadyExists, TeamAlreadyExists, MenuAlreadyExists {

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

        Company comp = companyService.saveCompany(compDTO);
        User user = userService.saveUser(userDTO, body.getPassword(), comp);

        Team team = teamService.createTeam("ADMIN", comp);
        team.addUser(user);
        team = teamService.updateTeam(team);

        List<String> menus = List.of("CREATE_TEAM", "CREATE_MENU", "INVITE_MEMBER", "ASSIGN_TEAM", "ASSIGN_MENU",
                "UPDATE_TEAM");

        for (String menu : menus) {
            Menu menuObj = menuService.addMenu(menu, comp);
            team.addMenu(menuObj);
            team = teamService.updateTeam(team);
        }

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

    @GetMapping("/signInWithEmail")
    public ResponseEntity<?> signInWithEmail(
            @Valid @RequestParam @NotBlank(message = "email is required") @Email(message = "email is invalid") String email)
            throws UserNotFound {
        return userService.signInWithEmail(email);
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
        UserDTO userDTO = userService.convertModelToDto(user);
        LoginResponse response = new LoginResponse(jwtToken, userDTO);

        ApiResponse<LoginResponse> resp = new ApiResponse<LoginResponse>(message, response);
        return ResponseEntity.ok(resp);
    }
}
