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
│   └── heapdump/        # Generated heap dumps
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

## 🔍 Monitoring Endpoint

The application exposes a shared diagnostic endpoint to monitor JVM heap usage:

```http
GET /case01/common/heap
```

Example response:

```json
{
  "used_mb": 210,
  "free_mb": 120,
  "total_mb": 512,
  "max_mb": 512
}
```

This endpoint is used during load testing to observe memory growth and verify stabilization after applying the fix.

---

## ⚠️ Buggy Implementation

### Endpoints:

```http
POST   /case01/buggy/products
PUT    /case01/buggy/products/{id}
GET    /case01/buggy/products/{id}
GET    /case01/buggy/products
DELETE /case01/buggy/products/{id}

GET    /case01/buggy/products/cache-size
DELETE /case01/buggy/products/cache
GET    /case01/buggy/products/heap
```

### Problem

* Cache is implemented **manually** using `ConcurrentHashMap`
* No eviction policy
* Objects remain strongly referenced

👉 Result:

```text
Unbounded memory growth
```

---

### 🧪 Running the Buggy Test

#### 1️⃣ Run application with JVM options:

```text
-Xms512m 
-Xmx512m 
-XX:+HeapDumpOnOutOfMemoryError 
-XX:HeapDumpPath=./docs/heapdump/heapdump.hprof
```

---

#### 2️⃣ Run Buggy Test

```bash
cd case-01-unbounded-cache/k6
k6 run buggy-cache-load-test.js
```

---

### 💣 Expected Result (Buggy)

```text
cache-size ↑ continuously
heap ↑ continuously
→ java.lang.OutOfMemoryError
```

Heap dump will be generated in:

```text
docs/heapdump/heapdump.hprof
```

---

### 🔍 Heap Dump Analysis

We analyze the heap dump using:

```text
Eclipse MAT (Memory Analyzer Tool)
```

---

#### 🔬 Step-by-Step Heap Analysis

##### 1️⃣ Open Eclipse MAT

Run:

```text
MemoryAnalyzer.exe
```

---

##### 2️⃣ Open Heap Dump

From top menu:

```text
File → Open Heap Dump
```

Choose file:

```text
springboot-memory-leak-case-study/
  case-01-unbounded-cache/
    docs/
      heapdump/
        heapdump.hprof
```

📸 See: `01.png`

---

##### 3️⃣ Initial Screen

MAT shows:

```text
Overview + Leak Suspects dialog
```

Click:

```text
Finish
```

📸 See: `02.png`

---

##### 4️⃣ Leak Suspects Report

You will see a pie chart.

👉 Important:

```text
(a) Problem Suspect 1 occupies most of heap memory
```

📸 See: `03.png`

---

##### 5️⃣ Investigate Main Suspect

Click:

```text
(a) Problem Suspect 1
```

Then:

```text
List objects → with outgoing references
```

📸 See: `04.png`

---

##### 6️⃣ Why Outgoing References?

Because it shows:

```text
What objects are being held in memory
```

NOT:

```text
Who references them
```

---

##### 7️⃣ Root Cause Discovery

```text
BuggyProductService
  → CACHE (ConcurrentHashMap)
    → ProductDto
      → large String (description)
```

📸 See: `05.png`

---

#### 🧩 Root Cause Analysis

```text
Because the cache has no eviction policy:

The ConcurrentHashMap cache retains all ProductDto objects
→ prevents garbage collection
→ causes continuous heap growth
→ leads to OutOfMemoryError
```

---

## ✅ Fixed Implementation

### Endpoints

```http
POST   /case01/fixed/products
PUT    /case01/fixed/products/{id}
GET    /case01/fixed/products/{id}
GET    /case01/fixed/products
DELETE /case01/fixed/products/{id}

GET    /case01/fixed/products/cache-size
DELETE /case01/fixed/products/cache
GET    /case01/fixed/products/heap
```

### Configuration

```yaml
spring:
  cache:
    type: caffeine
    cache-names:
      - products
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=1m
```

### Key Difference

```text
Cache is managed using Spring Cache abstraction backed by Caffeine
```

---

### 🔁 Running Fixed Test

```bash
cd case-01-unbounded-cache/k6
k6 run fixed-cache-load-test.js
```

---

### 📈 Expected Behavior (Fixed)

```text
Cache size stabilizes around configured maximum (~1000)
Eviction removes older entries
Heap usage stabilizes
No OutOfMemoryError under load
```

---

## 📊 Comparison

| Metric      | Buggy         | Fixed  |
| ----------- | ------------- | ------ |
| Cache Size  | Unlimited ↑   | ~1000  |
| Heap Usage  | Continuous ↑  | Stable |
| GC Behavior | High pressure | Normal |
| OOM         | Yes 💣        | No ✅   |

---

## ⚠️ Important Notes

Before rerun:

```text
Delete:
docs/heapdump/heapdump.hprof
```

Otherwise:

```text
Unable to create heapdump.hprof: File exists
```

---

## 💡 Final Takeaway

> Unbounded in-memory caching is a critical production risk that can lead to system crashes under load.

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
