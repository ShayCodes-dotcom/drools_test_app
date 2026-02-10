package com.droolstest.dto;

import com.droolstest.rules.ApplicantFact;

import java.util.*;

/**
 * Response body from POST /api/validate
 */
public class ValidationResponse {

    private boolean fullyValid;
    private int rulesMatched;
    private int totalRules;
    private Map<String, Boolean> ruleResults;
    private List<ValidationDetail> details;
    private ApplicantFact factAfterRules;

    public ValidationResponse() {}

    // ── Builder-style static factory ────────────────────────────────────────

    public static ValidationResponse fromFact(ApplicantFact fact, int totalRules) {
        ValidationResponse resp = new ValidationResponse();
        resp.fullyValid = fact.isFullyValid();
        resp.rulesMatched = fact.getValidationFlags().size();
        resp.totalRules = totalRules;
        resp.ruleResults = fact.getValidationFlags();
        resp.factAfterRules = fact;

        resp.details = new ArrayList<>();
        for (ApplicantFact.ValidationResult vr : fact.getValidationResults()) {
            resp.details.add(new ValidationDetail(
                    vr.getFieldName(), vr.isPassed(), vr.getMessage()));
        }

        // Add entries for rules that did NOT fire
        List<String> allRuleKeys = List.of(
                "applicantDataIsValid",
                "applicantIncomeIsVerified",
                "applicantAddressIsComplete",
                "applicantIsEligible",
                "applicantIdentityIsConfirmed"
        );
        List<String> allRuleLabels = List.of(
                "Personal data validation",
                "Income verification",
                "Address completeness",
                "Eligibility check",
                "Identity confirmation"
        );
        for (int i = 0; i < allRuleKeys.size(); i++) {
            String key = allRuleKeys.get(i);
            if (!resp.ruleResults.containsKey(key)) {
                resp.ruleResults.put(key, false);
                resp.details.add(new ValidationDetail(
                        key, false, allRuleLabels.get(i) + " - conditions not met"));
            }
        }

        return resp;
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public boolean isFullyValid() { return fullyValid; }
    public int getRulesMatched() { return rulesMatched; }
    public int getTotalRules() { return totalRules; }
    public Map<String, Boolean> getRuleResults() { return ruleResults; }
    public List<ValidationDetail> getDetails() { return details; }
    public ApplicantFact getFactAfterRules() { return factAfterRules; }

    // ── Inner detail class ──────────────────────────────────────────────────

    public static class ValidationDetail {
        private String rule;
        private boolean passed;
        private String message;

        public ValidationDetail() {}

        public ValidationDetail(String rule, boolean passed, String message) {
            this.rule = rule;
            this.passed = passed;
            this.message = message;
        }

        public String getRule() { return rule; }
        public boolean isPassed() { return passed; }
        public String getMessage() { return message; }
    }
}
