# PostPrep Backend

Backend service for **PostPrep**, a document analysis tool. It takes a **PDF or raw text**, runs OCR when needed, asks an **LLM** to produce structured metadata (title, summary, keywords, categories, SEO title, language), then computes a **semantic confidence score** that measures how faithful that metadata is to the source.

**Live demo:** https://aminesidki-postprep.hf.space

## How it works

```
 upload (PDF / text)
        │
        ▼
 Article created ──► status = PROCESSING ──► response returned immediately
        │
        ▼   (@Async, background thread)
 PDF? ──► OCR (2 passes) ──► raw text
        │
        ▼
 LLM analysis (Qwen2.5-7B-Instruct via Hugging Face router)
        │
        ▼
 Confidence scoring (local MiniLM embeddings + cosine similarity)
        │
        ▼
 Article saved ──► status = PROCESSED   (or INTERRUPTED on any failure)
```

Uploads never block on the AI pipeline. The endpoint returns the new article with `PROCESSING` status right away, and the client polls `GET /api/v1/article/{id}` (or `/myArticles`) until it becomes `PROCESSED` or `INTERRUPTED`.

### 1. OCR (scanned PDFs)

`OcrService` renders every page at 300 DPI with PDFBox and runs Tesseract (Tess4j) in **two passes**:

1. **Pass 1** uses `eng+ara` to get a first rough text covering Latin and Arabic scripts.
2. The language of that text is detected with `optimaize language-detector`.
3. **Pass 2** re-runs OCR with the detected language's Tesseract model (falls back to `eng`), which gives noticeably cleaner output.

### 2. LLM analysis

The text is wrapped in `<DATA_SOURCE>` tags (any closing tag in the input is stripped) and sent to the Hugging Face router's OpenAI-compatible `/chat/completions` endpoint with a system prompt that:

- demands a JSON-only answer,
- caps the summary (~200 words) and keywords (max 6),
- tells the model to ignore any instruction found inside the source text (basic prompt-injection guard).

The reply is parsed defensively: fenced ```` ```json ```` blocks are extracted first, then the outermost `{ ... }`, and unknown fields are ignored. Settings: `max_tokens=1500`, `temperature=0.1`.

Output stored per article:

| Field | Description |
| --- | --- |
| `title` | Generated title |
| `language` | Detected language |
| `summary` | Abstract of the content |
| `keywords` | Up to 6 tags |
| `categories` | Topical categories |
| `seoTitle` | SEO-oriented title |
| `confidenceScore` | Semantic fidelity score (see below) |

### 3. Semantic verification (confidence score)

To catch hallucinated or off-topic output, the backend compares the generated metadata against the original text:

1. Embed `title + summary + keywords + categories` as a single vector.
2. Split the source into **800-character chunks with 100-character overlap** (first 50 chunks max) and embed each one.
3. Compute the cosine similarity between the metadata vector and every chunk.
4. The `confidenceScore` is the **mean of the top 3 similarities**.

Embeddings are computed **locally** with `all-MiniLM-L6-v2` (ONNX, via Spring AI Transformers). The model files are downloaded from Hugging Face at first startup.

## API

All routes are under `/api/v1`. Authentication is done with **HttpOnly cookies** (no `Authorization` header needed).

### Auth: `/api/v1/auth`

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| POST | `/register` | public | Create an account (`username`, `email`, `password`). New users get role `USER`. |
| POST | `/login` | public | Log in with `email` + `password`. Sets `access_token` and `refresh_token` cookies. |
| POST | `/refresh` | refresh cookie | Issue a new token pair from the `refresh_token` cookie. |
| POST | `/logout` | authenticated | Invalidates the stored refresh token and clears both cookies. |

### Articles: `/api/v1/article`

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/myArticles` | Lightweight list (`id`, `title`, `owner`, `status`) of your own articles |
| GET | `/{id}` | Full article, including `outputJson` and `status` |
| GET | `/all` | All articles (admin only) |
| POST | `/upload/pdf` | Multipart PDF upload. Returns the article in `PROCESSING` state. |
| POST | `/upload/text` | Raw text body. Returns the article in `PROCESSING` state. |
| DELETE | `/delete/{postId}` | Delete one of your articles (403 if you are not the owner) |

