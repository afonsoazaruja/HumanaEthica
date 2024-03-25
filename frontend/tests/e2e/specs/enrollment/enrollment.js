describe('Enrollments', () => {
    beforeEach(() => {
      cy.deleteAllButArs()
      cy.demoMemberLogin()
      cy.createActivities()
    });
  
    afterEach(() => {
      cy.deleteAllButArs()
      cy.logout();
    });
  
    it('Verify number of instances (should be 3)', () =>{
      //create activities
      //Go to Activities page
      cy.get('[data-cy="institution"]').click();
      cy.get('[data-cy="activities"]').click();
  
      cy.get('[data-cy="memberActivitiesTable"]')
        .find('tbody > tr')
        .should('have.length', 3);
    })
  });