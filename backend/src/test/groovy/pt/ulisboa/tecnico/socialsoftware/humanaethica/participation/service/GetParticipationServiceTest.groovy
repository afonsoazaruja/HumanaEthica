package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@DataJpaTest
class GetParticipationServiceTest extends SpockTest {
    public static final Integer NO_EXIST = 222

    def activity
    def participation1
    def participation2

    def setup() {
        def institution = institutionService.getDemoInstitution()
        given: "activity info"
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,2,ACTIVITY_DESCRIPTION_1,
                ONE_DAY_AGO,IN_TWO_DAYS,IN_THREE_DAYS,null)
        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        and: "an activity"
        activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
        and: "a volunteer"
        def volunteer1 = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer1)
        and: "another volunteer"
        def volunteer2= new Volunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer2)
        and: "a participation"
        def participationDto = createParticipationDto(RATING_10)
        participation1 = new Participation(activity, volunteer1, participationDto)
        participationRepository.save(participation1)
        and: "another participation"
        participationDto.setRating(RATING_1)
        participation2 = new Participation(activity, volunteer2, participationDto)
        participationRepository.save(participation2)
    }

    def "get two participations"() {
        when:
        def result = participationService.getParticipationsByActivity(activity.getId())

        then: "check results"
        result.size() == 2
        result.get(0).activity.getId() == activity.getId()
        result.get(0).volunteer.getName() == USER_1_NAME
        result.get(0).rating == RATING_10
        result.get(0).acceptanceDate == DateHandler.toISOString(participation1.getAcceptanceDate())

        result.get(1).activity.getId() == activity.getId()
        result.get(1).volunteer.getName() == USER_2_NAME
        result.get(1).rating == RATING_1
        result.get(1).acceptanceDate == DateHandler.toISOString(participation2.getAcceptanceDate())
    }

    def "invalid arguments: activityId=#activityId"() {
        when:
        participationService.getParticipationsByActivity(activityId)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "2 participations exist"
        participationRepository.findAll().size() == 2

        where:
        activityId || errorMessage
        NO_EXIST   || ErrorMessage.ACTIVITY_NOT_FOUND
        null       || ErrorMessage.ACTIVITY_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

