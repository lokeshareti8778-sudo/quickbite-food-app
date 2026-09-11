# QuickBite Food Ordering System

QuickBite is a small, real-world food ordering system designed as a DevOps interview project. It uses a Java 17 Spring Boot API as the main application, a Java Azure Function as the serverless order-processing component, and a responsive HTML/CSS/JavaScript frontend.

## Architecture

```text
Developer -> GitHub -> CI
                    -> Checkout
                    -> Java 17 and Maven
                    -> Spring Boot build and tests
                    -> Function build and tests
                    -> Artifacts
                         |
                         v
                       CD (only after CI success)
                    -> Azure login
                    -> Azure App Service (Spring Boot)
                    -> Azure Function App (Java)

Browser -> Spring Boot REST API -> Azure Function /api/process-order
                                  <- order ID and CONFIRMED status
```

## Technologies

- Java 17, Spring Boot 3.4, Maven
- HTML, CSS, and vanilla JavaScript
- Azure App Service for the Spring Boot JAR
- Azure Function App using the Java HTTP trigger model
- GitHub and GitHub Actions
- No Docker and no SonarQube

## Project structure

```text
food-ordering-system/
├── backend/       Spring Boot API, menu, orders, validation, and tests
├── frontend/      Static browser UI
├── function-app/  Java Azure Function and unit tests
├── .github/       CI and CD workflows
├── .gitignore
└── README.md
```

## Prerequisites

Install Java 17 and Maven, then verify them:

```bash
java -version
mvn -version
```

Install Azure Functions Core Tools if you want to run the Function locally. Git is required for GitHub workflows. No Docker installation is needed.

## Run locally in VS Code

1. Open the `food-ordering-system` folder in VS Code.
2. Start the Function App in one terminal:

   ```bash
   cd function-app
   copy local.settings.json.example local.settings.json
   mvn clean package
   func start
   ```

3. Start Spring Boot in a second terminal:

   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. Open `frontend/index.html` with a static server extension, or serve the folder with any static file server. The page expects the API at `http://localhost:8080`.

Set a deployed Function URL before starting Spring Boot:

```bash
set FUNCTION_APP_URL=https://<function-app>.azurewebsites.net/api/process-order
```

For Azure Function auth, include the function key in the URL as a protected environment value in production. Do not commit it.

## REST APIs

Get the menu:

```http
GET http://localhost:8080/api/foods
```

Get one food item:

```http
GET http://localhost:8080/api/foods/1
```

Place an order:

```http
POST http://localhost:8080/api/orders
Content-Type: application/json

{
  "customerName": "Alex Morgan",
  "email": "alex@example.com",
  "phone": "+1 555 010 2040",
  "address": "42 Market Street",
  "items": [{ "foodId": 1, "quantity": 2 }]
}
```

Get a previously processed order:

```http
GET http://localhost:8080/api/orders/QB-10001
```

The Spring Boot service calculates the menu total, sends the order payload to the Function, stores the returned response in its in-memory order store, and returns the Function's order ID and status. The Function validates the request, generates the unique ID, and returns `CONFIRMED`.

## Test and build

```bash
cd backend
mvn clean test
mvn clean package

cd ../function-app
mvn clean test
mvn clean package
```

The Spring Boot artifact is `backend/target/food-ordering-backend.jar`. The Function package is created under `function-app/target/azure-functions/food-ordering-function`.

## Azure resources

Create these resources in one resource group:

1. An Azure App Service running Java 17 on Linux for Spring Boot.
2. An Azure Storage account for Functions.
3. An Azure Function App using the Java 17 runtime on Linux.
4. A Function App application setting named `FUNCTIONS_WORKER_RUNTIME=java`.
5. An App Service application setting named `FUNCTION_APP_URL` containing the Function endpoint.

The Function App Maven plugin contains a sample `food-ordering-rg` and `eastus` configuration for interview/demo use. The GitHub Action deploys to the already-created resources named by secrets.

## GitHub secrets

Create these repository or production-environment secrets:

- `AZURE_CREDENTIALS`: JSON output from an Azure service principal with deployment access.
- `AZURE_RESOURCE_GROUP`: resource group name for Azure resource administration.
- `AZURE_WEBAPP_NAME`: Azure App Service name.
- `AZURE_FUNCTIONAPP_NAME`: Azure Function App name.

Never commit credentials, function keys, passwords, or tokens. Add `FUNCTION_APP_URL` to the App Service in Azure configuration rather than source control.

## CI and CD

`ci.yml` runs on pushes to `main` and pull requests targeting `main`. It checks out the repository, installs Java 17 with Maven caching, runs `mvn clean test package` for both Maven projects, and uploads the Spring Boot JAR and Function package. Any failed compilation or test fails CI.

`cd.yml` listens for completion of the named `CI` workflow. Its job condition is `github.event.workflow_run.conclusion == 'success'`, so a failed or cancelled CI run cannot deploy. It checks out the exact tested commit, rebuilds both Maven packages, logs in using `AZURE_CREDENTIALS`, deploys the JAR to App Service, and deploys the Function package to the Function App.

## Verify deployment

1. Host `frontend/` on static hosting and set its API URL to the App Service URL if the frontend is not served by App Service.
2. Call `https://<app-service>.azurewebsites.net/api/foods` and confirm the menu JSON.
3. Call the Function URL with a valid order payload and confirm a `QB-...` ID and `CONFIRMED` status.
4. Submit an order in the frontend and confirm the displayed order ID and status.
5. Review GitHub Actions logs and Azure App Service / Function logs for deployment and runtime health.
