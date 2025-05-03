// src/components/LandingPage.tsx
import React from 'react';
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import { useNavigate } from 'react-router-dom';

const LandingPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <Container maxWidth="sm">
      <Box sx={{ textAlign: 'center', my: 4 }}>
        <Typography variant="h3" component="h1" gutterBottom>
          Welcome to Our Hotel!
        </Typography>
        <Typography variant="h6" component="p" gutterBottom>
          Experience a seamless booking process with our system.
        </Typography>
        <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 4 }}>
          <Button 
            variant="contained" 
            color="primary" 
            onClick={() => navigate('/login')}
          >
            Guest Login
          </Button>
          <Button 
            variant="outlined" 
            color="primary" 
            onClick={() => navigate('/signup')}
          >
            Create New Account
          </Button>
        </Box>
        <Box sx={{ mt: 2 }}>
          <Button 
            variant="text" 
            color="secondary" 
            onClick={() => navigate('/staff-login')}
          >
            Staff Login
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default LandingPage;