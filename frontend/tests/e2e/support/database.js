const credentials = {
  user: Cypress.env('psql_db_username'),
  host: Cypress.env('psql_db_host'),
  database: Cypress.env('psql_db_name'),
  password: Cypress.env('psql_db_password'),
  port: Cypress.env('psql_db_port'),
};

const INSTITUTION_COLUMNS = "institutions (id, active, confirmation_token, creation_date, email, name, nif, token_generation_date)";
const USER_COLUMNS = "users (user_type, id, creation_date, name, role, state, institution_id)";
const AUTH_USERS_COLUMNS = "auth_users (auth_type, id, active, email, username, user_id)";
const ACTIVITY_COLUMNS = "activity (id, application_deadline, creation_date, description, ending_date, name, participants_number_limit, region, starting_date, state, institution_id)";
const ENROLLMENT_COLUMNS = "enrollment (id, enrollment_date_time, motivation, activity_id, volunteer_id)";
const PARTICIPATION_COLUMNS = "participation (id, acceptance_date, rating, activity_id, volunteer_id)";

const now = new Date();
const tomorrow = new Date(now);
tomorrow.setDate(now.getDate() + 1);
const dayAfterTomorrow = new Date(now);
dayAfterTomorrow.setDate(now.getDate() + 2);
const yesterday = new Date(now);
yesterday.setDate(now.getDate() - 1);
const dayBeforeYesterday = new Date(now);
dayBeforeYesterday.setDate(now.getDate() - 2);

Cypress.Commands.add('deleteAllButArs', () => {
  cy.task('queryDatabase', {
    query: "DELETE FROM ASSESSMENT",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM ENROLLMENT",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM PARTICIPATION",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM ACTIVITY",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM AUTH_USERS WHERE NOT (username = 'ars')",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM USERS WHERE NOT (name = 'ars')",
    credentials: credentials,
  });
  cy.task('queryDatabase', {
    query: "DELETE FROM INSTITUTIONS",
    credentials: credentials,
  });
});

Cypress.Commands.add('createActivities', () => {
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTuple(1, "2024-08-06 17:58:21.402146",	"2024-08-06 17:58:21.402146", "Enrollment is open","2024-08-08 17:58:21.402146", "A1", 1, "LISBON", "2024-08-07 17:58:21.402146", "APPROVED", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTuple(2, "2024-08-06 17:58:21.402146",	"2024-08-06 17:58:21.402146",	"Enrollment is open and it is already enrolled", "2024-08-08 17:58:21.402146", "A2", 2, "LISBON", "2024-08-07 17:58:21.402146", "APPROVED", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateActivityTuple(3, "2024-02-06 17:58:21.402146", "2024-08-06 17:58:21.402146",	"Enrollment is closed", "2024-08-08 17:58:21.402146",	"A3", 3, "LISBON", "2024-08-07 17:58:21.402146", "APPROVED", 1),
    credentials: credentials,
  })
})

Cypress.Commands.add('enroll', () => {
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateEnrollmentTuple(5,	"2024-02-06 18:51:37.595713",	"sql-inserted-motivation", "2",	"3"),
    credentials: credentials,
  })
})

Cypress.Commands.add('createDemoEntities', () => {
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateInstitutionTuple(1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(2, "MEMBER","DEMO-MEMBER", "MEMBER", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(2, "DEMO", "demo-member", 2),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(3, "VOLUNTEER","DEMO-VOLUNTEER", "VOLUNTEER", "NULL"),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(3, "DEMO", "demo-volunteer", 3),
    credentials: credentials,
  })
});

