package ai.javis.menucontrol.service;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ai.javis.menucontrol.dto.ApiResponse;
import ai.javis.menucontrol.dto.UserDTO;
import ai.javis.menucontrol.exception.UserAlreadyExists;
import ai.javis.menucontrol.exception.UserNotFound;
import ai.javis.menucontrol.model.Company;
import ai.javis.menucontrol.model.ConfirmationToken;
import ai.javis.menucontrol.model.User;
import ai.javis.menucontrol.repository.ConfirmationTokenRepo;
import ai.javis.menucontrol.repository.UserRepo;

@Service
public class UserService implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfirmationTokenRepo confirmationTokenRepo;

    @Autowired
    private ModelMapper modelMapper;

    private void sendEmail(User user, String message) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepo.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText(message + ", please click here : "
                + "http://localhost:8080/confirm-account?token=" + confirmationToken.getConfirmationToken());
        emailService.sendEmail(mailMessage);

        System.out.println("Confirmation Token: " + confirmationToken.getConfirmationToken());
    }

    public User saveUser(UserDTO userDTO, String rawPassword, Company comp) throws UserAlreadyExists {
        if (userRepo.existsByEmail(userDTO.getEmail())) {
            throw new UserAlreadyExists("email already in use!");
        }

        if (userRepo.existsByUsername(userDTO.getUsername())) {
            throw new UserAlreadyExists("username already in use!");
        }

        User user = convertDtoToModel(userDTO);

        String encryptedPassword = encoder.encode(rawPassword);
        user.setPassword(encryptedPassword);
        user.setCompany(comp);
        User savedUser = userRepo.save(user);

        sendEmail(user, "To confirm your account");

        return savedUser;
    }

    public User getCurrentUser() throws UserNotFound {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return getUserByUsername(userDetails.getUsername());
    }

    public ResponseEntity<?> signInWithEmail(String email) throws UserNotFound {
        User user = userRepo.findByEmailIgnoreCase(email);
        if (user == null) {
            throw new UserNotFound("User not found with email: " + email);
        }

        sendEmail(user, "To signin in your account");

        ApiResponse<?> resp = new ApiResponse<>("login url sent to email", null);
        return ResponseEntity.ok(resp);
    }

    public String confirmEmail(String confirmationToken) throws UserNotFound {
        ConfirmationToken token = confirmationTokenRepo.findByConfirmationToken(confirmationToken);

        if (token == null || !token.getIsActive()) {
            throw new UserNotFound("token is invalid");
        }

        User user = userRepo.findByEmailIgnoreCase(token.getUser().getEmail());
        user.setEnabled(true);
        userRepo.save(user);

        token.setIsActive(false);
        confirmationTokenRepo.save(token);

        return user.getUsername();
    }

    public ResponseEntity<?> updatePassword(String username, String rawPassword) throws UserNotFound {
        User user = getUserByUsername(username);
        String encryptedPassword = encoder.encode(rawPassword);
        user.setPassword(encryptedPassword);
        userRepo.save(user);

        ApiResponse<?> resp = new ApiResponse<>("password updated successfully", null);
        return ResponseEntity.ok(resp);
    }

    public User getUserByUsername(String username) throws UserNotFound {
        User user = userRepo.findByUsername(username);
        if (user != null) {
            return user;
        }

        throw new UserNotFound("User not found with username: " + username);
    }

    public UserDTO convertModelToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User convertDtoToModel(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String userName = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();

        try {
            User user = getUserByUsername(userName);
            user.setLastLoginDate(LocalDate.now());
            userRepo.save(user);
        } catch (UserNotFound e) {
            e.printStackTrace();
        }
    }
}
