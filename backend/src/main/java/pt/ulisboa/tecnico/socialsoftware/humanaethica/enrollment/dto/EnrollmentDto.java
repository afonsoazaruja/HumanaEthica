package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class EnrollmentDto {

    private Integer id;
    private String motivation;
    private String enrollmentDateTime;
    private Integer activityId;
    private Integer volunteerId;

    public EnrollmentDto(){
    }

    public EnrollmentDto(Enrollment enrollment){
        setId(enrollment.getId());
        setMotivation(enrollment.getMotivation());
        setEnrollmentDateTime(DateHandler.toISOString(enrollment.getEnrollmentDateTime()));
        setActivityId(enrollment.getActivity().getId());
        setVolunteerId(enrollment.getVolunteer().getId());
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setEnrollmentDateTime(String enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
    }

    public String getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setVolunteerId(Integer volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Integer getVolunteerId() {
        return volunteerId;
    }

    @Override
    public String toString() {
        return "EnrollmentDTO{" +
                "id=" + id +
                ", motivation='" + motivation + '\'' +
                ", dateTime=" + enrollmentDateTime +
                "}";
    }
}

