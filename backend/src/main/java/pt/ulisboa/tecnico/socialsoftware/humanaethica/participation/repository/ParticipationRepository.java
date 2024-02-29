package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    @Query("SELECT p FROM Participation p WHERE p.activity.id = :activityId")
    List<Participation> getParticipationById(Integer activityId);

    @Modifying
    @Query(value = "DELETE FROM activity_participation", nativeQuery = true)
    void deleteAllActivityParticipation();
}
