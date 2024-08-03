package ai.javis.menucontrol.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ai.javis.menucontrol.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String jwtToken;
    @JsonIgnoreProperties({ "password" })
    private User userInfo;
}
