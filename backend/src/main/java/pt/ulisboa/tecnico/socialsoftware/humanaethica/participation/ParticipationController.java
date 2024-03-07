package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/participations")
public class ParticipationController {
    @Autowired
    ParticipationService participationService;

    public static final Logger logger = LoggerFactory.getLogger(ParticipationController.class);

    @GetMapping("/{activityId}/get")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public List<ParticipationDto> getActivityParticipations(@PathVariable Integer activityId) {
        return participationService.getParticipationsByActivity(activityId);
    }

    @PostMapping("/{activityId}/create")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ParticipationDto createParticipation(@PathVariable Integer activityId, @Valid @RequestBody ParticipationDto participationDto){
        return participationService.createParticipation(activityId, participationDto);
    }
}
