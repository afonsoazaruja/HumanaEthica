package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {

    @OneToMany(mappedBy = "volunteer", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Assessment> assessments = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participation> participations = new ArrayList<>();

    public Volunteer() {
    }

    public Volunteer(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.VOLUNTEER, type, state);
    }

    public Volunteer(String name, State state) {
        super(name, Role.VOLUNTEER, state);
    }

    public void addAssessment(Assessment assessment) {
        this.assessments.add(assessment);
    }

    public void removeAssessment(Assessment assessment) {
        this.assessments.remove(assessment);
    }

    public List<Assessment> getAssessments() {
        return this.assessments;
    }

    public void addParticipation(Participation participation) {
        participations.add(participation);
    }

    public void removeParticipation(Participation participation) {
        participations.remove(participation);
    }
}

