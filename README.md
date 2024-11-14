Here is a `README.md` file for your project:

```markdown
# PaymentService

## Description
PaymentService is a Spring Boot application that integrates with PayPal for processing payments. It uses PostgreSQL as the database.

## Prerequisites
- Java 17
- Maven
- Docker (for running PostgreSQL)

## Setup

### 1. Clone the repository
```sh
git clone https://github.com/your-repo/PaymentService.git
cd PaymentService
```

### 2. Start PostgreSQL using Docker
```sh
docker-compose up -d
```

### 3. Configure the application
Update the `application.yml` file with your PayPal credentials and database connection details.

```yaml
paypal:
  client-id: your-client-id
  client-secret: your-client-secret
  mode: sandbox
```

### 4. Build the project
```sh
mvn clean install
```

### 5. Run the application
```sh
mvn spring-boot:run
```

## API Endpoints
The application runs on `http://localhost:8080`.

### Create a Payment
**Endpoint:** `POST /api/payments/create`

**Request Body:**
```json
{
  "userId": "b5f8c34e-5d8b-4a3d-98b9-ecfd9d0c21b5",
  "amount": 100.00,
  "currency": "USD",
  "description": "Payment for chatbot subscription",
  "payerEmail": "user@example.com",
  "subscriptionId": "3d2c8c4b-4d5f-4e8e-b3f8-2e8d88e0f53b"
}
```

**Example Response:**
```
https://www.sandbox.paypal.com/checkoutnow?token=35B81566EC6448458
```

Click the response URL to redirect to PayPal for payment.

## License
This project is licensed under the MIT License.
```

This `README.md` file provides an overview of the project, setup instructions, API endpoints, and examples of how to integrate with PayPal.