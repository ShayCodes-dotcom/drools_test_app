# Drools Validation Test Harness

A Spring Boot application that loads Drools DRL rule files and exposes REST endpoints for testing them. Upload JSON representing an applicant, get back which validation rules passed or failed. Includes a batch runner that compares actual results against expected values — a test suite for your rules.

Designed as the companion to the **OPA-to-Drools Converter**. Convert your OPA `.docx` rules, drop the generated files into this project, and run the included test cases.

---

## Prerequisites

- Java 17+
- Maven 3.8+

## Quick Start

```bash
# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

Verify it's running:
```bash
curl http://localhost:8080/api/health
```

---

## API Endpoints

### `GET /api/health`

Health check. Returns status, engine version, and application name.

```json
{
  "status": "UP",
  "engine": "Drools 10.0.0",
  "application": "drools-validation-test"
}
```

### `GET /api/rules/info`

Describes every loaded validation rule — what it checks and its conditions.

### `POST /api/validate`

Validates a single applicant against all rules.

**Request body:**
```json
{
  "applicant": {
    "applicantFirstName": "Jane",
    "applicantLastName": "Doe",
    "applicantDateOfBirth": "1990-05-15",
    "applicantSocialSecurityNumber": "123-45-6789",
    "applicantAnnualIncome": 75000,
    "applicantEmploymentStatus": "Full-Time",
    "applicantEmployerName": "Acme Corp",
    "applicantStreetAddress": "123 Main Street",
    "applicantCity": "Baton Rouge",
    "applicantState": "LA",
    "applicantZipCode": "70801",
    "applicantAge": 34,
    "applicantCreditScore": 750,
    "applicantDriversLicenseNumber": "DL-9876543"
  }
}
```

**Response:**
```json
{
  "fullyValid": true,
  "rulesMatched": 5,
  "totalRules": 5,
  "ruleResults": {
    "applicantDataIsValid": true,
    "applicantIncomeIsVerified": true,
    "applicantAddressIsComplete": true,
    "applicantIsEligible": true,
    "applicantIdentityIsConfirmed": true
  },
  "details": [
    {
      "rule": "applicantDataIsValid",
      "passed": true,
      "message": "All required personal data fields are present and non-empty"
    }
  ]
}
```

**Quick test with the included sample file:**
```bash
curl -X POST http://localhost:8080/api/validate \
  -H "Content-Type: application/json" \
  -d @src/main/resources/single-test.json
```

### `POST /api/validate/batch`

Runs multiple test cases in one call. Each test case includes input data and expected results. The response compares actual vs. expected for every assertion and reports pass/fail.

**Quick test with all 34 included test cases:**
```bash
curl -X POST http://localhost:8080/api/validate/batch \
  -H "Content-Type: application/json" \
  -d @src/main/resources/test-cases.json
