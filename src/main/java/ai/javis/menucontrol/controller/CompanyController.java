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

import ai.javis.menucontrol.model.Company;
import ai.javis.menucontrol.service.CompanyService;

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
    public ResponseEntity<Company> getCompanyById(@PathVariable long compId) {
        try {
            Company comp = service.getCompanyById(compId);
            return new ResponseEntity<>(comp, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/company")
    public ResponseEntity<?> addCompany(@RequestBody Company comp) {
        try {
            service.addCompany(comp);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/company")
    public ResponseEntity<?> updateCompany(@RequestBody Company comp) {
        try {
            service.updateCompany(comp);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/company/{compId}")
    public ResponseEntity<?> deleteCompany(@PathVariable long compId) {
        try {
            service.deleteCompany(compId);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
