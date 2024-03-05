package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

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


    public Participation(Activity activity, Volunteer volunteer, ParticipationDto participationDto) {
        setRating(participationDto.getRating());
        setAcceptanceDate(DateHandler.now());
        setActivity(activity);
        setVolunteer(volunteer);
        verifyInvariants();
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
        activity.addParticipation(this);
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addParticipation(this);
    }

    private void verifyInvariants() {
        participantsWithinLimit();
        isUnique();
        isAfterDeadline();
    }

    private void participantsWithinLimit() {
        if (activity.getNumberOfParticipants() > activity.getParticipantsNumberLimit()) {
            throw new HEException(ErrorMessage.PARTICIPATION_TOO_MANY_PARTICIPANTS, activity.getParticipantsNumberLimit());
        }
    }

    private void isUnique() {
        if (this.volunteer.getParticipations() == null){
            return;
        }
        if (this.volunteer.getParticipations().stream()
                .anyMatch(participation -> participation != this && participation.getVolunteer().equals(this.volunteer))) {
            throw new HEException(ErrorMessage.PARTICIPATION_VOLUNTEER_ALREADY_PARTICIPATES, volunteer.getName());
        }
    }

    private void isAfterDeadline() {
        if (this.acceptanceDate.isBefore(activity.getApplicationDeadline())) {
            throw new HEException(ErrorMessage.ACCEPTANCEDATE_IS_BEFORE_DEADLINE, activity.getApplicationDeadline().toString());
        }
    }

}
