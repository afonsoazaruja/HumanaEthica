package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
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
        given:
        demoVolunteerLogin()

        when:
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

}