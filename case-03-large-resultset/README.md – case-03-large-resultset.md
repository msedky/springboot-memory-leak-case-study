# 🚨 Case 03 — Large Result Set Loaded Into Memory → OutOfMemoryError

## 📌 Overview

This case demonstrates a **production-like memory issue** caused by **loading an unbounded result set into memory without pagination**.

Under load:

```text
All matching records loaded into memory at once → Heap fills → GC pressure → OOM → Crash
```

---

## 📁 Project Structure

```text
case-03-large-resultset/
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
src/main/java/org/jvmmemoryleak/case03/
├── common/   # shared components (entity, repository, mapper, dto, seeder)
├── buggy/    # implementation with memory issue — no pagination
└── fixed/    # corrected implementation — pagination enforced
```

---

## 🔍 Monitoring Endpoint

The application exposes a shared diagnostic endpoint to monitor JVM heap usage in real time:

```http
GET /case03/common/heap
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

👉 While running the k6 load test, call this endpoint repeatedly from Postman to observe heap growth over time.

---

## 🏢 Business Scenario

A banking platform stores millions of financial transactions. The transaction search API allows filtering by date range to retrieve all matching records.

A developer built the search API without enforcing pagination — returning all matching records in a single response. In development with small datasets this worked fine.

In production, a date range query over a full month returns 500,000 fully-hydrated transaction records simultaneously into memory — each including beneficiary details, intermediary bank details, and exchange rate details — exhausting the heap.

**Both buggy and fixed implementations:**
- Accept the same `GET /transactions?from=...&to=...` request
- Query the same 500,000 transaction records in the DB
- Return `TransactionDto` objects

**The only difference is whether pagination is enforced.**

---

## ⚠️ Buggy Implementation

### Endpoints

```http
GET /case03/buggy/transactions?from=2026-05-01T00:00:00&to=2026-05-31T23:59:59
```

### Problem

No pagination — all 500,000 transactions are loaded into a `List<TransactionEntity>` at once. Each entity is fully hydrated including all embedded objects:

```text
TransactionEntity
  → BeneficiaryDetails      (beneficiaryName, accountNumber, bankName, swiftBic, ...)
  → IntermediaryBankDetails  (intermediaryBankName, swiftBic, routingNumber) — 30% of records
  → ExchangeRateDetails      (rate, targetCurrency, targetAmount, rateLockedAt) — 30% of records
```

👉 Result:

```text
500,000 fully-hydrated entities in memory simultaneously → heap exhaustion
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

#### 2️⃣ Seed the database

```http
POST http://localhost:8043/case03/common/seed
```

Wait for the response:

```json
{
  "data": {
    "inserted": 500000,
    "message": "Seeding completed successfully"
  }
}
```

---

#### 3️⃣ Run Buggy Test

```bash
cd case-03-large-resultset/k6
k6 run buggy-report-load-test.js
```

---

#### 4️⃣ Monitor Heap in Real Time

While the test is running, call from Postman repeatedly:

```http
GET http://localhost:8043/case03/common/heap
```

👉 Observe `used_mb` increasing until the app becomes unresponsive.

---

### 💣 Expected Result (Buggy)

```text
used_mb ↑ continuously
App becomes unresponsive (Stop-The-World GC)
→ java.lang.OutOfMemoryError: Java heap space
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
  case-03-large-resultset/
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
org.postgresql.jdbc.PgResultSet
```

📸 See: `03.png`

---

##### 5️⃣ Inspect the Accumulation Path

Expand the suspect object to see the full reference chain.

MAT shows that `PgResultSet` holds a `rows` field — an `ArrayList` containing all 500,000 raw row tuples fetched from the database:

```text
PgResultSet
  → rows: ArrayList
    → elementData: Object[]
      → [0] Tuple → UUID, TXN-2026-..., 42745.6516, IBAN..., HSBC, ...
      → [1] Tuple → UUID, TXN-2026-..., ...
      → [2] Tuple → ...
      → ... (500,000 tuples total)
```

Each `Tuple` holds the raw byte arrays for every column of the row — including all beneficiary, intermediary bank, and exchange rate fields.

📸 See: `04.png`

---

#### 🧩 Root Cause Analysis

The crash happens at the **JDBC layer** — before Hibernate even finishes mapping rows to entities:

```text
BuggyTransactionServiceImpl.findTransactions()
  → transactionRepository.findAllByCreatedAtBetween(from, to)
  → JDBC driver fetches ALL 500,000 rows into PgResultSet
  → PgResultSet.rows (ArrayList) holds all raw Tuple objects simultaneously
  → Hibernate never finishes hydrating TransactionEntity objects
  → GC cannot collect — PgResultSet strongly referenced by the active query
  → heap exhausted → OutOfMemoryError
```

---

## ✅ Fixed Implementation

### Endpoints

```http
GET /case03/fixed/transactions?from=2026-05-01T00:00:00&to=2026-05-31T23:59:59&page=0&size=1000
```

### Fix

Enforce pagination — the caller specifies `page` and `size`. Only the requested page is loaded into memory at a time.

```java
return transactionRepository
    .findAllByCreatedAtBetween(from, to, PageRequest.of(page, size))
    .map(transactionMapper::toDto);
```

### Key Difference

```text
Only 1,000 records loaded per request
Caller controls what data they need
Heap usage stays stable regardless of total record count
```

---

### 🔁 Running Fixed Test

```bash
cd case-03-large-resultset/k6
k6 run fixed-report-load-test.js
```

---

### 📈 Expected Behavior (Fixed)

```text
Only 1,000 records returned per request
Heap usage stays stable
No OutOfMemoryError
```

---

## 📊 Comparison

| Metric            | Buggy                          | Fixed                 |
|-------------------|--------------------------------|-----------------------|
| Records in memory | 500,000 simultaneously         | 1,000 per request     |
| Pagination        | None                           | Enforced by caller    |
| Heap Usage        | Continuous ↑                   | Stable                |
| GC Behavior       | High pressure / Stop-The-World | Normal                |
| OOM               | Yes 💣                         | No ✅                  |

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

> Never expose an API that returns unbounded result sets. Always enforce pagination — either server-side or by requiring the caller to provide page and size parameters.

Always define:

```text
maximum page size
default page size
server-side enforcement — reject requests without pagination parameters
```

---

## 🛠️ Tools Used

* k6 (load testing)
* Eclipse MAT (heap analysis)
* Spring Boot
* Spring Data JPA (Pagination)
* PostgreSQL