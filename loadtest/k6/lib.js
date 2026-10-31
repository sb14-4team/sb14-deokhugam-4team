import crypto from 'k6/crypto';
import http from 'k6/http';

export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// seed.sql과 같은 규모. S·L로 시드했으면 같은 값을 -e로 넘긴다
export const SCALE = {
  users: Number(__ENV.USERS || 1000),
  books: Number(__ENV.BOOKS || 3000),
  reviews: Number(__ENV.USERS || 1000) * Number(__ENV.REVIEWS_PER_USER || 30),
};

export const TOPICS = ['자바', '스프링', '데이터베이스', '클라우드', '알고리즘', '소설', '에세이', '역사', '과학', '경제'];

// seed.sql의 md5('<종류>-<번호>')::uuid와 같은 값
export function seedId(kind, n) {
  const h = crypto.md5(`${kind}-${n}`, 'hex');
  return `${h.slice(0, 8)}-${h.slice(8, 12)}-${h.slice(12, 16)}-${h.slice(16, 20)}-${h.slice(20)}`;
}

export function pick(list) {
  return list[Math.floor(Math.random() * list.length)];
}

export function randInt(max) {
  return Math.floor(Math.random() * max) + 1;
}

export function query(params) {
  return Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== null)
    .map(([k, v]) => `${k}=${encodeURIComponent(v)}`)
    .join('&');
}

// name 태그로 묶어야 id가 다른 URL이 지표에서 한 줄로 모인다. kind는 읽기·쓰기 기준을 따로 걸려고 붙인다
export function get(path, name, userId) {
  return http.get(`${BASE_URL}${path}`, {
    headers: userId ? { 'Deokhugam-Request-User-ID': userId } : {},
    tags: { name, kind: 'read' },
  });
}

export function post(path, name, body, userId, expected) {
  const headers = { 'Content-Type': 'application/json' };
  if (userId) headers['Deokhugam-Request-User-ID'] = userId;
  return http.post(`${BASE_URL}${path}`, body === null ? null : JSON.stringify(body), {
    headers,
    tags: { name, kind: 'write' },
    responseCallback: expected ? http.expectedStatuses(...expected) : undefined,
  });
}
