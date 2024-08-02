package ai.javis.menucontrol.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    EmailService emailService;

    @Autowired
    ConfirmationTokenRepo confirmationTokenRepo;

    public ResponseEntity<?> saveUser(User user) {

        System.out.println("Saving User");
        Map<String, Object> map = new HashMap<>();

        if (userRepo.existsByEmail(user.getEmail())) {
            map.put("message", "email already in use!");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        String encryptedPassword = encoder.encode(user.getPassword());
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

    public ResponseEntity<?> confirmEmail(String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepo.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepo.findByEmailIgnoreCase(token.getUserEntity().getEmail());
            user.setEnabled(true);
            userRepo.save(user);
            return ResponseEntity.ok("Email verified successfully!");
        }
        return ResponseEntity.badRequest().body("Error: Couldn't verify email");
    }
}
