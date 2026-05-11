import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 1,
  iterations: 1,
};

// Same date range — same data — different implementation
const from = '2026-05-01T00:00:00';
const to   = '2026-05-31T23:59:59';

// ✅ Fixed: caller controls page and size — only 1000 records loaded per request
const BASE_URL = `http://localhost:8043/case03/fixed/transactions?from=${from}&to=${to}&page=0&size=1000`;

export default function () {

  const params = {
    headers: { 'Accept': 'application/json' },
  };

  const response = http.get(BASE_URL, params);

  if (response.status !== 200) {
    console.log(`❌ Status: ${response.status}`);
    console.log(response.body);
  }

  check(response, {
    'status is 200': (r) => r.status === 200,
  });
}