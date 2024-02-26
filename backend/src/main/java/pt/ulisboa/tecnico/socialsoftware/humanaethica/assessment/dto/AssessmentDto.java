package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;

import java.time.LocalDateTime;

public class AssessmentDto {
    private Integer id;
    private String review;
    private LocalDateTime reviewDate;

    public AssessmentDto() {
    }

    public AssessmentDto(Assessment assessment) {
        setId(assessment.getId());
        setReview(assessment.getReview());
        setReviewDate(assessment.getReviewDate());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getReview() { return review; }

    public void setReview(String review) { this.review = review; }

    public LocalDateTime getReviewDate() { return reviewDate; }

    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    @Override
    public String toString() {
        return "AssessmentDto{" +
                "id=" + id +
                ", review='" + review + '\'' +
                ", reviewDate'" + reviewDate + '\'' +
                '}';
    }
}
