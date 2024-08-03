package ai.javis.menucontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ai.javis.menucontrol.model.Company;

@Repository
public interface CompanyRepo extends JpaRepository<Company, Long> {
    Company findByTaxId(String taxId);

    Company findByDomainIgnoreCase(String domain);

    Boolean existsByDomain(String domain);

    Boolean existsByTaxId(String taxId);
}
