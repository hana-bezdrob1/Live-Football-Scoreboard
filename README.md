# Live Football World Cup Scoreboard Library

A simple, immutable, Java 21 library for managing Live Football World Cup matches.  
Provides match lifecycle operations (start, update, finish) and a summary view ordered by total score and most‑recent start time.

---

## Features

- **Start a match** with home/away teams, initial score 0–0  
- **Update match score** (immutable records, returns new snapshot)  
- **Finish a match** 
- **Get matches in progress** (unmodifiable list)  
- **Get summary** ordered by:  
  1. Total goals (descending)  
  2. Start time (most recent first)  
- **Built‑in validation**:  
  - Non‑null, non‑blank, distinct team names  
  - One active match per team  
  - Scores non‑decreasing, non‑negative  
  - Maximum score per team (`MAX_SCORE`, default 30)  
  - Maximum increase per update (`MAX_DELTA`, default 5)  

---

## Assumptions

- **Single-threaded**, no concurrent access protection
- **System clock** used for start time
- **Lightweight** made with real-world expectations regarding the number of concurrent mathes and update rates
