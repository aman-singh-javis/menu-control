package ai.javis.menucontrol.model;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Menu_Info")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "menu_name")
    private String menuName;

    @ManyToMany(mappedBy = "menus")
    private Set<Team> teams = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public Menu(String menuName, Company company) {
        this.menuName = menuName.toUpperCase();
        this.company = company;
    }

    // Helper methods
    public void addTeam(Team team) {
        this.teams.add(team);
        team.getMenus().add(this);
    }

    public void removeTeam(Team team) {
        this.teams.remove(team);
        team.getMenus().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Menu menu = (Menu) o;
        return Objects.equals(menuId, menu.menuId) &&
                Objects.equals(menuName, menu.menuName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuId, menuName);
    }
}
