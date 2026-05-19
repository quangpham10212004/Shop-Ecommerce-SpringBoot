import client from './client';
import type { Order, CreateOrderRequest } from '../types';

const BASE = '/api/orders';

export const orderApi = {
  getAll: () => client.get<Order[]>(BASE),

  getById: (id: number) => client.get<Order>(`${BASE}/${id}`),

  create: (data: CreateOrderRequest) => client.post<Order>(BASE, data),

  delete: (id: number) => client.delete(`${BASE}/${id}`),
};
