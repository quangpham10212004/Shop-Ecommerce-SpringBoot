import React from 'react';
import type { ApiError } from '../types';

interface Props {
  error: ApiError | null;
}

const ErrorAlert: React.FC<Props> = ({ error }) => {
  if (!error) return null;
  return (
    <div className="alert alert-error">
      <strong>Error:</strong> {error.message}
      {error.errors && (
        <ul className="error-list">
          {Object.entries(error.errors).map(([field, msg]) => (
            <li key={field}>
              <b>{field}:</b> {msg}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ErrorAlert;
