package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
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
    }


    public Integer getId() {
        return id;
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
        return this.activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }


    private void verifyInvariants() {
        participantsWithinLimit();
        isUnique();
        isAfterAcceptanceDate();
    }

    private void participantsWithinLimit() {
        if (activity.getParticipations().size() > activity.getParticipantsNumberLimit()) {
            throw new HEException(ErrorMessage.PARTICIPATION_TOO_MANY_PARTICIPANTS, activity.getParticipantsNumberLimit());
        }
    }

    private void isUnique() {
        if (volunteer.verifyParticipation(this)) {
            throw new HEException(ErrorMessage.PARTICIPATION_VOLUNTEER_ALREADY_PARTICIPATES, volunteer.getName());
        }
    }

    private void isAfterAcceptanceDate() {
        if (this.acceptanceDate.isBefore(activity.getApplicationDeadline())) {
            throw new HEException(ErrorMessage.PARTICIPATION_IS_BEFORE_ACCEPTANCE_DATE, activity.getApplicationDeadline().toString());
        }
    }

}
