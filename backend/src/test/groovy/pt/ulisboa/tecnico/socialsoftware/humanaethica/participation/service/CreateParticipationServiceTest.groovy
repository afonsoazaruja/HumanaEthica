package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme

@DataJpaTest
class CreateParticipationServiceTest extends SpockTest {

    def volunteer
    def activityId

    def setup() {
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
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
        given: "participation info"
        def participationDto = createParticipationDto(RATING_10)

        when:
        def result = participationService.createParticipation(activityId, volunteer.getId(), participationDto)

        then: "the returned data is correct"
        result.activity.getId() == activityId
        result.volunteer.getName() == volunteer.getName()
        result.rating == RATING_10
        result.acceptanceDate != null
        and: "the activity is saved in the database"
        participationRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedParticipation = participationRepository.getParticipationByActivityId(activityId).get(0)
        storedParticipation.activity.id == activityId
        storedParticipation.volunteer.name == volunteer.getName()
        storedParticipation.rating == RATING_10
        storedParticipation.acceptanceDate != null

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
