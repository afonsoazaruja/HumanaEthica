package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll


@DataJpaTest
class CreateEnrollmentCreateTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"

    def activityId
    def volunteerId

    def setup() {
        given: "activity info"
        def activityDto =  createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1, 1,
                ACTIVITY_DESCRIPTION_1, IN_ONE_DAY, IN_TWO_DAYS, IN_THREE_DAYS, null)

        and: "an institution"
        def institution = institutionService.getDemoInstitution()

        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED, null))

        and: "an activity"
        def activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
        activityId = activity.getId()

        and: "a volunteer"
        def volunteer = authUserService.loginDemoVolunteerAuth().getUser()
        volunteerId = volunteer.getId()
    }

    @Unroll
    def 'invalid arguments: userID=#userID | activityID=#activityID'() {
        given: "an enrollmentDto"
        def enrollmentDto = createEnrollmentDto(ENROLLMENT_ID_1,ENROLLMENT_MOTIVATION_1,DateHandler.toISOString(NOW))

        when:
        enrollmentService.createEnrollment(getVolunteerID(userID), getActivityID(activityID), enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        and: "no enrollment is stored in the database"
        enrollmentRepository.findAll().size() == 0

        where:
        userID   | activityID || errorMessage
        NO_EXIST | EXIST      || ErrorMessage.USER_NOT_FOUND
        EXIST    | NO_EXIST   || ErrorMessage.ACTIVITY_NOT_FOUND
        null     | EXIST      || ErrorMessage.USER_NOT_FOUND
        EXIST    | null       || ErrorMessage.ACTIVITY_NOT_FOUND
    }

    def getVolunteerID(userID) {
        if (userID == EXIST)
            return volunteerId
        else if (userID == NO_EXIST)
            return 222
        return null
    }

    def getActivityID(activityID) {
        if (activityID == EXIST)
            return activityId
        else if (activityID == NO_EXIST)
            return 222
        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
