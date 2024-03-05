package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.repository.EnrollmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import java.util.List;

@Service
public class EnrollmentService {
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<EnrollmentDto> getEnrollmentsByActivity(Integer activityId) {
        if (activityId == null) throw new HEException(ErrorMessage.ACTIVITY_NOT_FOUND);

        return enrollmentRepository.getEnrollmentsByActivity(activityId).stream()
                .map(EnrollmentDto::new)
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EnrollmentDto createEnrollment(Integer userId, Integer activityId, EnrollmentDto enrollmentDto) {
        if (userId == null) throw new HEException(ErrorMessage.USER_NOT_FOUND);
        if (activityId == null) throw new HEException(ErrorMessage.ACTIVITY_NOT_FOUND);

        Volunteer volunteer = (Volunteer) userRepository.findById(userId)
                .orElseThrow(() -> new HEException(ErrorMessage.USER_NOT_FOUND, userId));
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new HEException(ErrorMessage.ACTIVITY_NOT_FOUND, activityId));

        Enrollment enrollment = new Enrollment(activity, volunteer, enrollmentDto);

        return new EnrollmentDto(enrollmentRepository.save(enrollment));
    }
}
