import React from 'react';

interface Props {
  message?: string;
}

const Spinner: React.FC<Props> = ({ message = 'Loading…' }) => (
  <div className="spinner-wrap">
    <div className="spinner" />
    <span>{message}</span>
  </div>
);

export default Spinner;
