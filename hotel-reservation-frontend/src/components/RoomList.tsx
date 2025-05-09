import React, { useEffect, useState } from 'react';
import {
  Box,
  Button,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  TextField,
  Typography,
  Snackbar,
  CircularProgress,
  Alert,
  MenuItem,
  Card,
  CardContent,
} from '@mui/material';
import { Add, Delete, Edit, Search } from '@mui/icons-material';
import api from '../services/api';

interface Room {
  id: number;
  roomNumber: string;
  type: string;
  price: number;
  status: string;
}

const roomTypes = ['Single', 'Double', 'Suite'];

const RoomList: React.FC = () => {
  const [rooms, setRooms] = useState<Room[]>([]);
  const [search, setSearch] = useState('');
  const [openModal, setOpenModal] = useState(false);
  const [selectedRoom, setSelectedRoom] = useState<Room | null>(null);
  const [formData, setFormData] = useState({
    roomNumber: '',
    type: '',
    price: '',
    status: 'AVAILABLE',
  });
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: 'success' | 'error' }>({ open: false, message: '', severity: 'success' });
  const [formError, setFormError] = useState('');

  useEffect(() => {
    fetchRooms();
  }, []);

  useEffect(() => {
    const filtered = rooms.filter(r =>
      r.roomNumber.toLowerCase().includes(search.toLowerCase()) ||
      r.type.toLowerCase().includes(search.toLowerCase())
    );
    setRooms(filtered);
  }, [search]);

  const fetchRooms = async () => {
    setLoading(true);
    try {
      const res = await api.getRooms();
      setRooms(res.data);
    } catch (err) {
      setSnackbar({ open: true, message: 'Failed to fetch rooms', severity: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (room?: Room) => {
    setFormError('');
    if (room) {
      setSelectedRoom(room);
      setFormData({
        roomNumber: room.roomNumber,
        type: room.type,
        price: room.price.toString(),
        status: room.status,
      });
    } else {
      setSelectedRoom(null);
      setFormData({
        roomNumber: '',
        type: '',
        price: '',
        status: 'AVAILABLE',
      });
    }
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
    setSelectedRoom(null);
  };

  const handleFormChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const validateForm = () => {
    if (!formData.roomNumber.trim() || !formData.type.trim() || !formData.price.trim() || !formData.status.trim()) {
      setFormError('All fields are required and must be valid.');
      return false;
    }
    setFormError('');
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;
    setLoading(true);
    try {
      if (selectedRoom) {
        await api.updateRoom(selectedRoom.id, {
          ...formData,
          price: parseFloat(formData.price),
        });
        setSnackbar({ open: true, message: 'Room updated successfully', severity: 'success' });
      } else {
        await api.addRoom({
          ...formData,
          price: parseFloat(formData.price),
        });
        setSnackbar({ open: true, message: 'Room added successfully', severity: 'success' });
      }
      handleCloseModal();
      fetchRooms();
    } catch (err) {
      setSnackbar({ open: true, message: 'Failed to save room', severity: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    setLoading(true);
    if (deleteId !== null) {
      try {
        await api.deleteRoom(deleteId);
        setSnackbar({ open: true, message: 'Room deleted successfully', severity: 'success' });
        setDeleteId(null);
        setConfirmOpen(false);
        fetchRooms();
      } catch (err) {
        setSnackbar({ open: true, message: 'Failed to delete room', severity: 'error' });
      } finally {
        setLoading(false);
      }
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 8 }}>
      <Typography variant="h4" gutterBottom>Rooms</Typography>
      <Box display="flex" alignItems="center" mb={2}>
        <TextField
          label="Search by number or type"
          value={search}
          onChange={e => setSearch(e.target.value)}
          InputProps={{ endAdornment: <Search /> }}
          sx={{ mr: 2 }}
        />
        <Button variant="contained" startIcon={<Add />} onClick={() => handleOpenModal()}>
          Add Room
        </Button>
      </Box>
      {loading ? (
        <Box display="flex" justifyContent="center" alignItems="center" minHeight={200}>
          <CircularProgress />
        </Box>
      ) : (
        <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 3 }}>
          {rooms.map((room) => (
            <Card key={room.id}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Room {room.roomNumber}
                </Typography>
                <Typography color="textSecondary" gutterBottom>
                  Type: {room.type}
                </Typography>
                <Typography color="textSecondary" gutterBottom>
                  Price: ${room.price}
                </Typography>
                <Typography color="textSecondary" gutterBottom>
                  Status: {room.status}
                </Typography>
                <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end' }}>
                  <IconButton onClick={() => handleOpenModal(room)} color="primary">
                    <Edit />
                  </IconButton>
                  <IconButton onClick={() => { setDeleteId(room.id); setConfirmOpen(true); }} color="error">
                    <Delete />
                  </IconButton>
                </Box>
              </CardContent>
            </Card>
          ))}
        </Box>
      )}

      {/* Add/Edit Modal */}
      <Dialog open={openModal} onClose={handleCloseModal}>
        <DialogTitle>{selectedRoom ? 'Edit Room' : 'Add Room'}</DialogTitle>
        <form onSubmit={handleSubmit}>
          <DialogContent>
            <TextField
              margin="dense"
              label="Room Number"
              name="roomNumber"
              value={formData.roomNumber}
              onChange={handleFormChange}
              fullWidth
              required
            />
            <TextField
              margin="dense"
              label="Type"
              name="type"
              value={formData.type}
              onChange={handleFormChange}
              select
              fullWidth
              required
            >
              {roomTypes.map(type => (
                <MenuItem key={type} value={type}>{type}</MenuItem>
              ))}
            </TextField>
            <TextField
              margin="dense"
              label="Price"
              name="price"
              type="number"
              value={formData.price}
              onChange={handleFormChange}
              fullWidth
              required
            />
            <TextField
              margin="dense"
              label="Status"
              name="status"
              value={formData.status}
              onChange={handleFormChange}
              select
              fullWidth
              required
            >
              <MenuItem value="AVAILABLE">Available</MenuItem>
              <MenuItem value="OCCUPIED">Occupied</MenuItem>
              <MenuItem value="MAINTENANCE">Maintenance</MenuItem>
            </TextField>
            {formError && <Alert severity="error" sx={{ mt: 2 }}>{formError}</Alert>}
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseModal}>Cancel</Button>
            <Button type="submit" variant="contained" disabled={loading}>Save</Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={confirmOpen} onClose={() => setConfirmOpen(false)}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to delete this room?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmOpen(false)}>Cancel</Button>
          <Button onClick={handleDelete} color="error" variant="contained" disabled={loading}>Delete</Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar Notification */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default RoomList; 