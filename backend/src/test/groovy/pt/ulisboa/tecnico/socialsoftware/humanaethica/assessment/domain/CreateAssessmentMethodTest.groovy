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
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class CreateAssessmentMethodTest extends SpockTest{
    Institution institution = Mock()
    Volunteer volunteer = Mock()
    Activity activity = Mock()
    def assessmentDto

    @Unroll
    def "create assessment and violate review has at least 10 characters invariant : review=#review"() {
        given:
        activity.getEndingDate() >> DateHandler.toISOString(TWO_DAYS_AGO)
        institution.getActivities() >> [activity]
        and: "an assessment dto"
        assessmentDto = new AssessmentDto()
        assessmentDto.setReview(review)
        assessmentDto.setReviewDate(NOW)

        when:
        new Assessment(institution, volunteer, assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ASSESSMENT_INVALID_REVIEW

        where:
        review << [null, "", " ", "123456789"]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
