describe("Test Login Page", () => {
  beforeEach(() => {
    cy.visit('/login');
  });

  it("Should log in with valid credentials", () => {

    cy.get('[data-testid="email-textfield"] > .MuiInputBase-root > #outlined-basic').type("gabriel@gmail.com"); // Campo de email
    cy.get('[data-testid="Password-textfield"] > .MuiInputBase-root').type("senhasenha");
    cy.get('[data-testid="Login-button"]').click();


    cy.url().should("eq", `${Cypress.config("baseUrl")}/start`);
  });

  it("Should show an error message with invalid credentials", () => {

    cy.get('[data-testid="email-textfield"] > .MuiInputBase-root > #outlined-basic').type("wronguser@example.com");
    cy.get('[data-testid="Password-textfield"] > .MuiInputBase-root').type("wrongpassword");
    cy.get('[data-testid="Login-button"]').click();

    cy.get("[data-testid='error-message']").should("contain", "Usuário ou senha inválidos");
  });
});
