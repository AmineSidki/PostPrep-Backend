# PostPrep Backend

The **PostPrep Backend** is the backend service for **PostPrep**, an intelligent document analysis and synthesis tool.

It accepts **PDFs or raw text**, processes the content (including OCR for scanned documents), and leverages **Large Language Models (LLMs)** to generate structured metadata. It also performs **semantic verification** to ensure the generated content remains faithful to the original source. 
Google Gemini

## 🔗 Live Demo

[https://aminesidki.postprep.hf.space](https://aminesidki.postprep.hf.space/)

## ✨ Core Features

### 1. Document Ingestion

### PDF Support

- Uses **Tesseract OCR** to extract text from scanned PDF documents.

### Raw Text

- Direct processing and sanitization of plain text inputs.

### 2. AI Synthesis

The system leverages an **LLM** to analyze the input text and generate a structured JSON object containing:

- **Title & Summary** – Abstract of the content
- **Keywords & Categories** – Relevant tags for indexing
- **Language** – Automatic language detection

### 3. Semantic Verification

> Note
> 
> 
> To ensure quality, the backend calculates a **Semantic Similarity score**.
> 
> It uses **embedding models** and cosine similarity to compare the generated summary with the original text, verifying that the AI has not *hallucinated* unrelated content.
> 

## 🔐 API Reference

### Authentication

**Base path:** `/api/v1/auth`

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/login` | Authenticate and retrieve an access token |
| POST | `/register` | Create a new user account |

### Article Management

**Base path:** `/api/v1/article`

### Fetching Content

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/all` | Retrieve all public articles |
| GET | `/myArticles` | Retrieve your personal library |
| GET | `/{id}` | Fetch a specific article |

### Processing

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/upload/text` | Upload raw text (JSON). System cleans, analyzes, and saves |
| POST | `/upload/pdf` | Upload a PDF (Multipart). System OCRs, analyzes, and saves |

### Cleanup

| Method | Endpoint | Description |
| --- | --- | --- |
| DELETE | `/delete/{postId}` | Remove an article from your library |

## 🧱 Tech Stack

- **Framework:** Java 17, Spring Boot
- **OCR:** Tesseract
- **AI Integration:** Hugging Face Inference API (LLM + Embeddings)
- **Deployment:** Docker (Hosted on Hugging Face Spaces)
