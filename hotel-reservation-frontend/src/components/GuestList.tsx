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
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
  Tooltip,
  Snackbar,
  CircularProgress,
  Alert,
} from '@mui/material';
import { Add, Delete, Edit, Search } from '@mui/icons-material';
import api from '../services/api';

interface Guest {
  guestId: number;
  guestName: string;
  address: string;
  phoneNumber: string;
  emailAddress: string;
}

const GuestList: React.FC = () => {
  const [guests, setGuests] = useState<Guest[]>([]);
  const [search, setSearch] = useState('');
  const [filteredGuests, setFilteredGuests] = useState<Guest[]>([]);
  const [openModal, setOpenModal] = useState(false);
  const [editingGuest, setEditingGuest] = useState<Guest | null>(null);
  const [form, setForm] = useState<Omit<Guest, 'guestId'>>({
    guestName: '',
    address: '',
    phoneNumber: '',
    emailAddress: '',
  });
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: 'success' | 'error' }>({ open: false, message: '', severity: 'success' });
  const [formError, setFormError] = useState('');

  useEffect(() => {
    fetchGuests();
  }, []);

  useEffect(() => {
    setFilteredGuests(
      guests.filter(g =>
        g.guestName.toLowerCase().includes(search.toLowerCase()) ||
        g.emailAddress.toLowerCase().includes(search.toLowerCase())
      )
    );
  }, [search, guests]);

  const fetchGuests = async () => {
    setLoading(true);
    try {
      const res = await api.getGuests();
      setGuests(res.data);
    } catch (err) {
      setSnackbar({ open: true, message: 'Failed to fetch guests', severity: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (guest?: Guest) => {
    setFormError('');
    if (guest) {
      setEditingGuest(guest);
      setForm({
        guestName: guest.guestName,
        address: guest.address,
        phoneNumber: guest.phoneNumber,
        emailAddress: guest.emailAddress,
      });
    } else {
      setEditingGuest(null);
      setForm({ guestName: '', address: '', phoneNumber: '', emailAddress: '' });
    }
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
  };

  const handleFormChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const validateForm = () => {
    if (!form.guestName.trim() || !form.emailAddress.trim()) {
      setFormError('Name and Email are required.');
      return false;
    }
    setFormError('');
    return true;
  };

  const handleSave = async () => {
    if (!validateForm()) return;
    setLoading(true);
    try {
      if (editingGuest) {
        await api.updateGuest(editingGuest.guestId, form);
        setSnackbar({ open: true, message: 'Guest updated successfully', severity: 'success' });
      } else {
        await api.addGuest(form);
        setSnackbar({ open: true, message: 'Guest added successfully', severity: 'success' });
      }
      setOpenModal(false);
      fetchGuests();
    } catch (err) {
      setSnackbar({ open: true, message: 'Failed to save guest', severity: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    setLoading(true);
    if (deleteId !== null) {
      try {
        await api.deleteGuest(deleteId);
        setSnackbar({ open: true, message: 'Guest deleted successfully', severity: 'success' });
        setDeleteId(null);
        setConfirmOpen(false);
        fetchGuests();
      } catch (err) {
        setSnackbar({ open: true, message: 'Failed to delete guest', severity: 'error' });
      } finally {
        setLoading(false);
      }
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 8 }}>
      <Typography variant="h4" gutterBottom>Guests</Typography>
      <Box display="flex" alignItems="center" mb={2}>
        <TextField
          label="Search by name or email"
          value={search}
          onChange={e => setSearch(e.target.value)}
          InputProps={{ endAdornment: <Search /> }}
          sx={{ mr: 2 }}
        />
        <Button variant="contained" startIcon={<Add />} onClick={() => handleOpenModal()}>
          Add Guest
        </Button>
      </Box>
      {loading ? (
        <Box display="flex" justifyContent="center" alignItems="center" minHeight={200}>
          <CircularProgress />
        </Box>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Name</TableCell>
                <TableCell>Address</TableCell>
                <TableCell>Phone</TableCell>
                <TableCell>Email</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredGuests.map(guest => (
                <TableRow key={guest.guestId}>
                  <TableCell>{guest.guestName}</TableCell>
                  <TableCell>{guest.address}</TableCell>
                  <TableCell>{guest.phoneNumber}</TableCell>
                  <TableCell>{guest.emailAddress}</TableCell>
                  <TableCell align="right">
                    <Tooltip title="Edit">
                      <IconButton onClick={() => handleOpenModal(guest)}><Edit /></IconButton>
                    </Tooltip>
                    <Tooltip title="Delete">
                      <IconButton color="error" onClick={() => { setDeleteId(guest.guestId); setConfirmOpen(true); }}><Delete /></IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Add/Edit Modal */}
      <Dialog open={openModal} onClose={handleCloseModal}>
        <DialogTitle>{editingGuest ? 'Edit Guest' : 'Add Guest'}</DialogTitle>
        <DialogContent>
          <TextField
            margin="dense"
            label="Name"
            name="guestName"
            value={form.guestName}
            onChange={handleFormChange}
            fullWidth
            required
          />
          <TextField
            margin="dense"
            label="Address"
            name="address"
            value={form.address}
            onChange={handleFormChange}
            fullWidth
          />
          <TextField
            margin="dense"
            label="Phone Number"
            name="phoneNumber"
            value={form.phoneNumber}
            onChange={handleFormChange}
            fullWidth
          />
          <TextField
            margin="dense"
            label="Email Address"
            name="emailAddress"
            value={form.emailAddress}
            onChange={handleFormChange}
            fullWidth
            required
          />
          {formError && <Alert severity="error" sx={{ mt: 2 }}>{formError}</Alert>}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseModal}>Cancel</Button>
          <Button onClick={handleSave} variant="contained" disabled={loading}>Save</Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={confirmOpen} onClose={() => setConfirmOpen(false)}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to delete this guest?</Typography>
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

export default GuestList; 