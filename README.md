# Drools Validation Test Harness

Spring Boot application for testing OPA-to-Drools converted validation rules.

## Prerequisites

- Java 17+
- Maven 3.8+

## Quick Start

```bash
# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run
# or
java -jar target/drools-validation-test-1.0.0.jar
```

The app starts on **http://localhost:8080**.

## API Endpoints

### 1. Health Check
```
GET /api/health
```

### 2. Rule Info
```
GET /api/rules/info
```
Returns descriptions of all 5 validation rules.

### 3. Single Validation
```
POST /api/validate
Content-Type: application/json
```
Validates one applicant against all rules.

**Example:**
```bash
curl -X POST http://localhost:8080/api/validate \
  -H "Content-Type: application/json" \
  -d @src/main/resources/single-test.json
```

### 4. Batch Validation with Assertions
```
POST /api/validate/batch
Content-Type: application/json
```
Runs multiple test cases and compares results against expected values.

**Example:**
```bash
curl -X POST http://localhost:8080/api/validate/batch \
  -H "Content-Type: application/json" \
  -d @src/main/resources/test-cases.json
```

## Validation Rules

| # | Rule | Conditions | Type |
|---|------|-----------|------|
| 1 | **Applicant Data Is Valid** | firstName, lastName, dateOfBirth, SSN all not null | AND |
| 2 | **Income Is Verified** | annualIncome > 0, employmentStatus not null, employerName not null | AND |
| 3 | **Address Is Complete** | streetAddress, city, state, zipCode all not null | AND |
| 4 | **Is Eligible** | age >= 18, creditScore > 600, annualIncome > 25000 | AND |
| 5 | **Identity Is Confirmed** | driversLicense OR passport OR stateId not null | OR |

## Test Cases Included

The `test-cases.json` file contains 25 test cases:

- **TC-01**: Complete valid applicant (all pass)
- **TC-02**: Empty applicant (all fail)
- **TC-03 to TC-06**: Personal data - individual missing fields
- **TC-07 to TC-09**: Income - zero, negative, missing employer
- **TC-10 to TC-12**: Address - complete, missing zip, missing city
- **TC-13 to TC-18**: Eligibility - boundary tests for age (18), credit (600/601), income (25000)
- **TC-19 to TC-23**: Identity - each ID type alone, none, multiple
- **TC-24**: High income but low credit
- **TC-25**: Minimum viable applicant (all thresholds at boundary)

## Batch Response Format

```json
{
  "totalTests": 25,
  "passed": 25,
  "failed": 0,
  "results": [
    {
      "testName": "TC-01: Complete valid applicant",
      "testPassed": true,
      "actualRuleResults": {
        "applicantDataIsValid": true,
        "applicantIncomeIsVerified": true,
        ...
      },
      "assertions": [
        { "field": "fullyValid", "expected": true, "actual": true, "match": true }
      ]
    }
  ]
}
```

## Adding Your Own Rules

1. Drop `.drl` files into `src/main/resources/rules/`
2. Create or update the fact class in `com.droolstest.rules`
3. Add test cases to `test-cases.json`
4. Restart the application

## Project Structure

```
src/main/
├── java/com/droolstest/
│   ├── DroolsValidationTestApplication.java  # Main class
│   ├── config/DroolsConfig.java              # Drools KieContainer setup
│   ├── controller/ValidationController.java  # REST endpoints
│   ├── dto/                                  # Request/Response DTOs
│   │   ├── ValidationRequest.java
│   │   ├── ValidationResponse.java
│   │   ├── BatchValidationRequest.java
│   │   └── BatchValidationResponse.java
│   ├── rules/ApplicantFact.java              # Drools fact class
│   └── service/ValidationService.java        # Rule execution service
└── resources/
    ├── application.properties
    ├── rules/applicant-validation.drl        # Drools rules
    ├── single-test.json                      # Single validation example
    └── test-cases.json                       # 25 batch test cases
```
