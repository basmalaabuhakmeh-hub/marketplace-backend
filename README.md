# Marketplace backend

Spring Boot REST API for a small marketplace: **Customer**, **Seller**, **Admin**, and **Driver**.

Forgot-password OTPs are **not** handled in this app. They live in a separate microservice:

**[ForgetPassword-microService](https://github.com/basmalaabuhakmeh-hub/ForgetPassword-microService)**

This service calls it with `RestTemplate` (`POST /otp/send` and `POST /otp/verify`).

## Features

- JWT login (signup on `/auth/**`, no token)
- Admin approval for sellers and drivers
- Products (pagination, bulk create, stock)
- Orders: place → ship → assign driver → deliver (or cancel while `PLACED`)
- Soft delete for users and products
- Forgot / reset password via the OTP microservice

## Run

- Java 17, Maven, MySQL
- Database: `backend_training` (see `src/main/resources/application.properties`)
- Start the **OTP service on port 8081 first**, then this app on **8080**

Default admin (seeded if no admin exists): `admin@mail.com` / `admin123`

## Forgot password (needs both apps)

```
POST http://localhost:8080/auth/forgot-password
{ "email": "your-existing-user@mail.com" }

POST http://localhost:8080/auth/reset-password
{ "email": "your-existing-user@mail.com", "otp": "123456", "newPassword": "new123" }
```

Until Gmail is configured on the OTP service, the 6-digit code is printed in that service’s console.

## Auth

Protected URLs: `Authorization: Bearer <token>`  
`/auth/**` is public. After a restart, log in again (JWT secret is new each run).
