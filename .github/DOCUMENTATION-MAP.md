# Documentation Map

Visual guide to ChanakyaIQ documentation structure and relationships.

## 📊 Documentation Hierarchy

```
┌─────────────────────────────────────────────────────────────────┐
│                     .github/README.md                           │
│                  Documentation Index                            │
│              (Start here for navigation)                        │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ├─────────────────────────────────────┐
                         │                                     │
                         ▼                                     ▼
         ┌───────────────────────────┐         ┌──────────────────────────┐
         │      AGENTS.md            │◄────────┤   OAUTH2-FLOW.md         │
         │  Complete Project Docs    │         │  Authentication Details  │
         │                           │         │                          │
         │  • Architecture           │         │  • Login Flow Diagram    │
         │  • Tech Stack             │         │  • Session Management    │
         │  • API Endpoints          │         │  • Security Features     │
         │  • Database Schema        │         │  • CORS & Cookies        │
         │  • Business Logic         │         │  • Troubleshooting       │
         │  • Dev Workflow           │         │  • Google OAuth Setup    │
         │  • Troubleshooting        │         │                          │
         │                           │         │  Links back to:          │
         │  Links to:                │────────►│  • AGENTS.md             │
         │  • OAUTH2-FLOW.md (×3)    │         │                          │
         └───────────────────────────┘         └──────────────────────────┘
                         │
                         │ Cross-references throughout:
                         │
                         ├─► Section: Authentication Flow
                         ├─► Section: Security Configuration  
                         └─► Section: Documentation Index
```

## 🎯 When to Use Each Document

### Use [README.md](./)
**Purpose**: Navigation hub and quick reference  
**When**:
- First time exploring documentation
- Looking for a specific topic quickly
- Need to find the right document

### Use [AGENTS.md](./AGENTS.md)
**Purpose**: Complete project context and reference  
**When**:
- Understanding project architecture
- Learning the tech stack
- Looking up API endpoints
- Checking database schema
- Setting up development environment
- Adding new features
- General troubleshooting
- Need comprehensive overview

### Use [OAUTH2-FLOW.md](./OAUTH2-FLOW.md)
**Purpose**: Authentication deep dive  
**When**:
- Implementing authentication features
- Debugging login issues
- Understanding session management
- Configuring OAuth2 credentials
- Learning how Google OAuth works
- Troubleshooting CORS/cookie issues
- Security audits

## 📍 Key Cross-References

### From AGENTS.md → OAUTH2-FLOW.md

1. **Header Section** (Line ~3):
   ```markdown
   > **📚 Additional Documentation:**
   > - [OAuth2 Authentication Flow](./OAUTH2-FLOW.md)
   ```

2. **Authentication Flow Section** (Line ~145):
   ```markdown
   > **📖 Detailed Documentation:** See [OAUTH2-FLOW.md](./OAUTH2-FLOW.md)
   > for complete flow diagrams, step-by-step process, security features
   ```

3. **Security Configuration Section** (Line ~310):
   ```markdown
   > **🔐 Deep Dive:** For complete OAuth2 authentication flow with diagrams,
   > see [OAUTH2-FLOW.md](./OAUTH2-FLOW.md)
   ```

4. **Documentation Index Section** (Line ~485):
   - Table with links to all specialized topics
   - Quick reference grid

### From OAUTH2-FLOW.md → AGENTS.md

1. **Header Section** (Line ~3):
   ```markdown
   > **📖 Main Documentation:** See [AGENTS.md](./AGENTS.md)
   > for complete project context, architecture, and development guide
   ```

## 🔗 Related Files Referenced

### Configuration Files
- `.env` - Environment variables
- `application.properties` - Spring Boot config
- `SecurityConfig.java` - Security setup
- `package.json` - Frontend dependencies
- `pom.xml` - Backend dependencies

### Key Source Files
- `App.tsx` - Frontend authentication logic
- `UserController.java` - Auth endpoint & user provisioning
- `SecurityConfig.java` - OAuth2 configuration
- `PortfolioServiceImpl.java` - Business logic example
- `TradeServiceImpl.java` - Transaction handling

## 📈 Documentation Flow for Common Tasks