Cypress.Commands.add('prepareAssessmentTest', () => {
  // institutions
  cy.task('queryDatabase', {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateAssessmentInstitutionTuple(1, "DEMO INSTITUTION", "000000000"),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateAssessmentInstitutionTuple(2, "DEMO INSTITUTION-2", "000000002"),
    credentials: credentials,
  })
  // users
  cy.task('queryDatabase', {
    query: "INSERT INTO " + USER_COLUMNS + generateAssessmentUserTuple("MEMBER", 2, "2024-02-06 17:58:21.419878", "DEMO-MEMBER", "MEMBER", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + USER_COLUMNS + generateAssessmentUserTuple("VOLUNTEER", 3, "2024-02-06 17:58:23.732513", "DEMO-VOLUNTEER", "VOLUNTEER", "NULL"),
    credentials: credentials,
  })
  // auth users
  cy.task('queryDatabase', {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAssessmentAuthUserTuple(2, "demo_member@mail.com", "demo-member", 2),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAssessmentAuthUserTuple(3, "demo_volunteer@mail.com", "demo-volunteer", 3),
    credentials: credentials,
  })
  // activities
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateAssessmentActivityTuple(1, "Same institution is enrolled and participates", "A1", 1, 1),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateAssessmentActivityTuple(2, "Same institution is enrolled and participates", "A2", 1, 1),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateAssessmentActivityTuple(3, "Same institution is enrolled and does not participate", "A3", 2, 1),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateAssessmentActivityTuple(4, "Same institution is not enrolled", "A4", 2, 1),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateAssessmentActivityTuple(5, "Same institution before end date", "A5", 2, 1),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateAssessmentActivityTuple(6, "Other institution is enrolled and participates", "A6", 3, 2),
    credentials: credentials,
  })
  // enrollments
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateAssessmentEnrollmentTuple(1, 1, 3),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateAssessmentEnrollmentTuple(2, 2, 3),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateAssessmentEnrollmentTuple(3, 3, 3),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateAssessmentEnrollmentTuple(4, 6, 3),
    credentials: credentials,
  })
  // participations
  cy.task('queryDatabase', {
    query: "INSERT INTO " + PARTICIPATION_COLUMNS + generateAssessmentParticipationTuple(1, 1, 3),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + PARTICIPATION_COLUMNS + generateAssessmentParticipationTuple(2, 2, 3),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + PARTICIPATION_COLUMNS + generateAssessmentParticipationTuple(3, 6, 3),
    credentials: credentials,
  })
});

Cypress.Commands.add('prepareParticipationTest', () => {
  // institution
  cy.task('queryDatabase', {
    query: "INSERT INTO " + INSTITUTION_COLUMNS + generateInstitutionTuple(1),
    credentials: credentials,
  })
  // users & auth users
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(2, "MEMBER","DEMO-MEMBER", "MEMBER", 1),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(2, "DEMO", "demo-member", 2),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(3, "VOLUNTEER","DEMO-VOLUNTEER", "VOLUNTEER", "NULL"),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(3, "DEMO", "demo-volunteer", 3),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(4, "VOLUNTEER","DEMO-VOLUNTEER2", "VOLUNTEER", "NULL"),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(4, "DEMO", "demo-volunteer1", 4),
    credentials: credentials,
  })
  cy.task('queryDatabase', {
    query: "INSERT INTO " + USER_COLUMNS + generateUserTuple(5, "VOLUNTEER","DEMO-VOLUNTEER3", "VOLUNTEER", "NULL"),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + AUTH_USERS_COLUMNS + generateAuthUserTuple(5, "DEMO", "demo-volunteer2", 5),
    credentials: credentials,
  })
  // activities
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateEnrollmentActivityTuple(1, "Has vacancies", "A1", 2),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ACTIVITY_COLUMNS + generateEnrollmentActivityTuple(2, "Has no vacancies", "A2", 1),
    credentials: credentials,
  })
  // enrollments
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS +  generateEnrollmentTuple(1, "2024-02-06 18:51:37.595713",	"Has vacancies and do not participate",1,3),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS +  generateEnrollmentTuple(2, "2024-02-06 19:51:37.595713",	"Has vacancies and participate",1,4),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateEnrollmentTuple(3, "2024-02-06 18:51:37.595713",	"Has no vacancies and participate",2,3),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + ENROLLMENT_COLUMNS + generateEnrollmentTuple(4, "2024-02-06 20:51:37.595713",	"Has no vacancies and do not participate",2,5),
    credentials: credentials,
  })
  // participations
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + PARTICIPATION_COLUMNS + generateEnrollmentParticipationTuple(5,1,4),
    credentials: credentials,
  })
  cy.task('queryDatabase',  {
    query: "INSERT INTO " + PARTICIPATION_COLUMNS + generateEnrollmentParticipationTuple(6,2,3),
    credentials: credentials,
  })
});

