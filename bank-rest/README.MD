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
- **Docker / Docker Compose**
- **Springdoc OpenAPI (Swagger UI)**

---

## Запуск проекта

### Через Docker

1. Перейти в корень проекта
2. Выполнить docker-compose up --build

---


###  Документация
Swagger UI:
http://localhost:8080/swagger-ui/index.html


Так как проект не боевой, не стал подключать .env, все секреты лежат в application.yaml :)
