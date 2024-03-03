package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain

import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

class CreateEnrollmentMethodTest extends SpockTest {
    Volunteer volunteer = Mock()
    Activity activity = Mock()
    def enrollmentDto

    @Unroll
    def "create enrollment and violate invariant motivation must have at least 10 characters"() {
        given:
        enrollmentDto = new EnrollmentDto()
        enrollmentDto.setMotivation(motivation)
        enrollmentDto.setEnrollmentDateTime(DateHandler.toISOString(NOW))

        when:
        new Enrollment(activity, volunteer, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_MOTIVATION_INVALID

        where:
        motivation << [null, "", "123456789", "wrong"]

    }
}
