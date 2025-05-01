import React, { useEffect, useState } from 'react';
import { fetchReservations, Reservation } from '../services/api'; // Import Reservation type

const Reservations: React.FC = () => {
  const [reservations, setReservations] = useState<Reservation[]>([]);

  useEffect(() => {
    const loadReservations = async () => {
      const data = await fetchReservations();
      if (data) setReservations(data);
    };

    loadReservations();
  }, []);

  return (
    <div>
      <h1>Reservations</h1>
      <ul>
        {reservations.length > 0 ? (
          reservations.map((res) => (
            <li key={res.id}>
              {res.name} - {res.date}
            </li>
          ))
        ) : (
          <p>No reservations found.</p>
        )}
      </ul>
    </div>
  );
};

export default Reservations;