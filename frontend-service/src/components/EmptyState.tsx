import React from 'react';

interface Props {
  text?: string;
}

const EmptyState: React.FC<Props> = ({ text = 'No data found.' }) => (
  <div className="empty-state">
    <span>📭</span>
    <p>{text}</p>
  </div>
);

export default EmptyState;
