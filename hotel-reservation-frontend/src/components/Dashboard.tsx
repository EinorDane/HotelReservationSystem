import React, { useEffect, useState } from 'react';
import { Box, Typography, Card, CardContent, CircularProgress, Alert } from '@mui/material';
import api from '../services/api';

interface Room {
  id: number;
  roomNumber: string;
  type: string;
  price: number;
  status: string;
}

interface Reservation {
  reservationId: number;
  guestName: string;
  roomNumber: string;
  checkInDate: string;
  checkOutDate: string;
  numberOfGuests: number;
  totalCost: number;
}

const Dashboard: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [rooms, setRooms] = useState<Room[]>([]);
  const [reservations, setReservations] = useState<Reservation[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [roomsRes, reservationsRes] = await Promise.all([
          api.getRooms(),
          api.getReservations()
        ]);
        setRooms(roomsRes.data);
        setReservations(reservationsRes.data);
        setError(null);
      } catch (err) {
        setError('Failed to fetch dashboard data');
        console.error('Dashboard data fetch error:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const stats = [
    { 
      title: 'Total Rooms', 
      value: rooms.length.toString() 
    },
    { 
      title: 'Available Rooms', 
      value: rooms.filter(room => room.status === 'AVAILABLE').length.toString() 
    },
    { 
      title: 'Occupied Rooms', 
      value: rooms.filter(room => room.status === 'OCCUPIED').length.toString() 
    },
    { 
      title: 'Total Reservations', 
      value: reservations.length.toString() 
    },
  ];

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Dashboard
      </Typography>
      <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: 3 }}>
        {stats.map((stat, index) => (
          <Card key={index}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {stat.title}
              </Typography>
              <Typography variant="h4">
                {stat.value}
              </Typography>
            </CardContent>
          </Card>
        ))}
      </Box>
    </Box>
  );
};

export default Dashboard; 