```

**Response structure:**
```json
{
  "totalTests": 34,
  "passed": 34,
  "failed": 0,
  "results": [
    {
      "testName": "TC-01: Complete valid applicant - all rules pass",
      "description": "All fields populated with valid values.",
      "testPassed": true,
      "actualRuleResults": {
        "applicantDataIsValid": true,
        "applicantIncomeIsVerified": true,
        "applicantAddressIsComplete": true,
        "applicantIsEligible": true,
        "applicantIdentityIsConfirmed": true
      },
      "assertions": [
        { "field": "fullyValid", "expected": true, "actual": true, "match": true },
        { "field": "applicantDataIsValid", "expected": true, "actual": true, "match": true }
      ]
    }
  ]
}
```

The `assertions` array only includes fields where an expected value was provided. If `expected` is omitted for a field, no assertion is made for it. A test case passes when every assertion matches.

---

## Validation Rules

The project ships with 5 rules converted from a sample OPA document. All String presence checks validate both `!= null` **and** `!= ""` (empty string).

| # | Rule Name | Conditions | Logic |
|---|-----------|-----------|-------|
| 1 | **Applicant Data Is Valid** | `firstName`, `lastName`, `dateOfBirth`, `socialSecurityNumber` are not null and not empty | AND |
| 2 | **Applicant Income Is Verified** | `annualIncome` > 0, `employmentStatus` and `employerName` are not null/empty | AND |
| 3 | **Applicant Address Is Complete** | `streetAddress`, `city`, `state`, `zipCode` are not null/empty | AND |
| 4 | **Applicant Is Eligible** | `age` >= 18, `creditScore` > 600, `annualIncome` > 25000 | AND |
| 5 | **Applicant Identity Is Confirmed** | At least one of `driversLicenseNumber`, `passportNumber`, `stateIdNumber` is not null/empty | OR |

---

## Test Cases

The `test-cases.json` file contains **34 test cases** organized by category:

### Baseline
| Test | Description | Expected |
|------|-------------|----------|
| TC-01 | All fields valid | All 5 rules pass |
| TC-02 | All fields absent | All 5 rules fail |

### Rule 1 — Personal Data Validation
| Test | Description | Expected |
|------|-------------|----------|
| TC-03 | Only personal data fields provided | Rule 1 passes, others fail |
| TC-04 | Missing `firstName` (null) | Rule 1 fails |
| TC-05 | Missing `lastName` (null) | Rule 1 fails |
| TC-06 | Missing `dateOfBirth` (null) | Rule 1 fails |
| TC-26 | `firstName` is `""` (empty string) | Rule 1 fails |
| TC-27 | `lastName` is `""` | Rule 1 fails |
| TC-28 | `socialSecurityNumber` is `""` | Rule 1 fails |

### Rule 2 — Income Verification
| Test | Description | Expected |
|------|-------------|----------|
| TC-07 | Income is `0` | Rule 2 fails |
| TC-08 | Income is `-5000` | Rule 2 fails |
| TC-09 | Missing `employerName` (null) | Rule 2 fails |
| TC-29 | `employerName` is `""` | Rule 2 fails |
| TC-30 | `employmentStatus` is `""` | Rule 2 fails |

### Rule 3 — Address Completeness
| Test | Description | Expected |
|------|-------------|----------|
| TC-10 | All 4 address fields present | Rule 3 passes |
| TC-11 | Missing `zipCode` (null) | Rule 3 fails |
| TC-12 | Missing `city` (null) | Rule 3 fails |
| TC-31 | All 4 address fields are `""` | Rule 3 fails |

### Rule 4 — Eligibility (Boundary Tests)
| Test | Description | Expected |
|------|-------------|----------|
| TC-13 | Age 25, credit 720, income 50K | Rule 4 passes |
| TC-14 | Age 17 (underage) | Rule 4 fails |
| TC-15 | Age exactly 18 (`>=` boundary) | Rule 4 passes |
| TC-16 | Credit score exactly 600 (`>` boundary) | Rule 4 fails |
| TC-17 | Credit score 601 | Rule 4 passes |
| TC-18 | Income exactly 25000 (`>` boundary) | Rule 4 fails |

### Rule 5 — Identity Confirmation (OR Logic)
| Test | Description | Expected |
|------|-------------|----------|
| TC-19 | Only drivers license provided | Rule 5 passes |
| TC-20 | Only passport provided | Rule 5 passes |
| TC-21 | Only state ID provided | Rule 5 passes |
| TC-22 | No IDs provided (all null) | Rule 5 fails |
| TC-23 | Multiple IDs provided | Rule 5 passes |
| TC-32 | All 3 IDs are `""` (empty string) | Rule 5 fails |
| TC-34 | DL is `""`, passport has value | Rule 5 passes |

### Cross-Rule Scenarios
| Test | Description | Expected |
|------|-------------|----------|
| TC-24 | High income, low credit score | Rules 1+2 pass, Rule 4 fails |
| TC-25 | Minimum viable — every field at boundary | All 5 rules pass |
| TC-33 | Every String field null or `""` | All 5 rules fail |

---

## Adding Your Own Rules

1. **Generate DRL files** using the OPA-to-Drools Converter, or write them by hand.
2. **Drop `.drl` files** into `src/main/resources/rules/`. The `DroolsConfig` class auto-loads all `*.drl` files in that directory on startup.
3. **Update or replace the fact class** in `src/main/java/com/droolstest/rules/`. If your rules reference a different class name, update the DRL `import` statements to match.
4. **Add test cases** to `test-cases.json` following the existing format.
5. **Restart** the application. DRL files are compiled at startup — check the console for any compilation errors.

If you need to test rules for a completely different domain (not applicant validation), replace `ApplicantFact.java` with your own fact class, update `ValidationResponse.fromFact()` to list your rule keys, and create new test case JSON.

---

## Project Structure

```
drools-test-app/
├── pom.xml                                          # Maven config (Spring Boot 3.2, Drools 10)
├── README.md
└── src/main/
    ├── java/com/droolstest/
    │   ├── DroolsValidationTestApplication.java     # Spring Boot entry point
    │   ├── config/
    │   │   └── DroolsConfig.java                    # Builds KieContainer from classpath DRL files
    │   ├── controller/
    │   │   └── ValidationController.java            # REST endpoints (/api/validate, /batch, /health, /rules/info)
    │   ├── dto/
    │   │   ├── ValidationRequest.java               # Single-validate request body
    │   │   ├── ValidationResponse.java              # Single-validate response with rule results
    │   │   ├── BatchValidationRequest.java          # Batch request with test cases and expected values
    │   │   └── BatchValidationResponse.java         # Batch response with assertion comparisons
    │   ├── rules/
    │   │   └── ApplicantFact.java                   # Drools fact class (inserted into working memory)
    │   └── service/
    │       └── ValidationService.java               # Creates KieSession, inserts fact, fires rules
    └── resources/
        ├── application.properties                   # Server port, Jackson config
        ├── rules/
        │   └── applicant-validation.drl             # 5 validation rules (DRL)
        ├── single-test.json                         # Example input for /api/validate
        └── test-cases.json                          # 34 test cases for /api/validate/batch
