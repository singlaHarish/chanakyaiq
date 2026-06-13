# OAuth2 Google Authentication Flow in ChanakyaIQ

> **📖 Main Documentation:** See [AGENTS.md](./AGENTS.md) for complete project context, architecture, and development guide.

## 🔐 Overview

Your application uses **Google OAuth2 with Spring Security** for authentication. It's a **session-based** authentication system using cookies, not JWT tokens.

---

## 📊 Complete Authentication Flow

### Step-by-Step Process

```
┌─────────────┐                  ┌──────────────┐                 ┌─────────────┐
│   Browser   │                  │ ChanakyaIQ   │                 │   Google    │
│  (Frontend) │                  │   Backend    │                 │  OAuth2     │
│   :5173     │                  │    :8080     │                 │   Server    │
└──────┬──────┘                  └──────┬───────┘                 └──────┬──────┘
       │                                │                                │
       │ 1. User clicks "Sign In"       │                                │
       ├────────────────────────────────>                                │
       │  window.location.href =        │                                │
       │  /oauth2/authorization/google  │                                │
       │                                │                                │
       │                                │ 2. Spring Security intercepts  │
       │                                │    and redirects to Google     │
       │                                ├───────────────────────────────>│
       │                                │    with client_id, redirect_uri│
       │                                │    scope=profile,email         │
       │                                │                                │
       │                3. Google Login Page                             │
       │<────────────────────────────────────────────────────────────────┤
       │                                │                                │
       │ 4. User enters credentials     │                                │
       │    and grants permissions      │                                │
       ├────────────────────────────────────────────────────────────────>│
       │                                │                                │
       │                                │ 5. Google sends auth code      │
       │                                │    to callback URL             │
       │                                │<───────────────────────────────┤
       │                                │  /login/oauth2/code/google     │
       │                                │  ?code=AUTHORIZATION_CODE      │
       │                                │                                │
       │                                │ 6. Spring exchanges code       │
       │                                │    for access token            │
       │                                ├───────────────────────────────>│
       │                                │  POST /token                   │
       │                                │  code=AUTHORIZATION_CODE       │
       │                                │                                │
       │                                │ 7. Google returns tokens       │
       │                                │<───────────────────────────────┤
       │                                │  {                             │
       │                                │    "access_token": "...",      │
       │                                │    "id_token": "...",          │
       │                                │    "expires_in": 3600          │
       │                                │  }                             │
       │                                │                                │
       │                                │ 8. Spring fetches user info    │
       │                                ├───────────────────────────────>│
       │                                │  GET /userinfo                 │
       │                                │  Authorization: Bearer token   │
       │                                │                                │
       │                                │ 9. Google returns user data    │
       │                                │<───────────────────────────────┤
       │                                │  {                             │
       │                                │    "sub": "219704",            │
       │                                │    "email": "user@gmail.com",  │
       │                                │    "name": "John Doe"          │
       │                                │  }                             │
       │                                │                                │
       │                                │ 10. Creates HTTP Session       │
       │                                │     & OAuth2User object        │
       │                                │                                │
       │ 11. Redirect to frontend       │                                │
       │     with JSESSIONID cookie     │                                │
       │<────────────────────────────────┤                                │
       │  Location: http://localhost:5173/                               │
       │  Set-Cookie: JSESSIONID=ABC123 │                                │
       │                                │                                │
       │ 12. Frontend calls /api/auth/status                             │
       │     with credentials: 'include'│                                │
       ├────────────────────────────────>                                │
       │  Cookie: JSESSIONID=ABC123     │                                │
       │                                │                                │
       │                                │ 13. Validates session          │
       │                                │     Provisions user in DB      │
       │                                │     if first login             │
       │                                │                                │
       │ 14. Returns user data          │                                │
       │<────────────────────────────────┤                                │
       │  {                             │                                │
       │    "authenticated": true,      │                                │
       │    "userId": "219704",         │                                │
       │    "email": "user@gmail.com",  │                                │
       │    "cashBalance": 1000000.00   │                                │
       │  }                             │                                │
       │                                │                                │
       │ 15. User is now logged in!     │                                │
       │     Frontend shows dashboard   │                                │
       │                                │                                │
```

---

## 🔑 Key Components Explained

### 1. **Frontend Trigger** (`App.tsx`)

