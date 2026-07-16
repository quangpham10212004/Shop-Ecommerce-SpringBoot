// ─── User ────────────────────────────────────────────────────────────────────
export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  phone?: string;
  role?: string;
  createdAt?: string;
}

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  phone?: string;
  role?: string;
}

export interface UpdateUserRequest {
  email?: string;
  fullName?: string;
  phone?: string;
  role?: string;
}

// ─── Product ─────────────────────────────────────────────────────────────────
export interface Product {
  id: number;
  name: string;
  description?: string;
  price: number;
  stock: number;
  category?: string;
  imageUrl?: string;
  createdAt?: string;
}

export interface CreateProductRequest {
  name: string;
  description?: string;
  price: number;
  stock: number;
  category?: string;
  imageUrl?: string;
}

export interface UpdateProductRequest {
  name?: string;
  description?: string;
  price?: number;
  stock?: number;
  category?: string;
  imageUrl?: string;
}

// ─── Order ───────────────────────────────────────────────────────────────────
export interface OrderItem {
  productId: number;
  quantity: number;
}

export interface CreateOrderRequest {
  userId: number;
  items: OrderItem[];
}

export interface Order {
  id: number;
  userId: number;
  status: string;
  totalAmount?: number;
  items?: OrderItem[];
  createdAt?: string;
}

// ─── API generic ─────────────────────────────────────────────────────────────
export interface ApiError {
  message: string;
  errors?: Record<string, string>;
  status?: number;
}
