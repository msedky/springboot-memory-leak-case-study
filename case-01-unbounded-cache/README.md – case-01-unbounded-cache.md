# 🚨 Case 01 — Unbounded Cache → OutOfMemoryError

## 📌 Overview

This case demonstrates a **production-like memory issue** caused by an **unbounded in-memory cache**.

Under load:

```text
Cache grows indefinitely → Heap fills → GC pressure → OOM → Crash
```

---

## 📁 Project Structure

```text
case-01-unbounded-cache/
├── src/                 # Spring Boot application
├── k6/                  # Load testing scripts
├── docs/
│   ├── screenshots/     # MAT + results screenshots
│	├── heapdump/            # Generated heap dumps
├── pom.xml
└── README.md
```

### Source Code Structure

```text
src/main/java/org/jvmmemoryleak/case01/
├── common/   # shared components (DTO, entity, repository, mapper)
├── buggy/    # implementation with memory issue
└── fixed/    # corrected implementation with proper caching
```

---

## ⚠️ Buggy Implementation

Endpoints:

```http
POST /case01/buggy/products
GET  /case01/buggy/products/{id}
GET  /case01/buggy/products/cache-size
GET  /case01/buggy/products/heap
```

### Problem

* Cache is implemented using `ConcurrentHashMap`
* No eviction policy
* Objects remain strongly referenced

👉 Result:

```text
Unbounded memory growth
```

---

## 🧪 Running the Test

### 1️⃣ Run application with JVM options:

```text
-Xms512m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./docs/heapdump/heapdump.hprof
```

---

### 2️⃣  Running Buggy Test

```bash
cd case-01-unbounded-cache/k6
k6 run buggy-cache-load-test.js
```

---

## 💣 Expected Result (Buggy)

```text
cache-size ↑ continuously
heap ↑ continuously
→ java.lang.OutOfMemoryError
```

Heap dump will be generated in:

```text
heapdump/heapdump.hprof
```

---

## 🔍 Heap Dump Analysis

Open the heap dump using:

```text
Eclipse MAT (Memory Analyzer Tool)
```

---

### Key Findings:

```text
BuggyProductService
  → CACHE (ConcurrentHashMap)
    → ProductDto
      → large String (description)
```

👉 Root cause:

```text
Unbounded cache retains objects → prevents GC → memory exhaustion
```

---

## 📸 Screenshots

Add screenshots under:

```text
docs/screenshots/
```

Recommended:

* Leak Suspects
* Dominator Tree
* Histogram
* Cache growth vs heap growth

---

## ✅ Fixed Implementation

Configuration:

```yaml
spring:
  cache:
    type: caffeine
    cache-names:
      - products
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=1m
```

---

### Behavior

```text
Cache size stabilizes (~1000)
Heap stabilizes
No OutOfMemoryError
```

---

## 🔁 Running Fixed Test

```bash
cd case-01-unbounded-cache/k6
k6 run fixed-cache-load-test.js
```

---

## 📊 Comparison

| Metric     | Buggy        | Fixed  |
| ---------- | ------------ | ------ |
| Cache Size | Unlimited ↑  | ~1000  |
| Heap Usage | Continuous ↑ | Stable |
| OOM        | Yes 💣       | No ✅   |

---

## ⚠️ Important Notes

* Delete old heap dump before rerun:

```text
heapdump/heapdump.hprof
```

Otherwise:

```text
Unable to create heapdump.hprof: File exists
```

---

## 💡 Key Takeaway

> In-memory caching without eviction is a production risk.

Always define:

```text
maximum size
TTL
eviction strategy
```

---

## 🛠️ Tools Used

* k6 (load testing)
* Eclipse MAT (heap analysis)
* Spring Boot
* Caffeine Cache
