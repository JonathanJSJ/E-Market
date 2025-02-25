import { TextField } from "@mui/material"
import { ChangeEvent } from "react";

export default function InputUsername(props: any) {
  const handleChange = (event: ChangeEvent<HTMLTextAreaElement | HTMLInputElement>) => {
    let { value } = event.target;

    value = value.replace(/[^a-zA-Z\s]/g, '').toUpperCase();
    props.onChange(value);

    event.target.setCustomValidity('');
  }

  return (
    <TextField
      className={props.className}
      disabled={props.disabled}
      required={props.required}
      fullWidth={true}
      value={props.username}
      inputProps={{ maxLength: 100 }}
      id="outlined-basic"
      label={props.label}
      variant="outlined"
      onChange={handleChange} 
      data-testid={`${props.label}-textfield`}/>

  )
}