// src/main.tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';

// Create your custom Material‑UI theme
const theme = createTheme({
  palette: {
    mode: 'dark', // Use 'dark' for a dark mode theme
    primary: {
      main: '#1976d2', // Customize your primary color
    },
    secondary: {
      main: '#ff4081', // Customize your secondary color
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h1: { fontSize: '2.5rem', fontWeight: 700 },
    h3: { fontWeight: 600, marginBottom: '1rem' },
  },
});

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <App />
    </ThemeProvider>
  </React.StrictMode>
);