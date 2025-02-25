import React, { useState } from 'react';
import { TextField, InputAdornment, IconButton } from '@mui/material';
import { Visibility, VisibilityOff } from '@mui/icons-material';

interface InputPasswordProps {
  onChange: (value: string) => void;
  password: string;
  required?: boolean;
  label: string;
  className?: string;
}

export default function InputPassword(props: InputPasswordProps) {
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = event.target;
    props.onChange(value);

    if (value.length < 8) {
      setError('The password must be at least 8 characters long');
    } else {
      setError('');
    }
  };

  const handleTogglePassword = () => {
    setShowPassword(!showPassword);
  };

  return (
    <div className={props.className}>
      <TextField
        required={props.required}
        fullWidth={true}
        value={props.password}
        type={showPassword ? 'text' : 'password'}
        id="outlined-basic"
        label={props.label}
        variant="outlined"
        error={!!error}
        helperText={error}
        onChange={handleChange}
        InputProps={{
          endAdornment: (
            <InputAdornment position="end">
              <IconButton
                onClick={handleTogglePassword}
                edge="end"
                aria-label="toggle password visibility"
              >
                {showPassword ? <VisibilityOff /> : <Visibility />}
              </IconButton>
            </InputAdornment>
          ),
        }}
        data-testid={`${props.label}-textfield`}
      />
    </div>
  );
}
