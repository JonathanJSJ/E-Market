import { TextField } from "@mui/material"
import { ChangeEvent } from "react";

export default function InputAge(props: any) {
  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    let { value } = event.target;
    value = value.replace(/\D/g, '');
    props.onChange(value);

    event.target.setCustomValidity("");
  }

  return (
    <div className={props.className}>
      <TextField
        disabled={props.disabled}
        required={props.required}
        fullWidth={true}
        value={props.age}
        inputProps={{ maxLength: props.maxNumbers || 3 }}
        id="outlined-basic"
        label={props.label}
        variant="outlined"
        onChange={handleChange} 
        data-testid="age-textfield"/>
    </div>
  )
}