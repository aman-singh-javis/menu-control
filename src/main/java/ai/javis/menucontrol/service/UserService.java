package ai.javis.menucontrol.service;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ai.javis.menucontrol.dto.UserDTO;
import ai.javis.menucontrol.exception.UserAlreadyExists;
import ai.javis.menucontrol.exception.UserNotFound;
import ai.javis.menucontrol.model.ConfirmationToken;
import ai.javis.menucontrol.model.User;
import ai.javis.menucontrol.repository.ConfirmationTokenRepo;
import ai.javis.menucontrol.repository.UserRepo;

@Service
public class UserService {

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

    public ResponseEntity<?> saveUser(UserDTO userDTO, String rawPassword) throws UserAlreadyExists {
        Map<String, Object> map = new HashMap<>();

        if (userRepo.existsByEmail(userDTO.getEmail())) {
            throw new UserAlreadyExists("email already in use!");
        }

        if (userRepo.existsByUsername(userDTO.getUsername())) {
            throw new UserAlreadyExists("username already in use!");
        }

        User user = convertDtoToModel(userDTO);

        String encryptedPassword = encoder.encode(rawPassword);
        user.setPassword(encryptedPassword);
        userRepo.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepo.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText("To confirm your account, please click here : "
                + "http://localhost:8080/confirm-account?token=" + confirmationToken.getConfirmationToken());
        emailService.sendEmail(mailMessage);

        System.out.println("Confirmation Token: " + confirmationToken.getConfirmationToken());

        map.put("message", "Verify email by the link sent on your email address");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public ResponseEntity<?> confirmEmail(String confirmationToken) throws UserNotFound {
        ConfirmationToken token = confirmationTokenRepo.findByConfirmationToken(confirmationToken);

        if (token == null) {
            throw new UserNotFound("token is invalid");
        }

        User user = userRepo.findByEmailIgnoreCase(token.getUserEntity().getEmail());
        user.setEnabled(true);
        userRepo.save(user);

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "email verified successfully");
        return ResponseEntity.ok(resp);
    }

    public ResponseEntity<?> updatePassword(String username, String rawPassword) throws UserNotFound {
        User user = getUserByUsername(username);
        String encryptedPassword = encoder.encode(rawPassword);
        user.setPassword(encryptedPassword);
        userRepo.save(user);

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "password updated successfully");
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
}
