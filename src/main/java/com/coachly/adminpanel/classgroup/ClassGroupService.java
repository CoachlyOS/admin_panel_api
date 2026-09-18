package com.coachly.adminpanel.classgroup;

import com.coachly.adminpanel.agegroup.AgeGroup;
import com.coachly.adminpanel.agegroup.AgeGroupRepository;
import com.coachly.adminpanel.classgroup.dto.ClassGroupDisciplineResponse;
import com.coachly.adminpanel.classgroup.dto.ClassGroupRequest;
import com.coachly.adminpanel.classgroup.dto.ClassGroupResponse;
import com.coachly.adminpanel.classgroup.dto.ClassGroupTrainerResponse;
import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import com.coachly.adminpanel.discipline.Discipline;
import com.coachly.adminpanel.discipline.DisciplineRepository;
import com.coachly.adminpanel.level.Level;
import com.coachly.adminpanel.level.LevelRepository;
import com.coachly.adminpanel.agegroup.dto.AgeGroupResponse;
import com.coachly.adminpanel.level.dto.LevelResponse;
import com.coachly.adminpanel.professional.Professional;
import com.coachly.adminpanel.professional.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassGroupService {

    private final ClassGroupRepository classGroupRepository;
    private final DisciplineRepository disciplineRepository;
    private final LevelRepository levelRepository;
    private final AgeGroupRepository ageGroupRepository;
    private final ProfessionalRepository professionalRepository;

    @Transactional(readOnly = true)
    public List<ClassGroupResponse> getAllClassGroups() {
        return classGroupRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClassGroupResponse getClassGroup(UUID id) {
        return classGroupRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Class group not found with id: " + id));
    }

    @Transactional
    public void createClassGroup(ClassGroupRequest request) {
        validateCapacity(request.capacity());

        Discipline discipline = resolveDiscipline(request.disciplineId());
        Level level = resolveActiveLevel(request.levelId());
        AgeGroup ageGroup = resolveActiveAgeGroup(request.ageGroupId());
        Professional trainer = resolveActiveTrainer(request.defaultTrainerId());

        var classGroup = ClassGroup.builder()
                .discipline(discipline)
                .level(level)
                .ageGroup(ageGroup)
                .defaultTrainer(trainer)
                .capacity(request.capacity())
                .name(request.name())
                .description(request.description())
                .build();

        classGroupRepository.save(classGroup);
    }

    @Transactional
    public void updateClassGroup(ClassGroupRequest request) {
        if (request.id() == null) {
            throw new IllegalArgumentException("Class group id is required for update");
        }

        var classGroup = classGroupRepository.findById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Class group not found with id: " + request.id()));

        validateCapacity(request.capacity());

        Discipline discipline = resolveDiscipline(request.disciplineId());
        Level level = resolveActiveLevel(request.levelId());
        AgeGroup ageGroup = resolveActiveAgeGroup(request.ageGroupId());
        Professional trainer = resolveActiveTrainer(request.defaultTrainerId());

        classGroup.setDiscipline(discipline);
        classGroup.setLevel(level);
        classGroup.setAgeGroup(ageGroup);
        classGroup.setDefaultTrainer(trainer);
        classGroup.setCapacity(request.capacity());
        classGroup.setName(request.name());
        classGroup.setDescription(request.description());

        classGroupRepository.save(classGroup);
    }

    @Transactional
    public void softDeleteClassGroup(UUID id) {
        var classGroup = classGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class group not found with id: " + id));

        classGroup.setIsActive(false);
        classGroupRepository.save(classGroup);
    }

    private void validateCapacity(Integer capacity) {
        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
    }

    private Discipline resolveDiscipline(UUID disciplineId) {
        return disciplineRepository.findById(disciplineId)
                .orElseThrow(() -> new ResourceNotFoundException("Discipline not found with id: " + disciplineId));
    }

    private Level resolveActiveLevel(UUID levelId) {
        var level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + levelId));
        if (!level.getIsActive()) {
            throw new IllegalArgumentException("Level '" + level.getSlug() + "' is not active");
        }
        return level;
    }

    private AgeGroup resolveActiveAgeGroup(UUID ageGroupId) {
        var ageGroup = ageGroupRepository.findById(ageGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Age group not found with id: " + ageGroupId));
        if (!ageGroup.getIsActive()) {
            throw new IllegalArgumentException("Age group '" + ageGroup.getSlug() + "' is not active");
        }
        return ageGroup;
    }

    private Professional resolveActiveTrainer(UUID trainerId) {
        var professional = professionalRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Professional not found with id: " + trainerId));
        if (!professional.getIsActive()) {
            throw new IllegalArgumentException("Professional '" + professional.getUsername() + "' is not active");
        }
        return professional;
    }

    private ClassGroupResponse toResponse(ClassGroup cg) {
        return new ClassGroupResponse(
                cg.getId(),
                new ClassGroupDisciplineResponse(
                        cg.getDiscipline().getId(),
                        cg.getDiscipline().getSlug(),
                        cg.getDiscipline().getName()
                ),
                new LevelResponse(
                        cg.getLevel().getId(),
                        cg.getLevel().getSlug(),
                        cg.getLevel().getName(),
                        cg.getLevel().getIsActive()
                ),
                new AgeGroupResponse(
                        cg.getAgeGroup().getId(),
                        cg.getAgeGroup().getSlug(),
                        cg.getAgeGroup().getName(),
                        cg.getAgeGroup().getIsActive()
                ),
                new ClassGroupTrainerResponse(
                        cg.getDefaultTrainer().getId(),
                        cg.getDefaultTrainer().getUsername(),
                        cg.getDefaultTrainer().getFirstName(),
                        cg.getDefaultTrainer().getLastName(),
                        cg.getDefaultTrainer().getIsActive()
                ),
                cg.getCapacity(),
                cg.getName(),
                cg.getDescription(),
                cg.getIsActive(),
                cg.getCreatedAt(),
                cg.getUpdatedAt()
        );
    }
}
