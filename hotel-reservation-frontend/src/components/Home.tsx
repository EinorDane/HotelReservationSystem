import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  Container,
  Typography,
  Paper,
  Card,
  CardContent,
  CardMedia,
} from '@mui/material';
import {
  Hotel as HotelIcon,
  EventAvailable as EventAvailableIcon,
  Person as PersonIcon,
} from '@mui/icons-material';

const Home: React.FC = () => {
  const navigate = useNavigate();

  const features = [
    {
      icon: <HotelIcon sx={{ fontSize: 40 }} />,
      title: 'Luxurious Rooms',
      description: 'Experience comfort in our well-appointed rooms and suites.',
    },
    {
      icon: <EventAvailableIcon sx={{ fontSize: 40 }} />,
      title: 'Easy Booking',
      description: 'Book your stay with our simple and secure reservation system.',
    },
    {
      icon: <PersonIcon sx={{ fontSize: 40 }} />,
      title: 'Personalized Service',
      description: 'Enjoy dedicated service from our professional staff.',
    },
  ];

  return (
    <Box sx={{ width: '100vw', overflowX: 'hidden' }}>
      {/* Hero Section */}
      <Paper
        sx={{
          position: 'relative',
          backgroundColor: 'grey.800',
          color: '#fff',
          mb: 4,
          backgroundSize: 'cover',
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center',
          backgroundImage: 'url(https://source.unsplash.com/random/?hotel)',
          minHeight: '500px',
          display: 'flex',
          alignItems: 'center',
          width: '100vw',
          left: '50%',
          right: '50%',
          transform: 'translateX(-50%)',
          boxSizing: 'border-box',
        }}
      >
        {/* Increase the priority of the hero background image */}
        <Box
          sx={{
            position: 'absolute',
            top: 0,
            bottom: 0,
            right: 0,
            left: 0,
            backgroundColor: 'rgba(0,0,0,.5)',
          }}
        />
        <Container maxWidth="lg" sx={{ position: 'relative', py: 8 }}>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 3 }}>
            <Box sx={{ flexBasis: { xs: '100%', md: '50%' } }}>
              <Typography
                component="h1"
                variant="h2"
                color="inherit"
                gutterBottom
                sx={{ fontWeight: 'bold' }}
              >
                Welcome to Our Hotel
              </Typography>
              <Typography variant="h5" color="inherit" paragraph>
                Experience luxury and comfort at its finest. Book your stay with us today and create
                unforgettable memories.
              </Typography>
              <Box sx={{ mt: 4 }}>
                <Button
                  variant="contained"
                  size="large"
                  onClick={() => navigate('/login')}
                  sx={{ mr: 2 }}
                >
                  Login
                </Button>
                <Button
                  variant="outlined"
                  size="large"
                  onClick={() => navigate('/signup')}
                  sx={{ color: 'white', borderColor: 'white' }}
                >
                  Sign Up
                </Button>
              </Box>
            </Box>
          </Box>
        </Container>
      </Paper>

      {/* Features Section */}
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Typography
          component="h2"
          variant="h3"
          align="center"
          color="text.primary"
          gutterBottom
        >
          Why Choose Us
        </Typography>
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 4, mt: 4 }}>
          {features.map((feature, index) => (
            <Box key={index} sx={{ flexBasis: { xs: '100%', md: 'calc(33.333% - 32px)' } }}>
              <Card
                sx={{
                  height: '100%',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  textAlign: 'center',
                  p: 3,
                }}
              >
                <Box sx={{ color: 'primary.main', mb: 2 }}>{feature.icon}</Box>
                <Typography gutterBottom variant="h5" component="h3">
                  {feature.title}
                </Typography>
                <Typography color="text.secondary">{feature.description}</Typography>
              </Card>
            </Box>
          ))}
        </Box>
      </Container>

      {/* Image Gallery */}
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Typography
          component="h2"
          variant="h3"
          align="center"
          color="text.primary"
          gutterBottom
        >
          Our Facilities
        </Typography>
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mt: 4 }}>
          {[1, 2, 3, 4].map((item) => (
            <Box key={item} sx={{ flexBasis: { xs: '100%', sm: 'calc(50% - 8px)', md: 'calc(25% - 12px)' } }}>
              <Card>
                <CardMedia
                  component="img"
                  height="200"
                  image={`https://source.unsplash.com/random/?hotel,room${item}`}
                  alt={`Hotel Image ${item}`}
                />
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {item === 1
                      ? 'Luxury Suites'
                      : item === 2
                      ? 'Fine Dining'
                      : item === 3
                      ? 'Spa & Wellness'
                      : 'Conference Rooms'}
                  </Typography>
                </CardContent>
              </Card>
            </Box>
          ))}
        </Box>
      </Container>
    </Box>
  );
};

export default Home; 