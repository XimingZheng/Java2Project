# Stack Overflow Java Insight

## Project Description
Analyzes Java-tagged Stack Overflow threads to surface topic trends, co-occurrence patterns, solvable vs. unsolved ratios, and multithreading pain points, then visualizes the findings for faster exploration.

## Features
- Topic trends and activity scores over custom date windows.
- Top co-occurring tags/keywords across Java questions.
- Solvable vs. unsolved distribution with supporting metrics.
- Multithreading recurring issues spotlight.
- Interactive frontend dashboards powered by React + Recharts.

## Technology Stack
- Backend: Spring Boot 3.5 (Java 17), Maven wrapper, Jackson, SLF4J.
- Frontend: React 19, TypeScript, Vite, TailwindCSS, Recharts, Axios.
- Data: Python 3 + requests pulling Stack Exchange API; cached artifacts under `data/`.

## Analysis
Raw Stack Overflow data is fetched via `python/data_collection.py`, enriched into JSON models, and loaded into in-memory services. Analysis services compute time-series trends, co-occurrence rankings, solvable ratios, and multithreading recurrences exposed through REST endpoints consumed by the frontend.

## RESTful API Endpoints
- `GET /api/topics/list` - available topics.
- `GET /api/topics/trend?topics=java,spring&startDate=2022-01-01&endDate=2024-12-31&period=month` - topic frequency trends.
- `GET /api/topics/activity?topics=java,spring&startDate=2022-01-01&endDate=2024-12-31&period=month` - activity scores across time.
- `GET /api/occurrence/top?n=10` - top co-occurring tags/keywords.
- `GET /api/multithreading/top?n=5` - most frequent multithreading issues.
- `GET /api/solvable` - solvable vs. unsolved analysis summary.

## Frontend and Visualization
The `frontend/` app (Vite) consumes the above APIs, rendering line charts, radar charts, and tables. Static assets live in `frontend/public`, source in `frontend/src`. Use `npm run dev -- --host` for local previews; `npm run build` for production bundles.

## Installation Instructions
1) Prereqs: Java 17+, Node 18+, Python 3 if regenerating data.
2) Backend: `./mvnw package` (or `./mvnw spring-boot:run` for dev). Default port 8080; configure in `src/main/resources/application.properties`.
3) Frontend: `cd frontend && npm install`; run `npm run dev -- --host` (expects backend on http://localhost:8080) or `npm run build` then `npm run preview`.
4) Data refresh (optional): `python python/data_collection.py` with a Stack Exchange API key in `python/API_KEY.py`; outputs go to `data/`.
