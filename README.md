# Project Tenpo

## 📌 Project Description

Project Tenpo is a **Spring Boot** application that provides a set of **RESTful APIs** for performing calculations and retrieving call history. It includes **rate limiting** to control request flow and uses **Redis caching** for efficient handling of dynamic percentages.

---

## 🚀 Tech Stack & Tools

- **Java 21** - Primary programming language.
- **Spring Boot** - Framework for rapid application development.
- **Lombok** - Reduces boilerplate code in Java classes.
- **Spring AOP** - Implements cross-cutting concerns (e.g., logging and call history tracking).
- **Reactor** - Enables reactive programming using the **Reactive Streams API**.
- **Redis** - Used for caching dynamic percentage values.
- **Resilience4j** - Provides fault tolerance (Rate Limiting & Retry mechanisms).
- **Swagger** - API documentation and testing interface.
- **Docker** - Ensures environment consistency through containerization.
- **Gradle** - Build automation and dependency management tool.

---

## 🛠 How to Run the Project

### Prerequisites
Ensure you have the following installed on your machine:
- **Docker** & **Docker Compose**

### Steps to Run
1. **Clone the repository:**
   ```sh
   git clone https://github.com/santigg9/tenpo-challenge.git
   cd project-tenpo
   ```
2. **Navigate to the project root where `docker-compose.yml` is located.**
3. **Run the application using Docker Compose:**
   ```sh
   docker-compose up --build -d
   ```
   This will build and start the application along with any required services like Redis.
4. **Access the API documentation:**
   - Open [Swagger UI](http://localhost:8080/swagger-ui/index.html#/) in your browser.

---

## 📖 API Documentation

You can explore the available API endpoints using **Swagger UI** at:

🔗 [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)

---

