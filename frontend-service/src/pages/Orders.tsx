import React, { useState } from 'react';
import { orderApi } from '../api/orderApi';
import type { Order, OrderItem, ApiError } from '../types';
import ErrorAlert from '../components/ErrorAlert';
import SuccessAlert from '../components/SuccessAlert';
import Spinner from '../components/Spinner';

const Orders: React.FC = () => {
  // ── Create order ────────────────────────────────────────────────────────────
  const [userId, setUserId] = useState('');
  const [items, setItems] = useState<OrderItem[]>([{ productId: 0, quantity: 1 }]);
  const [createError, setCreateError] = useState<ApiError | null>(null);
  const [createSuccess, setCreateSuccess] = useState('');
  const [createdOrder, setCreatedOrder] = useState<Order | null>(null);
  const [creating, setCreating] = useState(false);

  // ── Get order by ID ─────────────────────────────────────────────────────────
  const [searchId, setSearchId] = useState('');
  const [foundOrder, setFoundOrder] = useState<Order | null>(null);
  const [getError, setGetError] = useState<ApiError | null>(null);
  const [getting, setGetting] = useState(false);

  // ── Item helpers ────────────────────────────────────────────────────────────
  const addItem = () => setItems([...items, { productId: 0, quantity: 1 }]);
  const removeItem = (idx: number) => setItems(items.filter((_, i) => i !== idx));
  const updateItem = (idx: number, field: keyof OrderItem, value: number) => {
    const next = [...items];
    next[idx] = { ...next[idx], [field]: value };
    setItems(next);
  };

  // ── Submit create ───────────────────────────────────────────────────────────
  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setCreateError(null);
    setCreatedOrder(null);
    setCreating(true);
    try {
      const res = await orderApi.create({ userId: parseInt(userId), items });
      setCreatedOrder(res.data);
      setCreateSuccess('Order created successfully!');
      setTimeout(() => setCreateSuccess(''), 3000);
    } catch (err: any) {
      setCreateError(err);
    } finally {
      setCreating(false);
    }
  };

  // ── Submit get by ID ────────────────────────────────────────────────────────
  const handleGetById = async (e: React.FormEvent) => {
    e.preventDefault();
    setGetError(null);
    setFoundOrder(null);
    setGetting(true);
    try {
      const res = await orderApi.getById(parseInt(searchId));
      setFoundOrder(res.data);
    } catch (err: any) {
      setGetError(err);
    } finally {
      setGetting(false);
    }
  };

  return (
    <div className="page">
      <h1>Orders</h1>

      {/* ── Create Order ── */}
      <section className="card form-card">
        <h2>Create Order</h2>
        <ErrorAlert error={createError} />
        {createSuccess && <SuccessAlert message={createSuccess} />}

        <form onSubmit={handleCreate}>
          <div className="form-row">
            <label>User ID *</label>
            <input
              required
              type="number"
              min="1"
              value={userId}
              onChange={e => setUserId(e.target.value)}
              placeholder="e.g. 1"
            />
          </div>

          <div className="items-section">
            <div className="items-header">
              <label>Order Items</label>
              <button type="button" className="btn btn-sm" onClick={addItem}>+ Add Item</button>
            </div>

            {items.map((item, idx) => (
              <div key={idx} className="item-row">
                <div className="form-row">
                  <label>Product ID</label>
                  <input
                    required
                    type="number"
                    min="1"
                    value={item.productId || ''}
                    onChange={e => updateItem(idx, 'productId', parseInt(e.target.value))}
                    placeholder="e.g. 1"
                  />
                </div>
                <div className="form-row">
                  <label>Quantity</label>
                  <input
                    required
                    type="number"
                    min="1"
                    value={item.quantity}
                    onChange={e => updateItem(idx, 'quantity', parseInt(e.target.value))}
                  />
                </div>
                {items.length > 1 && (
                  <button type="button" className="btn btn-sm btn-danger remove-item" onClick={() => removeItem(idx)}>✕</button>
                )}
              </div>
            ))}
          </div>

          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={creating}>
              {creating ? 'Creating…' : 'Create Order'}
            </button>
          </div>
        </form>

        {createdOrder && (
          <div className="json-panel">
            <h3>Response</h3>
            <pre>{JSON.stringify(createdOrder, null, 2)}</pre>
          </div>
        )}
      </section>

      {/* ── Get by ID ── */}
      <section className="card form-card">
        <h2>Get Order by ID</h2>
        <ErrorAlert error={getError} />

        <form onSubmit={handleGetById} className="inline-form">
          <input
            required
            type="number"
            min="1"
            placeholder="Order ID…"
            value={searchId}
            onChange={e => setSearchId(e.target.value)}
          />
          <button type="submit" className="btn btn-primary" disabled={getting}>
            {getting ? '…' : 'Fetch'}
          </button>
        </form>

        {getting && <Spinner message="Fetching order…" />}

        {foundOrder && (
          <div className="json-panel">
            <h3>Order #{foundOrder.id}</h3>
            <pre>{JSON.stringify(foundOrder, null, 2)}</pre>
          </div>
        )}
      </section>
    </div>
  );
};

export default Orders;
