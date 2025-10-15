# Bank System API

REST API для управления пользователями и банковскими картами.  
Поддерживает регистрацию, авторизацию (JWT), создание и блокировку карт, просмотр баланса и транзакций.  
Реализованы юнит-тесты, интеграционные тесты и OpenAPI документация.

---

## Технологии

- **Java 17**
- **Spring Boot 3**
- **Spring Security (JWT)**
- **Spring Data JPA + PostgreSQL**
- **Lombok**
- **JUnit 5 + Mockito**
- **Testcontainers (интеграционные тесты)**
- **Docker / Docker Compose**
- **Springdoc OpenAPI (Swagger UI)**

---

## Запуск проекта

### Через IDE (IntelliJ IDEA)

1. Поднять Docker, выполнив команду docker-compose up --build.
2. Открыть проект в IntelliJ IDEA.
3. Найти класс `BankRestApplication.java` (в пакете `org.example.bankcards`).
4. Запустить его как **Spring Boot Application**.
5. API будет доступен по адресу:  
    `http://localhost:8080/api/v1`

---

###  Через Maven (если без IDE)

mvnw.cmd clean package
java -jar target/bank-0.0.1-SNAPSHOT.jar

###  Документация
Swagger UI:
http://localhost:8080/swagger-ui/index.html


Так как проект не боевой, не стал подключать .env, все секреты лежат в application.yaml :)
