describe('Assessment', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.prepareAssessmentTest();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('assess institution', () => {
    const NAME = 'A1'
    const REVIEW = "valid review"

    cy.demoVolunteerLogin();
    // intercept get activities request
    cy.intercept('GET', '/activities').as('getActivities');
    // go to volunteer activities view
    cy.get('[data-cy="volunteerActivities"]').click();
    // check request was done
    cy.wait('@getActivities');
    // check results
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
      .should('have.length', 6);
    // check if the first activity name is A1
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr').eq(0).children().eq(0).should('contain', NAME);
    // assess the first activity
    cy.get('[data-cy="newAssessmentButton"]').eq(0).click();
    cy.intercept('POST', '/institutions/*/assessments').as('postAssessment');
    cy.get('[data-cy="reviewInput"]').type(REVIEW);
    cy.get('[data-cy="saveAssessment"]').click();
    cy.wait('@postAssessment');
    cy.logout();

    cy.demoMemberLogin();
    // go to assessments table
    cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="assessments"]').click();
    cy.wait('@getInstitutions');
    // check results - 1 assessment on the table
    cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
        .should('have.length', 1)
    // check if assessment has the text inserted by the volunteer
    cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
        .eq(0).children().eq(0).should('contain', REVIEW);
    cy.logout()
  });
});