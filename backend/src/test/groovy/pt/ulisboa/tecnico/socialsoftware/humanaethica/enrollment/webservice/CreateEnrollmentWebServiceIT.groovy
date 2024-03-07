package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.webservice

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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

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

        given: "demo institution"
        def institution = institutionService.getDemoInstitution()

        and: "activity dto"
        def activityDto = new ActivityDto()
        activityDto.setName(ACTIVITY_NAME_1)
        activityDto.setRegion(ACTIVITY_REGION_1)
        activityDto.setDescription(ACTIVITY_DESCRIPTION_1)
        activityDto.setParticipantsNumberLimit(1)
        activityDto.setStartingDate(DateHandler.toISOString(IN_TWO_DAYS))
        activityDto.setEndingDate(DateHandler.toISOString(IN_THREE_DAYS))
        activityDto.setApplicationDeadline(DateHandler.toISOString(IN_ONE_DAY))

        and: "activity"
        activity = new Activity(activityDto, institution, Collections.emptyList())
        activityRepository.save(activity)

        and: "enrollment dto"
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

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and create an invalid enrollment"() {
        given:
        demoVolunteerLogin()

        and: "invalid motivation"
        enrollmentDto.motivation = "  "

        when:
        webClient.post().uri("/enrollments/" + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST

        and: "enrollment was not stored"
        enrollmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as member, and create an enrollment"() {
        given:
        demoMemberLogin()

        when:
        webClient.post().uri("/enrollments/" + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        and: "enrollment was not stored"
        enrollmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as admin, and create an enrollment"() {
        given:
        demoAdminLogin()

        when:
        webClient.post().uri("/enrollments/" + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(enrollmentDto)
                .retrieve()
                .bodyToMono(EnrollmentDto.class)
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        and: "enrollment was not stored"
        enrollmentRepository.count() == 0

        cleanup:
        deleteAll()
    }
}
