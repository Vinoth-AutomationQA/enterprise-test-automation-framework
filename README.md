# Enterprise E2E Automation Framework

## Overview
Production-ready unified UI and API test automation framework designed for enterprise scalability, maintainability, and auditability.

## Key Features
- **Layered Architecture**: Tests → Flows → Helpers → Core (strict dependency boundaries)
- **Unified Reporting**: Single report for UI screenshots, API traces, and DB validations
- **Environment Management**: Dev/Staging/Prod with dynamic credential resolution
- **Evidence Capture**: Automatic screenshots, API traces, and logs at every step
- **Cross-Layer Validation**: UI → API → Database verification in single test
- **CI/CD Ready**: GitHub Actions, Jenkins pipelines, Docker containerization

## Technology Stack
- **Language**: Java 11
- **Build Tool**: Maven
- **UI Automation**: Selenium WebDriver 4.x
- **API Testing**: REST Assured 5.x
- **Test Framework**: TestNG 7.x
- **Reporting**: ExtentReports 5.x / Allure 2.x
- **Logging**: Log4j2
- **Database**: JDBC (MySQL, PostgreSQL)

## Quick Start

### Prerequisites
- JDK 11 or higher
- Maven 3.8+
- Git

### Clone and Setup
```bash
git clone <your-repo-url>
cd automation-framework
mvn clean install
```

### Run Tests
```bash
# Run smoke tests on dev environment
mvn test -Denv=dev -DsuiteXmlFile=suites/smoke-suite.xml

# Run regression tests on staging
mvn test -Denv=staging -DsuiteXmlFile=suites/regression-suite.xml

# Run specific test class
mvn test -Dtest=LoginTests
```

### View Reports
```bash
# Open ExtentReports
open reports/extent-report.html

# Generate and open Allure report
mvn allure:serve
```

## Project Structure
```
automation-framework/
├── src/main/java/          # Framework core code
├── src/test/java/          # Test implementations
├── src/main/resources/     # Configuration files, test data
├── ci/                     # CI/CD pipeline configurations
├── docs/                   # Documentation
└── reports/                # Test execution reports
```

## Configuration
Environment configuration files are located in `src/main/resources/config/`:
- `default.properties` - Default values
- `dev.properties` - Development environment
- `staging.properties` - Staging environment
- `prod.properties` - Production environment

Switch environments using: `-Denv=<environment>`

## Writing Tests

### Simple UI Test
```java
@Test
public void testLogin() {
    LoginFlow loginFlow = new LoginFlow();
    DashboardPage dashboard = loginFlow.loginAsUser("testuser", "password");
    
    WebAssertions.assertElementVisible(dashboard.getWelcomeMessage());
}
```

### Simple API Test
```java
@Test
public void testCreateTransfer() {
    TransferRequest request = TransferRequestBuilder.newTransfer()
        .from("ACC001")
        .to("ACC002")
        .amount(100.0)
        .build();
    
    TransfersClient client = new TransfersClient();
    TransferResponse response = client.createTransfer(request);
    
    Assert.assertEquals(response.getStatus(), "COMPLETED");
}
```

### End-to-End Test
```java
@Test
public void testCompleteTransfer() {
    // UI Layer
    LoginFlow loginFlow = new LoginFlow();
    loginFlow.loginAsUser("testuser", "password");
    
    TransferFlow transferFlow = new TransferFlow();
    String transferId = transferFlow.initiateTransfer("ACC001", "ACC002", 500.0);
    
    // API Layer
    TransactionHelper txnHelper = new TransactionHelper();
    TransferResponse apiResponse = txnHelper.getTransferDetails(transferId);
    
    // DB Layer
    DatabaseClient dbClient = new DatabaseClient();
    TransferRecord dbRecord = dbClient.getTransferById(transferId);
    
    // Cross-layer validation
    ValidationHelper.assertAPIMatchesDB(apiResponse.getAmount(), dbRecord.getAmount());
}
```

## CI/CD Integration

### GitHub Actions
```bash
# Workflow file located at: .github/workflows/test-automation.yml
# Triggered on: push, pull_request, schedule (nightly)
```

### Jenkins
```bash
# Jenkinsfile located at: ci/pipelines/Jenkinsfile
# Configure Jenkins job to use this file
```

### Docker
```bash
# Build test runner image
docker build -t automation-framework:latest -f ci/docker/Dockerfile .

# Run tests in container
docker-compose -f ci/docker/docker-compose.yml up
```

## Contributing
Please read [docs/contribution-guide.md](docs/contribution-guide.md) for coding standards and review process.

## Documentation
- [Architecture Guide](docs/architecture.md)
- [Onboarding Guide](docs/onboarding.md)
- [Locator Strategy](docs/locator-strategy.md)
- [Troubleshooting](docs/troubleshooting.md)

## Support
For issues and questions, please create a GitHub issue or contact the automation team.

## License
Internal Enterprise Use Only