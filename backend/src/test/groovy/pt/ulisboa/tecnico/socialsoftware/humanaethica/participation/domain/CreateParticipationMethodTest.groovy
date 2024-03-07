package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class CreateParticipationMethodTest extends SpockTest {
    Activity activity = Mock()
    Activity otherActivity = Mock()
    Participation otherParticipation = Mock()
    Volunteer volunteer = Mock()
    def participationDto

    @Unroll
    def "create participation with activity and volunteer has another participation"() {
        given: "a volunteer"
        Volunteer otherVolunteer = Mock()
        and: "a participation"
        otherParticipation.getVolunteer() >> otherVolunteer
        and: "an activity with this participation and this volunteer"
        activity.getName() >> ACTIVITY_NAME_1
        activity.getNumberOfParticipants() >> 1
        activity.getParticipantsNumberLimit() >> 2
        activity.getApplicationDeadline() >> ONE_DAY_AGO
        activity.getParticipations() >> [otherParticipation]
        and: "another volunteer"
        volunteer.getName() >> USER_1_NAME
        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.rating = RATING_10

        when: "a participation is created with this new volunteer in the same activity"
        def result = new Participation(activity, volunteer, participationDto)

        then: "check results"
        result.getVolunteer() == volunteer
        result.getActivity() == activity
        result.getRating() == RATING_10
        and: "invocations"
        1 * activity.addParticipation(_)
        1 * volunteer.addParticipation(_)
    }

    @Unroll
    def "create participation with with same volunteer in another activity"() {
        given: "a volunteer"
        volunteer.getParticipations() >> [otherParticipation]
        and: "another participation"
        otherParticipation.getVolunteer() >> volunteer
        otherParticipation.getActivity() >> otherActivity
        and: "another activity"
        otherActivity.getParticipantsNumberLimit() >> 2
        otherActivity.getParticipations() >> [otherParticipation]
        and: "a activity"
        activity.getNumberOfParticipants() >> 1
        activity.getParticipantsNumberLimit() >> 2
        activity.getApplicationDeadline() >> ONE_DAY_AGO
        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.rating = 10

        when: "when a participation is created in another activity"
        def result = new Participation(activity, volunteer, participationDto)

        then: "check results"
        result.getVolunteer() == volunteer
        result.getActivity() == activity
        result.getRating() == 10
        and: "invocations"
        1 * activity.addParticipation(_)
        1 * volunteer.addParticipation(_)
    }

    @Unroll
    def "create participation and violate invariant participantsWithinLimit, participantsNumberLimit < participations"() {
        given: "an activity"
        activity.getNumberOfParticipants() >> 2
        activity.getName() >> ACTIVITY_NAME_1
        activity.getParticipantsNumberLimit() >> 1
        activity.getApplicationDeadline() >> ONE_DAY_AGO
        and: "a volunteer"
        volunteer.getName() >> USER_1_NAME
        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.rating = 10

        when:
        new Participation(activity, volunteer, participationDto)

        then: "check errors"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.PARTICIPATION_TOO_MANY_PARTICIPANTS
    }

    @Unroll
    def "create participation and violate invariant isUnique, two participations from the same volunteer"() {
        given: "an activity"
        activity.getName() >> ACTIVITY_NAME_1
        activity.getNumberOfParticipants() >> 1
        activity.getParticipantsNumberLimit() >> 2
        activity.getApplicationDeadline() >> ONE_DAY_AGO
        and: "a participation"
        otherParticipation.getVolunteer() >> volunteer
        otherParticipation.getActivity() >> activity
        and: "a volunteer"
        volunteer.getName() >> USER_1_NAME
        volunteer.getParticipations() >> [otherParticipation]
        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.rating = 10

        when:
        new Participation(activity, volunteer, participationDto)

        then: "check errors"
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.PARTICIPATION_VOLUNTEER_ALREADY_PARTICIPATES
    }

    @Unroll
    def "create participation and violate invariant isAfterDeadline, acceptanceDate isBefore ApplicationDeadline"() {
        given: "an activity"
        activity.getName() >> ACTIVITY_NAME_1
        activity.getNumberOfParticipants() >> 1
        activity.getParticipantsNumberLimit() >> 2
        activity.getApplicationDeadline() >> IN_THREE_DAYS
        and: "a volunteer"
        volunteer.getName() >> USER_1_NAME
        and: "a participation dto"
        participationDto = new ParticipationDto()
        participationDto.rating = 10
        participationDto.setAcceptanceDate(date instanceof LocalDateTime ? DateHandler.toISOString(date) : date as String)

        when:
        new Participation(activity, volunteer, participationDto)

        then: "check errors"
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
