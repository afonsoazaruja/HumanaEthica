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
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
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
