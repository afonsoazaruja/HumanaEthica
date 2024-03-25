describe('Assessment', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.prepareAssessmentTest();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('assess institution', () => {
    cy.demoVolunteerLogin();
    // intercept get activities request
    cy.intercept('GET', '/activities').as('getActivities');
    // go to volunteer activities view
    cy.get('[data-cy="volunteerActivities"]').click();
    // check request was done
    cy.wait('@getActivities');
    // check results
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
      .should('have.length', 6)


    cy.logout()
  });
});