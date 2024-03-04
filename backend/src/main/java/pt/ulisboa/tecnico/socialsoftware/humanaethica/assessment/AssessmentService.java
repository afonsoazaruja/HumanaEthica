package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.util.List;

@Service
public class AssessmentService {
    @Autowired
    AssessmentRepository assessmentRepository;
    @Autowired
    InstitutionRepository institutionRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AssessmentDto> getAssessmentsByInstitution(Integer institutionId) {
        if (institutionId == null) throw new HEException(INSTITUTION_NOT_FOUND);
        if (!institutionRepository.existsById(institutionId)) throw new HEException(INSTITUTION_NOT_FOUND, institutionId);

        return assessmentRepository.getAssessmentsByInstitutionId(institutionId)
                .stream()
                .map(AssessmentDto::new)
                .toList();
    }
}
