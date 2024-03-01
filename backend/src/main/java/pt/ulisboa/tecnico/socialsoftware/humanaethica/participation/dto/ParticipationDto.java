package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto;

import jakarta.activation.DataHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;

public class ParticipationDto {
    private Integer id;
    private Integer rating;
    private LocalDateTime acceptanceDate;
    private Activity activity;
    private Volunteer volunteer;


    public ParticipationDto() {
    }

    public ParticipationDto(Participation participation) {
        setId(participation.getId());
        setRating(participation.getRating());
        setAcceptanceDate(participation.getAcceptanceDate());
        setActivity(participation.getActivity());
        setVolunteer(participation.getVolunteer());
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDateTime getAcceptanceDate() {
        return acceptanceDate;
    }

    public void setAcceptanceDate(LocalDateTime acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    @Override
    public String toString() {
        return "ParticipationDto{" +
                "rating=" + rating +
                ", acceptanceDate='" + acceptanceDate +
                '}';
    }
}
