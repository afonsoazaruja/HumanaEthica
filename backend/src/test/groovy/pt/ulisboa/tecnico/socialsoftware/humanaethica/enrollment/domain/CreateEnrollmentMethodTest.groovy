package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain

import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import spock.lang.Unroll

class CreateEnrollmentMethodTest extends SpockTest {
    
    Volunteer volunteer = Mock()
    Activity activity = Mock()
    Enrollment otherEnrollment = Mock()

    def enrollmentDto

    def setup() {
        enrollmentDto = new EnrollmentDto()
        enrollmentDto.setMotivation(ENROLLMENT_MOTIVATION_1)
    }

    @Unroll
    def "create enrollment with activity and volunteer"() {
        given: "avoid exception on invariants 2 and 3"
        volunteer.getEnrollments() >> []
        activity.getApplicationDeadline() >> IN_ONE_DAY

        when:
        def enrollment = new Enrollment(activity, volunteer, enrollmentDto)

        then:
        enrollment.getVolunteer() == volunteer
        enrollment.getActivity() == activity
        enrollment.getMotivation() == ENROLLMENT_MOTIVATION_1
        // can we test enrollment.getEnrollmentDateTime()?
        and:
        1 * volunteer.addEnrollment(_)
        1 * activity.addEnrollment(_)
    }

    @Unroll
    def "create enrollment and violate invariant motivation must have at least 10 characters"() {
        given: "avoid exception on invariant 2"
        volunteer.getEnrollments() >> []

        and:
        enrollmentDto.setMotivation(motivation)

        when:
        new Enrollment(activity, volunteer, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_MOTIVATION_INVALID

        where:
        motivation << [null, "", "123456789", "wrong"]

    }
    @Unroll
    def "create enrollment and violate invariant enrollment must be unique for a volunteer"() {
        given:
        otherEnrollment.getActivity() >> activity

        and:
        volunteer.getEnrollments() >> [otherEnrollment]

        when:
        new Enrollment(activity, volunteer, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_ALREADY_EXISTS
    }

    @Unroll
    def "create enrollment and violate invariant apply before deadline"() {
        given: "avoid exception on invariant 2"
        volunteer.getEnrollments() >> []

        and:
        activity.getApplicationDeadline() >> deadline

        when:
        new Enrollment(activity, volunteer, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_AFTER_DEADLINE

        where:
        deadline << [ONE_DAY_AGO, TWO_DAYS_AGO]
    }
}
