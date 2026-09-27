# Notifan

![Status](https://img.shields.io/badge/status-final%20development-orange?style=flat-square)
![Distributed Systems](https://img.shields.io/badge/distributed%20systems-5C6BC0?style=flat-square)
![Event Driven](https://img.shields.io/badge/event--driven-00897B?style=flat-square)
![Reliability](https://img.shields.io/badge/reliability-7B1FA2?style=flat-square)

A distributed notification system built to explore the parts of backend engineering that get interesting when things start failing.

Built with **Spring Boot, Kafka, Redis, PostgreSQL, Prometheus, and Grafana**.

## 🚧 Status

The project is currently in the **final phase of development**. The main reliability and observability mechanisms are already implemented:

- idempotency
- rate limiting
- retries with exponential backoff
- dead-letter handling
- circuit breaking
- application metrics with Prometheus
- dashboards with Grafana

I’m currently finalizing the remaining features, tests, and documentation.

> [!IMPORTANT]
> A full README with architecture diagrams, failure scenarios, observability, and design decisions is coming soon.
