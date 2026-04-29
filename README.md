# Project Documentation

## Project Overview
This project focuses on demonstrating memory leaks in Spring Boot applications by implementing specific case studies that reveal how memory leaks can occur and how to analyze them effectively.

## Tech Stack
- **Java**: 11+
- **Spring Boot**: 2.x
- **JVM**: OpenJDK
- **Tools**: JVisualVM, Eclipse Memory Analyzer Tool (MAT)

## Project Structure
```
├── src
│   ├── main
│   │   └── java
│   └── resources
├── pom.xml
└── README.md
```

## Prerequisites
- Java Development Kit (JDK) 11+
- Maven (for dependency management)
- IDE (Eclipse, IntelliJ, etc.)

## How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/msedky/springboot-memory-leak-case-study.git
   cd springboot-memory-leak-case-study
   ```
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## How to Reproduce Memory Leaks
Follow these steps to observe memory leaks:
1. Execute specific endpoints provided in the case studies.
2. Monitor the application memory usage using JVisualVM.

## JVisualVM and Eclipse MAT Analysis Guides
- **JVisualVM**: Monitor and analyze memory usage.
- **Eclipse MAT**: Use this tool to find memory leaks by analyzing heap dumps.

## Case Studies
1. **Case Study 1**: Inefficient caching mechanism.
2. **Case Study 2**: Improper resource handling.

## Performance Comparison
- Analyze memory usage and performance metrics before and after applying fixes for memory leaks.

## Tools Reference
- JVisualVM
- Eclipse MAT
- Spring Boot Actuator

## Learning Outcomes
- Understand how memory leaks can occur in Spring Boot applications.
- Learn how to prevent and fix these issues effectively.

## Recommended Reading
- [Java Performance: The Definitive Guide](https://www.oreilly.com/library/view/java-performance-the/9781440343488/)
- [Spring Boot in Action](https://www.manning.com/books/spring-boot-in-action)

## Contributing Guidelines
1. Fork the repository.
2. Create a new branch for your feature or fix.
3. Make your changes and submit a pull request.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author Information
Created by [msedky](https://github.com/msedky)