package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class EnrollmentDto {
    private Integer id;
    private String motivation;
    private String enrollmentDateTime;
    private Integer volunteerId;
    private boolean participating;

    public EnrollmentDto() {}

    public EnrollmentDto(Enrollment enrollment) {
        this.id = enrollment.getId();
        this.motivation = enrollment.getMotivation();
        this.enrollmentDateTime = DateHandler.toISOString(enrollment.getEnrollmentDateTime());
        this.volunteerId = enrollment.getVolunteer().getId();
        this.participating = enrollment.getActivity().getParticipations()
                .stream()
                .anyMatch(participation -> participation.getVolunteer().getId().equals(volunteerId));

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    public void setEnrollmentDateTime(String enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
    }

    public Integer getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Integer volunteerId) {
        this.volunteerId = volunteerId;
    }

    public boolean isParticipating() {
        return participating;
    }

    public void setParticipating(boolean participating) {
        this.participating = participating;
    }
}
