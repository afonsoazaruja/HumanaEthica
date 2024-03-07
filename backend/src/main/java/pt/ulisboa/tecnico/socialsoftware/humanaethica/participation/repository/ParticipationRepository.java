package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.repository;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    @Query("SELECT p FROM Participation p WHERE p.activity.id = :activityId")
    List<Participation> getParticipationByActivityId(Integer activityId);

    @Modifying
    @Query(value = "DELETE FROM activity_participation", nativeQuery = true)
    void deleteAllActivityParticipation();
}
