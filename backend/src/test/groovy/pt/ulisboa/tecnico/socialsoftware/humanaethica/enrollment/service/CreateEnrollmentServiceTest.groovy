package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class CreateEnrollmentServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def enrollmentDto
    def activity
    def volunteer

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

        and: "volunteer"
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        and: "enrollmentDto"
        enrollmentDto = new EnrollmentDto()
        enrollmentDto.setMotivation(ENROLLMENT_MOTIVATION_1)
    }

    @Unroll
    def "create enrollment"() {
        when:
        def result = enrollmentService.createEnrollment(activity.id, volunteer.id, enrollmentDto)

        then: "the returned enrollment is correct"
        with (result) {
            motivation == ENROLLMENT_MOTIVATION_1
            enrollmentDateTime != null
            activityId == this.activity.id
            volunteerId == this.volunteer.id
        }

        and: "enrollment is saved in the database"
        enrollmentRepository.count() == 1

        and: "enrollment data stored in repository is correct"
        def enrollment = enrollmentRepository.getEnrollmentsByActivity(activity.id).get(0)

        with (enrollment) {
            motivation == ENROLLMENT_MOTIVATION_1
            enrollmentDateTime != null
            activity.getId() == this.activity.id
            volunteer.getId() == this.volunteer.id
        }
    }

    @Unroll
    def 'invalid arguments: userId=#userId | activityId=#activityId'() {
        when:
        enrollmentService.createEnrollment(getVolunteerId(userId), getActivityId(activityId), enrollmentDto)

        then: "exception is thrown"
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        and: "no enrollment is stored in the database"
        enrollmentRepository.count() == 0

        where:
        userId   | activityId || errorMessage
        NO_EXIST | EXIST      || ErrorMessage.USER_NOT_FOUND
        EXIST    | NO_EXIST   || ErrorMessage.ACTIVITY_NOT_FOUND
        null     | EXIST      || ErrorMessage.USER_NOT_FOUND
        EXIST    | null       || ErrorMessage.ACTIVITY_NOT_FOUND
    }

    def getVolunteerId(userId) {
        if (userId == EXIST)
            return volunteer.id
        else if (userId == NO_EXIST)
            return 222
        return null
    }

    def getActivityId(activityId) {
        if (activityId == EXIST)
            return activity.id
        else if (activityId == NO_EXIST)
            return 222
        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
