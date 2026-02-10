package com.droolstest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Request body for POST /api/validate/batch
 * Runs multiple test cases in one call.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchValidationRequest {

    private List<TestCase> testCases;

    public BatchValidationRequest() {}

    public List<TestCase> getTestCases() { return testCases; }
    public void setTestCases(List<TestCase> testCases) { this.testCases = testCases; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TestCase {
        private String testName;
        private String description;
        private ValidationRequest input;
        private ExpectedResults expected;

        public TestCase() {}

        public String getTestName() { return testName; }
        public void setTestName(String v) { this.testName = v; }

        public String getDescription() { return description; }
        public void setDescription(String v) { this.description = v; }

        public ValidationRequest getInput() { return input; }
        public void setInput(ValidationRequest v) { this.input = v; }

        public ExpectedResults getExpected() { return expected; }
        public void setExpected(ExpectedResults v) { this.expected = v; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExpectedResults {
        private Boolean fullyValid;
        private Boolean applicantDataIsValid;
        private Boolean applicantIncomeIsVerified;
        private Boolean applicantAddressIsComplete;
        private Boolean applicantIsEligible;
        private Boolean applicantIdentityIsConfirmed;

        public ExpectedResults() {}

        public Boolean getFullyValid() { return fullyValid; }
        public void setFullyValid(Boolean v) { this.fullyValid = v; }

        public Boolean getApplicantDataIsValid() { return applicantDataIsValid; }
        public void setApplicantDataIsValid(Boolean v) { this.applicantDataIsValid = v; }

        public Boolean getApplicantIncomeIsVerified() { return applicantIncomeIsVerified; }
        public void setApplicantIncomeIsVerified(Boolean v) { this.applicantIncomeIsVerified = v; }

        public Boolean getApplicantAddressIsComplete() { return applicantAddressIsComplete; }
        public void setApplicantAddressIsComplete(Boolean v) { this.applicantAddressIsComplete = v; }

        public Boolean getApplicantIsEligible() { return applicantIsEligible; }
        public void setApplicantIsEligible(Boolean v) { this.applicantIsEligible = v; }

        public Boolean getApplicantIdentityIsConfirmed() { return applicantIdentityIsConfirmed; }
        public void setApplicantIdentityIsConfirmed(Boolean v) { this.applicantIdentityIsConfirmed = v; }
    }
}
