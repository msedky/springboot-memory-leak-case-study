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

📸 See: `00.png`

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

##### 3️⃣ Generate Leak Suspects Report

After opening the heap dump, MAT shows the Getting Started Wizard.

Choose:

```text
Leak Suspects Report
```

Then click:

```text
Finish
```

📸 See: `02.png`

---

##### 4️⃣ Review Leak Suspects Report

MAT generates a Leak Suspects report with a memory usage pie chart.

👉 The important observation is:

```text
(a) Problem Suspect 1 occupies most of heap memory
```

In this case, MAT points to:

```text
org.jvmmemoryleak.case01.buggy.service.BuggyProductService
```

📸 See: `03.png`

---

##### 5️⃣ Inspect the Suspect Object

Open the suspect details and inspect the outgoing references.

This shows what the suspect object is holding in memory.

In the result, we can see that:

```text
BuggyProductService
  → CACHE
  → ConcurrentHashMap
```

📸 See: `04.png`

---

##### 6️⃣ Expand the Cache Entries

Expand the ConcurrentHashMap internal table entries.

MAT shows many retained entries inside the cache:

```text
ConcurrentHashMap
  → Node[]
  → ConcurrentHashMap$Node
  → key
  → value
```

Each node represents a cached product entry that is still strongly referenced.

📸 See: `05.png`

---

#### 🧩 Root Cause Analysis

The root cause is the static/manual cache inside the buggy service:

```text
BuggyProductService
  → CACHE (ConcurrentHashMap)
    → ProductDto objects
      → large description strings
```

Because the cache has no eviction policy:

```text
The ConcurrentHashMap grows without any size limit or eviction policy
→ cached ProductDto objects remain strongly referenced
→ garbage collector cannot remove them
→ heap usage keeps increasing
→ application eventually crashes with OutOfMemoryError
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
