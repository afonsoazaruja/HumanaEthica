package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {
    private List<Assessment> assessments = new ArrayList<>();
    public Volunteer() {
    }

    public Volunteer(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.VOLUNTEER, type, state);
    }

    public Volunteer(String name, State state) {
        super(name, Role.VOLUNTEER, state);
    }

    public void makeAssessment(String review, LocalDateTime reviewDate, Institution institution) {
        Assessment assessment = new Assessment(institution, this, review, reviewDate);
        addAssessment(assessment);
    }

    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }

    public void removeAssessment(Assessment assessment) {
        assessments.remove(assessment);
    }
    public List<Assessment> getAssessments() {
        return this.assessments;
    }
}
