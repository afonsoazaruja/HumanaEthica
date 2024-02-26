package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import java.time.LocalDateTime;

public class Assessment {
    private String review;
    private LocalDateTime reviewDate;

    public Assessment(String review, LocalDateTime reviewDate) {
        setReview(review);
        setReviewDate(DateHandler.now());
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
}