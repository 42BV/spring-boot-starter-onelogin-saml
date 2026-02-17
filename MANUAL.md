# Spring Boot Starter OneLogin SAML - Manual

A Spring Boot starter that integrates the [OneLogin java-saml](https://github.com/onelogin/java-saml) library with Spring Security, providing SAML 2.0 SSO and SLO support.

**Coordinates:** `nl.42:spring-boot-starter-onelogin-saml`
**Requires:** Java 25+, Spring Boot 4.x

## Table of Contents

- [Getting Started](#getting-started)
- [Configuration Reference](#configuration-reference)
- [Spring Security Integration](#spring-security-integration)
- [Endpoints](#endpoints)
- [Authentication Flows](#authentication-flows)
- [Attribute Mapping](#attribute-mapping)
- [Role Mapping](#role-mapping)
- [Extension Points](#extension-points)
- [Accessing User Information](#accessing-user-information)
- [Multiple IdP Registrations](#multiple-idp-registrations)
- [Session Management](#session-management)
- [SP Metadata](#sp-metadata)
- [Troubleshooting](#troubleshooting)

---

## Getting Started

### 1. Add the dependency

```xml
<dependency>
    <groupId>nl.42</groupId>
    <artifactId>spring-boot-starter-onelogin-saml</artifactId>
    <version>2.0.0</version>
</dependency>
```

### 2. Configure your IdP registration

Add the following to your `application.yml`:

```yaml
onelogin:
  saml:
    enabled: true
    base_url: https://myapp.example.com
    success_url: /
    failure_url: /error
    registrations:
      my-idp:
        label: My Identity Provider
        identity_provider_id: https://idp.example.com/saml2
        sign_on_url: https://idp.example.com/saml2/sso
        certificate: |
          -----BEGIN CERTIFICATE-----
          MIIDXTCCAkWgAwIBAgIJ...
          -----END CERTIFICATE-----
```

### 3. Wire the SAML filter into Spring Security

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private Saml2Filter saml2Filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(saml2Filter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/saml2/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/saml2/**")
            );

        return http.build();
    }
}
```

That's it. Your application now supports SAML 2.0 SSO.

---

## Configuration Reference

All properties use the prefix `onelogin.saml`.

### Global Properties

| Property | Type | Default | Description |
|---|---|---|---|
| `enabled` | `boolean` | `false` | Master switch to activate SAML support |
| `base_url` | `String` | - | Your application's public base URL (e.g. `https://myapp.example.com`) |
| `default_login_url` | `String` | - | Default login URL returned by the `/saml2/config` endpoint |
| `default_login_skip_redirect` | `boolean` | `false` | If `true`, resolves the final IdP URL by following redirects |
| `success_url` | `String` | `/` | Redirect target after successful login |
| `failure_url` | `String` | `/error` | Redirect target after failed login |
| `session_timeout` | `int` | `21600` | Session timeout in seconds (default: 6 hours) |

### Registration Properties

Each IdP registration lives under `onelogin.saml.registrations.<id>`.

| Property | Type | Default | Description |
|---|---|---|---|
| `enabled` | `boolean` | `true` | Whether this registration is shown in `/saml2/config` |
| `label` | `String` | - | Display name for the IdP |
| `service_provider_id` | `String` | auto | SP entity ID (defaults to `{base_url}/saml2/login/{id}`) |
| `service_provider_certificate` | `String` | - | SP signing certificate (PEM) |
| `identity_provider_id` | `String` | - | IdP entity ID |
| `metadata_url` | `String` | - | IdP metadata URL |
| `sign_on_url` | `String` | - | IdP SSO endpoint |
| `sign_on_binding` | `String` | HTTP-POST | SSO binding type |
| `logout_url` | `String` | - | IdP SLO endpoint |
| `logout_binding` | `String` | HTTP-Redirect | SLO binding type |
| `certificate` | `String` | - | IdP signing certificate (PEM) |
| `signature_algorithm` | `String` | RSA-SHA1 | XML signature algorithm |
| `force_auth_n` | `boolean` | `false` | Force re-authentication at IdP |
| `validate` | `boolean` | `true` | Validate full SAML response (set `false` for SP-only validation) |
| `debug` | `boolean` | `false` | Log all SAML attributes after login |
| `attributes` | `Map` | - | Attribute name translations (see [Attribute Mapping](#attribute-mapping)) |
| `roles` | `Map` | - | Role translations (see [Role Mapping](#role-mapping)) |
| `properties` | `Properties` | - | Additional OneLogin SDK properties |

### OneLogin SDK Properties

You can pass additional properties directly to the OneLogin SDK at both the global and per-registration level. These are prefixed with `onelogin.saml2.` internally.

```yaml
onelogin:
  saml:
    properties:
      organization:
        name: My Organization
        displayname: My Organization Display Name
        url: https://myorg.example.com
      contacts:
        technical:
          given_name: John Doe
          email_address: john@example.com
    registrations:
      my-idp:
        properties:
          security:
            want_name_id: true
```

---

## Spring Security Integration

The library provides a `Saml2Filter` bean that handles all SAML endpoints. You must add it to your Spring Security filter chain and permit access to the `/saml2/**` URLs.

### Minimal configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private Saml2Filter saml2Filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(saml2Filter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/saml2/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/saml2/**")
            );

        return http.build();
    }
}
```

### With custom SecurityContextRepository

By default, the library uses `RequestAttributeSecurityContextRepository`. To use a different one (e.g. `HttpSessionSecurityContextRepository`), define it as a bean:

```java
@Bean
public SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
}
```

The library will pick it up automatically.

### With RememberMeServices

If your application defines a `RememberMeServices` bean, it will be called on successful SAML login:

```java
@Bean
public RememberMeServices rememberMeServices() {
    return new TokenBasedRememberMeServices("key", userDetailsService);
}
```

### With custom AuthenticationFailureHandler

To run additional logic on authentication failure before the redirect, provide an `AuthenticationFailureHandler` bean:

```java
@Bean
public AuthenticationFailureHandler authenticationFailureHandler() {
    return (request, response, exception) -> {
        // Custom failure logic (e.g. logging, audit)
        log.warn("SAML authentication failed: {}", exception.getMessage());
    };
}
```

The library will call your handler, then redirect to `failure_url`.

---

## Endpoints

The library registers the following endpoints:

| Endpoint | Method | Description |
|---|---|---|
| `/saml2/config` | GET | Returns JSON listing all enabled registrations and the login URL |
| `/saml2/login/{id}` | GET | Initiates SSO with the specified IdP registration |
| `/saml2/SSO/{id}` | POST | Assertion Consumer Service (ACS) - processes the IdP response |
| `/saml2/logout/{id}` | GET | Initiates logout / SLO |
| `/saml2/SingleLogout/{id}` | POST/GET | Processes SLO callback from the IdP |
| `/saml2/metadata/{id}` | GET | Downloads SP metadata XML for the registration |

### Config endpoint response

`GET /saml2/config` returns:

```json
{
  "registrations": [
    {
      "id": "my-idp",
      "label": "My Identity Provider",
      "enabled": true,
      "logout": true
    }
  ],
  "loginUrl": "https://myapp.example.com/saml2/login/my-idp"
}
```

The `enabled` field indicates whether `sign_on_url` is configured. The `logout` field indicates whether `logout_url` is configured.

Use this endpoint to build a dynamic login page that lists all available identity providers.

---

## Authentication Flows

### SSO (Single Sign-On)

```
1. Browser  -->  GET /saml2/login/my-idp      (your app)
2. App      -->  Redirect to IdP with AuthnRequest
3. User authenticates at IdP
4. IdP      -->  POST /saml2/SSO/my-idp        (your app)
5. App validates assertion, creates session
6. App      -->  303 redirect to success_url
```

To initiate login, redirect the user to `/saml2/login/{registrationId}`.

You can pass a custom success URL as a query parameter:

```
/saml2/login/my-idp?successUrl=/dashboard
```

### SLO (Single Logout) - HTTP-Redirect binding (default)

```
1. Browser  -->  GET /saml2/logout/my-idp      (your app)
2. App clears SecurityContext, notifies listeners
3. App      -->  303 redirect to IdP logout URL
```

### SLO - HTTP-POST binding

```
1. Browser  -->  GET /saml2/logout/my-idp      (your app)
2. App      -->  LogoutRequest form auto-submits to IdP
3. IdP processes logout
4. IdP      -->  POST /saml2/SingleLogout/my-idp
5. App clears SecurityContext, notifies listeners
6. App      -->  303 redirect to success_url
```

---

## Attribute Mapping

The library uses logical attribute names internally: `username` and `role`. Since different IdPs send attributes under different names, the `attributes` map translates between the logical names and your IdP's actual attribute names.

### Example: Azure AD

```yaml
onelogin:
  saml:
    registrations:
      azure:
        attributes:
          username: http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress
          role: http://schemas.microsoft.com/ws/2008/06/identity/claims/role
          # You can add custom attributes too:
          first_name: http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname
          last_name: http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname
```

### Example: Okta

```yaml
onelogin:
  saml:
    registrations:
      okta:
        attributes:
          username: email
          role: groups
```

### How it works

1. `Saml2Response.getValue("username")` looks up `"username"` in the `attributes` map
2. If found, uses the mapped value as the actual SAML attribute name to read
3. If not found, uses `"username"` directly as the SAML attribute name

This means if your IdP already sends attributes named `username` and `role`, you don't need an `attributes` map at all.

### Determining the username

The library determines the authenticated username in this order:

1. The value of the `username` attribute (mapped or direct)
2. The SAML NameID
3. Throws `Saml2Exception` if both are blank

---

## Role Mapping

The `roles` map translates SAML role values to Spring Security authority strings. Only mapped roles are granted - unmapped roles are silently ignored.

### Example

```yaml
onelogin:
  saml:
    registrations:
      my-idp:
        attributes:
          role: http://schemas.microsoft.com/ws/2008/06/identity/claims/role
        roles:
          Admin: ROLE_ADMIN
          Editor: ROLE_EDITOR
          Viewer: ROLE_VIEWER
```

If the IdP sends `Admin` and `Manager` as role values:
- `Admin` maps to `ROLE_ADMIN` (granted)
- `Manager` has no mapping (ignored)

### Using roles in Spring Security

Once mapped, you can use standard Spring Security authorization:

```java
@PreAuthorize("hasRole('ADMIN')")
public void adminOnly() { ... }

@PreAuthorize("hasAuthority('ROLE_EDITOR')")
public void editorsOnly() { ... }
```

---

## Extension Points

### Saml2UserDecorator

Enrich the `UserDetails` object after SAML authentication. Implement this interface and register it as a Spring bean.

```java
@Component
public class CustomUserDecorator implements Saml2UserDecorator {

    @Override
    public UserDetails decorate(UserDetails details, Saml2Response response) {
        // Read additional attributes from the SAML response
        String email = response.getValue("email").orElse("");
        String firstName = response.getValue("first_name").orElse("");
        String lastName = response.getValue("last_name").orElse("");

        // Return an enriched user object
        return new CustomUser(
            details.getUsername(),
            details.getAuthorities(),
            email,
            firstName,
            lastName
        );
    }
}
```

Multiple decorators can coexist - they are applied in sequence.

### Saml2LogoutListener

React to logout events. Implement this interface and register it as a Spring bean.

```java
@Component
public class LogoutAuditListener implements Saml2LogoutListener {

    @Override
    public void onLogoutSuccess(Registration registration) {
        log.info("User logged out from IdP: {}", registration.getId());
        // e.g., audit logging, cache invalidation, etc.
    }
}
```

---

## Accessing User Information

After a successful SAML login, the `SecurityContext` holds a `Saml2Authentication`.

### In a controller

```java
@GetMapping("/profile")
public ResponseEntity<Map<String, Object>> profile(Authentication authentication) {
    Saml2Authentication samlAuth = (Saml2Authentication) authentication;
    Saml2Response response = samlAuth.getResponse();

    Map<String, Object> profile = new HashMap<>();
    profile.put("username", authentication.getName());
    profile.put("registrationId", response.getRegistrationId());
    profile.put("authorities", authentication.getAuthorities());
    profile.put("logoutUrl", samlAuth.getLogoutUrl());
    profile.put("sessionExpires", samlAuth.getExpiration());

    // Access any SAML attribute (using the logical name from your attributes map)
    response.getValue("email").ifPresent(email -> profile.put("email", email));
    response.getValue("first_name").ifPresent(name -> profile.put("firstName", name));

    return ResponseEntity.ok(profile);
}
```

### Getting all raw attributes

```java
Saml2Response response = samlAuth.getResponse();

// List all attribute names sent by the IdP
Collection<String> attributeNames = response.getAttributes();

// Get all values for a specific attribute
Set<String> groups = response.getValues("groups");
```

### Building a logout link

```java
// The logout URL is available on the authentication object
String logoutUrl = samlAuth.getLogoutUrl();
// e.g., "https://myapp.example.com/saml2/logout/my-idp"
```

---

## Multiple IdP Registrations

You can configure multiple identity providers. Each gets its own set of endpoints.

```yaml
onelogin:
  saml:
    enabled: true
    base_url: https://myapp.example.com
    registrations:
      azure:
        label: Azure AD
        identity_provider_id: https://login.microsoftonline.com/tenant-id/saml2
        sign_on_url: https://login.microsoftonline.com/tenant-id/saml2
        logout_url: https://login.microsoftonline.com/tenant-id/saml2/logout
        certificate: |
          -----BEGIN CERTIFICATE-----
          ...
          -----END CERTIFICATE-----
        attributes:
          username: http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress
          role: http://schemas.microsoft.com/ws/2008/06/identity/claims/role
        roles:
          Admin: ROLE_ADMIN
          User: ROLE_USER

      okta:
        label: Okta
        identity_provider_id: https://myorg.okta.com/saml
        sign_on_url: https://myorg.okta.com/app/myapp/sso/saml
        certificate: |
          -----BEGIN CERTIFICATE-----
          ...
          -----END CERTIFICATE-----
        attributes:
          username: email
          role: groups
        roles:
          admins: ROLE_ADMIN
          everyone: ROLE_USER
```

### Building a login page

Use the `/saml2/config` endpoint to dynamically list available IdPs:

```javascript
// Fetch available identity providers
const response = await fetch('/saml2/config');
const config = await response.json();

// config.registrations = [
//   { id: "azure", label: "Azure AD", enabled: true, logout: true },
//   { id: "okta",  label: "Okta",     enabled: true, logout: false }
// ]

// If only one registration, redirect directly
if (config.registrations.length === 1) {
    window.location.href = config.loginUrl;
} else {
    // Show a selection page
    config.registrations.forEach(reg => {
        // Create a button for each IdP
        // Link: /saml2/login/{reg.id}
    });
}
```

---

## Session Management

### Session timeout

The library sets the HTTP session timeout based on the SAML assertion's `SessionNotOnOrAfter` attribute. If the assertion does not include this attribute, the configured `session_timeout` (default: 6 hours) is used as a fallback.

### Checking session expiration

```java
Saml2Authentication auth = (Saml2Authentication) SecurityContextHolder
    .getContext().getAuthentication();

LocalDateTime expiration = auth.getExpiration();
if (expiration != null && expiration.isBefore(LocalDateTime.now())) {
    // Session has expired according to the SAML assertion
}
```

---

## SP Metadata

To configure your IdP, it typically needs your SP metadata. Download it from:

```
GET /saml2/metadata/{registrationId}
```

For example: `https://myapp.example.com/saml2/metadata/my-idp`

This returns an XML file containing:
- SP entity ID
- Assertion Consumer Service (ACS) URL and binding
- Single Logout Service URL and binding
- SP signing certificate (if configured)

Share this URL or the downloaded XML with your IdP administrator.

---

## Troubleshooting

### Enable debug logging

Set `debug: true` on a registration to log all received SAML attributes after login:

```yaml
onelogin:
  saml:
    registrations:
      my-idp:
        debug: true
```

### Disable response validation

For development or debugging, you can disable full SAML response validation. This enables SP-only validation (skips IdP signature checks):

```yaml
onelogin:
  saml:
    registrations:
      my-idp:
        validate: false
```

**Do not use this in production.**
