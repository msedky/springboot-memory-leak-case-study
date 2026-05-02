import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 100,
  duration: '2m',
};

const BASE_URL = 'http://localhost:8042/case02/buggy/tracking';

export default function () {
  const orderId = Math.floor(Math.random() * 1000000000);

  const payload = JSON.stringify({
    userId: `user-${__VU}-${__ITER}`,
    sessionPayload: 'S'.repeat(50000)
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  };

  const response = http.post(`${BASE_URL}/${orderId}`, payload, params);

  check(response, {
    'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
  });

  sleep(0.1);
}