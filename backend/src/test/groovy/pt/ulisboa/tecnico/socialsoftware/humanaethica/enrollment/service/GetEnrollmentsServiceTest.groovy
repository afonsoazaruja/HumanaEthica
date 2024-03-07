package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@DataJpaTest
class GetEnrollmentsServiceTest extends SpockTest {
    public static final Integer NO_EXIST = 222

    def activity
    def volunteer1
    def volunteer2

    def setup() {
        given: "institution"
        def institution = institutionService.getDemoInstitution()

        and: "activity dto"
        def activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_1)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setParticipantsNumberLimit(1)
        activityDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))
        activityDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))

        and: "activity"
        activity = new Activity(activityDto, institution, Collections.emptyList())
        activityRepository.save(activity)

        and: "2 volunteers"
        volunteer1 = new Volunteer(
                USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                AuthUser.Type.NORMAL, User.State.SUBMITTED)
        volunteer2 = new Volunteer(
                USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                AuthUser.Type.NORMAL, User.State.SUBMITTED)

        userRepository.save(volunteer1)
        userRepository.save(volunteer2)
        
        and: "2 enrollment"
        def enrollmentDto = new EnrollmentDto()

        enrollmentDto.setMotivation(ENROLLMENT_MOTIVATION_1)
        enrollmentRepository.save(new Enrollment(activity, volunteer1, enrollmentDto))

        enrollmentDto.setMotivation(ENROLLMENT_MOTIVATION_2)
        enrollmentRepository.save(new Enrollment(activity, volunteer2, enrollmentDto))
    }

    def "get two enrollments"() {
        when:
        def result = enrollmentService.getEnrollmentsByActivity(activity.id)

        then: "two enrollments"
        result.size() == 2

        and: "enrollment 1 is correct"
        with(result.get(0)) {
            motivation == ENROLLMENT_MOTIVATION_1
            activityId == activity.id
            volunteerId == volunteer1.id
        }

        and: "enrollment 2 is correct"
        with(result.get(1)) {
            motivation == ENROLLMENT_MOTIVATION_2
            activityId == activity.id
            volunteerId == volunteer2.id
        }
    }

    def "invalid arguments: activityId=#activityId"() {
        when:
        enrollmentService.getEnrollmentsByActivity(activityId)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        activityId  || errorMessage
        null        || ErrorMessage.ACTIVITY_NOT_FOUND
        NO_EXIST    || ErrorMessage.ACTIVITY_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
