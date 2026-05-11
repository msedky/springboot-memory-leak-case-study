# 🧠 Spring Boot JVM Memory Leak Case Studies

A hands-on collection of **3 real-world JVM memory leak scenarios** in Spring Boot — each reproduced, diagnosed with Heap Dump analysis using Eclipse MAT, and fixed.

Built to demonstrate production-level debugging skills and senior engineering thinking around JVM internals, memory management, and system reliability.

---

## 📌 What Is This?

Each case simulates a realistic production bug that causes `java.lang.OutOfMemoryError: Java heap space`.

The approach for every case is the same:

```text
Reproduce → Load Test → Heap Dump → Eclipse MAT Analysis → Fix → Validate
```

---

## 📂 Cases

| # | Case | Root Cause | Fix |
|---|------|-----------|-----|
| 01 | [Unbounded Cache](./case-01-unbounded-cache/README.md) | Static `ConcurrentHashMap` with no size limit or eviction policy | Caffeine cache with `maximumSize` + TTL |
| 02 | [Unregistered Event Listeners](./case-02-event-listener/README.md) | Per-request `ApplicationListener` registered dynamically and never removed | Spring `@EventListener` singleton component |
| 03 | [Large Result Set Loaded Into Memory](./case-03-large-resultset/README.md) | Unbounded DB query result loaded into memory without pagination | Server-side pagination enforced via `Pageable` |

---

## 🛠️ Prerequisites

Before running any case, make sure you have the following installed:

| Tool | Purpose | Version |
|------|---------|---------|
| Java | Run Spring Boot applications | 17+ |
| Maven | Build the projects | 3.8+ |
| Docker | Run PostgreSQL for Case 03 | Latest |
| k6 | Load testing | Latest |
| Eclipse MAT | Heap dump analysis | 1.15+ |
| PostgreSQL | Database for Case 03 | 15+ |

---

## 🚀 How to Run

Each case is a standalone Spring Boot application with its own `pom.xml`, `application.yml`, and `README.md`.

### General Steps

**1. Navigate to the case folder:**

```bash
cd case-01-unbounded-cache
```

**2. Build the project:**

```bash
mvn clean package -DskipTests
```

**3. Run with JVM options:**

```text
-Xms512m
-Xmx512m
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./docs/heapdump/heapdump.hprof
```

**4. Run the k6 load test:**

```bash
cd k6
k6 run buggy-*.js
```

**5. Analyze the heap dump in Eclipse MAT.**

👉 See each case's `README.md` for detailed step-by-step instructions.

---

## 📊 Case Summaries

### Case 01 — Unbounded Cache

An e-commerce product catalog service caches products in a static `ConcurrentHashMap` with no eviction. Under load, the cache grows indefinitely until the heap is exhausted.

```text
Buggy:  static ConcurrentHashMap — no size limit, no TTL
Fixed:  Caffeine cache — maximumSize=1000, expireAfterWrite=1m
```

---

### Case 02 — Unregistered Event Listeners

An order shipping service registers a new `ApplicationListener` per ship request to handle the `OrderShippedEvent`. The listener holds ~50KB of per-request data and is never removed. Under load, thousands of listeners accumulate in `SimpleApplicationEventMulticaster`.

```text
Buggy:  new BuggyOrderShippedListener() registered per request — never removed
Fixed:  @Component + @EventListener singleton — Spring manages lifecycle
```

---

### Case 03 — Large Result Set Loaded Into Memory

A banking transaction search API returns all matching records for a date range without pagination. A month's worth of 500,000 fully-hydrated transaction records — each with embedded beneficiary, intermediary bank, and exchange rate details — are loaded simultaneously into memory.

```text
Buggy:  findAllByCreatedAtBetween(from, to) → List<TransactionEntity> — no pagination
Fixed:  findAllByCreatedAtBetween(from, to, Pageable) → Page<TransactionDto> — 1,000 per request
```

---

## 🔍 Key Concepts Covered

| Concept | Description |
|---------|-------------|
| JVM Heap | Memory region where all Java objects are allocated |
| Garbage Collector | Reclaims unreferenced objects — cannot collect strongly referenced ones |
| OutOfMemoryError | Thrown when JVM cannot allocate memory for a new object |
| Heap Dump | Snapshot of JVM memory at a point in time — analyzed with Eclipse MAT |
| Strong Reference | A normal Java reference — prevents GC from collecting the object |
| GC Root | Starting points for GC traversal — objects reachable from GC roots are never collected |
| Stop-The-World | GC pauses all application threads — causes request timeouts under memory pressure |

---

## 🧰 Tools Used

| Tool | Purpose |
|------|---------|
| k6 | Load testing — generates concurrent requests to reproduce the leak |
| Eclipse MAT | Heap dump analysis — identifies leak suspects, dominator tree, reference chains |
| Spring Boot | Application framework |
| Caffeine | Production-grade in-memory cache (Case 01 fix) |
| Spring Data JPA | Pagination support (Case 03 fix) |
| PostgreSQL | Database for Case 03 |
| Docker | Containerization |

---

## 📁 Repository Structure

```text
springboot-memory-leak-case-study/
├── case-01-unbounded-cache/
│   ├── src/
│   ├── k6/
│   ├── docs/
│   │   ├── screenshots/
│   │   └── heapdump/
│   ├── pom.xml
│   └── README.md – case-01-unbounded-cache.md
│
├── case-02-event-listener/
│   ├── src/
│   ├── k6/
│   ├── docs/
│   │   ├── screenshots/
│   │   └── heapdump/
│   ├── pom.xml
│   └── README.md – case-02-event-listener.md
│
├── case-03-large-resultset/
│   ├── src/
│   ├── k6/
│   ├── docs/
│   │   ├── screenshots/
│   │   └── heapdump/
│   ├── pom.xml
│   └── README.md – case-03-large-resultset.md
│
└── README.MD
```