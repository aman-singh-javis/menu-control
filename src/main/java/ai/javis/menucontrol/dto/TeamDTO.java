package ai.javis.menucontrol.dto;

import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {

    @NotBlank(message = "team name is required")
    private String teamName;

    @Valid
    private Set<MenuDTO> menus;
}
