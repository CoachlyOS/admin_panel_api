package com.coachly.adminpanel.discipline;

import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import com.coachly.adminpanel.common.exception.DisciplineAlreadyExistsException;
import com.coachly.adminpanel.discipline.dto.DisciplineRequest;
import com.coachly.adminpanel.discipline.dto.DisciplineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplineService {

    private final DisciplineRepository disciplineRepository;

    public List<DisciplineResponse> getAllDisciplines() {
        return disciplineRepository.findAll().stream()
                .map(d -> new DisciplineResponse(d.getSlug(), d.getName()))
                .toList();
    }

    public DisciplineResponse getDiscipline(String slug) {
        return disciplineRepository.findBySlug(slug)
                .map(d -> new DisciplineResponse(d.getSlug(), d.getName()))
                .orElseThrow(() -> new ResourceNotFoundException("Discipline not found with slug: " + slug));
    }

    @Transactional
    public void createDiscipline(DisciplineRequest request) {
        if (disciplineRepository.findBySlug(request.slug()).isPresent()) {
            throw new DisciplineAlreadyExistsException("Discipline with slug " + request.slug() + " already exists");
        }

        var discipline = Discipline.builder()
                .slug(request.slug())
                .name(request.name())
                .build();

        disciplineRepository.save(discipline);
    }

    @Transactional
    public void updateDiscipline(DisciplineRequest request) {
        var discipline = disciplineRepository.findBySlug(request.slug())
                .orElseThrow(() -> new ResourceNotFoundException("Discipline not found with slug: " + request.slug()));

        discipline.setName(request.name());
        disciplineRepository.save(discipline);
    }

    @Transactional
    public void deleteDiscipline(String slug) {
        var discipline = disciplineRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Discipline not found with slug: " + slug));

        disciplineRepository.delete(discipline);
    }
}
