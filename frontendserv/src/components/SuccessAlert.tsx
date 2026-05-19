import React from 'react';

interface Props {
  message: string;
}

const SuccessAlert: React.FC<Props> = ({ message }) => (
  <div className="alert alert-success">
    <strong>✓</strong> {message}
  </div>
);

export default SuccessAlert;
