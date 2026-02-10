package com.droolstest.rules;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.*;

/**
 * Fact class for Drools validation rules.
 * Converted from Oracle Policy Automation (OPA) rule documents.
 *
 * This class is inserted into the Drools working memory and evaluated
 * against the DRL rules. Validation results are collected in the
 * validationResults list and validationFlags map.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantFact {

    // ── Validation results storage ──────────────────────────────────────────

    private List<ValidationResult> validationResults = new ArrayList<>();
    private Map<String, Boolean> validationFlags = new HashMap<>();

    // ── Personal data fields (Rule 1: applicant data is valid) ──────────────

    private String applicantFirstName;
    private String applicantLastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date applicantDateOfBirth;

    private String applicantSocialSecurityNumber;

    // ── Income fields (Rule 2: applicant income is verified) ────────────────

    private BigDecimal applicantAnnualIncome;
    private String applicantEmploymentStatus;
    private String applicantEmployerName;

    // ── Address fields (Rule 3: applicant address is complete) ──────────────

    private String applicantStreetAddress;
    private String applicantCity;
    private String applicantState;
    private String applicantZipCode;

    // ── Eligibility fields (Rule 4: applicant is eligible) ──────────────────

    private Integer applicantAge;
    private Integer applicantCreditScore;

    // ── Identity fields (Rule 5: applicant identity is confirmed) ───────────

    private String applicantDriversLicenseNumber;
    private String applicantPassportNumber;
    private String applicantStateIdNumber;

    // ── Conclusion flags (set by rules when conditions are met) ─────────────

    private boolean applicantDataIsValid;
    private boolean applicantIncomeIsVerified;
    private boolean applicantAddressIsComplete;
    private boolean applicantIsEligible;
    private boolean applicantIdentityIsConfirmed;

    // ── Constructors ────────────────────────────────────────────────────────

    public ApplicantFact() {
    }

    // ── Validation Result Methods ───────────────────────────────────────────

    public void addValidationResult(String field, boolean passed, String message) {
        this.validationResults.add(new ValidationResult(field, passed, message));
        this.validationFlags.put(field, passed);
    }

    public List<ValidationResult> getValidationResults() {
        return this.validationResults;
    }

    public boolean isFullyValid() {
        return this.validationFlags.values().stream().allMatch(v -> v);
    }

    public Map<String, Boolean> getValidationFlags() {
        return this.validationFlags;
    }

    // ── Personal data getters/setters ───────────────────────────────────────

    public String getApplicantFirstName() { return applicantFirstName; }
    public void setApplicantFirstName(String v) { this.applicantFirstName = v; }

    public String getApplicantLastName() { return applicantLastName; }
    public void setApplicantLastName(String v) { this.applicantLastName = v; }

    public Date getApplicantDateOfBirth() { return applicantDateOfBirth; }
    public void setApplicantDateOfBirth(Date v) { this.applicantDateOfBirth = v; }

    public String getApplicantSocialSecurityNumber() { return applicantSocialSecurityNumber; }
    public void setApplicantSocialSecurityNumber(String v) { this.applicantSocialSecurityNumber = v; }

    // ── Income getters/setters ──────────────────────────────────────────────

    public BigDecimal getApplicantAnnualIncome() { return applicantAnnualIncome; }
    public void setApplicantAnnualIncome(BigDecimal v) { this.applicantAnnualIncome = v; }

    public String getApplicantEmploymentStatus() { return applicantEmploymentStatus; }
    public void setApplicantEmploymentStatus(String v) { this.applicantEmploymentStatus = v; }

    public String getApplicantEmployerName() { return applicantEmployerName; }
    public void setApplicantEmployerName(String v) { this.applicantEmployerName = v; }

    // ── Address getters/setters ─────────────────────────────────────────────

    public String getApplicantStreetAddress() { return applicantStreetAddress; }
    public void setApplicantStreetAddress(String v) { this.applicantStreetAddress = v; }

    public String getApplicantCity() { return applicantCity; }
    public void setApplicantCity(String v) { this.applicantCity = v; }

    public String getApplicantState() { return applicantState; }
    public void setApplicantState(String v) { this.applicantState = v; }

    public String getApplicantZipCode() { return applicantZipCode; }
    public void setApplicantZipCode(String v) { this.applicantZipCode = v; }

    // ── Eligibility getters/setters ─────────────────────────────────────────

    public Integer getApplicantAge() { return applicantAge; }
    public void setApplicantAge(Integer v) { this.applicantAge = v; }

    public Integer getApplicantCreditScore() { return applicantCreditScore; }
    public void setApplicantCreditScore(Integer v) { this.applicantCreditScore = v; }

    // ── Identity getters/setters ────────────────────────────────────────────

    public String getApplicantDriversLicenseNumber() { return applicantDriversLicenseNumber; }
    public void setApplicantDriversLicenseNumber(String v) { this.applicantDriversLicenseNumber = v; }

    public String getApplicantPassportNumber() { return applicantPassportNumber; }
    public void setApplicantPassportNumber(String v) { this.applicantPassportNumber = v; }

    public String getApplicantStateIdNumber() { return applicantStateIdNumber; }
    public void setApplicantStateIdNumber(String v) { this.applicantStateIdNumber = v; }

    // ── Conclusion flag getters/setters ─────────────────────────────────────

    public boolean isApplicantDataIsValid() { return applicantDataIsValid; }
    public void setApplicantDataIsValid(boolean v) { this.applicantDataIsValid = v; }

    public boolean isApplicantIncomeIsVerified() { return applicantIncomeIsVerified; }
    public void setApplicantIncomeIsVerified(boolean v) { this.applicantIncomeIsVerified = v; }

    public boolean isApplicantAddressIsComplete() { return applicantAddressIsComplete; }
    public void setApplicantAddressIsComplete(boolean v) { this.applicantAddressIsComplete = v; }

    public boolean isApplicantIsEligible() { return applicantIsEligible; }
    public void setApplicantIsEligible(boolean v) { this.applicantIsEligible = v; }

    public boolean isApplicantIdentityIsConfirmed() { return applicantIdentityIsConfirmed; }
    public void setApplicantIdentityIsConfirmed(boolean v) { this.applicantIdentityIsConfirmed = v; }

    // ── Validation Result inner class ───────────────────────────────────────

    public static class ValidationResult {
        private String fieldName;
        private boolean passed;
        private String message;
        private Date timestamp;

        public ValidationResult() {}

        public ValidationResult(String fieldName, boolean passed, String message) {
            this.fieldName = fieldName;
            this.passed = passed;
            this.message = message;
            this.timestamp = new Date();
        }

        public String getFieldName() { return fieldName; }
        public boolean isPassed() { return passed; }
        public String getMessage() { return message; }
        public Date getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("[%s] %s: %s", passed ? "PASS" : "FAIL", fieldName, message);
        }
    }
}
