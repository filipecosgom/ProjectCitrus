import React, { useState } from 'react';
import citrusLogo from "../../assets/logos/citrus-logo_final.png";
import './Login2.css';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [language, setLanguage] = useState('English');

  const handleSubmit = (e) => {
    e.preventDefault();
    // Your login logic here
    console.log({ email, password });
  };

  return (
    <div className="login-container d-flex">
      {/* Left Panel - Just for background/logo */}
      <div className="left-panel">
        <img
            src={citrusLogo}
            alt="CITRUS Logo"
            className="login-logo"
            width={280}
            style={{ marginTop: 60 }}
          />
          <h1 className="logo-title">Sign in to your CITRUS account</h1>
      </div>

      {/* Right Panel - Login Form */}
      <div className="right-panel">
        <form onSubmit={handleSubmit} className="login-form">
          <h1 className="login-title">Login</h1>
          <p className="login-subtitle">Sign in to your account</p>
          <div className="form-group">
            <label htmlFor="email">Email Address</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <a href="/forgot-password" className="forgot-password">Forgot your password?</a>
          </div>

          <button type="submit" className="signin-button">Sign in</button>

          <div className="right-aligned-group">
            <p className="signup-text">
              Don't have an account yet? <a href="/signup">Join CITRUS today</a>
            </p>
            <select 
              value={language} 
              onChange={(e) => setLanguage(e.target.value)}
              className="language-selector"
            >
              <option value="English">English</option>
              <option value="Spanish">Spanish</option>
              <option value="French">French</option>
            </select>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;