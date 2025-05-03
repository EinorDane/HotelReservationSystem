import React, { useState } from 'react';
import {
  Container,
  Box,
  TextField,
  Button,
  Typography,
  Paper,
  IconButton
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const Signup: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState(false); // ✅ Added loading state

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      setErrorMessage('Passwords do not match');
      return;
    }

    setLoading(true); // ✅ Indicate that signup is in progress
    try {
      const payload = { username, password, role: 'guest' };
      const response = await api.post('/users/signup', payload); // ✅ Removed `/api` (already set in `api.ts`)
      
      console.log('Signup successful:', response.data);
      navigate('/dashboard'); // ✅ Navigate after signup
    } catch (error: any) {
      console.error('Signup error:', error);
      setErrorMessage(error.response?.data?.message || 'Signup failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      transition={{ duration: 0.5 }}
    >
      <Container maxWidth="sm" sx={{ mt: 8, mb: 8 }}>
        <Paper sx={{ p: 4, position: 'relative' }} elevation={3}>
          {/* Back Button */}
          <IconButton
            onClick={() => navigate(-1)}
            aria-label="Go back"
            sx={{ position: 'absolute', top: 8, left: 8 }}
          >
            <ArrowBackIcon />
          </IconButton>

          <Typography variant="h4" align="center" gutterBottom>
            Create New Account
          </Typography>

          <Box
            component="form"
            onSubmit={handleSignup}
            sx={{
              mt: 2,
              display: 'flex',
              flexDirection: 'column',
              gap: 2
            }}
          >
            <TextField
              label="Username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              fullWidth
            />
            <TextField
              label="Password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              fullWidth
            />
            <TextField
              label="Confirm Password"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
              fullWidth
            />
            {errorMessage && (
              <Typography variant="body2" color="error">
                {errorMessage}
              </Typography>
            )}
            <Button 
              type="submit" 
              variant="contained" 
              color="primary" 
              fullWidth 
              disabled={loading} // ✅ Disable button during loading
            >
              {loading ? 'Signing Up...' : 'Sign Up'}
            </Button>
          </Box>
        </Paper>
      </Container>
    </motion.div>
  );
};

export default Signup;