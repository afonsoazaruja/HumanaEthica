package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateEnrollmentWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity
    def enrollmentDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

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

        and: "an enrollment dto"
        enrollmentDto = new EnrollmentDto()
        enrollmentDto.setMotivation(ENROLLMENT_MOTIVATION_1)
    }

    def "login as volunteer, and create an enrollment"() {
        given:
        def volunteer = demoVolunteerLogin()

        when:
        def response = webClient.post()
                .uri("/enrollments/" + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then: "returned value is correct"
        with (response) {
            motivation == ENROLLMENT_MOTIVATION_1
            enrollmentDateTime != null
            activityId == activity.id
            volunteerId == volunteer.id
        }

        and: "enrollment was added to database"
        enrollmentRepository.count() == 1

        and: "enrollment in database is correct"
        def enrollment = enrollmentRepository.getEnrollmentsByActivity(activity.id).get(0)

        enrollment.motivation == ENROLLMENT_MOTIVATION_1
        enrollment.enrollmentDateTime != null
        enrollment.activity.id == activity.id
        enrollment.volunteer.id == volunteer.id
    }
}
