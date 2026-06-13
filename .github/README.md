# ChanakyaIQ Documentation

This directory contains comprehensive documentation for the ChanakyaIQ project, designed to provide complete context for developers, AI agents, and contributors.

> **🗺️ Need help navigating?** See [DOCUMENTATION-MAP.md](./DOCUMENTATION-MAP.md) for visual guide and documentation relationships.

## 📚 Documentation Files

### [AGENTS.md](./AGENTS.md) - **START HERE**
Complete project documentation including:
- Project overview and purpose
- Tech stack (Spring Boot 4, React 19, Java 21, TypeScript)
- Architecture and directory structure
- API endpoints and database schema
- Business logic and key features
- Development workflow and setup
- Troubleshooting guide
- Configuration files

**Use this as your primary reference for understanding the entire project.**

### [OAUTH2-FLOW.md](./OAUTH2-FLOW.md)
Deep dive into authentication:
- Complete OAuth2 flow with step-by-step diagrams
- Google OAuth2 integration details
- Session management (JSESSIONID cookies)
- Security features and configuration
- User provisioning logic
- CORS and credential handling
- Troubleshooting authentication issues

**Use this when working on authentication, security, or understanding the login flow.**

## 🎯 Quick Navigation

| I want to... | Go to... |
|--------------|----------|
| Understand the entire project | [AGENTS.md](./AGENTS.md) |
| Learn how authentication works | [OAUTH2-FLOW.md](./OAUTH2-FLOW.md) → [Security Config](./AGENTS.md#security-configuration) |
| See all API endpoints | [AGENTS.md - API Endpoints](./AGENTS.md#api-endpoints) |
| Understand database structure | [AGENTS.md - Database Schema](./AGENTS.md#database-schema) |
| Setup development environment | [AGENTS.md - Development Workflow](./AGENTS.md#development-workflow) |
| Fix authentication issues | [OAUTH2-FLOW.md - Troubleshooting](./OAUTH2-FLOW.md#-common-issues--solutions) |
| Add new features | [AGENTS.md - Common Development Tasks](./AGENTS.md#common-development-tasks) |
| Understand business logic | [AGENTS.md - Business Logic Highlights](./AGENTS.md#business-logic-highlights) |

## 🏗️ Project Structure

```
chanakyaiq/
├── .github/
│   ├── README.md          ← You are here
│   ├── AGENTS.md          ← Complete project documentation
│   └── OAUTH2-FLOW.md     ← Authentication deep dive
├── backend/               ← Spring Boot API (Java 21)
│   └── chanakya-iq-service/
└── frontend/              ← React UI (TypeScript)
```

## 🚀 Quick Start

1. **Read [AGENTS.md](./AGENTS.md)** for complete context
2. **Setup environment** following [Development Workflow](./AGENTS.md#development-workflow)
3. **Configure OAuth2** using [OAUTH2-FLOW.md](./OAUTH2-FLOW.md)
4. **Start developing** with [Common Development Tasks](./AGENTS.md#common-development-tasks)

## 🔑 Key Technologies

- **Backend**: Java 21, Spring Boot 4.0.6, Spring Security, JPA/Hibernate, H2 Database
- **Frontend**: React 19.2.6, TypeScript 6.0.3, Vite 8.0.12
- **Authentication**: Google OAuth2 (session-based with JSESSIONID cookies)
- **Architecture**: Layered architecture with interface-driven design

## 💡 Documentation Philosophy

These documents are designed to:
- ✅ Provide **complete context** without needing to explore every file
- ✅ Enable **AI agents** to understand the project quickly
- ✅ Help **new developers** onboard efficiently
- ✅ Serve as **reference documentation** for existing team members
- ✅ Document **architecture decisions** and **design patterns**

## 📝 Keeping Documentation Updated

When making significant changes:
1. Update [AGENTS.md](./AGENTS.md) for structural/architectural changes
2. Update [OAUTH2-FLOW.md](./OAUTH2-FLOW.md) for authentication changes
3. Add new documentation files for major new features
4. Link new docs from this README

## 🤝 Contributing

See [AGENTS.md - Contributing Guidelines](./AGENTS.md#contact--contribution) for contribution process.

---

**Last Updated**: June 13, 2026  
**Maintained by**: Harish Singla
