package ai.javis.menucontrol.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.javis.menucontrol.dto.CompanyDTO;
import ai.javis.menucontrol.exception.CompanyAlreadyExists;
import ai.javis.menucontrol.exception.CompanyNotFound;
import ai.javis.menucontrol.model.Company;
import ai.javis.menucontrol.repository.CompanyRepo;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepo repo;

    @Autowired
    private ModelMapper modelMapper;

    public List<Company> getCompanies() {
        return repo.findAll();
    }

    public Company getCompanyById(long companyId) throws CompanyNotFound {
        Company comp = repo.findById(companyId).orElse(null);
        if (comp == null) {
            throw new CompanyNotFound("company not found with id: " + companyId);
        }

        return comp;
    }

    public Company saveCompany(CompanyDTO companyDTO) throws CompanyAlreadyExists {
        if (repo.existsByDomain(companyDTO.getDomain())) {
            throw new CompanyAlreadyExists("domain already in use!");
        }

        if (repo.existsByTaxId(companyDTO.getTaxId())) {
            throw new CompanyAlreadyExists("taxId already in use!");
        }

        Company company = convertDtoToModel(companyDTO);
        return repo.save(company);
    }

    public void updateCompany(Long companyId, CompanyDTO companyDTO) throws CompanyAlreadyExists {
        Company comp = repo.findByDomainIgnoreCase(companyDTO.getDomain());
        if (comp != null && comp.getCompanyId() != companyId) {
            throw new CompanyAlreadyExists("domain already in use!");
        }

        comp = repo.findByTaxId(companyDTO.getTaxId());
        if (comp != null && comp.getCompanyId() != companyId) {
            throw new CompanyAlreadyExists("taxId already in use!");
        }

        Company company = convertDtoToModel(companyDTO);
        company.setCompanyId(companyId);
        repo.save(company);
    }

    public void deleteCompany(Long companyId) {
        repo.deleteById(companyId);
    }

    public Company convertDtoToModel(CompanyDTO companyDTO) {
        return modelMapper.map(companyDTO, Company.class);
    }

    public CompanyDTO convertModelToDto(Company company) {
        return modelMapper.map(company, CompanyDTO.class);
    }
}