```typescript
const loginWithGoogle = () => {
    window.location.href = `${API_BASE}/oauth2/authorization/google`;
};
```

**What happens:**
- User clicks "Sign In with Google" button
- Browser navigates to `http://localhost:8080/oauth2/authorization/google`
- This is a Spring Security endpoint that automatically handles OAuth2 flow

---

### 2. **Spring Security Configuration** (`SecurityConfig.java`)

#### OAuth2 Login Setup
```java
.oauth2Login(oauth2 -> oauth2
    .successHandler(new SimpleUrlAuthenticationSuccessHandler("http://localhost:5173/"))
)
```

**What it does:**
- Enables OAuth2 login functionality
- After successful authentication, redirects user to `http://localhost:5173/`
- Sets `JSESSIONID` cookie for session management

#### Authorization Rules
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/h2-console/**").permitAll()           // Public: Database console
    .requestMatchers("/api/auth/status").permitAll()         // Public: Auth check
    .requestMatchers("/api/stocks/price/**").permitAll()     // Public: Stock prices
    .anyRequest().authenticated()                            // Protected: Everything else
)
```

**What it means:**
- Some endpoints are public (no login needed)
- All other endpoints require authentication
- Spring Security automatically validates session cookie

#### CORS Configuration
```java
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```

```java
CorsConfiguration configuration = new CorsConfiguration();
configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
configuration.setAllowCredentials(true);  // ← Critical for cookies!
```

**Why it matters:**
- Allows frontend (port 5173) to call backend (port 8080)
- `setAllowCredentials(true)` allows cookies to be sent cross-origin
- Without this, `JSESSIONID` cookie wouldn't work

---

### 3. **Google OAuth2 Configuration** (`application.properties`)

```properties
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.google.scope=profile,email
```

**What this does:**
- `client-id`: Your app's identifier with Google
- `client-secret`: Your app's secret key (like a password)
- `scope`: What data you request from Google (user's profile and email)

**Google Console Setup Required:**
- Authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`
- This is where Google sends the authorization code after user approves

---

### 4. **User Provisioning** (`UserController.java`)

```java
@GetMapping("/api/auth/status")
public ResponseEntity<Map<String, Object>> getAuthStatus(
        @AuthenticationPrincipal OAuth2User oauth2User) {
    
    if (oauth2User == null) {
        response.put("authenticated", false);
        return ResponseEntity.ok(response);
    }

    String googleId = oauth2User.getAttribute("sub");
    String email = oauth2User.getAttribute("email");

    // On-demand user provisioning
    Optional<User> userOpt = userRepository.findById(googleId);
    User user;
    if (userOpt.isEmpty()) {
        user = User.builder()
                .id(googleId)
                .email(email)
                .cashBalance(new BigDecimal("1000000.00"))
                .build();
        userRepository.save(user);
    } else {
        user = userOpt.get();
    }
    // ...
}
```

**What happens:**
- `@AuthenticationPrincipal OAuth2User` - Spring injects authenticated user
- Extracts Google user ID (`sub` claim) and email from OAuth2User
- Checks if user exists in database
- **If new user**: Creates account with ₹10,00,000 starting balance
- **If existing user**: Loads their data
- Returns user info to frontend

---

### 5. **Session Management**

#### Cookie-Based Sessions
```
JSESSIONID=550E8400-E29B-41D4-A716-446655440000
```

**How it works:**
- Spring Security creates HTTP session after successful login
- Session ID stored in `JSESSIONID` cookie
- Cookie automatically sent with every request (due to `credentials: 'include'`)
- Spring validates session on each request

#### Frontend Requests
```typescript
fetch(`${API_BASE}/api/auth/status`, { credentials: 'include' })
```

**`credentials: 'include'` is critical:**
- Tells browser to send cookies with cross-origin requests
- Without it, `JSESSIONID` wouldn't be sent
- Backend wouldn't know who you are

---

### 6. **Logout Flow** (`SecurityConfig.java`)

```java
.logout(logout -> logout
    .logoutUrl("/api/auth/logout")
    .logoutSuccessHandler((req, res, auth) -> {
        res.setStatus(200);
        res.getWriter().write("{\"status\":\"logged_out\"}");
    })
    .deleteCookies("JSESSIONID")
    .invalidateHttpSession(true)
)
```

**What happens:**
1. Frontend calls `POST /api/auth/logout`
2. Spring Security deletes `JSESSIONID` cookie
3. Invalidates HTTP session on server
4. Returns success JSON response
5. User is logged out

---

## 🔒 Security Features

### 1. **CSRF Protection**
```java
.csrf(csrf -> csrf
    .ignoringRequestMatchers("/h2-console/**", "/api/**")
)
```

**Why disabled for API:**
- CSRF protection is for traditional form-based apps
- REST APIs use `credentials: 'include'` pattern
- Your app validates session on every request

### 2. **HttpOnly Cookies**
- `JSESSIONID` is HttpOnly by default (Spring Boot)
- JavaScript cannot access it (prevents XSS attacks)
- Only browser sends it automatically

### 3. **Secure in Production**
For production, add:
```properties
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.same-site=strict
```

---

## 📋 OAuth2 Data Flow

### Google Returns (id_token claims):
```json
{
  "sub": "219704",                      // Google User ID (unique)
  "email": "user@gmail.com",            // User's email
  "email_verified": true,               // Email verified?
  "name": "John Doe",                   // Full name
  "picture": "https://...",             // Profile picture URL
  "given_name": "John",                 // First name
  "family_name": "Doe",                 // Last name
  "locale": "en"                        // Language
}
```

### Your App Uses:
- `sub` → User ID (primary key in database)
- `email` → User's email address
- Other fields available via `oauth2User.getAttribute("name")`, etc.

---

## 🧪 Testing the Flow

### 1. Check if Logged In
```bash
curl -i http://localhost:8080/api/auth/status \
  --cookie "JSESSIONID=YOUR_SESSION_ID"
```

### 2. Initiate Login
```bash
# Just open in browser:
http://localhost:8080/oauth2/authorization/google
```

### 3. Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  --cookie "JSESSIONID=YOUR_SESSION_ID"
```

---

## 🛠️ Configuration Checklist

### ✅ Backend (.env file)
- [x] `GOOGLE_CLIENT_ID` set
- [x] `GOOGLE_CLIENT_SECRET` set
- [x] Session cookie settings (default is OK for dev)

### ✅ Google Cloud Console
- [x] OAuth2 Client created
- [x] Authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
- [x] Authorized JavaScript origins: `http://localhost:5173`

### ✅ Frontend
- [x] `credentials: 'include'` on all fetch calls
- [x] Correct backend URL: `http://localhost:8080`
- [x] CORS allowed from backend

---

## 🔍 Common Issues & Solutions

### Issue: "Redirect URI mismatch"
**Solution:** Add to Google Console:
```
http://localhost:8080/login/oauth2/code/google
```

### Issue: Cookies not sent
**Solution:** Ensure `credentials: 'include'` in all fetch calls

### Issue: CORS errors
**Solution:** Backend CORS must allow:
- Origin: `http://localhost:5173`
- Credentials: `true`

### Issue: Session expires
**Solution:** Default is 30 minutes. Configure in `application.properties`:
```properties
server.servlet.session.timeout=24h
```

---

## 🎯 Key Takeaways

1. **Session-Based Auth**: Uses HTTP sessions, not JWT tokens
2. **Cookie-Based**: `JSESSIONID` cookie stores session ID
3. **Auto-Provisioning**: Creates user account on first login
4. **Google Manages**: Google handles login UI and password
5. **Stateful**: Server stores session data in memory (or can use Redis)

---

## 📚 Flow Summary

| Step | Who | What | Result |
|------|-----|------|--------|
| 1 | User | Clicks "Sign In" | Redirects to backend |
| 2 | Backend | Redirects to Google | Google login page |
| 3 | User | Enters credentials | Google validates |
| 4 | Google | Returns auth code | Backend receives code |
| 5 | Backend | Exchanges code for token | Gets access token |
| 6 | Backend | Fetches user info | Gets email, name, etc. |
| 7 | Backend | Creates session | Sets JSESSIONID cookie |
| 8 | Backend | Redirects to frontend | User sees dashboard |
| 9 | Frontend | Checks auth status | Gets user data |
| 10 | User | Uses app | Session validated on each request |

---

**Authentication Type:** OAuth2 Authorization Code Flow  
**Session Storage:** Server-side (in-memory by default)  
**Token Type:** Session cookie (JSESSIONID)  
**Provider:** Google OAuth2 (OpenID Connect)

