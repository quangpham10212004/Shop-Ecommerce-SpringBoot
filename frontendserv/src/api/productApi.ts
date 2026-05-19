import client from './client';
import type { Product, CreateProductRequest, UpdateProductRequest } from '../types';

const BASE = '/api/products';

export const productApi = {
  getAll: () => client.get<Product[]>(BASE),

  getById: (id: number) => client.get<Product>(`${BASE}/${id}`),

  create: (data: CreateProductRequest) => client.post<Product>(BASE, data),

  update: (id: number, data: UpdateProductRequest) =>
    client.put<Product>(`${BASE}/${id}`, data),

  delete: (id: number) => client.delete(`${BASE}/${id}`),
};
