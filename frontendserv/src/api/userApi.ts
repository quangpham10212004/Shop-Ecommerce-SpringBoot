import client from './client';
import type { User, CreateUserRequest, UpdateUserRequest } from '../types';

const BASE = '/api/users';

export const userApi = {
  getAll: (search?: string) =>
    client.get<User[]>(BASE, { params: search ? { search } : undefined }),

  getById: (id: number) => client.get<User>(`${BASE}/${id}`),

  create: (data: CreateUserRequest) => client.post<User>(BASE, data),

  update: (id: number, data: UpdateUserRequest) =>
    client.put<User>(`${BASE}/${id}`, data),

  delete: (id: number) => client.delete(`${BASE}/${id}`),
};
