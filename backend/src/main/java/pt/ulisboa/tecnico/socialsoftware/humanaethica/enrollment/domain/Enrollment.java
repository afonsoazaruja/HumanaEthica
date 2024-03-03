package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
@Table(name = "enrollment")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String motivation;
    private LocalDateTime enrollmentDateTime;

    @ManyToOne
    @JoinColumn(name = "volunteer")
    private Volunteer volunteer;

    @ManyToOne
    @JoinColumn(name = "activity")
    private Activity activity;

    public Enrollment() {
    }

    public Enrollment(Activity activity, Volunteer volunteer, EnrollmentDto enrollmentDto) {
        setMotivation(enrollmentDto.getMotivation());
        setEnrollmentDateTime(DateHandler.toLocalDateTime(enrollmentDto.getEnrollmentDateTime()));
        setActivity(activity);
        setVolunteer(volunteer);

        verifyInvariants();
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
        return enrollmentDateTime;
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

    public void setEnrollmentDateTime(LocalDateTime dateTime) {
        this.enrollmentDateTime = dateTime;
    }

    public LocalDateTime getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    private void verifyInvariants() {
        verifyMotivationLength();
        enrollmentIsUnique();
        enrollmentBeforeDeadline();
    }

    private void verifyMotivationLength() {
        if (this.motivation == null || this.motivation.length() < 10) {
            throw new HEException(ENROLLMENT_MOTIVATION_INVALID, this.motivation);
        }
    }

    private void enrollmentIsUnique() {
        if (this.volunteer.getEnrollments().contains(this)) {
            throw new HEException(ENROLLMENT_ALREADY_EXISTS, this.id);
        }
    }

    private void enrollmentBeforeDeadline() {
        if (this.enrollmentDateTime.isAfter(this.activity.getApplicationDeadline())) {
            throw new HEException(ENROLLMENT_AFTER_DEADLINE);
        }
    }
}
