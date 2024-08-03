package ai.javis.menucontrol.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "domain is required")
    private String domain;

    @NotBlank(message = "taxId is required")
    private String taxId;
}
