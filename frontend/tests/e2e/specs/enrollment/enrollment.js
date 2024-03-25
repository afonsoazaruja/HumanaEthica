describe('Enrollments', () => {
    beforeEach(() => {
      cy.deleteAllButArs();
      cy.createDemoEntities();
      cy.createActivities();
      cy.demoMemberLogin();
    });
  
    afterEach(() => {
      cy.deleteAllButArs();
      cy.logout();
    });
  
    it('apply to activity', () =>{
      //Go to Activities page
      cy.get('[data-cy="institution"]').click();
      cy.get('[data-cy="activities"]').click();
  
      cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .should('have.length', 3);

      cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .eq(0)
        .children()
        .eq(3)
        .should('contain',0);

    })

  });