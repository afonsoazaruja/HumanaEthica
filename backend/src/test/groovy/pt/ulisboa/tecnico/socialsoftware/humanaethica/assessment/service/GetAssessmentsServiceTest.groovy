package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.AssessmentService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@DataJpaTest
class GetAssessmentsServiceTest extends SpockTest {
    def "get two assessments"() {
        given: "an institution with a completed activity and assessment info"
        def institution = institutionService.getDemoInstitution()
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                DateHandler.now().minusDays(3),TWO_DAYS_AGO,ONE_DAY_AGO,null)
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        def activity = new Activity(activityDto, institution, themes)
        def assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1)

        and: "two volunteers"
        def volunteer1 = new Volunteer(USER_1_NAME, User.State.ACTIVE)
        userRepository.save(volunteer1)
        def volunteer2 = new Volunteer(USER_2_NAME, User.State.ACTIVE)
        userRepository.save(volunteer2)

        and: "an assessment"
        def assessment = new Assessment(institution, volunteer1, assessmentDto)
        assessmentRepository.save(assessment)
        and: "another assessment"
        assessmentDto.setReview(ASSESSMENT_REVIEW_2)
        assessment = new Assessment(institution, volunteer2, assessmentDto)
        assessmentRepository.save(assessment)

        when:
        def result = assessmentService.getAssessmentsByInstitution(institution.id)

        then:
        result.size() == 2
        result.get(0).review == ASSESSMENT_REVIEW_1
        result.get(1).review == ASSESSMENT_REVIEW_2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
