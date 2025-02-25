import { TextField } from "@mui/material";
import { ChangeEvent } from "react";

export default function InputEmail(props: any) {
  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    let { value } = event.target;

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) && value.length > 0) {
      event.target.setCustomValidity("Digite um EMAIL válido");
      event.target.reportValidity();
    } else {
      event.target.setCustomValidity("");
    }

    props.onChange(value);
  };

  return (
    <div className="inputEmail">
      <TextField
        type="email"
        disabled={props.disabled}
        required={props.required}
        fullWidth={true}
        value={props.email}
        id="outlined-basic"
        inputProps={{ maxLength: 100 }}
        label={props.label}
        variant="outlined"
        onChange={handleChange}
        data-testid="email-textfield"
      />
    </div>
  );
}
