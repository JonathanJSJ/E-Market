describe("Navbar responsiveness", () => {
  beforeEach(() => {
    cy.visit("/start");
  });

  it("Should have products button in desktop view", () => {
    cy.get('[data-testid="products-button"]') 
      .should("be.visible")
      .and("contain.text", "products");
  });

  it("Should not have products button in mobile view", () => {
    cy.viewport('samsung-s10')
    cy.get('[data-testid="products-button"]') 
    .should("not.be.visible");
  });

  it("Should have login button in desktop view", () => {
    cy.get('[data-testid="login-user-button"]') 
      .should("be.visible")
      .and("contain.text", "login");
  });

  it("Should not have login button in mobile view", () => {
    cy.viewport('samsung-s10')
    cy.get('[data-testid="login-user-button"]') 
    .should("not.be.visible");
  });

  it("Should have login button in desktop view", () => {
    cy.viewport('samsung-s10')
    cy.get('[data-testid="menu-button"]') 
      .should("be.visible")
  });
});
