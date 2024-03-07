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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetEnrollmentsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activity
    def volunteer1
    def volunteer2

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
    }

    def "login as member, and get enrollments"() {
        given: "2 volunteers"
        volunteer1 = new Volunteer(
                USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                AuthUser.Type.NORMAL, User.State.SUBMITTED)

        volunteer2 = new Volunteer(
                USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                AuthUser.Type.NORMAL, User.State.SUBMITTED)

        userRepository.save(volunteer1)
        userRepository.save(volunteer2)

        and: "2 enrollments"
        def enrollmentDto = new EnrollmentDto()

        enrollmentDto.setMotivation(ENROLLMENT_MOTIVATION_1)
        enrollmentRepository.save(new Enrollment(activity, volunteer1, enrollmentDto))

        enrollmentDto.setMotivation(ENROLLMENT_MOTIVATION_2)
        enrollmentRepository.save(new Enrollment(activity, volunteer2, enrollmentDto))

        and: "demo member (is member of demo institution)"
        demoMemberLogin()

        when:
        def response = webClient.get()
                .uri("/enrollments/" + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then: "we got 2 enrollments"
        response.size() == 2

        and: "enrollment 1 is correct"
        with (response.get(0)) {
            motivation == ENROLLMENT_MOTIVATION_1
            enrollmentDateTime != null
            activityId == activity.id
            volunteerId == volunteer1.id
        }

        and: "enrollment 2 is correct"
        with (response.get(1)) {
            motivation == ENROLLMENT_MOTIVATION_2
            enrollmentDateTime != null
            activityId == activity.id
            volunteerId == volunteer2.id
        }

        cleanup:
        deleteAll()
    }

    def "login as member of another institution, and get enrollments"() {
        given: "another institution"
        def otherInstitution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(otherInstitution)

        and: "member of that institution"
        createMember(
                USER_3_NAME, USER_3_USERNAME, USER_1_PASSWORD, USER_3_EMAIL,
                AuthUser.Type.NORMAL, otherInstitution, User.State.APPROVED)
        normalUserLogin(USER_3_USERNAME, USER_1_PASSWORD)

        when:
        webClient.get().uri("/enrollments/" + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        deleteAll()
    }

    def "login as volunteer, and get enrollments"() {
        given:
        demoVolunteerLogin()

        when:
        webClient.get().uri("/enrollments/" + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        deleteAll()
    }

    def "login as admin, and get enrollments"() {
        given:
        demoAdminLogin()

        when:
        webClient.get().uri("/enrollments/" + activity.id)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        deleteAll()
    }

    def "login as member, and get enrollments of invalid activity"() {
        given:
        demoMemberLogin()

        when:
        webClient.get().uri("/enrollments/222")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        deleteAll()
    }
}
