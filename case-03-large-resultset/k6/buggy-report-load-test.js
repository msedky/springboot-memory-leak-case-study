import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 1,
  iterations: 1,
};

// Date range covering the entire current month
const from = '2026-05-01T00:00:00';
const to   = '2026-05-31T23:59:59';

const BASE_URL = `http://localhost:8043/case03/buggy/transactions?from=${from}&to=${to}`;

export default function () {

  const params = {
    headers: { 'Accept': 'application/json' },
  };

  // 🔥 Single request — no pagination — loads ALL 500k records at once
  // Expected: java.lang.OutOfMemoryError: Java heap space
  const response = http.get(BASE_URL, params);

  if (response.status !== 200) {
    console.log(`❌ Status: ${response.status}`);
    console.log(response.body);
  }

  check(response, {
    'status is 200': (r) => r.status === 200,
  });
}