function generateAuthUserTuple(id, authType, username, userId) {
  return "VALUES ('"
    + authType + "', '"
    + id + "', 't', 'demo_member@mail.com','"
    + username + "', '"
    + userId + "')"
}

function generateUserTuple(id, userType, name, role, institutionId) {
  return "VALUES ('"
    + userType + "', '"
    + id + "', '2022-02-06 17:58:21.419878', '"
    + name + "', '"
    + role + "', 'ACTIVE', "
    + institutionId + ")";
}

function generateInstitutionTuple(id) {
  return "VALUES ('"
    + id + "', 't', 'abca428c09862e89', '2022-08-06 17:58:21.402146','demo_institution@mail.com', 'DEMO INSTITUTION', '000000000', '2024-02-06 17:58:21.402134')";
}

function generateActivityTuple(id, application_deadline, create_date, description, ending_date, name, participants_number_limit, region, starting_date, state, institution_id) {
  return "VALUES ('"
    + id + "', '"
    + application_deadline + "', '"
    + create_date + "', '"
    + description + "', '"
    + ending_date + "', '"
    + name + "', '"
    + participants_number_limit + "', '"
    + region + "', '"
    + starting_date + "', '"
    + state + "', '"
    + institution_id + "')";
}

function generateEnrollmentTuple(id, enrollment_date_time, motivation, activity_id, volunteer_id) {
  return "VALUES ('"
    + id + "', '"
    + enrollment_date_time + "', '"
    + motivation + "', '"
    + activity_id + "', '"
    + volunteer_id + "')";
}

// Assessments test

function generateAssessmentInstitutionTuple(id, name, nif) {
  return "VALUES ('"
    + id + "', 't', 'abca428c09862e89', '2024-02-06 17:58:21.402146','demo_institution@mail.com', '"
    + name + "', '"
    + nif + "', '2024-02-06 17:58:21.402134')";
}

function generateAssessmentUserTuple(userType, id, creation_date, name, role, institutionId) {
  return "VALUES ('"
    + userType + "', '"
    + id + "', '"
    + creation_date + "', '"
    + name + "', '"
    + role + "', 'ACTIVE', "
    + institutionId + ")";
}

function generateAssessmentAuthUserTuple(id, email, username, userId) {
  return "VALUES ('DEMO', '"
      + id + "', 't', '"
      + email + "', '"
      + username + "', '"
      + userId + "')";
}

function generateAssessmentActivityTuple(id, description, name, participantNumberLimit, institutionId) {
  return "VALUES ('"
    + id + "', '2024-02-06 17:58:21.402146', '2024-02-06 17:58:21.402146', '"
    + description + "', '2024-02-08 10:58:21.402146', '"
    + name + "', '"
    + participantNumberLimit + "', 'Lisbon', '2024-02-07 17:58:21.402146', 'APPROVED', "
    + institutionId + ")";
}

function generateAssessmentEnrollmentTuple(id, activityId, volunteerId) {
  return "VALUES ('"
    + id + "', '2024-02-06 18:51:37.595713', 'sql-inserted-motivation', '"
    + activityId + "', '"
    + volunteerId + "')";
}

function generateAssessmentParticipationTuple(id, activityId, volunteerId) {
  return "VALUES ('"
    + id + "', '2024-02-06 18:51:37.595713', '5', '"
    + activityId + "', '"
    + volunteerId + "')";
}

// Enrollments test

function generateEnrollmentActivityTuple(id, description, name, participants_number_limit) {
  return "VALUES ('"
      + id + "', '2024-02-06 17:58:21.402146',	'2024-01-06 17:58:21.402146', '"
      + description + "', '2024-02-08 17:58:21.402146', '"
      + name +"', '"
      + participants_number_limit + "', 'Lisbon', '2024-02-07 17:58:21.402146', 'APPROVED', '1')";
}

function generateEnrollmentParticipationTuple(id, activityId, volunteerId) {
  return "VALUES ('"
      + id + "', '2024-02-06 18:51:37.595713', '5', '"
      + activityId + "', '"
      + volunteerId + "')";
}
