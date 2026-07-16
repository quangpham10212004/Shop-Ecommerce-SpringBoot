import React, { useEffect, useState } from 'react';
import { productApi } from '../api/productApi';
import type { Product, CreateProductRequest, UpdateProductRequest, ApiError } from '../types';
import Spinner from '../components/Spinner';
import ErrorAlert from '../components/ErrorAlert';
import SuccessAlert from '../components/SuccessAlert';
import EmptyState from '../components/EmptyState';

const EMPTY_CREATE: CreateProductRequest = {
  name: '', description: '', price: 0, stock: 0, category: '', imageUrl: '',
};

const Products: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);
  const [success, setSuccess] = useState('');

  const [showCreate, setShowCreate] = useState(false);
  const [createForm, setCreateForm] = useState<CreateProductRequest>(EMPTY_CREATE);

  const [editProduct, setEditProduct] = useState<Product | null>(null);
  const [editForm, setEditForm] = useState<UpdateProductRequest>({});

  const fetchProducts = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await productApi.getAll();
      setProducts(res.data);
    } catch (e: any) {
      setError(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchProducts(); }, []);

  const notify = (msg: string) => {
    setSuccess(msg);
    setTimeout(() => setSuccess(''), 3000);
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      await productApi.create(createForm);
      notify('Product created!');
      setCreateForm(EMPTY_CREATE);
      setShowCreate(false);
      fetchProducts();
    } catch (e: any) { setError(e); }
  };

  const startEdit = (p: Product) => {
    setEditProduct(p);
    setEditForm({ name: p.name, description: p.description, price: p.price, stock: p.stock, category: p.category, imageUrl: p.imageUrl });
  };

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editProduct) return;
    setError(null);
    try {
      await productApi.update(editProduct.id, editForm);
      notify('Product updated!');
      setEditProduct(null);
      fetchProducts();
    } catch (e: any) { setError(e); }
  };

  const handleDelete = async (p: Product) => {
    if (!window.confirm(`Delete product "${p.name}"?`)) return;
    setError(null);
    try {
      await productApi.delete(p.id);
      notify('Product deleted!');
      fetchProducts();
    } catch (e: any) { setError(e); }
  };

  const ProductForm = ({
    form, setForm, onSubmit, submitLabel, onCancel, title,
  }: {
    form: CreateProductRequest | UpdateProductRequest;
    setForm: (f: any) => void;
    onSubmit: (e: React.FormEvent) => void;
    submitLabel: string;
    onCancel: () => void;
    title: string;
  }) => (
    <form className="card form-card" onSubmit={onSubmit}>
      <h2>{title}</h2>
      <div className="form-row">
        <label>Name *</label>
        <input required value={(form as any).name || ''} onChange={e => setForm({ ...form, name: e.target.value })} />
      </div>
      <div className="form-row">
        <label>Description</label>
        <textarea value={(form as any).description || ''} onChange={e => setForm({ ...form, description: e.target.value })} rows={3} />
      </div>
      <div className="form-row-2">
        <div className="form-row">
          <label>Price *</label>
          <input required type="number" min="0" step="0.01" value={(form as any).price ?? ''} onChange={e => setForm({ ...form, price: parseFloat(e.target.value) })} />
        </div>
        <div className="form-row">
          <label>Stock *</label>
          <input required type="number" min="0" value={(form as any).stock ?? ''} onChange={e => setForm({ ...form, stock: parseInt(e.target.value) })} />
        </div>
      </div>
      <div className="form-row">
        <label>Category</label>
        <input value={(form as any).category || ''} onChange={e => setForm({ ...form, category: e.target.value })} />
      </div>
      <div className="form-row">
        <label>Image URL</label>
        <input value={(form as any).imageUrl || ''} onChange={e => setForm({ ...form, imageUrl: e.target.value })} />
      </div>
      <div className="form-actions">
        <button type="submit" className="btn btn-primary">{submitLabel}</button>
        <button type="button" className="btn btn-ghost" onClick={onCancel}>Cancel</button>
      </div>
    </form>
  );

  return (
    <div className="page">
      <div className="page-header">
        <h1>Products</h1>
        <button className="btn btn-primary" onClick={() => { setShowCreate(!showCreate); setError(null); }}>
          {showCreate ? '✕ Cancel' : '+ New Product'}
        </button>
      </div>

      <ErrorAlert error={error} />
      {success && <SuccessAlert message={success} />}

      {showCreate && (
        <ProductForm
          form={createForm}
          setForm={setCreateForm}
          onSubmit={handleCreate}
          submitLabel="Create"
          onCancel={() => setShowCreate(false)}
          title="Create Product"
        />
      )}

      {editProduct && (
        <ProductForm
          form={editForm}
          setForm={setEditForm}
          onSubmit={handleUpdate}
          submitLabel="Save"
          onCancel={() => setEditProduct(null)}
          title={`Edit Product – ${editProduct.name}`}
        />
      )}

      {loading ? <Spinner /> : products.length === 0 ? <EmptyState text="No products found." /> : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th><th>Name</th><th>Category</th><th>Price</th><th>Stock</th><th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {products.map(p => (
                <tr key={p.id}>
                  <td>{p.id}</td>
                  <td>{p.name}</td>
                  <td>{p.category || '—'}</td>
                  <td>${p.price.toFixed(2)}</td>
                  <td>{p.stock}</td>
                  <td className="actions">
                    <button className="btn btn-sm" onClick={() => startEdit(p)}>Edit</button>
                    <button className="btn btn-sm btn-danger" onClick={() => handleDelete(p)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default Products;
