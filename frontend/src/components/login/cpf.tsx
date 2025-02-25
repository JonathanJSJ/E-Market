import { TextField } from "@mui/material"
import { ChangeEvent, useState } from "react";

export default function InputCPF(props: any) {

  const [cpfInvalid, setCpfInvalid] = useState(false)
  const handleChange = (event: ChangeEvent<HTMLTextAreaElement | HTMLInputElement>) => {
    let { value } = event.target;

    value = value.replace(/\D/g, '');
    value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    props.onChange(value);
    let trucante = false;
    if (value.length > 0) {
      if (validCPF(value)) {
        setCpfInvalid(false)
        trucante = true;
      } else {
        setCpfInvalid(true)
      }
    } else {
      setCpfInvalid(false)
    }
    if (cpfInvalid && !trucante) {
      event.target.setCustomValidity(
        "Digite um CPF válido"
      );
      event.target.reportValidity();
      return;
    }
    event.target.setCustomValidity('');
  }

  return (

    <TextField
      className={props.className}
      disabled={props.disabled}
      error={cpfInvalid}
      required={props.required}
      fullWidth={true}
      value={props.username}
      inputProps={{ maxLength: 14 }}
      id="outlined-basic"
      label={props.label}
      variant="outlined"
      onChange={handleChange} />

  )
}
function validCPF(cpf: string) {
  cpf = cpf.replace(/\D/g, '');
  if (cpf.length !== 11) {
    return false;
  }

  let sum = 0;
  for (let i = 0; i < 9; i++) {
    sum += parseInt(cpf.charAt(i)) * (10 - i);
  }
  let rest = sum % 11;
  let digit1 = rest < 2 ? 0 : 11 - rest;

  sum = 0;
  for (let i = 0; i < 10; i++) {
    sum += parseInt(cpf.charAt(i)) * (11 - i);
  }
  rest = sum % 11;
  let digit2 = rest < 2 ? 0 : 11 - rest;

  if (parseInt(cpf.charAt(9)) !== digit1 || parseInt(cpf.charAt(10)) !== digit2) {
    return false;
  }

  return true;
}