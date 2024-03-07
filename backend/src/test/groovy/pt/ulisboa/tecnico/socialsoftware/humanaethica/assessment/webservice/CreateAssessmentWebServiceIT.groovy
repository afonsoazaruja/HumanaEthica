package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateAssessmentWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def assessmentDto
    def institution

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        given: "assessment info and an institution with a completed activity"
        assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1)
        institution = institutionService.getDemoInstitution()
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                DateHandler.now().minusDays(3),TWO_DAYS_AGO,ONE_DAY_AGO,null)
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        def activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
    }

    def "login as volunteer, and create an assessment"() {
        given: 'a volunteer'
        demoVolunteerLogin()

        when: 'the volunteer creates an assessment'
        def response = webClient.post()
                .uri('/assessments/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "check response data"
        response.review == ASSESSMENT_REVIEW_1
        and: 'check database data'
        assessmentRepository.count() == 1
        def assessment = assessmentRepository.findAll().get(0)
        assessment.review == ASSESSMENT_REVIEW_1

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and create an assessment with error (invalid assessmentDto)"() {
        given: 'a volunteer'
        demoVolunteerLogin()
        and: 'an invalid review'
        assessmentDto.review = " "

        when: 'the volunteer creates an assessment'
        webClient.post()
                .uri('/assessments/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        assessmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and create an assessment with error (invalid institution id)"() {
        given: 'a volunteer'
        demoVolunteerLogin()
        and: 'an invalid institution id'
        institution.id = 222

        when: 'the volunteer creates an assessment'
        webClient.post()
                .uri('/assessments/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        assessmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as member, and create an assessment"() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member creates an assessment'
        webClient.post()
                .uri('/assessments/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        assessmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as admin, and create an assessment"() {
        given: 'an admin'
        demoAdminLogin()

        when: 'the admin creates an assessment'
        webClient.post()
                .uri('/assessments/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        assessmentRepository.count() == 0

        cleanup:
        deleteAll()
    }
}