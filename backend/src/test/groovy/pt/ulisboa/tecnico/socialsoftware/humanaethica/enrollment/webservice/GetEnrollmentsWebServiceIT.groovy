package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

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

        and: "a volunteer"
        volunteer1 = createVolunteer(
                USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                AuthUser.Type.NORMAL, User.State.SUBMITTED)

        and: "another volunteer"
        volunteer2 = createVolunteer(
                USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                AuthUser.Type.NORMAL, User.State.SUBMITTED)

        and: "an enrollment"
        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.setId(ENROLLMENT_ID_1)
        enrollmentDto.setMotivation(ENROLLMENT_MOTIVATION_1)

        def enrollment = new Enrollment(activity, volunteer1, enrollmentDto)
        enrollmentRepository.save(enrollment)

        enrollmentDto.setMotivation(ENROLLMENT_MOTIVATION_2)
        enrollment = new Enrollment(activity, volunteer2, enrollmentDto)
        enrollmentRepository.save(enrollment)
    }

    def "login as volunteer, and create an enrollment"() {
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
}
