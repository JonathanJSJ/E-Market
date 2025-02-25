import React from 'react';
import { Button } from '@mui/material';
import { ButtonProps } from '@/types/buttons';

const ButtonLogin: React.FC<ButtonProps> = ({ onClick, disabledLoading, label, type, className }) => {
  return (
    <div className={className}>
      <Button className={className} type={type} onClick={onClick} disabled={disabledLoading} variant='contained' color='primary' data-testid={`${label}-button`}>
        {label || 'Enter'}
      </Button>
    </div>
  );
};

export default ButtonLogin;