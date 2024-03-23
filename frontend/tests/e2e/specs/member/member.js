describe('Volunteer', () => {
  beforeEach(() => {
    cy.demoMemberLogin()
  });

  afterEach(() => {
    cy.logout();
  });

  it('close', () => {
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="members"]').click();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="themes"]').click();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
  });

  it('select participation', () => {
    const NUM_ACTIVITIES = '2';
    const NUM_PARTICIPATIONS = '1';
    const NUM_ENROLLMENTS = '2';

    // go to activities table
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();

    // check results - 2 activities on the table
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .should('have.length', NUM_ACTIVITIES)
    
  });
});
