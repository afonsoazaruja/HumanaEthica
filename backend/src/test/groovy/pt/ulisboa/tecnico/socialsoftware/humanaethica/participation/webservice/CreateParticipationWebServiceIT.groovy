package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateParticipationWebServiceIT extends SpockTest{

    @LocalServerPort
    private int port

    def participationDto
    def activityId
    def volunteerId

    def setup(){
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        volunteerId = authUserService.loginDemoVolunteerAuth().getUser().getId()
        def institution = institutionService.getDemoInstitution()
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,2,ACTIVITY_DESCRIPTION_1,
                ONE_DAY_AGO,IN_TWO_DAYS,IN_THREE_DAYS,null)
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        def activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
        activityId = activity.getId()
        participationDto = createParticipationDto(RATING_10, volunteerId)

    }

    def "login as member, and create an participation"(){
        given: "a member"
        demoMemberLogin()

        when: "the member creates a participation"
        def response = webClient.post()
                .uri('/participations/' + activityId + '/create')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDto)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()


        then: "the returned data is correct"

        response.activity.getId() == activityId
        response.rating == RATING_10
        response.getVolunteerId() == volunteerId
        response.acceptanceDate != null
        and: "the activity is saved in the database"
        participationRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedParticipation = participationRepository.getParticipationByActivityId(activityId).get(0)
        storedParticipation.activity.id == activityId
        storedParticipation.rating == RATING_10
        storedParticipation.getVolunteer().getId() == volunteerId
        storedParticipation.acceptanceDate != null


        cleanup:
        deleteAll()
    }

    def "login as member, and create an participation with error"() {
        given: 'a member'
        demoMemberLogin()
        and: 'a no existing volunteer Id'
        participationDto.volunteerId = 222

        when: 'the member creates the participation'
        webClient.post()
                .uri('/participations/' + activityId + '/create')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDto)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "check response status"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        participationRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and create a participation"() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when: 'the volunteer creates the participation'
        webClient.post()
                .uri('/participations/' + activityId + '/create')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDto)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        participationRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as admin, and create a participation"() {
        given: 'a admin'
        demoAdminLogin()

        when: 'the admin creates the participation'
        webClient.post()
                .uri('/participations/' + activityId + '/create')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(participationDto)
                .retrieve()
                .bodyToMono(ParticipationDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        participationRepository.count() == 0

        cleanup:
        deleteAll()
    }

}
