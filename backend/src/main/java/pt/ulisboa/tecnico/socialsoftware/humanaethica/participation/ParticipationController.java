package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/participations")
public class ParticipationController {
    @Autowired
    ParticipationService participationService;

    public static final Logger logger = LoggerFactory.getLogger(ParticipationController.class);

    @GetMapping("/{activityId}")
    public List<ParticipationDto> getActivityParticipations(@PathVariable Integer activityId) {
        return participationService.getParticipationsByActivity(activityId);
    }
}
