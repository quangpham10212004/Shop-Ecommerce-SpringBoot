import React, { useEffect, useState } from 'react';
import { userApi } from '../api/userApi';
import type { User, CreateUserRequest, UpdateUserRequest, ApiError } from '../types';
import Spinner from '../components/Spinner';
import ErrorAlert from '../components/ErrorAlert';
import SuccessAlert from '../components/SuccessAlert';
import EmptyState from '../components/EmptyState';

const EMPTY_CREATE: CreateUserRequest = {
  username: '', email: '', password: '', fullName: '', phone: '', role: 'USER',
};

const Users: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);
  const [success, setSuccess] = useState('');
  const [search, setSearch] = useState('');

  // Form state
  const [showCreate, setShowCreate] = useState(false);
  const [createForm, setCreateForm] = useState<CreateUserRequest>(EMPTY_CREATE);

  const [editUser, setEditUser] = useState<User | null>(null);
  const [editForm, setEditForm] = useState<UpdateUserRequest>({});

  // ── Fetch ───────────────────────────────────────────────────────────────────
  const fetchUsers = async (q?: string) => {
    setLoading(true);
    setError(null);
    try {
      const res = await userApi.getAll(q);
      setUsers(res.data);
    } catch (e: any) {
      setError(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchUsers(); }, []);

  const notify = (msg: string) => {
    setSuccess(msg);
    setTimeout(() => setSuccess(''), 3000);
  };

  // ── Create ──────────────────────────────────────────────────────────────────
  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      await userApi.create(createForm);
      notify('User created!');
      setCreateForm(EMPTY_CREATE);
      setShowCreate(false);
      fetchUsers();
    } catch (e: any) { setError(e); }
  };

  // ── Edit ────────────────────────────────────────────────────────────────────
  const startEdit = (u: User) => {
    setEditUser(u);
    setEditForm({ email: u.email, fullName: u.fullName, phone: u.phone, role: u.role });
  };

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editUser) return;
    setError(null);
    try {
      await userApi.update(editUser.id, editForm);
      notify('User updated!');
      setEditUser(null);
      fetchUsers();
    } catch (e: any) { setError(e); }
  };

  // ── Delete ──────────────────────────────────────────────────────────────────
  const handleDelete = async (u: User) => {
    if (!window.confirm(`Delete user "${u.username}"?`)) return;
    setError(null);
    try {
      await userApi.delete(u.id);
      notify('User deleted!');
      fetchUsers();
    } catch (e: any) { setError(e); }
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1>Users</h1>
        <button className="btn btn-primary" onClick={() => { setShowCreate(!showCreate); setError(null); }}>
          {showCreate ? '✕ Cancel' : '+ New User'}
        </button>
      </div>

      <ErrorAlert error={error} />
      {success && <SuccessAlert message={success} />}

      {/* Search */}
      <div className="search-bar">
        <input
          placeholder="Search by keyword…"
          value={search}
          onChange={e => setSearch(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && fetchUsers(search)}
        />
        <button className="btn" onClick={() => fetchUsers(search)}>Search</button>
        <button className="btn btn-ghost" onClick={() => { setSearch(''); fetchUsers(); }}>Reset</button>
      </div>

      {/* Create form */}
      {showCreate && (
        <form className="card form-card" onSubmit={handleCreate}>
          <h2>Create User</h2>
          <div className="form-row">
            <label>Username *</label>
            <input required value={createForm.username} onChange={e => setCreateForm({ ...createForm, username: e.target.value })} />
          </div>
          <div className="form-row">
            <label>Full Name *</label>
            <input required value={createForm.fullName} onChange={e => setCreateForm({ ...createForm, fullName: e.target.value })} />
          </div>
          <div className="form-row">
            <label>Email *</label>
            <input required type="email" value={createForm.email} onChange={e => setCreateForm({ ...createForm, email: e.target.value })} />
          </div>
          <div className="form-row">
            <label>Password *</label>
            <input required type="password" value={createForm.password} onChange={e => setCreateForm({ ...createForm, password: e.target.value })} />
          </div>
          <div className="form-row">
            <label>Phone</label>
            <input value={createForm.phone} onChange={e => setCreateForm({ ...createForm, phone: e.target.value })} />
          </div>
          <div className="form-row">
            <label>Role</label>
            <select value={createForm.role} onChange={e => setCreateForm({ ...createForm, role: e.target.value })}>
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-primary">Create</button>
            <button type="button" className="btn btn-ghost" onClick={() => setShowCreate(false)}>Cancel</button>
          </div>
        </form>
      )}

      {/* Edit form */}
      {editUser && (
        <form className="card form-card" onSubmit={handleUpdate}>
          <h2>Edit User – <em>{editUser.username}</em></h2>
          <div className="form-row">
            <label>Full Name</label>
            <input value={editForm.fullName || ''} onChange={e => setEditForm({ ...editForm, fullName: e.target.value })} />
          </div>
          <div className="form-row">
            <label>Email</label>
            <input type="email" value={editForm.email || ''} onChange={e => setEditForm({ ...editForm, email: e.target.value })} />
          </div>
          <div className="form-row">
            <label>Phone</label>
            <input value={editForm.phone || ''} onChange={e => setEditForm({ ...editForm, phone: e.target.value })} />
          </div>
          <div className="form-row">
            <label>Role</label>
            <select value={editForm.role || 'USER'} onChange={e => setEditForm({ ...editForm, role: e.target.value })}>
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-primary">Save</button>
            <button type="button" className="btn btn-ghost" onClick={() => setEditUser(null)}>Cancel</button>
          </div>
        </form>
      )}

      {/* Table */}
      {loading ? <Spinner /> : users.length === 0 ? <EmptyState text="No users found." /> : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th><th>Username</th><th>Full Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map(u => (
                <tr key={u.id}>
                  <td>{u.id}</td>
                  <td>{u.username}</td>
                  <td>{u.fullName}</td>
                  <td>{u.email}</td>
                  <td>{u.phone || '—'}</td>
                  <td><span className="badge">{u.role || '—'}</span></td>
                  <td className="actions">
                    <button className="btn btn-sm" onClick={() => startEdit(u)}>Edit</button>
                    <button className="btn btn-sm btn-danger" onClick={() => handleDelete(u)}>Delete</button>
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

export default Users;
