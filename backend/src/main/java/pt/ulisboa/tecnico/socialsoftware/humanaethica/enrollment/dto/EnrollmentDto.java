package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;

public class EnrollmentDto {
    private Integer id;
    private String motivation;
    private String dateTime;

    public EnrollmentDto(){
    }

    public EnrollmentDto(Enrollment enrollment){
        setId(enrollment.getId());
        setMotivation(enrollment.getMotivation());
        setDateTime(DateHandler.toISOString(enrollment.getDateTime()));
    }

    public void setId(Integer id) {this.id = id;}

    public Integer getId() {return id;}

    public void setMotivation(String motivation) {this.motivation = motivation;}

    public String getMotivation() {return motivation;}

    public void setDateTime(String dateTime) {this.dateTime = dateTime;}

    public String getDateTime() {return dateTime;}

    @Override
    public String toString() {
        return "EnrollmentDTO{" +
                "id=" + id +
                ", motivation='" + motivation + '\'' +
                ", dateTime=" + dateTime +
                "}";
    }
}

