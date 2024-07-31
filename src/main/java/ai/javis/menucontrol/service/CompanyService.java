package ai.javis.menucontrol.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ai.javis.menucontrol.model.Company;
import ai.javis.menucontrol.repository.CompanyRepo;

public class CompanyService {

    @Autowired
    CompanyRepo repo;

    public List<Company> getCompanies() {
        return repo.findAll();
    }

    public Company getCompanyById(long companyId) {
        return repo.findById(companyId).orElseThrow();
    }

    public void addCompany(Company company) {
        repo.save(company);
    }

    public void updateCompany(Company company) {
        repo.save(company);
    }

    public void deleteCompany(Long companyId) {
        repo.deleteById(companyId);
    }
}