### Admin: `/api/v1/admin`

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/users` | List users |
| GET | `/users/Details/{id}` | One user |
| PUT | `/users/{id}` | Update username / email |
| DELETE | `/users/{id}` | Delete a user |
| GET | `/articles` | List all articles |
| DELETE | `/articles/{id}` | Delete any article |
| GET | `/dashboard` | Global counts (`articles`, `users`) |
| GET | `/dashboard/stats/daily` | Articles per day, last 30 days |
| GET | `/dashboard/stats/monthly` | Articles per month, last 12 months |

### Article status

| Status | Meaning |
| --- | --- |
| `PROCESSING` | Accepted, AI pipeline running |
| `PROCESSED` | Done, `outputJson` populated |
| `INTERRUPTED` | OCR or AI step failed; title/output are empty |

## Authentication & security

- **Stateless JWT**, signed with **RS256** (RSA keypair supplied through environment variables).
- **Access token**: 1 hour, cookie path `/`. **Refresh token**: 7 days, cookie path `/api/v1/auth/refresh`.
- The refresh token is stored on the user row; it is replaced on every refresh/login and invalidated on logout.
- Cookies are `HttpOnly` and `SameSite=Strict`.
- Passwords are hashed with a Spring `PasswordEncoder`.
- A custom `JwtCookieFilter` reads the `access_token` cookie and builds the security context.

## Tech stack

- **Java 17**, **Spring Boot 3.4.2** (Web, Data JPA, Validation, Security, OAuth2 Resource Server)
- **PostgreSQL** (Hibernate `ddl-auto: update`)
- **Spring AI 1.0.0-M6**: Transformers starter for local ONNX embeddings
- **Hugging Face router** for chat completions (`Qwen/Qwen2.5-7B-Instruct`)
- **Tess4j 5.9.0** (Tesseract) + **Apache PDFBox 2.0.29** for OCR
- **optimaize language-detector** for language identification
- **Lombok**, records as DTOs, mapper classes for entity ↔ DTO conversion
- **Docker**, deployed on **Hugging Face Spaces**

## Project layout

```
src/main/java/org/aminesidki/postprep
├── config/        Security, JWT, CORS, AI client, OCR, language detector
├── controller/    Auth, Article, and admin/ controllers
├── dto/           regular/, lite/, request/ (records + request classes)
├── entity/        AppUser, Article, OutputJson
├── enumeration/   Role (USER, ADMIN), Status (PROCESSING, PROCESSED, INTERRUPTED)
├── filter/        JwtCookieFilter
├── mapper/        Entity ↔ DTO mappers
├── repository/    Spring Data repositories
├── security/      CustomUserDetails(+Service)
└── service/       Article, AppUser, OCR, TextProcessing, jwt/ (TokenService)
```

## Running locally

### Prerequisites

- JDK 17 and Maven (or the included `./mvnw`)
- PostgreSQL
- Tesseract with the language packs you need (`eng` and `ara` are required for pass 1)
- A Hugging Face API token

### 1. Generate the JWT keypair

```bash
./RSA_keygen.sh
```

This creates `certs/private.pem` and `certs/public.pem`.

### 2. Set environment variables

| Variable | Description |
| --- | --- |
| `DB_URL` | JDBC URL, e.g. `jdbc:postgresql://localhost:5432/postprep` |
| `DB_USR` / `DB_PWD` | Database credentials |
| `HUGGINGFACE_API_KEY` | Hugging Face token used for chat completions |
| `PUBLIC_KEY` / `PRIVATE_KEY` | Locations of the RSA keys (Spring resolves them into `RSAPublicKey` / `RSAPrivateKey`), e.g. `file:./certs/public.pem` |
| `TESSERACT_DIR` | Path to Tesseract's `tessdata` directory |

> The private key used for signing must be in **PKCS#8** PEM format for Spring to load it. If the app refuses to start, convert it with `openssl pkcs8 -topk8 -nocrypt -in certs/private.pem -out certs/private_pkcs8.pem`.

### 3. Run

```bash
./mvnw spring-boot:run
```

The API listens on port `8080`.

### Docker

```bash
docker build -t postprep .
docker run -p 8080:8080 --env-file .env postprep
```

The image is based on `maven:3.9.12-eclipse-temurin-17-noble`, installs `tesseract-ocr` plus all language packs, builds the jar, and runs `PostPrep-0.0.1.jar`.

## Deployment

Every push to `main` triggers a GitHub Actions workflow (`sync_to_hf.yml`) that swaps `README.md` with `hf_README.md` (the Hugging Face Space metadata) and force-pushes to the `AmineSidki/PostPrep` Space, which builds the Docker image and serves it on port `8080`. Requires the `HF_TOKEN` repository secret.
