# frontendserv — Shop E-commerce Admin UI

A clean, minimal CRUD demo UI for testing the Spring Boot microservices backend.

## Stack

| Tool | Purpose |
|------|---------|
| React 18 + Vite | UI framework & build tool |
| TypeScript | Type safety |
| Axios | HTTP client |
| React Router v6 | Client-side routing |
| Vanilla CSS | Styling (no framework) |

---

## Prerequisites

- Node.js ≥ 18
- Backend running at `http://localhost:8282` (API Gateway + Eureka)

---

## Install & Run

```bash
# 1. Enter the directory
cd frontendserv

# 2. Copy env file
cp .env.example .env

# 3. Install dependencies
npm install

# 4. Start dev server  →  http://localhost:5173
npm run dev
```

---

## Environment Config

| Variable | Default | Description |
|----------|---------|-------------|
| `VITE_API_BASE_URL` | `http://localhost:8282` | API Gateway base URL |

Edit `.env` to change the gateway address:
```
VITE_API_BASE_URL=http://localhost:8282
```

---

## Folder Structure

```
src/
├── api/
│   ├── client.ts       # Axios instance (base URL, error interceptor)
│   ├── userApi.ts      # /api/users/**
│   ├── productApi.ts   # /api/products/**
│   └── orderApi.ts     # /api/orders/**
├── components/
│   ├── Navbar.tsx
│   ├── Spinner.tsx
│   ├── ErrorAlert.tsx
│   ├── SuccessAlert.tsx
│   └── EmptyState.tsx
├── pages/
│   ├── Dashboard.tsx
│   ├── Users.tsx
│   ├── Products.tsx
│   └── Orders.tsx
├── types/
│   └── index.ts        # TS interfaces for User, Product, Order, ApiError
├── App.tsx
├── main.tsx
└── index.css
```

---

## Manual Test Checklist

### Dashboard (`/`)
- [ ] Page loads without errors
- [ ] Three stat cards show live counts from the backend (Users / Products / Orders)
- [ ] Clicking a card navigates to the correct page

---

### Users (`/users`)

| Action | Steps | Expected |
|--------|-------|----------|
| List users | Load page | Table shows all users |
| Search | Type keyword → press Enter or click Search | Filtered results appear |
| Create | Click **+ New User** → fill form → Submit | Row added, success toast shown |
| Validation error | Submit with duplicate username/email | Backend error displayed field-by-field |
| Edit | Click **Edit** on any row → change fields → Save | Row updated, success toast shown |
| Delete | Click **Delete** → confirm dialog → OK | Row removed, success toast shown |
| Cancel delete | Click **Delete** → confirm dialog → Cancel | Nothing changes |

---

### Products (`/products`)

| Action | Steps | Expected |
|--------|-------|----------|
| List products | Load page | Table shows all products |
| Create | Click **+ New Product** → fill Name, Price, Stock → Submit | Row added |
| Edit | Click **Edit** → change price/stock → Save | Row updated |
| Delete | Click **Delete** → confirm → OK | Row removed |
| Empty state | No products in DB | "No products found." message shown |

---

### Orders (`/orders`)

| Action | Steps | Expected |
|--------|-------|----------|
| Create order | Enter User ID, add item (Product ID + Quantity) → Create Order | JSON response panel appears below form |
| Multi-item order | Click **+ Add Item** → fill 2+ items → Create Order | All items included in response |
| Remove item | Click **✕** on an item row | Row removed (min 1 item stays) |
| Get by ID | Enter an Order ID → Fetch | JSON panel shows order details |
| Invalid ID | Enter non-existent ID → Fetch | Error alert with backend message |

---

## API Routes Used

| Page | Method | Endpoint |
|------|--------|----------|
| Users | GET | `/api/users?search=` |
| Users | POST | `/api/users` |
| Users | PUT | `/api/users/{id}` |
| Users | DELETE | `/api/users/{id}` |
| Products | GET | `/api/products` |
| Products | POST | `/api/products` |
| Products | PUT | `/api/products/{id}` |
| Products | DELETE | `/api/products/{id}` |
| Orders | POST | `/api/orders` |
| Orders | GET | `/api/orders/{id}` |
| Dashboard | GET | `/api/users`, `/api/products`, `/api/orders` |
