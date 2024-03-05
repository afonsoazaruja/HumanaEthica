package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import groovy.transform.Trait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.repository.ParticipationRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;

import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.ACTIVITY_NOT_FOUND;

@Service
public class ParticipationService {
    @Autowired
    ParticipationRepository participationRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserRepository userRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ParticipationDto> getParticipationsByActivity(Integer activityId) {
        return participationRepository.getParticipationByActivityId(activityId)
                .stream()
                .map(ParticipationDto::new)
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ParticipationDto createParticipation(Integer activityId, ParticipationDto participationDto){
        if (activityId == null) throw new HEException(ACTIVITY_NOT_FOUND);
        Activity activity = (Activity) activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));
        Volunteer volunteer = participationDto.getVolunteer();

        Participation participation = new Participation(activity,volunteer,participationDto);

        participationRepository.save(participation);

        return new ParticipationDto(participation);

    }
}
