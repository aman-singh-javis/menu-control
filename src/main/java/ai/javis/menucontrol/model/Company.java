package ai.javis.menucontrol.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Company_Master")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "domain")
    private String domain;

    @Column(name = "tax_id")
    private String taxId;

    @OneToMany(mappedBy = "company")
    private Set<Team> teams;

    @OneToMany(mappedBy = "company")
    private Set<User> users;

    // @OneToMany(mappedBy = "company")
    // private Set<Menu> menus;
}
