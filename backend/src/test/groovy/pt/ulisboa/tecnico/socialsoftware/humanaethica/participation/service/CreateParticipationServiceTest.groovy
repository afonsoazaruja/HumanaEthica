package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import spock.lang.Unroll

@DataJpaTest
class CreateParticipationServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"


    def volunteerId
    def activityId

    def setup() {
        volunteerId = authUserService.loginDemoVolunteerAuth().getUser().getId()
        def institution = institutionService.getDemoInstitution()
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,2,ACTIVITY_DESCRIPTION_1,
                ONE_DAY_AGO,IN_TWO_DAYS,IN_THREE_DAYS,null)
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        def activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
        activityId = activity.getId()
    }

    def "create participation"(){
        given: "an participation dto"
        def participationDto = createParticipationDto(RATING_10, volunteerId)

        when:
        def result = participationService.createParticipation(activityId, participationDto)

        then: "the returned data is correct"
        result.activity.getId() == activityId
        result.rating == RATING_10
        result.getVolunteerId() == volunteerId
        result.acceptanceDate != null
        and: "the activity is saved in the database"
        participationRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedParticipation = participationRepository.getParticipationByActivityId(activityId).get(0)
        storedParticipation.activity.id == activityId
        storedParticipation.rating == RATING_10
        storedParticipation.getVolunteer().getId() == volunteerId
        storedParticipation.acceptanceDate != null

    }

    @Unroll
    def 'invalid arguments: IDactivity=#IDactivity | volunteerId=#IDvolunteer'(){
        given: "an participation dto"
        def participationDto = createParticipationDto(RATING_1, getVolunteerId(IDvolunteer))

        when:
        participationService.createParticipation(getActivityId(IDactivity), participationDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no participation is stored in the database"
        participationRepository.findAll().size() == 0

        where:
        IDactivity | IDvolunteer || errorMessage
        NO_EXIST   | EXIST       || ErrorMessage.ACTIVITY_NOT_FOUND
        null       | EXIST       || ErrorMessage.ACTIVITY_NOT_FOUND
        EXIST      | NO_EXIST    || ErrorMessage.USER_NOT_FOUND
        EXIST      | null        || ErrorMessage.USER_NOT_FOUND
    }

    def getActivityId(IDactivity){
        if(IDactivity == EXIST)
            return activityId
        else if(IDactivity == NO_EXIST)
            return 222
        return null
    }

    def getVolunteerId(IDvolunteer){
        if(IDvolunteer == EXIST)
            return volunteerId
        else if (IDvolunteer == NO_EXIST)
            return 222
        return null
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
