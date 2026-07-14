# Scoutly 🎯

> A microservices-based price tracking platform with automated web scraping, message queuing, and custom JWT authentication.

[![Angular](https://img.shields.io/badge/Angular-18-DD0031?style=flat-square&logo=angular)](https://angular.io/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Node.js](https://img.shields.io/badge/Node.js-Worker-339933?style=flat-square&logo=nodedotjs)](https://nodejs.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Queues-FF6600?style=flat-square&logo=rabbitmq)](https://www.rabbitmq.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-4169E1?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)
[![Deploy](https://img.shields.io/badge/Deploy-Live-brightgreen?style=flat-square)](https://scoutly.vercel.app/)

**[Live Demo →](https://scoutly-sand.vercel.app/)**

---

# Preview

![Dashboard Screen](https://i.ibb.co/hxkPMK4B/Captura-de-tela-2026-07-13-212242.png)

![Auth Screen](https://i.ibb.co/Dgg2D8pS/Captura-de-tela-2026-07-13-212315.png)

---

# About the Project

Scoutly is an automated price monitoring system that allows users to track products from supported e-commerce stores (like Amazon). Instead of constantly checking for sales, users set a target price, and the system does the heavy lifting in the background.

The application showcases a **Microservices Architecture**, separating the main REST API (Java/Spring Boot) from the web scraping engine (Node.js/Puppeteer), using **RabbitMQ** to handle asynchronous communication and prevent server bottlenecks.

---

# Architecture Overview

```text
┌─────────────────────┐        REST API (JWT)        ┌──────────────────────────┐
│                     │ ──────────────────────────▶  │                          │
│   Angular 18 SPA    │                              │   Spring Boot REST API   │
│   (Vercel)          │ ◀──────────────────────────  │   (Render)               │
│                     │                              │                          │
└─────────────────────┘                              └───────┬───────────┬──────┘
                                                             │           │
                                                             ▼           │
┌──────────────────────────┐      AMQP Protocol      ┌───────────────┐   │ JPA
│                          │ ◀────────────────────── │               │   │
│   Node.js Scraper        │                         │   RabbitMQ    │   │
│   Puppeteer + Cheerio    │ ──────────────────────▶ │  (CloudAMQP)  │   │
│                          │                         │               │   │
└───────────┬──────────────┘                         └───────────────┘   │
            │                                                            ▼
            │ HTTP Webhook (Shared Secret)                   ┌──────────────────────┐
            └──────────────────────────────────────────────▶ │  PostgreSQL (Neon)   │
                                                             └──────────────────────┘
```

---

# How the Engine Works

1. User adds a product URL and target price via the Angular frontend.
2. Spring Boot saves it to PostgreSQL and publishes a message to the `scoutly.scraping.queue`.
3. The Node.js worker consumes the message, launches a stealth headless browser (Puppeteer), and extracts the current price.
4. The worker sends the price back to Spring Boot via a secured Webhook.
5. If the current price hits the target, Spring Boot triggers a new event to the `price.alerts.queue`, formatting and dispatching an email notification to the user.

---

# Tech Stack

## Backend (Core API)

| Technology | Purpose |
|------------|---------|
| Spring Boot | Core REST API framework |
| Spring Security + JWT | Custom authentication, password recovery with tokens |
| Spring AMQP | RabbitMQ integration for event-driven processing |
| Spring Data JPA & Flyway | Database ORM and automated schema migrations |
| JavaMailSender | SMTP integration for email alerts |
| Gradle | Dependency management and multi-stage Docker builds |

---

## Background Worker (Scraper)

| Technology | Purpose |
|------------|---------|
| Node.js & TypeScript | Fast, async execution environment |
| Puppeteer & Stealth Plugin | Headless browser for bypassing basic bot detection |
| Cheerio | Fast HTML parsing and DOM traversal |
| amqplib | RabbitMQ client for consuming tasks |
| Express | Dummy server to satisfy Render's Web Service requirements |

---

## Frontend

| Technology | Purpose |
|------------|---------|
| Angular 18 (Standalone) | SPA framework |
| Reactive Forms | Login, Registration, and Tracking form validation |
| RxJS | Asynchronous state and API calls |

---

## Infrastructure

| Service | Role |
|---------|------|
| Render | Hosting for Spring Boot API and Node.js Worker (Dockerized) |
| Vercel | Frontend SPA hosting |
| Neon | Serverless PostgreSQL database |
| CloudAMQP | RabbitMQ message broker |

---

# Key Features

- **Microservices & Messaging Queue:** Heavy tasks (web scraping) are decoupled from the main API using RabbitMQ, ensuring the UI remains fast and responsive.
- **Stealth Web Scraping:** Node.js worker dynamically rotates User-Agents and utilizes `puppeteer-extra-plugin-stealth` to scrape prices accurately.
- **Webhook Security:** Communication between the Scraper and the API is protected by a custom Shared Secret header (`X-Scoutly-Secret`), preventing unauthorized price manipulations.
- **Custom JWT Authentication:** Full login, registration, and forgot/reset password flows built from scratch.
- **Automated Email Alerts:** Sends beautifully formatted HTML emails via SMTP when a product drops below the target price.
- **Multi-Service Architecture:** Independent services communicate asynchronously through RabbitMQ, making the system scalable and fault-tolerant.
- **Dockerized Worker:** Puppeteer runs inside a Docker container with all Chromium dependencies pre-installed.

---

# API & Webhook Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/login` | Authenticates and returns JWT | No |
| POST | `/api/auth/forgot-password` | Generates recovery token & sends email | No |
| GET | `/api/tracking` | Lists all tracked products for the user | Yes (JWT) |
| POST | `/api/tracking` | Adds a new product to the scraping queue | Yes (JWT) |
| PATCH | `/api/tracking/{id}/status` | Pauses/Resumes product tracking | Yes (JWT) |
| POST | `/api/tracking/webhook/price` | Receives extracted price from Node.js | Yes (Shared Secret) |

---

# Running Locally

## Prerequisites

- Java 17+
- Node.js 22+
- PostgreSQL
- RabbitMQ (Local or CloudAMQP)
- Gmail App Password (SMTP)

---

## 1. Spring Boot API

```bash
cd backend

# Database
export DB_URL=jdbc:postgresql://localhost:5432/scoutly
export DB_USER=postgres
export DB_PASSWORD=your_password

# RabbitMQ
export RABBITMQ_HOST=localhost
export RABBITMQ_PORT=5672
export RABBITMQ_USER=guest
export RABBITMQ_PASSWORD=guest
export RABBITMQ_VHOST=/

# JWT
export JWT_SECRET=your_super_secret_jwt_key
export JWT_EXPIRATION=7200000

# SMTP
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password

# Shared Secret
export SCOUTLY_WEBHOOK_SECRET=your_webhook_secret

./gradlew bootRun
```

---

## 2. Node.js Worker

```bash
cd worker

npm install

export RABBITMQ_URL=amqp://localhost:5672
export BACKEND_URL=http://localhost:8080
export SCOUTLY_WEBHOOK_SECRET=your_webhook_secret

npm run dev
```

---

## 3. Angular Frontend

```bash
cd frontend

npm install

ng serve
```

The application will be available at:

```
http://localhost:4200
```

---

# Author

**Leonardo Alvarenga**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=flat-square&logo=linkedin)](https://www.linkedin.com/in/leoalvarengam/)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-181717?style=flat-square&logo=github)](https://github.com/leoalvarengam)
