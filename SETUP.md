# Setup Guide: ACM Internal Certification Tool

This guide provides instructions to set up the ACM Internal Certification Tool on your local machine.

## Prerequisites

Before you begin, ensure you have the following installed on your machine:

1.  **JDK 21**: This project requires Java 21 or higher.
    *   Download from [Adoptium](https://adoptium.net/temurin/releases/?version=21).
2.  **Maven 3.9+**: Used for building and running the project.
    *   Alternatively, use the provided `./mvnw` script.

---

## Step 1: Clone the Repository

```bash
git clone https://github.com/PEC-CSS/acm-internal-tools.git
cd acm-internal-tools
```

---

## Step 2: Configure Google Cloud Credentials

The required credentials (`secret.json` and `application.yml`) contain sensitive information and are **not** pushed to GitHub. To set up the project:

1.  **Obtain the Credentials**: Contact the project maintainer or team lead to get the pre-configured `secret.json` and `application.yml` files.
2.  **Place `secret.json`**: Move the `secret.json` file to the following directory:
    `server/certification/src/main/resources/secret.json`
3.  **Place `application.yml`**: Move the `application.yml` file to the following directory (overwrite the existing one if necessary):
    `server/certification/src/main/resources/application.yml`

---

## Step 3: Build and Run

Navigate to the project directory and run the application:

```bash
cd server/certification
./mvnw clean install
./mvnw spring-boot:run
```

The server will start on `http://localhost:8080`.

---

## Step 4: How to Use

1.  Open your browser and navigate to `http://localhost:8080/ui`.
2.  **Upload a Template**: Use the `Cream Bordered Appreciation Certificate (1).pdf` file located in the root directory as the default certificate template.
3.  **Upload CSV**: Use the `test-participants.csv` file located in the root directory of this repository for testing.
4.  **Send Certificates**: Click the "Send Certificates" button to start the mass-mailing process using the configured Gmail account.

---

## Troubleshooting

*   **Database Error**: The project uses SQLite. A file named `certificate.db` will be created automatically in the `server/certification` directory.
*   **Gmail Authentication**: If you encounter authentication errors, double-check that your `secret.json` and `application.yml` are correctly placed in the `src/main/resources` folder.
