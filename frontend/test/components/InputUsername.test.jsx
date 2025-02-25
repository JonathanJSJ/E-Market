import { render, screen, fireEvent } from "@testing-library/react";
import InputUsername from "@/components/login/username";

describe("InputUsername Component", () => {
  it("formats the username correctly and calls onChange", () => {
    const handleChange = jest.fn();
    render(<InputUsername onChange={handleChange} username="" label="Username" />);

    const usernameInput = screen.getByTestId("Username-textfield").querySelector("input");

    if (!usernameInput) {
      throw new Error("Input not found inside TextField");
    }

    fireEvent.input(usernameInput, { target: { value: "john_doe123" } });

    expect(handleChange).toHaveBeenCalledWith("JOHNDOE");
  });

  it("calls onChange with an empty value when input is empty", () => {
    const handleChange = jest.fn();
    render(<InputUsername onChange={handleChange} username="ExistingValue" label="Username" />);

    const usernameInput = screen.getByTestId("Username-textfield").querySelector("input");

    if (!usernameInput) {
      throw new Error("Input not found inside TextField");
    }

    fireEvent.input(usernameInput, { target: { value: "" } });

    expect(handleChange).toHaveBeenCalledWith("");
  });
});

