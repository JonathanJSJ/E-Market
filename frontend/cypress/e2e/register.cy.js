import { v4 as uuidv4 } from 'uuid';

describe("Register flow",() => {
  it("Register flow", () => {
    const uniqueEmail = `${uuidv4()}@example.com`;

    cy.visit('/start')

    cy.get('[data-testid="login-user-button"]').should("be.visible").click();
    
    cy.url().should("include", "/login");

    cy.get('[data-testid="register-button"]').should("be.visible").click();

    cy.url().should("include", "/register");

    cy.get('[data-testid="email-textfield"]').should("be.visible").type(uniqueEmail);
    cy.get('[data-testid="FirstName-textfield"]',).should("be.visible").type("user");
    cy.get('[data-testid="LastName-textfield"]').should("be.visible").type("test");
    cy.get('[data-testid="age-textfield"]').should("be.visible").type("20");
    cy.get('[data-testid="Password-textfield"]').should("be.visible").type("123123123");
    cy.get('[data-testid="Confirm Password-textfield"]').should("be.visible").type("123123123");

    cy.get('[data-testid="register-button"]').should("be.visible").click();

    cy.url().should("include", "/login");

    cy.get('[data-testid="email-textfield"]').should("be.visible").type(uniqueEmail);
    cy.get('[data-testid="Password-textfield"]').should("be.visible").type("123123123");

    cy.get('[data-testid="Login-button"]').should("be.visible").click();

    cy.url().should("include", "/start");

  });
});
