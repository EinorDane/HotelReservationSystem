import React from 'react';
import { useNavigate } from 'react-router-dom';
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import { motion } from 'framer-motion';

const LandingPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ duration: 1 }}
    >
      <Box
        sx={{
          backgroundImage: 'url(https://your-image-url.com/hotel-background.jpg)',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          minHeight: '80vh',
          position: 'relative',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          '&:before': {
            content: '""',
            position: 'absolute',
            width: '100%',
            height: '100%',
            top: 0,
            left: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
          },
        }}
      >
        <Container
          sx={{
            position: 'relative',
            zIndex: 1,
            textAlign: 'center',
            color: '#fff',
          }}
        >
          <motion.div
            initial={{ y: -20, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            transition={{ delay: 0.5, duration: 1 }}
          >
            <Typography variant="h3" component="h1" gutterBottom>
              Welcome to Our Hotel!
            </Typography>
          </motion.div>
          <motion.div
            initial={{ y: 20, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            transition={{ delay: 0.7, duration: 1 }}
          >
            <Typography variant="h6" component="p" gutterBottom>
              Experience a seamless booking process with our system.
            </Typography>
          </motion.div>
          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 4 }}>
            <motion.div whileHover={{ scale: 1.05 }}>
              <Button
                variant="contained"
                color="primary"
                onClick={() => navigate('/login')}
              >
                Guest Login
              </Button>
            </motion.div>
            <motion.div whileHover={{ scale: 1.05 }}>
              <Button
                variant="outlined"
                color="primary"
                onClick={() => navigate('/signup')}
              >
                Create New Account
              </Button>
            </motion.div>
          </Box>
          <Box sx={{ mt: 2 }}>
            <motion.div whileHover={{ scale: 1.05 }}>
              <Button
                variant="text"
                color="secondary"
                onClick={() => navigate('/staff-login')}
              >
                Staff Login
              </Button>
            </motion.div>
          </Box>
        </Container>
      </Box>
    </motion.div>
  );
};

export default LandingPage;