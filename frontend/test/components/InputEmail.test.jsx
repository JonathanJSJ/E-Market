import InputEmail from '@/components/login/email';
import { render, screen, fireEvent } from "@testing-library/react";

describe("InputEmail Component", () => {
  it("displays a validation message for invalid emails", () => {
    const handleChange = jest.fn();
    render(<InputEmail onChange={handleChange} email="" label="Email" />);

    const emailInput = screen.getByTestId("email-textfield").querySelector("input");

    if (!emailInput) {
      throw new Error("Input not found inside TextField");
    }

    fireEvent.input(emailInput, { target: { value: "invalid-email" } });

    expect(emailInput.validationMessage).toBe("Digite um EMAIL válido");
    expect(handleChange).toHaveBeenCalledWith("invalid-email");
  });

  it("does not display a validation message for valid emails", () => {
    const handleChange = jest.fn();
    render(<InputEmail onChange={handleChange} email="" label="Email" />);

    const emailInput = screen.getByTestId("email-textfield").querySelector("input");

    if (!emailInput) {
      throw new Error("Input not found inside TextField");
    }

    fireEvent.input(emailInput, { target: { value: "valid@email.com" } });

    expect(emailInput.validationMessage).toBe("");
    expect(handleChange).toHaveBeenCalledWith("valid@email.com");
  });
});
