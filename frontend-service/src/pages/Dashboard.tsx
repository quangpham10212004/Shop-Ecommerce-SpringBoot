import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { userApi } from '../api/userApi';
import { productApi } from '../api/productApi';
import { orderApi } from '../api/orderApi';

const Dashboard: React.FC = () => {
  const [counts, setCounts] = useState({ users: '—', products: '—', orders: '—' });

  useEffect(() => {
    userApi.getAll().then(r => setCounts(c => ({ ...c, users: String(r.data.length) }))).catch(() => {});
    productApi.getAll().then(r => setCounts(c => ({ ...c, products: String(r.data.length) }))).catch(() => {});
    orderApi.getAll().then(r => setCounts(c => ({ ...c, orders: String(r.data.length) }))).catch(() => {});
  }, []);

  const cards = [
    { label: 'Users',    count: counts.users,    icon: '👤', to: '/users'    },
    { label: 'Products', count: counts.products, icon: '📦', to: '/products' },
    { label: 'Orders',   count: counts.orders,   icon: '📋', to: '/orders'  },
  ];

  return (
    <div className="page">
      <h1>Dashboard</h1>
      <p className="subtitle">Quick overview of your backend data.</p>

      <div className="card-grid">
        {cards.map(card => (
          <Link key={card.label} to={card.to} className="stat-card">
            <span className="stat-icon">{card.icon}</span>
            <div>
              <div className="stat-count">{card.count}</div>
              <div className="stat-label">{card.label}</div>
            </div>
          </Link>
        ))}
      </div>

      <div className="quick-links">
        <h2>Quick Actions</h2>
        <ul>
          <li><Link to="/users">→ Manage Users (CRUD)</Link></li>
          <li><Link to="/products">→ Manage Products (CRUD)</Link></li>
          <li><Link to="/orders">→ Test Order Flow (Create / Get by ID)</Link></li>
        </ul>
      </div>
    </div>
  );
};

export default Dashboard;
