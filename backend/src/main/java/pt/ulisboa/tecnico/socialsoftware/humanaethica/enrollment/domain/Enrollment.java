package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
@Table(name = "enrollment")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String motivation;
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "volunteer")
    private Volunteer volunteer;

    @ManyToOne
    @JoinColumn(name = "activity")
    private Activity activity;

    public Enrollment() {
    }

    public Integer getId() {
        return id;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public Activity getActivity() {
        return activity;
    }

    public String getMotivation() {
        return motivation;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    private void verifyInvariants() {
        verifyMotivationLength();
        enrollmentIsUnique();
    }
    private void verifyMotivationLength() {
        if (this.motivation.length() < 10) {
            throw new HEException(ENROLLMENT_MOTIVATION_INVALID, this.motivation);
        }
    }
    private void enrollmentIsUnique() {
        if (this.volunteer.getEnrollments().stream()
                .anyMatch(enrollment -> enrollment != this && enrollment.getVolunteer().equals(this.volunteer))) {
            throw new HEException(ENROLLMENT_ALREADY_EXISTS, this.id);
        }
    }
}
