package com.droolstest.dto;

import java.util.List;
import java.util.Map;

/**
 * Response body from POST /api/validate/batch
 */
public class BatchValidationResponse {

    private int totalTests;
    private int passed;
    private int failed;
    private List<TestCaseResult> results;

    public BatchValidationResponse() {}

    public int getTotalTests() { return totalTests; }
    public void setTotalTests(int v) { this.totalTests = v; }

    public int getPassed() { return passed; }
    public void setPassed(int v) { this.passed = v; }

    public int getFailed() { return failed; }
    public void setFailed(int v) { this.failed = v; }

    public List<TestCaseResult> getResults() { return results; }
    public void setResults(List<TestCaseResult> v) { this.results = v; }

    public static class TestCaseResult {
        private String testName;
        private String description;
        private boolean testPassed;
        private Map<String, Boolean> actualRuleResults;
        private List<AssertionResult> assertions;
        private ValidationResponse validationResponse;

        public TestCaseResult() {}

        public String getTestName() { return testName; }
        public void setTestName(String v) { this.testName = v; }

        public String getDescription() { return description; }
        public void setDescription(String v) { this.description = v; }

        public boolean isTestPassed() { return testPassed; }
        public void setTestPassed(boolean v) { this.testPassed = v; }

        public Map<String, Boolean> getActualRuleResults() { return actualRuleResults; }
        public void setActualRuleResults(Map<String, Boolean> v) { this.actualRuleResults = v; }

        public List<AssertionResult> getAssertions() { return assertions; }
        public void setAssertions(List<AssertionResult> v) { this.assertions = v; }

        public ValidationResponse getValidationResponse() { return validationResponse; }
        public void setValidationResponse(ValidationResponse v) { this.validationResponse = v; }
    }

    public static class AssertionResult {
        private String field;
        private Boolean expected;
        private Boolean actual;
        private boolean match;

        public AssertionResult() {}

        public AssertionResult(String field, Boolean expected, Boolean actual) {
            this.field = field;
            this.expected = expected;
            this.actual = actual;
            this.match = (expected == null) || expected.equals(actual);
        }

        public String getField() { return field; }
        public Boolean getExpected() { return expected; }
        public Boolean getActual() { return actual; }
        public boolean isMatch() { return match; }
    }
}
