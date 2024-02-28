package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;

import java.time.LocalDateTime;

public class EnrollmentDto {
    private Integer id;
    private String motivation;
    private LocalDateTime dateTime;

    public EnrollmentDto(){
    }

    public EnrollmentDto(Enrollment enrollment){
        setId(enrollment.getId());
        setMotivation(enrollment.getMotivation());
        setDateTime(enrollment.getDateTime());
    }
    public void setId(Integer id) {this.id = id;}

    public Integer getId() {return id;}

    public void setMotivation(String motivation) {this.motivation = motivation;}

    public String getMotivation() {return motivation;}

    public void setDateTime(LocalDateTime dateTime) {this.dateTime = dateTime;}

    public LocalDateTime getDateTime() {return dateTime;}

    @Override
    public String toString() {
        return "EnrollmentDTO{" +
                "id=" + id +
                ", motivation='" + motivation + '\'' +
                ", dateTime=" + dateTime +
                "}";
    }
}

