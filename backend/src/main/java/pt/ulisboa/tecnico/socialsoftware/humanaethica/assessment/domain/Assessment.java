package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
@Table(name = "assessment")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String review;
    private LocalDateTime reviewDate;

    @ManyToOne
    private Institution institution;

    @ManyToOne
    private Volunteer volunteer;

    public Assessment() {
    }

    public Assessment(Institution institution, Volunteer volunteer, AssessmentDto assessmentDto) {
        setReview(assessmentDto.getReview());
        setReviewDate(DateHandler.now());
        setInstitution(institution);
        setVolunteer(volunteer);
        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        institution.addAssessment(this);
    }

    public Institution getInstitution() {
        return this.institution;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addAssessment(this);
    }

    public Volunteer getVolunteer() {
        return this.volunteer;
    }

    private void verifyInvariants() {
        isValidReview();
        assessmentIsUnique();
        institutionHasOneCompletedActivity();
    }

    private void isValidReview() { // Invariant 1
        if (this.review == null || this.review.length() < 10) {
            throw new HEException(ASSESSMENT_INVALID_REVIEW, this.review);
        }
    }

    private void assessmentIsUnique() { // Invariant 2
        if (this.institution.getAssessments() == null) {
            return;
        }
        if (this.institution.getAssessments().stream()
                .anyMatch(assessment -> assessment != this && assessment.getVolunteer().equals(this.volunteer))) {
            throw new HEException(VOLUNTEER_ALREADY_MADE_ASSESSMENT_FOR_INSTITUTION);
        }
    }

    private void institutionHasOneCompletedActivity() { // Invariant 3
        if (this.institution.getActivities() == null || this.institution.getActivities().stream()
                .noneMatch(activity -> activity.getEndingDate().isBefore(this.reviewDate))) {
            throw new HEException(INSTITUTION_HAS_NO_COMPLETED_ACTIVITIES);
        }
    }
}