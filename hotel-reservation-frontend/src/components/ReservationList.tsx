import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import {
  Box,
  Paper,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  CircularProgress,
  Alert,
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';

interface Room {
  id: number;
  number: string;
  type: string;
  price: number;
  status: string;
}

interface Reservation {
  id: number;
  guestId: number;
  roomId: number;
  checkIn: string;
  checkOut: string;
  status: string;
  room?: Room;
}

interface User {
  id: number;
  username: string;
  role: string;
}

const ReservationList: React.FC = () => {
  const { user, hasRole } = useAuth();
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [rooms, setRooms] = useState<Room[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [newReservation, setNewReservation] = useState({
    roomId: '',
    checkIn: '',
    checkOut: '',
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [reservationsResponse, roomsResponse] = await Promise.all([
        api.getReservations(),
        api.getRooms(),
      ]);
      setReservations(reservationsResponse.data);
      setRooms(roomsResponse.data.filter((room: Room) => room.status === 'available'));
    } catch (err) {
      setError('Failed to fetch data');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateReservation = async () => {
    try {
      const currentUser = user as User;
      await api.addReservation({
        guestId: currentUser.id,
        roomId: parseInt(newReservation.roomId),
        checkIn: newReservation.checkIn,
        checkOut: newReservation.checkOut,
        status: 'pending',
      });
      setOpenDialog(false);
      fetchData();
    } catch (err) {
      setError('Failed to create reservation');
      console.error(err);
    }
  };

  const handleUpdateStatus = async (id: number, status: string) => {
    try {
      await api.updateReservation(id, { status });
      fetchData();
    } catch (err) {
      setError('Failed to update reservation status');
      console.error(err);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box p={3}>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  return (
    <Box p={3}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Reservations</Typography>
        {hasRole('guest') && (
          <Button
            variant="contained"
            color="primary"
            startIcon={<AddIcon />}
            onClick={() => setOpenDialog(true)}
          >
            New Reservation
          </Button>
        )}
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Room</TableCell>
              <TableCell>Check In</TableCell>
              <TableCell>Check Out</TableCell>
              <TableCell>Status</TableCell>
              {hasRole('staff') && <TableCell>Actions</TableCell>}
            </TableRow>
          </TableHead>
          <TableBody>
            {reservations.map((reservation) => (
              <TableRow key={reservation.id}>
                <TableCell>{reservation.room?.number}</TableCell>
                <TableCell>{new Date(reservation.checkIn).toLocaleDateString()}</TableCell>
                <TableCell>{new Date(reservation.checkOut).toLocaleDateString()}</TableCell>
                <TableCell>{reservation.status}</TableCell>
                {hasRole('staff') && (
                  <TableCell>
                    {reservation.status === 'pending' && (
                      <>
                        <Button
                          size="small"
                          color="primary"
                          onClick={() => handleUpdateStatus(reservation.id, 'confirmed')}
                        >
                          Confirm
                        </Button>
                        <Button
                          size="small"
                          color="error"
                          onClick={() => handleUpdateStatus(reservation.id, 'cancelled')}
                        >
                          Cancel
                        </Button>
                      </>
                    )}
                  </TableCell>
                )}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
        <DialogTitle>New Reservation</DialogTitle>
        <DialogContent>
          <Box display="flex" flexDirection="column" gap={2} mt={1}>
            <TextField
              select
              label="Room"
              value={newReservation.roomId}
              onChange={(e) => setNewReservation({ ...newReservation, roomId: e.target.value })}
              fullWidth
            >
              {rooms.map((room) => (
                <MenuItem key={room.id} value={room.id}>
                  {room.number} - {room.type} (${room.price}/night)
                </MenuItem>
              ))}
            </TextField>
            <TextField
              type="date"
              label="Check In"
              value={newReservation.checkIn}
              onChange={(e) => setNewReservation({ ...newReservation, checkIn: e.target.value })}
              fullWidth
              InputLabelProps={{ shrink: true }}
            />
            <TextField
              type="date"
              label="Check Out"
              value={newReservation.checkOut}
              onChange={(e) => setNewReservation({ ...newReservation, checkOut: e.target.value })}
              fullWidth
              InputLabelProps={{ shrink: true }}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={handleCreateReservation} color="primary">
            Create
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default ReservationList; 