describe('Enrollment', () => {
    beforeEach(() => {
        cy.deleteAllButArs();
        cy.prepareEnrollmentTest();
        cy.demoMemberLogin();
    });

    afterEach(() => {
        cy.logout();
        cy.deleteAllButArs();
    });

    it('select participation', () => {
        const NUM_ACTIVITIES = '2';
        const NUM_PARTICIPATIONS = '1';
        const NUM_ENROLLMENTS = '2';
        const NUM_RATING = '5';
        const NUM_PARTICIPATIONS_AFTER = '2';

        // go to activities table
        cy.get('[data-cy="institution"]').click();
        cy.get('[data-cy="activities"]').click();

        // check results - 2 activities on the table
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .should('have.length', NUM_ACTIVITIES)

        // check results - first activity has 1 participation
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .eq(0).children().eq(3).should('contain', NUM_PARTICIPATIONS)

        // go to show enrollments from first activity
        cy.get('[data-cy="showEnrollments"]').eq(0).click();

        // check results - 2 enrollments on the table
        cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
            .should('have.length', NUM_ENROLLMENTS)

        // check the first enrollment has the Participating column as false
        cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
            .eq(0).children().eq(2).should('contain', 'false')

        // create participant
        cy.get('[data-cy="selectParticipant"]').click();
        cy.get('[data-cy="ratingInput"]').type(NUM_RATING);
        cy.get('[data-cy="saveParticipation"]').click();

        // check the first enrollment's Participating column is now true
        cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
            .eq(0).children().eq(2).should('contain', 'true')

        // check that the activity has now 2 participations
        cy.get('[data-cy="getActivities"]').click();
        cy.get('[data-cy="memberActivitiesTable"] tbody tr')
            .eq(0).children().eq(3).should('contain', NUM_PARTICIPATIONS_AFTER)
    });
});

