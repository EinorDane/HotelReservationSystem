import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../../contexts/AuthContext';
import {
  Box,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
} from '@mui/material';
import {
  Dashboard as DashboardIcon,
  People as PeopleIcon,
  Hotel as HotelIcon,
  EventNote as EventNoteIcon,
  Logout as LogoutIcon,
} from '@mui/icons-material';

const Sidenav: React.FC = () => {
  const { user, logout, hasRole } = useAuth();

  const handleLogout = () => {
    logout();
  };

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: 240,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: 240,
          boxSizing: 'border-box',
        },
      }}
    >
      <Box sx={{ overflow: 'auto', mt: 8 }}>
        <List>
          {hasRole('staff') && (
            <>
              <ListItem component={Link} to="/dashboard">
                <ListItemIcon>
                  <DashboardIcon />
                </ListItemIcon>
                <ListItemText primary="Dashboard" />
              </ListItem>
              <ListItem component={Link} to="/guests">
                <ListItemIcon>
                  <PeopleIcon />
                </ListItemIcon>
                <ListItemText primary="Guests" />
              </ListItem>
              <ListItem component={Link} to="/rooms">
                <ListItemIcon>
                  <HotelIcon />
                </ListItemIcon>
                <ListItemText primary="Rooms" />
              </ListItem>
            </>
          )}
          <ListItem component={Link} to="/reservations">
            <ListItemIcon>
              <EventNoteIcon />
            </ListItemIcon>
            <ListItemText primary="Reservations" />
          </ListItem>
        </List>
        <Divider />
        <List>
          <ListItem onClick={handleLogout}>
            <ListItemIcon>
              <LogoutIcon />
            </ListItemIcon>
            <ListItemText primary="Logout" />
          </ListItem>
        </List>
      </Box>
    </Drawer>
  );
};

export default Sidenav; 