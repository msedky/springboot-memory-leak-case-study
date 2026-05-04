# 🚨 Case 02 — Underegistered Event Listeners → OutOfMemoryError

## 📌 Overview

This case demonstrates a **production-like memory issue** caused by **dynamically registered application listeners that are never removed**.

Under load:

```text
New listener registered per request → listeners accumulate → Heap fills → GC pressure → OOM → Crash
```

---

## 📁 Project Structure

```text
case-02-event-listener/
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
src/main/java/org/jvmmemoryleak/case02/
├── common/   # shared components (event, request, response, config)
├── buggy/    # implementation with memory issue
└── fixed/    # corrected implementation with proper listener management
```

---

## 🔍 Monitoring Endpoint

The application exposes a shared diagnostic endpoint to monitor JVM heap usage in real time:

```http
GET /case02/common/heap
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

An order shipping service receives ship requests. When an order is shipped, an `OrderShippedEvent` is published to notify downstream components such as notification services, audit systems, and tracking modules.

**Both buggy and fixed implementations:**
- Accept the same `POST /{orderId}` request
- Publish the same `OrderShippedEvent`
- Return the same `ShipOrderResponse`

**The only difference is how the event is handled.**

---

## ⚠️ Buggy Implementation

### Endpoints

```http
POST   /case02/buggy/orders/{orderId}
```

### Problem

On every ship request, a new `BuggyOrderShippedListener` instance is created and registered dynamically into `SimpleApplicationEventMulticaster`.

The listener holds per-request data:

```text
trackedOrderId
userId
shippingAddress  (large payload ~50KB)
estimatedDelivery
```

The listener is **never removed** after handling the event.

👉 Result:

```text
Unbounded listener accumulation → retained per-request data → heap exhaustion
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
cd case-02-event-listener/k6
k6 run buggy-listener-load-test.js
```

---

#### 3️⃣ Monitor Heap in Real Time

While the test is running, call from Postman repeatedly:

```http
GET http://localhost:8042/case02/common/heap
```

👉 Observe `used_mb` increasing continuously until the app becomes unresponsive.

---

### 💣 Expected Result (Buggy)

```text
Listeners accumulate continuously
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
  case-02-event-listener/
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

In this case, MAT points to **9,375 instances** of:

```text
org.jvmmemoryleak.case02.buggy.listener.BuggyOrderShippedListener
```

📸 See: `03.png`

---

##### 5️⃣ Inspect the Accumulation Path

Scroll down below the pie chart to see the full suspect details.

MAT shows the stack trace and the Common Path to the Accumulation Point:

```text
TaskThread (Tomcat worker thread)
  → java.lang.Object[9369] (listeners array)
    → BuggyOrderShippedListener[4754]
    → BuggyOrderShippedListener[8099]
    → BuggyOrderShippedListener[4384]
    → ... (thousands more)
```

📸 See: `04.png`

---

##### 6️⃣ Inspect the Accumulation Point — Listener List

Expand the `java.lang.Object[]` array to see all accumulated listener instances.

Each entry is a separate `BuggyOrderShippedListener` instance registered per request and never removed.

📸 See: `05.png`

---

##### 7️⃣ Inspect a Single Listener Instance

Click on any single `BuggyOrderShippedListener` instance to inspect what it retains:

```text
BuggyOrderShippedListener
  → trackedOrderId: 419993133
  → userId: user-81-81
  → shippingAddress: SSSSS... (~50KB String)
  → estimatedDelivery: java.time.LocalDate
```

Each listener holds ~50KB of data. Multiplied by thousands of instances — the heap fills completely.

📸 See: `06.png`

---

#### 🧩 Root Cause Analysis

```text
BuggyOrderService.shipOrder()
  → new BuggyOrderShippedListener(orderId, userId, shippingAddress, estimatedDelivery)
  → applicationEventMulticaster.addApplicationListener(listener)
  → listener is NEVER removed
  → SimpleApplicationEventMulticaster holds strong reference to every registered listener
  → GC cannot collect any of them
  → heap usage keeps increasing
  → application eventually crashes with OutOfMemoryError
```

---

## ✅ Fixed Implementation

### Endpoints

```http
POST   /case02/fixed/orders/{orderId}
```

### Fix

Replace dynamic per-request listener registration with a single Spring-managed `@Component` listener using `@EventListener`.

```java
@Slf4j
@Component
public class FixedOrderShippedListener {

    @EventListener
    public void handle(OrderShippedEvent event) {
        log.info("Order {} shipped to {} — estimated delivery: {}",
                event.getOrderId(), event.getShippingAddress(), event.getEstimatedDelivery());
    }
}
```

### Key Difference

```text
One singleton listener managed by Spring
Handles ALL OrderShippedEvents
No per-request data retained
Nothing accumulates in memory
```

---

### 🔁 Running Fixed Test

```bash
cd case-02-event-listener/k6
k6 run fixed-listener-load-test.js
```

---

### 📈 Expected Behavior (Fixed)

```text
One singleton listener handles all events
Heap usage stays stable regardless of request volume
No listener accumulation
No OutOfMemoryError under load
```

---

## 📊 Comparison

| Metric               | Buggy                        | Fixed         |
|----------------------|------------------------------|---------------|
| Listener instances   | 1 per request → unbounded ↑  | 1 singleton   |
| Per-request data retained | ~50KB per listener      | Nothing       |
| Heap Usage           | Continuous ↑                 | Stable        |
| GC Behavior          | High pressure / Stop-The-World | Normal      |
| OOM                  | Yes 💣                       | No ✅          |

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

> Dynamically registering listeners per request without a deregistration strategy is a silent production killer.

Always prefer:

```text
Spring-managed singleton listeners via @EventListener
Stateless listener design
If dynamic registration is required — always remove the listener after use
```

---

## 🛠️ Tools Used

* k6 (load testing)
* Eclipse MAT (heap analysis)
* Spring Boot
* Spring Application Events