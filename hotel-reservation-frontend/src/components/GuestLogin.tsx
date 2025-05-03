// src/components/GuestLogin.tsx
import React, { useState } from 'react';
import { Container, Box, TextField, Button, Typography } from '@mui/material';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const GuestLogin: React.FC = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      // Adjust the endpoint and payload as needed based on your back end
      const payload = { email, password };
      const response = await api.post('/login', payload);
      console.log('Login successful:', response.data);
      // You might store a token here before navigating away
      navigate('/dashboard');
    } catch (error: any) {
      console.error('Login error:', error);
      setErrorMessage('Login failed. Please check your credentials.');
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      transition={{ duration: 0.5 }}
    >
      <Container sx={{ my: 4 }}>
        <Typography variant="h4" gutterBottom>
          Guest Login
        </Typography>
        <Box
          component="form"
          onSubmit={handleLogin}
          sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}
        >
          <TextField
            label="Email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            fullWidth
            required
          />
          <TextField
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            fullWidth
            required
          />
          {errorMessage && (
            <Typography variant="body2" color="error">
              {errorMessage}
            </Typography>
          )}
          <Button type="submit" variant="contained" color="primary">
            Log In
          </Button>
        </Box>
      </Container>
    </motion.div>
  );
};

export default GuestLogin;