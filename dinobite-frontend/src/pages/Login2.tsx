import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Login.css';
import { useUserStore } from "@/store/UserStore";
import axios from 'axios';

const Login2 = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const setUser = useUserStore((state) => state.setUser);
  const clearUser = useUserStore((state) => state.clearUser);
  const navigate = useNavigate();

  const [forgotMode, setForgotMode] = useState(false);
  const [resetEmail, setResetEmail] = useState('');
  const [codeSent, setCodeSent] = useState(false);
  const [resetCode, setResetCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [resetMessage, setResetMessage] = useState('');


  const fetchUserDetails = async (userType: string, userId: number) => {
    const endpoints: Record<string, string> = {
      CUSTOMER: 'customers',
      COURIER: 'couriers',
      RESTAURANT: 'restaurants',
    };

    const res = await axios.get(`http://localhost:9090/api/v1/${endpoints[userType]}/users/${userId}`);
    return res.data;
  };

  const handleLogout = async () => {
    try {
      await fetch('http://localhost:9090/api/auth/logout', { method: 'POST', credentials: 'include' });
      clearUser();
      navigate('/login');
    } catch (err) {
      console.error("Logout failed", err);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      const response = await fetch('http://localhost:9090/api/auth/login', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ mail: email, password }),
      });

      const data = await response.json();
      if (!response.ok) return setError(data.message || 'Email or password is incorrect');

      const { userId, userName, userType, mail } = data;
      let userPayload;

      if (userType === 'ADMIN') {
        userPayload = {
          id: userId,
          name: userName,
          mail,
          userType,
        };
      } else {
        const detail = await fetchUserDetails(userType, userId);

        userPayload = {
          id: userId,
          name: userName,
          mail,
          userType,
          [`${userType.toLowerCase()}Id`]: detail.id,
        };
      }

      setUser(userPayload);
      alert('Login successful');

      const redirects: Record<string, string> = {
        CUSTOMER: '/',
        RESTAURANT: '/restaurant-dashboard',
        COURIER: `/courier-profile/${userPayload.id}`,
        ADMIN: '/admin-dashboard',
      };

      navigate(redirects[userType]);
    } catch (err) {
      console.error('Login error', err);
      setError('Something went wrong.');
      handleLogout();
    }
  };

 const sendResetCode = async (email: string) => {
  try {
    await axios.post(`http://localhost:9090/api/auth/forgot-password`, { email });
    setCodeSent(true);
    setResetMessage('Code sent to your email.');
  } catch (err: any) {
    const message = err.response?.data?.message || 'Email not found.';
    setResetMessage(message);
  }
};

const resetPassword = async (email: string, code: string, newPassword: string) => {
  try {
    await axios.post(`http://localhost:9090/api/auth/reset-password`, {
      email,
      code,
      newPassword
    });
    setResetMessage('Password reset successful. Please log in.');
    setForgotMode(false);
    setCodeSent(false);
  } catch (err: any) {
    const message = err.response?.data?.message || 'Wrong code. Try again or request new code.';
    setResetMessage(message);
  }
};

  return (
    <div className="login-container">
      <form className="login-form" onSubmit={handleSubmit}>
        <h2 style={{
          textAlign: 'center',
          fontSize: '2rem',
          color: '#ff6600',
          textShadow: '1px 1px 2px black',
          marginBottom: '1rem'
        }}>Login</h2>

        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <div className="password-wrapper">
          <input
            type={showPassword ? "text" : "password"}
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <span
            onClick={() => setShowPassword(!showPassword)}
            className="toggle-password"
            role="button"
            aria-label={showPassword ? "Hide password" : "Show password"}
          >
            {showPassword ? "🙈" : "👁️"}
          </span>
        </div>

        {error && <p className="error-text">{error}</p>}
        <button type="submit">Login</button>

        <p className="forgot-password" onClick={() => setForgotMode(true)}>
          I forgot my password 🐟
        </p>
        {forgotMode && (
          <div className="reset-password-box">
            {!codeSent ? (
              <>
                <input
                  type="email"
                  placeholder="Enter your email"
                  value={resetEmail}
                  onChange={(e) => setResetEmail(e.target.value)}
                />
                <button type="button" onClick={() => sendResetCode(resetEmail)}>
                  Send Code
                </button>
              </>
            ) : (
              <>
                <input
                  type="text"
                  placeholder="Enter the code"
                  value={resetCode}
                  onChange={(e) => setResetCode(e.target.value)}
                />
                <input
                  type="password"
                  placeholder="Enter new password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                />
                <button type="button" onClick={() => resetPassword(resetEmail, resetCode, newPassword)}>
                  Save
                </button>
              </>
            )}
            {resetMessage && <p className="info-text">{resetMessage}</p>}
          </div>
        )}
      </form>

    </div>
  );
};

export default Login2;
