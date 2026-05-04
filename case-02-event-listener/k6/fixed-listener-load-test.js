import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 100,
  duration: '2m',
};

const BASE_URL = 'http://localhost:8042/case02/fixed/orders';

export default function () {
  const orderId = Math.floor(Math.random() * 1000000000);


  // Same payload as buggy test — same load, same conditions, different implementation
  const payload = JSON.stringify({
    userId: `user-${__VU}-${__ITER}`,
    shippingAddress: 'S'.repeat(50000),
	estimatedDelivery: '2026-07-01'
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  };

  const response = http.post(`${BASE_URL}/${orderId}`, payload, params);

  if (response.status !== 200 && response.status !== 201) {
    console.log(`❌ Status: ${response.status}`);
    console.log(response.body);
  }

  check(response, {
    'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
  });

  sleep(0.1);
}