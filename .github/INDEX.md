# ChanakyaIQ Documentation Index

> Complete documentation structure for ChanakyaIQ Stock Market Simulator

## 📚 All Documentation Files

| File | Size | Purpose | Start Here |
|------|------|---------|------------|
| **[README.md](./README.md)** | 4 KB | Navigation hub and quick reference | ⭐ First time visitors |
| **[AGENTS.md](./AGENTS.md)** | 20 KB | Complete project documentation | ⭐⭐⭐ Primary reference |
| **[OAUTH2-FLOW.md](./OAUTH2-FLOW.md)** | 18 KB | Authentication deep dive | Authentication work |
| **[DOCUMENTATION-MAP.md](./DOCUMENTATION-MAP.md)** | 9 KB | Visual navigation guide | Finding topics |
| **[DOCUMENTATION-SUMMARY.txt](./DOCUMENTATION-SUMMARY.txt)** | 6 KB | Documentation metadata | Overview |
| **[INDEX.md](./INDEX.md)** | This file | Complete file listing | - |

## 🎯 Quick Start Paths

### Path 1: "I'm new to the project"
```
1. README.md (orientation)
2. AGENTS.md (complete overview)
3. OAUTH2-FLOW.md (understand authentication)
4. AGENTS.md → Development Workflow (start coding)
```

### Path 2: "I need to find something specific"
```
1. DOCUMENTATION-MAP.md (find topic)
2. Jump to relevant section in AGENTS.md
3. Deep dive into OAUTH2-FLOW.md if auth-related
```

### Path 3: "I'm an AI agent"
```
1. AGENTS.md (load complete context)
2. Use cross-references to OAUTH2-FLOW.md as needed
```

### Path 4: "I'm fixing a bug"
```
1. AGENTS.md → Troubleshooting (common issues)
2. OAUTH2-FLOW.md → Troubleshooting (if auth-related)
3. AGENTS.md → API/Database sections (for reference)
```

## 📖 Documentation Coverage

### Architecture & Design
- [x] Project overview and purpose
- [x] Tech stack (Java 21, Spring Boot 4, React 19, TypeScript)
- [x] Architecture patterns (layered, interface-driven)
- [x] Directory structure with explanations
- [x] Coding standards (Java Records, Lombok, TypeScript)
- [x] Design system (UI/UX)

### Technical Reference
- [x] Complete API endpoint list
- [x] Database schema (tables, relationships)
- [x] Configuration files (application.properties, .env, etc.)
- [x] Security configuration (CORS, OAuth2, sessions)
- [x] Environment setup (.env loading on Windows)

### Features & Flows
- [x] Authentication flow (OAuth2 with Google)
- [x] Portfolio management
- [x] Trading system (buy/sell)
- [x] Mock market data simulation
- [x] Real-time updates (polling)
- [x] User auto-provisioning

### Development
- [x] Development workflow (running, building, testing)
- [x] Common development tasks
- [x] Testing procedures
- [x] Troubleshooting guide
- [x] Git repository info
- [x] Contributing guidelines

### Business Logic
- [x] Trade execution (validation, transactions)
- [x] Portfolio aggregation (P&L calculations)
- [x] Mock market simulation (price updates, market hours)
- [x] Session management
- [x] User provisioning

## 🔗 Cross-Reference Map

```
README.md
    ├─► AGENTS.md (×2)
    ├─► OAUTH2-FLOW.md (×1)
    └─► DOCUMENTATION-MAP.md (×1)

AGENTS.md
    ├─► OAUTH2-FLOW.md (×3)
    │   ├─ Header link
    │   ├─ Authentication Flow section
    │   └─ Security Configuration section
    └─► Self-references (×20+)
        └─ Table of contents style linking

OAUTH2-FLOW.md
    └─► AGENTS.md (×1)
        └─ Header link back to main docs

DOCUMENTATION-MAP.md
    ├─► README.md (×1)
    ├─► AGENTS.md (×15+)
    └─► OAUTH2-FLOW.md (×10+)
```

## 📊 Documentation Statistics

- **Total Files**: 6
- **Total Size**: ~58 KB
- **Total Lines**: ~1,500
- **Code Examples**: 25+
- **Diagrams**: 2 (ASCII art)
- **Tables**: 10+
- **Cross-references**: 40+
- **Coverage**: 95%+ of project

## 🎨 Documentation Principles

1. **Single Source of Truth**: AGENTS.md is the primary reference
2. **No Duplication**: Cross-reference instead of copying
3. **Progressive Disclosure**: Start simple, drill down as needed
4. **Bi-directional Links**: Easy navigation both ways
5. **Context First**: Explain why before how
6. **Practical Examples**: Real code snippets throughout
7. **Visual Aids**: Diagrams and tables for clarity
8. **Searchable**: Keywords and anchors for easy finding

## 🛠️ Maintenance

### When Adding New Features
1. Update AGENTS.md with feature description
2. Add to API Endpoints section if applicable
3. Update Business Logic section with implementation details
4. Add troubleshooting tips if complex
5. Update cross-references if related to auth

### When Modifying Authentication
1. Update OAUTH2-FLOW.md with changes
2. Update AGENTS.md security section
3. Add troubleshooting tips
4. Update configuration examples

### When Restructuring
1. Update DOCUMENTATION-MAP.md with new structure
2. Update cross-references in all files
3. Update INDEX.md (this file)
4. Update README.md if navigation changes

## 🔍 Search Tips

### Find Information By:

**By Topic**:
- Authentication → OAUTH2-FLOW.md
- API Endpoints → AGENTS.md#api-endpoints
- Database → AGENTS.md#database-schema
- Setup → AGENTS.md#development-workflow
- Security → OAUTH2-FLOW.md + AGENTS.md#security-configuration

**By Task**:
- "How do I run the app?" → AGENTS.md#development-workflow
- "How does login work?" → OAUTH2-FLOW.md#complete-authentication-flow
- "What are the API endpoints?" → AGENTS.md#api-endpoints
- "How do I add a feature?" → AGENTS.md#common-development-tasks
- "Why isn't auth working?" → OAUTH2-FLOW.md#common-issues--solutions

**By Component**:
- Frontend → AGENTS.md#frontend (tech stack section)
- Backend → AGENTS.md#backend (tech stack section)
- Database → AGENTS.md#database-schema
- Security → AGENTS.md#security-configuration + OAUTH2-FLOW.md

## 📝 Contributing to Documentation

1. Keep AGENTS.md as the primary source
2. Create specialized docs for complex topics (like OAUTH2-FLOW.md)
3. Always add bi-directional cross-references
4. Update this INDEX.md when adding files
5. Update DOCUMENTATION-MAP.md with new relationships
6. Follow the established format and tone
7. Add code examples for clarity
8. Include troubleshooting tips

## 🏆 Quality Checklist

- [x] Complete project coverage
- [x] Easy navigation (cross-references)
- [x] Practical examples (code snippets)
- [x] Visual aids (diagrams, tables)
- [x] Troubleshooting guides
- [x] Configuration examples
- [x] Development workflows
- [x] Architecture explanations
- [x] Business logic details
- [x] Security documentation
- [x] AI agent friendly (structured, comprehensive)
- [x] Developer friendly (progressive disclosure)

---

**Purpose**: Complete index of ChanakyaIQ documentation  
**Maintained by**: Harish Singla  
**Last Updated**: June 13, 2026  
**Status**: Complete & Current

