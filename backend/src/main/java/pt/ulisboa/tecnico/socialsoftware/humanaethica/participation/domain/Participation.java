package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;
@Entity
@Table(name = "participation")
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer rating;
    private LocalDateTime acceptanceDate;

    @ManyToOne
    private Activity activity;

    @ManyToOne
    private Volunteer volunteer;

    public Participation() {

    }
    public Participation(Integer rating, LocalDateTime acceptanceDate, Activity activity, Volunteer volunteer) {
        this.rating = rating;
        this.acceptanceDate = acceptanceDate;
        this.activity = activity;
        this.volunteer = volunteer;
        addParticipation();
    }


    public Integer getId() {
        return id;
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

    public void addParticipation(){
        boolean participantsWithinLimit = activity.getParticipations().size() < activity.getParticipantsNumberLimit();

        if (participantsWithinLimit) {
            this.activity.addParticipation(this);
            this.volunteer.addParticipation(this);
        }
    }

    public void removeParticipation(){activity.removeParticipation(this);volunteer.removeParticipation(this);}
    public Activity getActivity() {
        return this.activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

}

