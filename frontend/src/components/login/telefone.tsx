import { TextField } from "@mui/material"
import { ChangeEvent } from "react";

export default function InputTelefone(props: any) {
  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    let { value } = event.target;
    value = value.replace(/\D/g, '');
    value = value.replace(/(\d{2})?(\d{4,5})(\d{4})/, '($1) $2-$3');
    props.onChange(value);

    if (value.length < 14 && value.length > 0) {
      event.target.setCustomValidity(
        "Digite um NÚMERO válido"
      );
      event.target.reportValidity();
      return;
    }
    event.target.setCustomValidity("");
  }

  return (
    <div className="inputTelefone">
      <TextField
        disabled={props.disabled}
        required={props.required}
        fullWidth={true}
        value={props.telefone}
        inputProps={{ maxLength: props.maxNumbers || 15 }}
        id="outlined-basic"
        label={props.label}
        variant="outlined"
        onChange={handleChange} />
    </div>
  )
}