package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class CreateAssessmentServiceTest extends SpockTest {
    public static final String EXIST = "exist"
    public static final String NO_EXIST = "noExist"
    def volunteer
    def institution
    def assessmentDto

    def setup() {
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
        institution = institutionService.getDemoInstitution()
        assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1)
    }

    def "create assessment"() {
        given: "an assessment dto"
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                DateHandler.now().minusDays(3),TWO_DAYS_AGO,ONE_DAY_AGO,null)
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        def activity = new Activity(activityDto, institution, themes)
        when:
        def result = assessmentService.createAssessment(volunteer.getId(), institution.getId(), assessmentDto)

        then: "the returned data is correct"
        result.review == ASSESSMENT_REVIEW_1
        and: "the assessment is saved in the database"
        assessmentRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedAssessment = assessmentRepository.findById(result.id).get()
        storedAssessment.review == ASSESSMENT_REVIEW_1
    }

    @Unroll
    def 'invalid arguments: volunteerId=#volunteerId | institutionId=#institutionId'() {
        given:
        def assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1)

        when:
        assessmentService.createAssessment(getVolunteerId(volunteerId), getInstitutionId(institutionId), assessmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage
        and: "no assessment is stored in the database"
        assessmentRepository.findAll().size() == 0

        where:
        volunteerId     | institutionId   || errorMessage
        null            | EXIST           || ErrorMessage.USER_NOT_FOUND
        NO_EXIST        | EXIST           || ErrorMessage.USER_NOT_FOUND
        EXIST           | null            || ErrorMessage.INSTITUTION_NOT_FOUND
        EXIST           | NO_EXIST        || ErrorMessage.INSTITUTION_NOT_FOUND
    }

    def getVolunteerId(volunteerId){
        if (volunteerId == EXIST)
            return volunteer.id
        else if (volunteerId == NO_EXIST)
            return 222
        return null
    }

    def getInstitutionId(institutionId){
        if (institutionId == EXIST)
            return institution.id
        else if (institutionId == NO_EXIST)
            return 222
        return null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}