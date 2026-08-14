This project demonstrates how I used AI as an accelerator while retaining full engineering ownership, architectural judgment, and validation responsibility. Over the course of building the URL Shortener prototype, I applied disciplined prompting, multi-step reasoning, and controlled oversight to ensure that every artifact — code, tests, documentation, and architecture — met production-grade engineering standards.

1. What I Built
Core System
A complete URL Shortener service built using Java + Spring Boot.

REST APIs for:

Shortening URLs

Redirecting short URLs

Retrieving analytics

Persistence layer using Spring Data JPA + Hibernate.

Analytics tracking (click count, timestamps, expiry).

Reliability features (validation, error handling, rate limiting).

Security layer using Spring Security (Basic Auth + BCrypt).

Comprehensive JUnit test suite (controllers + service).

Full engineering documentation and architecture overview.

2. How I Used AI (Copilot/Claude) During Development
AI was used as an accelerator, not an autonomous agent.
I used AI in the following ways:

2.1 Requirement Clarification
I asked AI to help interpret ambiguous requirements such as:

“Add analytics” → clarified into click count + timestamps.

“Add reliability” → clarified into validation + rate limiting.

“Add security” → clarified into Basic Auth + BCrypt + CSRF disabled.

AI helped normalize vague requirements into actionable engineering tasks.

2.2 Architecture & Design Support
I used AI to:

Validate my controller → service → repository architecture.

Confirm redirect behavior (HTTP 301 + Location header).

Reason about analytics update flow.

Compare authentication strategies (Basic Auth vs API keys vs JWT).

Validate trade-offs between prototype-level and production-level security.

I accepted architectural suggestions only when they aligned with best practices.

2.3 Code Generation & Refinement
AI generated:

Initial controller/service skeletons.

Validation logic.

Rate limiting filter.

Exception classes.

DTOs and response models.

**I reviewed, refactored, and corrected every AI-generated block:**

-> Removed unnecessary complexity.

-> Ensured readability and maintainability.

-> Ensured alignment with Spring Boot idioms.

-> Ensured correctness of redirect and analytics logic.

-> AI accelerated implementation, I owned correctness.

2.4 Debugging & Issue Resolution
AI assisted in diagnosing:

BCrypt warnings (“Encoded password does not look like BCrypt”).

Regex pattern NPEs in unit tests.

I validated each fix manually and ensured the root cause was understood.

2.5 JUnit Test Development
AI helped generate:

Controller tests (Redirect, Analytics, Shorten).

Service tests (alias generation, analytics update, expiry logic).

Mocking strategies using Mockito.

Integration-style tests using MockMvc (optional).

I took ownership by:

Correcting failing tests.

Adding missing edge cases.

Injecting private fields using ReflectionTestUtils.

Ensuring tests matched real business logic.

Rejecting AI-generated tests that were incorrect or incomplete.

This section is highlighted in the README as AI-Assisted JUnit Testing.

2.6 Documentation & Engineering Summary
AI assisted in:

Drafting architecture overview.

Writing scenario breakdowns (greenfield, brownfield, ambiguous).

Producing validation and risk analysis.

Structuring the final engineering summary.

I ensured:

All documentation was accurate.

All decisions were defensible.

All trade-offs were clearly explained.

All assumptions were explicitly stated.

3. How I Exercised Engineering Ownership
AI did not make decisions — I did.

3.1 Cross-Questioning AI
Whenever AI suggested something, I validated it by:

Checking Spring Boot documentation.

Verifying correctness against REST standards.

Ensuring alignment with security best practices.

Testing behavior manually.

Rejecting suggestions that were incorrect or over-engineered.

Examples:

AI suggested unnecessary filters — I removed them.

AI suggested regex patterns — I validated them manually.

AI generated incorrect test mocks — I corrected them.

AI suggested alternate authentication — I chose Basic Auth for prototype scope.

3.2 Manual Validation
I manually validated:

Redirect behavior (HTTP 301 + Location header).

Analytics increment logic.

Alias generation using Base62.

Expiry logic.

Error handling and exception mapping.

Security configuration (Basic Auth + BCrypt + CSRF disabled).

AI accelerated execution; I ensured correctness.

3.3 Controlled Oversight
I ensured:

No AI-generated code was accepted blindly.

All high-impact changes were manually reviewed.

All tests were manually validated.

All architectural decisions were justified.

All security decisions were defensible.

This aligns with the assessment requirement:
“Engineer leads execution and approves all outputs; AI assists within tasks.”

4. Summary of AI-Assisted Engineering Execution
This project demonstrates:

Strong architectural reasoning.

Effective use of AI as an accelerator.

Clear task decomposition.

Production-grade code quality.

FEW AI PROMTS I USED TO ENGINEER THIS PROJECT
AI Prompts Demonstrating Deep Architectural Knowledge
1. Architecture & System Design
“Help me design the high-level architecture for a URL shortener service. I need clear separation between controller, service, repository, and analytics components, with stateless REST APIs and a clean domain model.”

“Explain the trade-offs between storing analytics in the same table vs. a separate entity. I want to optimize for write performance and avoid contention during redirects.”

“Validate my architecture: redirect flow must be O(1), analytics updates must be atomic, and rate limiting must not block the main request thread.”

2. Security & Authentication
“I need a minimal but secure authentication layer. Compare Basic Auth, API keys, and JWT for a prototype. Recommend the simplest option that still demonstrates good engineering judgment.”

“Explain how Spring Security intercepts requests before controller execution. I want to ensure my Basic Auth configuration does not interfere with redirect performance.”

“Help me generate a BCrypt hash and validate that Spring Security recognizes it. I’m seeing warnings that the encoded password does not look like BCrypt.”

3. Rate Limiting & Reliability
“Design a lightweight rate limiting filter for my Spring Boot API. It must run before controller logic, use in-memory counters, and avoid blocking threads.”

“Explain where in the filter chain I should apply rate limiting so it does not conflict with Spring Security’s authentication filter.”

“Help me reason about failure scenarios: what happens if the rate limiter is too aggressive, or if analytics writes slow down redirect performance?”

4. Persistence & Data Modeling
“Help me design the JPA entity for shortened URLs. I need fields for original URL, short code, creation timestamp, and click count.”

“Explain how Hibernate handles concurrent updates to the click counter during redirects. Should I use optimistic locking or atomic increments?”

“Validate my repository design: I want a single query to fetch the URL and update analytics without causing N+1 queries.”

5. API Design & REST Principles
“Help me define REST API contracts for shortening URLs, retrieving analytics, and performing redirects. Include status codes, error responses, and validation rules.”

“Explain how to structure my controller so redirect logic does not return JSON but performs an HTTP 302 with a Location header.”

“Validate my error-handling strategy: I want consistent JSON error responses for API calls but proper HTTP redirects for short links.”

Comprehensive testing.

Robust validation and risk control.

Clear documentation.

Explicit engineering ownership.

AI helped me move faster.
Engineering judgment ensured the system was correct, maintainable, and secure
