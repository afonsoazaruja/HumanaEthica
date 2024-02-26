package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;

@Entity
@Table(name = "Enrollment")
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
}
