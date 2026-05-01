import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100,
    duration: '2m',
};

const BASE_URL = 'http://localhost:8041/case01/buggy/products';

export default function () {

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
    };

    // 🔥 generate unique product each time
    const payload = JSON.stringify({
        name: `Product-${__VU}-${__ITER}-${Date.now()}`,
        description: "X".repeat(200_000), // large payload 🔥
        price: Math.random() * 1000
    });

    const response = http.post(BASE_URL, payload, params);

    // ✅ debug if issue occurred
    if (response.status !== 200 && response.status !== 201) {
        console.log(`❌ Status: ${response.status}`);
        console.log(response.body);
    }

    check(response, {
        'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    });

    sleep(0.2);
}