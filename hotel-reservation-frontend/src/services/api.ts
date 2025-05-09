import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
});

const api = {
  login: (username: string, password: string) =>
    instance.post('/auth/login', { username, password }),
  signup: (username: string, password: string, role: string) =>
    instance.post('/auth/signup', { username, password, role }),
  getGuests: () => instance.get('/guests'),
  addGuest: (guest: any) => instance.post('/guests', guest),
  updateGuest: (id: number, guest: any) => instance.put(`/guests/${id}`, guest),
  deleteGuest: (id: number) => instance.delete(`/guests/${id}`),
  getRooms: () => instance.get('/rooms'),
  addRoom: (room: any) => instance.post('/rooms', room),
  updateRoom: (id: number, room: any) => instance.put(`/rooms/${id}`, room),
  deleteRoom: (id: number) => instance.delete(`/rooms/${id}`),
  getReservations: () => instance.get('/reservations'),
  addReservation: (reservation: any) => instance.post('/reservations', reservation),
  updateReservation: (id: number, reservation: any) => instance.put(`/reservations/${id}`, reservation),
  deleteReservation: (id: number) => instance.delete(`/reservations/${id}`),
};

export default api; 