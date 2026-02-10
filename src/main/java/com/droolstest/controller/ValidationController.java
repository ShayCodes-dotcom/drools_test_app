package com.droolstest.controller;

import com.droolstest.dto.*;
import com.droolstest.service.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST API for testing Drools validation rules.
 *
 * Endpoints:
 *   POST /api/validate       - Validate a single applicant
 *   POST /api/validate/batch  - Run batch test cases with assertions
 *   GET  /api/rules/info      - Show loaded rule descriptions
 *   GET  /api/health          - Health check
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ValidationController {

    private final ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * Validate a single applicant against all rules.
     *
     * POST /api/validate
     * Body: { "applicant": { ... } }
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validate(@RequestBody ValidationRequest request) {
        if (request.getApplicant() == null) {
            return ResponseEntity.badRequest().build();
        }
        ValidationResponse response = validationService.validate(request.getApplicant());
        return ResponseEntity.ok(response);
    }

    /**
     * Run batch test cases with expected result assertions.
     *
     * POST /api/validate/batch
     * Body: { "testCases": [ { "testName": "...", "input": {...}, "expected": {...} } ] }
     */
    @PostMapping("/validate/batch")
    public ResponseEntity<BatchValidationResponse> validateBatch(
            @RequestBody BatchValidationRequest request) {
        if (request.getTestCases() == null || request.getTestCases().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        BatchValidationResponse response = validationService.validateBatch(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Describes the loaded validation rules.
     */
    @GetMapping("/rules/info")
    public ResponseEntity<Map<String, Object>> rulesInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("engine", "Drools 10.0.0");
        info.put("totalRules", 5);

        Map<String, String> rules = new LinkedHashMap<>();
        rules.put("applicantDataIsValid",
                "Checks: firstName, lastName, dateOfBirth, socialSecurityNumber are not null");
        rules.put("applicantIncomeIsVerified",
                "Checks: annualIncome > 0, employmentStatus and employerName are not null");
        rules.put("applicantAddressIsComplete",
                "Checks: streetAddress, city, state, zipCode are not null");
        rules.put("applicantIsEligible",
                "Checks: age >= 18, creditScore > 600, annualIncome > 25000");
        rules.put("applicantIdentityIsConfirmed",
                "Checks: at least one of driversLicenseNumber, passportNumber, stateIdNumber is not null");
        info.put("rules", rules);

        return ResponseEntity.ok(info);
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "engine", "Drools 10.0.0",
                "application", "drools-validation-test"
        ));
    }
}
