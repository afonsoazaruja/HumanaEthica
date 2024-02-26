package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;

import java.time.LocalDateTime;

public class Participation {
    private Integer rating;
    private LocalDateTime acceptanceDate;

    public Participation(Integer rating, LocalDateTime acceptanceDate) {
        this.rating = rating;
        this.acceptanceDate = acceptanceDate;
    }

    public Integer getRating() {
        return rating;
    }

    public LocalDateTime getAcceptanceDate() {
        return acceptanceDate;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setAcceptanceDate(LocalDateTime acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }
}
