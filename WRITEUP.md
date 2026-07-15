# Write-up: URL Shortener

## 1. What I asked the AI to do, and what I decided myself

I used the AI as a design sparring partner and a code‑generation assistant.  
It handled the heavy lifting on boilerplate and tests, while I focused on the architectural decisions and quality control.

**AI‑generated:**
- All unit tests (`CodeGeneratorTest`, `UrlValidatorTest`, `ShortenServiceTest`, `RedirectServiceTest`, `AnalyticsServiceTest`) – the AI wrote the test cases and mocking logic, which I then ran and verified.
- Project scaffolding (package structure, `pom.xml` dependencies, entity annotations, DTO records, exception classes, and the HTML client skeleton).
- The initial controller, service, and repository boilerplate, which I later refined.

**My decisions:**
- **Language & framework** – Java with Spring Boot, because it’s the stack I know best.
- **Short code generation** – Random Base62 strings (8 characters) rather than hashing. The AI initially suggested hashing, but after a discussion about idempotency (always check the DB for an existing URL), I realised that a deterministic hash adds collision risk without real benefit. The random approach is simpler and astronomically collision‑safe (218 trillion combinations).
- **Collision retry logic** – I designed the loop inside `ShortenService`: generate a code, check `existsById`, retry up to 10 times.
- **Service layer split** – The AI originally proposed a single `UrlService`. I deliberately split it into `ShortenService`, `RedirectService`, and `AnalyticsService` for clarity and to follow the Single Responsibility Principle.
- **Data store** – I chose H2 (in‑memory) for zero‑setup development, with Spring Data JPA as the persistence layer. The AI’s first suggestion was SQLite with raw JDBC; I pushed back because JPA reduces boilerplate and swapping to Postgres later is a one‑line config change.
- **CodeGenerator as a static utility** – The AI wanted to inject it as a Spring bean; I kept it static because it has no dependencies and making it a bean would only complicate testing.

## 2. Where I overrode, corrected, or threw away the AI’s output

- **Spring Boot version** – The AI’s knowledge was based on an older version. I used Spring Boot 4.1.0, the latest stable release in my environment, and adjusted dependencies accordingly.
- **Raw JDBC / `DatabaseManager`** – The AI proposed a manual `DatabaseManager` class with `Connection` and `PreparedStatement`. I discarded this entirely and replaced it with Spring Data JPA + H2. This removed ~50 lines of boilerplate and made the data layer testable with real repositories instead of mocks.
- **Controller regex fix** – The AI’s original controller used `/{code}` without a pattern constraint. This caused static resources (`index.html`, etc.) to be caught by the `redirect` handler. I fixed it by adding a regex (`{code:[a-zA-Z0-9]{4,20}}`), leaving other paths free for Spring’s static resource handler.
- **Import / package inconsistencies** – Several times the AI mixed up package names (e.g., `com.shortener` vs `com.shortener.url_shortener`). I corrected these manually to keep the project compilable.
- **DTO design** – The AI initially wanted to pass raw strings; I introduced `CreateShortUrlRequest` and `CreateShortUrlResponse` records to make the API contract explicit and self‑documenting.
- **HTML client** – The AI provided a basic HTML page; I refined it with proper error handling and a clean design.

## 3. The two or three biggest trade-offs I made

**1. Random code generation vs. hashing**
- *Chosen:* Random 8‑char Base62 strings, with idempotency enforced by a database lookup on `longUrl`.
- *Alternative considered:* Truncated SHA‑256/MD5 hash of the URL.
- *Why I chose random:* The database lookup is already required for idempotency (return existing code for a duplicate URL). Once that lookup exists, the code doesn’t need to be derived from the URL. Random generation avoids hash truncation collisions entirely and is trivial to implement. The probability of collision with 8 characters is negligible even after millions of URLs.
- *Trade‑off:* The code is meaningless – you can’t infer anything about the URL from it. That’s acceptable for a generic shortener.

**2. In‑memory H2 vs. persistent database (Postgres)**
- *Chosen:* H2 in‑memory for development and testing.
- *Alternative:* Postgres with Testcontainers for integration tests.
- *Why I chose H2:* It makes the project instantly runnable with zero external dependencies. The schema is created automatically via `ddl‑auto=update`. Switching to Postgres requires only a driver and URL change; the JPA entity and repository stay identical. This keeps the submission simple while demonstrating that I can work with a real database.
- *Trade‑off:* Data is lost on restart. For a production system I’d use H2 file‑based (`jdbc:h2:file:...`) or Postgres.

**3. Three separate services vs. a single `UrlService`**
- *Chosen:* `ShortenService`, `RedirectService`, and `AnalyticsService`.
- *Alternative:* A single class with methods `shorten()`, `redirect()`, `getStats()`.
- *Why I split them:* Each service has one reason to change and can be tested in isolation. This design shows an understanding of the Single Responsibility Principle and makes the code easier to discuss in a live review.
- *Trade‑off:* Three extra files. The overhead is minimal and the clarity is worth it.

## 4. What’s missing, or what I’d do with another day

- **Persistent database** – Switch to H2 file‑based or Postgres so that data survives restarts. Add Testcontainers for integration tests.
- **Authentication / user namespace** – Custom aliases are currently global; a user system would prevent alias squatting and allow per‑user analytics.
- **Rate limiting** – Protect the `/shorten` endpoint from abuse.
- **Richer analytics** – Track referrer, user‑agent, IP, and geographic location. This would likely require a separate click‑events table.
- **Better frontend** – A JavaScript framework (React/Vue) with client‑side validation and a nicer UX, plus the ability to see stats inline.
- **CI/CD pipeline** – GitHub Actions to run tests and build on push.
- **Swagger/OpenAPI** – Auto‑generated API documentation for discoverability.

All of these are natural extensions that don’t change the core architecture.