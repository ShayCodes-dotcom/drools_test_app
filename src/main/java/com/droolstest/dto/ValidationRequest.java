package com.droolstest.dto;

import com.droolstest.rules.ApplicantFact;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Request body for POST /api/validate
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationRequest {

    private ApplicantFact applicant;

    public ValidationRequest() {}

    public ApplicantFact getApplicant() { return applicant; }
    public void setApplicant(ApplicantFact applicant) { this.applicant = applicant; }
}
