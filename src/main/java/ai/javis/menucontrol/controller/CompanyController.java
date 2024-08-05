package ai.javis.menucontrol.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.javis.menucontrol.dto.CompanyDTO;
import ai.javis.menucontrol.exception.CompanyAlreadyExists;
import ai.javis.menucontrol.exception.CompanyNotFound;
import ai.javis.menucontrol.model.Company;
import ai.javis.menucontrol.service.CompanyService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CompanyController {

    @Autowired
    CompanyService service;

    @GetMapping("/company")
    public ResponseEntity<List<Company>> getCompanies() {
        return new ResponseEntity<>(service.getCompanies(), HttpStatus.OK);
    }

    @GetMapping("/company/{compId}")
    public ResponseEntity<Company> getCompanyById(@PathVariable long compId) throws CompanyNotFound {
        Company comp = service.getCompanyById(compId);
        return new ResponseEntity<>(comp, HttpStatus.OK);
    }

    @PostMapping("/company")
    public ResponseEntity<?> addCompany(@RequestBody @Valid CompanyDTO comp) throws CompanyAlreadyExists {
        service.saveCompany(comp);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/company/{compId}")
    public ResponseEntity<?> updateCompany(@PathVariable long compId, @RequestBody CompanyDTO comp)
            throws CompanyAlreadyExists {
        service.updateCompany(compId, comp);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/company/{compId}")
    public ResponseEntity<?> deleteCompany(@PathVariable long compId) {
        service.deleteCompany(compId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
