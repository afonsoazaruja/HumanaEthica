package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@DataJpaTest
class GetEnrollmentsServiceTest extends SpockTest {
    public static final Integer NO_EXIST = 222

    def activity
    def volunteer1
    def volunteer2

    def setup() {
        def institution = institutionService.getDemoInstitution()
        given: "activity info"
        def activityDto = createActivityDto(
                ACTIVITY_NAME_1, ACTIVITY_REGION_1, 1, ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY, IN_TWO_DAYS, IN_THREE_DAYS,null)
        
        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED, null))

        and: "an activity"
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)

        and: "a volunteer"
        volunteer1 = createVolunteer(
                USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                AuthUser.Type.NORMAL, User.State.SUBMITTED)
        
        and: "another volunteer"
        volunteer2 = createVolunteer(
                USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                AuthUser.Type.NORMAL, User.State.SUBMITTED)
        
        and: "an enrollment"
        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.setId(ENROLLMENT_ID_1)
        enrollmentDto.setMotivation(ENROLLMENT_MOTIVATION_1)

        def enrollment = new Enrollment(activity, volunteer1, enrollmentDto)
        enrollmentRepository.save(enrollment)

        enrollmentDto.setMotivation(ENROLLMENT_MOTIVATION_2)
        enrollment = new Enrollment(activity, volunteer2, enrollmentDto)
        enrollmentRepository.save(enrollment)
    }

    def "get two enrollments"() {
        when:
        def result = enrollmentService.getEnrollmentsByActivity(activity.id)

        then: "two enrollments"
        result.size() == 2

        and: "enrollment 1 is correct"
        with(result.get(0)) {
            motivation == ENROLLMENT_MOTIVATION_1
            activityId == this.activity.id
            volunteerId == this.volunteer1.id
        }

        and: "enrollment 2 is correct"
        with(result.get(1)) {
            motivation == ENROLLMENT_MOTIVATION_2
            activityId == this.activity.id
            volunteerId == this.volunteer2.id
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
