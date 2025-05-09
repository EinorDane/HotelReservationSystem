import type { ElementType } from 'react';
import {
  Home as HomeIcon,
  Dashboard as DashboardIcon,
  People as PeopleIcon,
  Hotel as HotelIcon,
  EventAvailable as EventAvailableIcon,
} from '@mui/icons-material';

interface Route {
  key: string;
  title: string;
  path: string;
  icon: ElementType;
  requiresAuth: boolean;
  allowedRoles: string[];
}

const routes: Route[] = [
  {
    key: 'home',
    title: 'Home',
    path: '/',
    icon: HomeIcon,
    requiresAuth: false,
    allowedRoles: ['guest', 'staff'],
  },
  {
    key: 'dashboard',
    title: 'Dashboard',
    path: '/dashboard',
    icon: DashboardIcon,
    requiresAuth: true,
    allowedRoles: ['staff'],
  },
  {
    key: 'guests',
    title: 'Guests',
    path: '/guests',
    icon: PeopleIcon,
    requiresAuth: true,
    allowedRoles: ['staff'],
  },
  {
    key: 'rooms',
    title: 'Rooms',
    path: '/rooms',
    icon: HotelIcon,
    requiresAuth: true,
    allowedRoles: ['staff'],
  },
  {
    key: 'reservations',
    title: 'Reservations',
    path: '/reservations',
    icon: EventAvailableIcon,
    requiresAuth: true,
    allowedRoles: ['staff', 'guest'],
  },
];

export default routes; 