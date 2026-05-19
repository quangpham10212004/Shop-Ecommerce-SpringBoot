import React from 'react';
import { NavLink } from 'react-router-dom';

const Navbar: React.FC = () => (
  <nav className="navbar">
    <div className="navbar-brand">🛒 Shop Admin</div>
    <ul className="navbar-links">
      <li><NavLink to="/" end className={({ isActive }) => isActive ? 'active' : ''}>Dashboard</NavLink></li>
      <li><NavLink to="/users" className={({ isActive }) => isActive ? 'active' : ''}>Users</NavLink></li>
      <li><NavLink to="/products" className={({ isActive }) => isActive ? 'active' : ''}>Products</NavLink></li>
      <li><NavLink to="/orders" className={({ isActive }) => isActive ? 'active' : ''}>Orders</NavLink></li>
    </ul>
  </nav>
);

export default Navbar;
