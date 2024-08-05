package ai.javis.menucontrol.controller;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.javis.menucontrol.dto.ApiResponse;
import ai.javis.menucontrol.dto.ResetPasswordRequest;
import ai.javis.menucontrol.dto.UserDTO;
import ai.javis.menucontrol.exception.UserAlreadyExists;
import ai.javis.menucontrol.exception.UserNotFound;
import ai.javis.menucontrol.model.User;
import ai.javis.menucontrol.service.UserService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+<>?";

    private String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return password.toString();
    }

    private String getUsernameFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> updateUserPassword(
            @Valid @RequestBody ResetPasswordRequest body) throws UserNotFound {

        String username = getUsernameFromSecurityContext();
        return userService.updatePassword(username, body.getPassword());
    }

    @PostMapping("/org-invite")
    public ResponseEntity<?> orgInvite(@Valid @RequestBody UserDTO user) throws UserNotFound, UserAlreadyExists {
        String username = getUsernameFromSecurityContext();
        User currentUser = userService.getUserByUsername(username);

        // Assert company domain and email domain should be same
        String emailArr[] = user.getEmail().split("@");
        if (emailArr.length != 2) {
            return new ResponseEntity<>("invalid email", HttpStatus.BAD_REQUEST);
        } else if (!currentUser.getCompany().getDomain().equalsIgnoreCase(emailArr[1])) {
            return new ResponseEntity<>("please register using company email", HttpStatus.BAD_REQUEST);
        }

        User savedUser = userService.saveUser(user, generateRandomPassword(6), currentUser.getCompany());

        ApiResponse<UserDTO> resp = new ApiResponse<>("user invited to org successfully",
                userService.convertModelToDto(savedUser));
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() throws UserNotFound {
        String username = getUsernameFromSecurityContext();
        List<User> users = userService.getUserByUsername(username).getCompany().getUsers();

        List<UserDTO> userDTOs = users.stream()
                .map((user) -> userService.convertModelToDto(user))
                .collect(Collectors.toList());

        ApiResponse<List<UserDTO>> rsp = new ApiResponse<List<UserDTO>>("current user list", userDTOs);
        return ResponseEntity.ok(rsp);
    }

}
