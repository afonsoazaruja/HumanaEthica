package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain

import jakarta.activation.DataHandler
import jakarta.persistence.criteria.CriteriaBuilder
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateParticipationMethodTest extends SpockTest {
    Activity activity = Mock()
    Participation otherParticipation = Mock()
    Volunteer volunteer = Mock()
    def participationDto

    @Unroll
    def "create participation with activity and volunteer has another participation"() {
        given:
        otherParticipation.getVolunteer().getName() >> USER_2_NAME
        activity.getNumberOfParticipants() >> 1
        activity.getName() >> ACTIVITY_NAME_1
        activity.getRegion() >> ACTIVITY_REGION_1
        activity.getParticipantsNumberLimit() >> 2
        activity.getDescription() >> ACTIVITY_DESCRIPTION_1
        activity.getApplicationDeadline() >> ONE_DAY_AGO
        volunteer.getName() >> USER_1_NAME
        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.rating = 10


        when:
        def result = new Participation(activity, volunteer, participationDto)

        then: "check results"
        result.getVolunteer().getName() == USER_1_NAME
        result.getActivity().getName() == ACTIVITY_NAME_1
        result.getRating() == 10
        and: "invocations"
        1 * activity.addParticipation(_)
        1 * volunteer.addParticipation(_)
    }

    @Unroll
    def "create participation and violate invariant participantsWithinLimit, participantsNumberLimit < participations"() {
        given:
        activity.getNumberOfParticipants() >> 2
        activity.getName() >> ACTIVITY_NAME_1
        activity.getRegion() >> ACTIVITY_REGION_1
        activity.getParticipantsNumberLimit() >> 1
        activity.getDescription() >> ACTIVITY_DESCRIPTION_1
        activity.getApplicationDeadline() >> ONE_DAY_AGO
        volunteer.getName() >> USER_1_NAME
        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.rating = 10

        when:
        new Participation(activity, volunteer, participationDto)

        then: "check results"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.PARTICIPATION_TOO_MANY_PARTICIPANTS
    }

    @Unroll
    def "create participation and violate invariant isUnique, two participations from the same volunteer"() {
        given:
        activity.getName() >> ACTIVITY_NAME_1
        activity.getRegion() >> ACTIVITY_REGION_1
        activity.getNumberOfParticipants() >> 1
        activity.getParticipantsNumberLimit() >> 2
        activity.getDescription() >> ACTIVITY_DESCRIPTION_1
        activity.getApplicationDeadline() >> ONE_DAY_AGO
        otherParticipation.getVolunteer() >> volunteer
        otherParticipation.getActivity() >> activity
        volunteer.getName() >> USER_1_NAME
        volunteer.getParticipations() >> [otherParticipation]
        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.rating = 10

        when:
        new Participation(activity, volunteer, participationDto)

        then: "check results"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.PARTICIPATION_VOLUNTEER_ALREADY_PARTICIPATES
    }

    @Unroll
    def "create participation and violate invariant isAfterDeadline, acceptanceDate isBefore ApplicationDeadline"() {
        given:
        activity.getName() >> ACTIVITY_NAME_1
        activity.getRegion() >> ACTIVITY_REGION_1
        activity.getNumberOfParticipants() >> 1
        activity.getParticipantsNumberLimit() >> 2
        activity.getDescription() >> ACTIVITY_DESCRIPTION_1
        activity.getApplicationDeadline() >> IN_THREE_DAYS
        volunteer.getName() >> USER_1_NAME
        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.rating = 10
        participationDto.setAcceptanceDate(date instanceof LocalDateTime ? DateHandler.toISOString(date) : date as String)

        when:
        new Participation(activity, volunteer, participationDto)

        then: "check results"
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        date         || errorMessage
        NOW          || ErrorMessage.ACCEPTANCEDATE_IS_BEFORE_DEADLINE
        IN_ONE_DAY   || ErrorMessage.ACCEPTANCEDATE_IS_BEFORE_DEADLINE
        IN_TWO_DAYS  || ErrorMessage.ACCEPTANCEDATE_IS_BEFORE_DEADLINE
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
