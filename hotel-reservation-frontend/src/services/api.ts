import axios from 'axios';

const API_BASE_URL = 'http://localhost:5000/api'; // Adjust based on your backend

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export interface Reservation {
  id: number;
  name: string;
  date: string;
}

export const fetchReservations = async (): Promise<Reservation[]> => {
  try {
    const response = await api.get('/reservations'); 
    return response.data;
  } catch (error) {
    console.error('Error fetching reservations:', error);
    return [];
  }
};

export default api;