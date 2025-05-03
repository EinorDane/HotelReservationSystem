import axios from 'axios';

// ✅ Centralized API base URL
const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ✅ Set dynamic authorization headers (for future authentication)
export const setAuthToken = (token: string | null) => {
  if (token) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common['Authorization'];
  }
};

// ✅ Example Reservation interface (adjust properties as needed)
export interface Reservation {
  id: number;
  name: string;
  date: string;
}

// ✅ Function to fetch reservations from the backend
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