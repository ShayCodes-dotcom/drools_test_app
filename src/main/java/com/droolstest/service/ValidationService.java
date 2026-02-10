package com.droolstest.service;

import com.droolstest.dto.*;
import com.droolstest.rules.ApplicantFact;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service that creates a stateless KieSession per request,
 * inserts the fact, fires all rules, and returns the results.
 */
@Service
public class ValidationService {

    private static final int TOTAL_RULES = 5;

    private final KieContainer kieContainer;

    public ValidationService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    /**
     * Run all Drools rules against a single ApplicantFact.
     */
    public ValidationResponse validate(ApplicantFact fact) {
        KieSession session = kieContainer.newKieSession();
        try {
            session.insert(fact);
            int rulesFired = session.fireAllRules();
            System.out.println("Rules fired: " + rulesFired);
            return ValidationResponse.fromFact(fact, TOTAL_RULES);
        } finally {
            session.dispose();
        }
    }

    /**
     * Run batch test cases with expected-result assertions.
     */
    public BatchValidationResponse validateBatch(BatchValidationRequest request) {
        List<BatchValidationResponse.TestCaseResult> results = new ArrayList<>();
        int passed = 0;
        int failed = 0;

        for (BatchValidationRequest.TestCase tc : request.getTestCases()) {
            ApplicantFact fact = tc.getInput().getApplicant();
            ValidationResponse vr = validate(fact);

            // Compare against expected
            List<BatchValidationResponse.AssertionResult> assertions = new ArrayList<>();
            boolean allMatch = true;

            if (tc.getExpected() != null) {
                BatchValidationRequest.ExpectedResults exp = tc.getExpected();
                Map<String, Boolean> actual = vr.getRuleResults();

                allMatch &= assertField(assertions, "fullyValid",
                        exp.getFullyValid(), vr.isFullyValid());
                allMatch &= assertField(assertions, "applicantDataIsValid",
                        exp.getApplicantDataIsValid(), actual.getOrDefault("applicantDataIsValid", false));
                allMatch &= assertField(assertions, "applicantIncomeIsVerified",
                        exp.getApplicantIncomeIsVerified(), actual.getOrDefault("applicantIncomeIsVerified", false));
                allMatch &= assertField(assertions, "applicantAddressIsComplete",
                        exp.getApplicantAddressIsComplete(), actual.getOrDefault("applicantAddressIsComplete", false));
                allMatch &= assertField(assertions, "applicantIsEligible",
                        exp.getApplicantIsEligible(), actual.getOrDefault("applicantIsEligible", false));
                allMatch &= assertField(assertions, "applicantIdentityIsConfirmed",
                        exp.getApplicantIdentityIsConfirmed(), actual.getOrDefault("applicantIdentityIsConfirmed", false));
            }

            BatchValidationResponse.TestCaseResult tcr = new BatchValidationResponse.TestCaseResult();
            tcr.setTestName(tc.getTestName());
            tcr.setDescription(tc.getDescription());
            tcr.setTestPassed(allMatch);
            tcr.setActualRuleResults(vr.getRuleResults());
            tcr.setAssertions(assertions);
            tcr.setValidationResponse(vr);
            results.add(tcr);

            if (allMatch) passed++;
            else failed++;
        }

        BatchValidationResponse resp = new BatchValidationResponse();
        resp.setTotalTests(request.getTestCases().size());
        resp.setPassed(passed);
        resp.setFailed(failed);
        resp.setResults(results);
        return resp;
    }

    private boolean assertField(List<BatchValidationResponse.AssertionResult> assertions,
                                String field, Boolean expected, Boolean actual) {
        if (expected == null) return true; // no assertion for this field
        BatchValidationResponse.AssertionResult ar =
                new BatchValidationResponse.AssertionResult(field, expected, actual);
        assertions.add(ar);
        return ar.isMatch();
    }
}
