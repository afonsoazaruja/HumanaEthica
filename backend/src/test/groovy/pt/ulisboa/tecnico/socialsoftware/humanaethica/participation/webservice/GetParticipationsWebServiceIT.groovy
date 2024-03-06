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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetParticipationsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activityId
    def volunteerId1
    def volunteerId2


    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()
        given: "activity info"
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,2,ACTIVITY_DESCRIPTION_1,
                ONE_DAY_AGO,IN_TWO_DAYS,IN_THREE_DAYS,null)
        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        and: "an activity"
        def activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
        activityId = activity.getId()
        and: "a volunteer"
        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        volunteerId1 = volunteer.getId()
        and: "a participation"
        def participationDto = createParticipationDto(RATING_10, volunteerId1)
        def participation = new Participation(activity, volunteer, participationDto)
        participationRepository.save(participation)
        and: "another volunteer"
        volunteer = new Volunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)
        volunteerId2 = volunteer.getId()
        and: "another participation"
        participationDto.setRating(RATING_1)
        participationDto.setVolunteerId(volunteerId2)
        participation = new Participation(activity, volunteer, participationDto)
        participationRepository.save(participation)
    }

    def "get two participations from an activity"() {
        given: "a member"
        demoMemberLogin()

        when: "the member gets the participations"
        def response = webClient.get()
                .uri('/participations/' + activityId + '/get')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
                .collectList()
                .block()

        then: "check response status"
        response.size() == 2
        response.get(0).activity.getName() == ACTIVITY_NAME_1
        response.get(0).volunteerId == volunteerId1
        response.get(0).rating == RATING_10
        response.get(0).acceptanceDate != null
        response.get(1).activity.getName() == ACTIVITY_NAME_1
        response.get(1).volunteerId == volunteerId2
        response.get(1).rating == RATING_1
        response.get(1).acceptanceDate != null

        cleanup:
        deleteAll()
    }

    def "get participaions with invalid activityId"() {
        given: "a member"
        demoMemberLogin()
        and: "an acitivityId with blanks"
        activityId = "  "

        when: "the member gets the participations"
        webClient.get()
                .uri('/participations/' + activityId + '/get')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
                .collectList()
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and get participations"() {
        given: "a volunteer"
        demoVolunteerLogin()

        when: "the volunteer gets the activities"
        webClient.get()
                .uri('/participations/' + activityId + '/get')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
                .collectList()
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        deleteAll()
    }

    def "login as admin, and get participations"() {
        given: "an admin"
        demoAdminLogin()

        when: "the admin gets the participations"
        webClient.get()
                .uri('/participations/' + activityId + '/get')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(ParticipationDto.class)
                .collectList()
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        deleteAll()
    }
}
