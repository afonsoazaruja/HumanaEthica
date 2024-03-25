describe('Enrollments', () => {
    beforeEach(() => {
      cy.deleteAllButArs();
      cy.createDemoEntities();
      cy.createActivities();
    });
  
    afterEach(() => {
      cy.deleteAllButArs();
      cy.logout();
    });
  
    it('as a member create activities and ensure the first one does not have any enrollment', () =>{
        cy.demoMemberLogin();
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
    it('as a volunteer apply to the first activity', () =>{
        const MOTIVATION = "valid motivation"
        cy.demoVolunteerLogin();
        cy.get('[data-cy="volunteerActivities"]').click();

        cy.get('[data-cy="applyForActivityButton"]')
        .eq(0).click();
        cy.get('[data-cy="motivationInput"]').type(MOTIVATION);
        cy.get('[data-cy="saveActivity"]').click();

        
      })

  });