### Task: "Set up the project from scratch"
```
1. .github/README.md (orientation)
   ↓
2. AGENTS.md → Development Workflow
   ↓
3. OAUTH2-FLOW.md → Configuration Checklist
   ↓
4. AGENTS.md → Troubleshooting
```

### Task: "Fix OAuth login issues"
```
1. OAUTH2-FLOW.md → Troubleshooting
   ↓
2. OAUTH2-FLOW.md → Complete Flow (understand what should happen)
   ↓
3. AGENTS.md → Security Configuration (check CORS setup)
   ↓
4. OAUTH2-FLOW.md → Testing the Flow
```

### Task: "Add a new API endpoint"
```
1. AGENTS.md → Architecture (understand layers)
   ↓
2. AGENTS.md → API Endpoints (see existing patterns)
   ↓
3. AGENTS.md → Common Development Tasks → Adding Endpoint
   ↓
4. AGENTS.md → Business Logic Highlights (implementation examples)
```

### Task: "Understand how authentication works"
```
1. OAUTH2-FLOW.md → Overview
   ↓
2. OAUTH2-FLOW.md → Complete Authentication Flow (diagram)
   ↓
3. OAUTH2-FLOW.md → Key Components Explained
   ↓
4. AGENTS.md → Security Configuration (practical setup)
```

## 🎨 Documentation Design Principles

### 1. **Progressive Disclosure**
- README → Quick navigation
- AGENTS.md → Comprehensive reference
- OAUTH2-FLOW.md → Specialized deep dive

### 2. **No Duplication**
- Core info in AGENTS.md
- Specialized topics in dedicated files
- Cross-references instead of copying

### 3. **Bi-directional Links**
- Every specialized doc links back to AGENTS.md
- AGENTS.md links forward to specialized docs
- Easy navigation in both directions

### 4. **Context Awareness**
- Each doc states its purpose upfront
- Links provide context ("for complete flow diagrams...")
- Clear guidance on when to use each doc

## 🧭 Quick Links by Topic

| Topic | Primary Source | Secondary Source |
|-------|---------------|------------------|
| **Project Overview** | [AGENTS.md](./AGENTS.md#project-overview) | - |
| **Architecture** | [AGENTS.md](./AGENTS.md#project-architecture) | - |
| **Tech Stack** | [AGENTS.md](./AGENTS.md#tech-stack) | - |
| **Authentication** | [OAUTH2-FLOW.md](./OAUTH2-FLOW.md) | [AGENTS.md](./AGENTS.md#1-authentication-flow) |
| **Security** | [OAUTH2-FLOW.md](./OAUTH2-FLOW.md#-security-features) | [AGENTS.md](./AGENTS.md#security-configuration) |
| **API Reference** | [AGENTS.md](./AGENTS.md#api-endpoints) | - |
| **Database** | [AGENTS.md](./AGENTS.md#database-schema) | - |
| **Business Logic** | [AGENTS.md](./AGENTS.md#business-logic-highlights) | - |
| **Development Setup** | [AGENTS.md](./AGENTS.md#development-workflow) | - |
| **Troubleshooting** | [AGENTS.md](./AGENTS.md#troubleshooting) | [OAUTH2-FLOW.md](./OAUTH2-FLOW.md#-common-issues--solutions) |
| **Session Management** | [OAUTH2-FLOW.md](./OAUTH2-FLOW.md#5-session-management) | - |
| **CORS Configuration** | [OAUTH2-FLOW.md](./OAUTH2-FLOW.md#cors-configuration) | [AGENTS.md](./AGENTS.md#cors-settings) |

## 🔄 Documentation Maintenance

### When to Update AGENTS.md
- New features added
- Architecture changes
- New API endpoints
- Database schema changes
- Configuration changes
- New development tasks

### When to Update OAUTH2-FLOW.md
- Authentication flow changes
- Security configuration updates
- New OAuth providers
- Session management changes
- CORS policy updates

### When to Update README.md
- New documentation files added
- Structure changes
- Navigation improvements

---

**Purpose**: This map helps you navigate the documentation efficiently  
**Audience**: Developers, AI agents, contributors  
**Last Updated**: June 13, 2026