```

---

## How the Drools Engine Works in This App

1. **Startup:** `DroolsConfig` scans `classpath:rules/*.drl`, compiles them with `KieBuilder`, and creates a `KieContainer` bean. Any DRL syntax errors cause the app to fail fast with a clear error message.

2. **Per request:** `ValidationService.validate()` creates a new `KieSession` from the container, inserts the `ApplicantFact` object, and calls `fireAllRules()`. Each rule that matches sets a boolean flag on the fact and adds a `ValidationResult` entry.

3. **Response:** `ValidationResponse.fromFact()` reads the flags and results from the fact object. For any of the 5 known rules that did **not** fire, it adds an explicit "conditions not met" entry so the response always shows all 5 rules.

4. **Batch mode:** `ValidationService.validateBatch()` loops through test cases, runs each one through the same validate flow, then compares actual results against the expected values and reports assertion-level pass/fail.

---

## Troubleshooting

**DRL compilation errors on startup**
Check the console output — Drools reports the line number and error. Common issues: mismatched field names between the DRL and the fact class, missing imports, or incorrect MVEL syntax.

**Rule doesn't fire when expected**
Verify the field names in the DRL exactly match the getter method names on the fact class (Drools uses JavaBean conventions). For a field `applicantAge`, Drools calls `getApplicantAge()`. For a boolean `applicantIsEligible`, it calls `isApplicantIsEligible()`.

**Empty strings pass validation**
All rules now check `!= ""` in addition to `!= null`. If you're using rules generated before this fix, re-convert your OPA documents with the updated converter.

**BigDecimal comparison issues**
The DRL uses MVEL dialect, which handles `BigDecimal > 25000` correctly. If you switch to Java dialect, you'll need to use `.compareTo()` instead.
