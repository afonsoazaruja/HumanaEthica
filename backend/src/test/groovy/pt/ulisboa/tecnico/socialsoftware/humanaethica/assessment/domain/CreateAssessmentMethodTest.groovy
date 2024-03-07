package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import spock.lang.Unroll

@DataJpaTest
class CreateAssessmentMethodTest extends SpockTest {
    Institution institution = Mock()
    Volunteer volunteer = Mock()
    Activity activity = Mock()
    Assessment otherAssessment = Mock()
    Volunteer otherVolunteer = Mock()
    def assessmentDto

    def "successfully create a new assessment where the institution has another assessment"() {
        given:
        activity.getEndingDate() >> TWO_DAYS_AGO
        institution.getActivities() >> [activity]
        institution.getAssessments() >> [otherAssessment]
        otherAssessment.getVolunteer() >> otherVolunteer
        and: "an assessment dto"
        assessmentDto = new AssessmentDto()
        assessmentDto.setReview(ASSESSMENT_REVIEW_1)

        when:
        Assessment result = new Assessment(institution, volunteer, assessmentDto)

        then: "check result"
        result.getInstitution() == institution
        result.getVolunteer() == volunteer
        result.getReview() == ASSESSMENT_REVIEW_1
        // check if methods were called only once
        and: "invocations"
        1 * institution.addAssessment(_)
        1 * volunteer.addAssessment(_)
    }


    @Unroll
    def "create assessment and violate review has at least 10 characters invariant : review=#review"() {
        given:
        activity.getEndingDate() >> TWO_DAYS_AGO
        institution.getActivities() >> [activity]
        and: "an assessment dto"
        assessmentDto = new AssessmentDto()
        assessmentDto.setReview(review)

        when:
        new Assessment(institution, volunteer, assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ASSESSMENT_INVALID_REVIEW

        where:
        review << [null, "", " ", "123456789"]
    }

    def "create assessment violate unique institution for volunteer"() {
        given:
        activity.getEndingDate() >> TWO_DAYS_AGO
        institution.getActivities() >> [activity]
        otherAssessment.getVolunteer() >> volunteer
        otherAssessment.getInstitution() >> institution
        institution.getAssessments() >> [otherAssessment]
        and: "an assessment dto"
        assessmentDto = new AssessmentDto()
        assessmentDto.setReview(ASSESSMENT_REVIEW_1)

        when:
        new Assessment(institution, volunteer, assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.VOLUNTEER_ALREADY_MADE_ASSESSMENT_FOR_INSTITUTION
    }

    def "create an assessment whose institution has no activities"() {
        given: "an assessment dto"
        assessmentDto = new AssessmentDto()
        assessmentDto.setReview(ASSESSMENT_REVIEW_1)

        when:
        new Assessment(institution, volunteer, assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_HAS_NO_COMPLETED_ACTIVITIES

    }

    @Unroll
    def "create assessment and violate institution can only be assessed when it has at least one completed activity : deadline=#deadline"() {
        given:
        activity.getEndingDate() >> deadline
        institution.getActivities() >> [activity]
        and: "an assessment dto"
        assessmentDto = new AssessmentDto()
        assessmentDto.setReview(ASSESSMENT_REVIEW_1)

        when:
        new Assessment(institution, volunteer, assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.INSTITUTION_HAS_NO_COMPLETED_ACTIVITIES

        where:
        deadline << [IN_ONE_DAY, IN_THREE_DAYS, IN_TWO_DAYS]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
