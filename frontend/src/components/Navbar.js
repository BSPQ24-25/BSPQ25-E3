import React from 'react';
import { Link } from 'react-router-dom';

function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-content">
          <div className="navbar-brand">
            <Link to="/" className="navbar-logo">
              Logo
            </Link>
          </div>
          <div className="navbar-links">
            <Link to="/" className="navbar-link">
              Home
            </Link>
            <Link to="/about" className="navbar-link">
              About
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
}

export default Navbar; 