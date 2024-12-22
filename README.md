## Getting Started with the Project

This project serves as an educational platform for mastering Controller Tests and Integration Tests, utilizing PostgreSQL and Testcontainers within Docker containers. 
Ensure that Docker Desktop is active on your system to guarantee a smooth setup and operational flow.

In this learning module, we will develop Repository tests and Controller tests that interact with our database.

### Key Resources
- **Test Suite**: All tests can be found in the `/src/test/java/` directory.
- **Exception Handling**: Custom exception handling is implemented to provide precise and informative error messages. 
The relevant code is located in `src/main/java/dev/william/willson/globalException`.
- **API Documentation**: Explore the API endpoints through the Swagger UI accessible at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

### How to Run
Spring Boot is configured to manage the `docker-compose.yml` file, automatically running `docker-compose up` at startup. 
This feature streamlines the development and testing process, allowing you to focus on learning and experimentation.