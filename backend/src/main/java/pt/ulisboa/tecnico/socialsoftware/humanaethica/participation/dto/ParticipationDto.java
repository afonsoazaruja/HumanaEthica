package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto;

import jakarta.activation.DataHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;

import java.time.LocalDateTime;

public class ParticipationDto {
    private Integer rating;
    private LocalDateTime acceptanceDate;

    public ParticipationDto() {
    }

    public ParticipationDto(Participation participation) {
        setRating(participation.getRating());
        setAcceptanceDate(participation.getAcceptanceDate());
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

    @Override
    public String toString() {
        return "ParticipationDto{" +
                "rating=" + rating +
                ", acceptanceDate='" + acceptanceDate +
                '}';
    }
}
