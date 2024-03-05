package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetAssessmentsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def institution

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        given: "an institution with a completed activity"
        institution = institutionService.getDemoInstitution()
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                DateHandler.now().minusDays(3),TWO_DAYS_AGO,ONE_DAY_AGO,null)
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        def activity = new Activity(activityDto, institution, themes)

        and: "two volunteers"
        def volunteer1 = new Volunteer(USER_1_NAME, User.State.ACTIVE)
        userRepository.save(volunteer1)
        def volunteer2 = new Volunteer(USER_2_NAME, User.State.ACTIVE)
        userRepository.save(volunteer2)

        and: "an assessment"
        def assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1)
        def assessment = new Assessment(institution, volunteer1, assessmentDto)
        assessmentRepository.save(assessment)
        and: "another assessment"
        assessmentDto.setReview(ASSESSMENT_REVIEW_2)
        assessment = new Assessment(institution, volunteer2, assessmentDto)
        assessmentRepository.save(assessment)
    }

    def "get assessments"() {
        when:
        def response = webClient.get()
                .uri("/assessments/" + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(AssessmentDto.class)
                .collectList()
                .block()

        then: "check response"
        response.size() == 2
        response.get(0).review == ASSESSMENT_REVIEW_1
        response.get(1).review == ASSESSMENT_REVIEW_2

        cleanup:
        deleteAll()
    }
}
