describe('Enrollments', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createDemoEntities();
    cy.createActivities();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('apply to activity', () => {
    const MOTIVATION = "valid motivation";

    cy.intercept('GET', '/activities/*/enrollments').as('getEnrollments');
    cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');
    cy.intercept('GET', '/activities').as('getActivities');
    
    cy.demoMemberLogin();
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.wait('@getInstitutions');
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 3)
      .eq(0).children().eq(3).should('contain', 0);
    cy.logout();

    cy.demoVolunteerLogin();
    cy.get('[data-cy="volunteerActivities"]').click();
    cy.wait('@getActivities');
    cy.get('[data-cy="applyForActivityButton"]').eq(0).click();
    cy.get('[data-cy="motivationInput"]').type(MOTIVATION);
    cy.get('[data-cy="applyButton"]').click();
    cy.logout();

    cy.demoMemberLogin()
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.wait('@getInstitutions');
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0).children().eq(3).should('contain', 1);
    cy.get('[data-cy="showEnrollments"]').eq(0).click();
    cy.wait('@getEnrollments')
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .should('have.length', 1)
      .eq(0).children().eq(0).should('contain', MOTIVATION);
    cy.logout();
  });
});
