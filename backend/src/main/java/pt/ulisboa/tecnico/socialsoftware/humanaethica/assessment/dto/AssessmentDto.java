package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;

public class AssessmentDto {
    private Integer id;
    private String review;
    private String reviewDate;

    public AssessmentDto() {
    }

    public AssessmentDto(Assessment assessment) {
        setId(assessment.getId());
        setReview(assessment.getReview());
        setReviewDate(DateHandler.toISOString(assessment.getReviewDate()));
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getReview() { return review; }

    public void setReview(String review) { this.review = review; }

    public String getReviewDate() { return reviewDate; }

    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }

    @Override
    public String toString() {
        return "AssessmentDto{" +
                "id=" + id +
                ", review='" + review + '\'' +
                ", reviewDate'" + reviewDate + '\'' +
                '}';
    }
}
