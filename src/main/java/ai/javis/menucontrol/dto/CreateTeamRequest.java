package ai.javis.menucontrol.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTeamRequest {
    @NotBlank(message = "teamName is required")
    private String teamName;
    @NotBlank(message = "menus are required")
    private List<String> menus;
}
