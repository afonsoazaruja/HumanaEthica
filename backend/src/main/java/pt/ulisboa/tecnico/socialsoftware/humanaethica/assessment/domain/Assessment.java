package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import java.time.LocalDateTime;

public class Assessment {
    private String review;
    private LocalDateTime reviewDate;
    private Institution institution;
    private Volunteer volunteer;

    public Assessment(Institution institution, Volunteer volunteer, String review, LocalDateTime reviewDate) {
        setReview(review);
        setReviewDate(DateHandler.now());
        setInstitution(institution);
        setVolunteer(volunteer);
        institution.addAssessment(this);
    }

    public String getReview() {
        return review;
    }
    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReview(String review) {
        this.review = review;
    }
    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }
    public Institution getInstitution() {
        return this.institution;
    }
    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }
    public Volunteer getVolunteer() {
        return this.volunteer;
    }
}