package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.repository.AssessmentRepository;

import java.util.List;

@Service
public class AssessmentService {
    @Autowired
    AssessmentRepository assessmentRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AssessmentDto> getAssessmentsByInstitutionId(Integer institutionId) {
        return assessmentRepository.getAssessmentsByInstitutionId(institutionId)
                .stream()
                .map(AssessmentDto::new)
                .toList();
    }
}
