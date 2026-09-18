package com.coachly.adminpanel.classgroup;

import com.coachly.adminpanel.agegroup.AgeGroup;
import com.coachly.adminpanel.agegroup.AgeGroupRepository;
import com.coachly.adminpanel.classgroup.dto.ClassGroupRequest;
import com.coachly.adminpanel.classgroup.dto.ClassGroupResponse;
import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import com.coachly.adminpanel.discipline.Discipline;
import com.coachly.adminpanel.discipline.DisciplineRepository;
import com.coachly.adminpanel.level.Level;
import com.coachly.adminpanel.level.LevelRepository;
import com.coachly.adminpanel.professional.Professional;
import com.coachly.adminpanel.professional.ProfessionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassGroupServiceTest {

    @Mock
    private ClassGroupRepository classGroupRepository;
    @Mock
    private DisciplineRepository disciplineRepository;
    @Mock
    private LevelRepository levelRepository;
    @Mock
    private AgeGroupRepository ageGroupRepository;
    @Mock
    private ProfessionalRepository professionalRepository;

    @InjectMocks
    private ClassGroupService classGroupService;

    private UUID disciplineId;
    private UUID levelId;
    private UUID ageGroupId;
    private UUID trainerId;
    private Discipline discipline;
    private Level activeLevel;
    private Level inactiveLevel;
    private AgeGroup activeAgeGroup;
    private AgeGroup inactiveAgeGroup;
    private Professional activeTrainer;
    private Professional inactiveTrainer;

    @BeforeEach
    void setUp() {
        disciplineId = UUID.randomUUID();
        levelId = UUID.randomUUID();
        ageGroupId = UUID.randomUUID();
        trainerId = UUID.randomUUID();

        discipline = Discipline.builder()
                .id(disciplineId)
                .slug("boxing")
                .name(Map.of("en", "Boxing"))
                .build();

        activeLevel = Level.builder()
                .id(levelId)
                .slug("beginner")
                .name(Map.of("en", "Beginner"))
                .isActive(true)
                .build();

        inactiveLevel = Level.builder()
                .id(levelId)
                .slug("beginner")
                .name(Map.of("en", "Beginner"))
                .isActive(false)
                .build();

        activeAgeGroup = AgeGroup.builder()
                .id(ageGroupId)
                .slug("kids")
                .name(Map.of("en", "Kids"))
                .isActive(true)
                .build();

        inactiveAgeGroup = AgeGroup.builder()
                .id(ageGroupId)
                .slug("kids")
                .name(Map.of("en", "Kids"))
                .isActive(false)
                .build();

        activeTrainer = Professional.builder()
                .id(trainerId)
                .username("johndoe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .build();

        inactiveTrainer = Professional.builder()
                .id(trainerId)
                .username("johndoe")
                .firstName("John")
                .lastName("Doe")
                .isActive(false)
                .build();
    }

    @Test
    void getAllClassGroups_shouldReturnList() {
        var classGroup = ClassGroup.builder()
                .id(UUID.randomUUID())
                .discipline(discipline)
                .level(activeLevel)
                .ageGroup(activeAgeGroup)
                .defaultTrainer(activeTrainer)
                .capacity(20)
                .name(Map.of("en", "Kids Boxing"))
                .isActive(true)
                .build();

        when(classGroupRepository.findAll()).thenReturn(List.of(classGroup));

        List<ClassGroupResponse> result = classGroupService.getAllClassGroups();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).capacity()).isEqualTo(20);
        assertThat(result.get(0).discipline().slug()).isEqualTo("boxing");
        assertThat(result.get(0).level().slug()).isEqualTo("beginner");
        assertThat(result.get(0).ageGroup().slug()).isEqualTo("kids");
        assertThat(result.get(0).defaultTrainer().username()).isEqualTo("johndoe");
        verify(classGroupRepository, times(1)).findAll();
    }

    @Test
    void getClassGroup_whenExists_shouldReturnResponse() {
        UUID id = UUID.randomUUID();
        var classGroup = ClassGroup.builder()
                .id(id)
                .discipline(discipline)
                .level(activeLevel)
                .ageGroup(activeAgeGroup)
                .defaultTrainer(activeTrainer)
                .capacity(15)
                .name(Map.of("en", "Kids Boxing"))
                .isActive(true)
                .build();

        when(classGroupRepository.findById(id)).thenReturn(Optional.of(classGroup));

        ClassGroupResponse result = classGroupService.getClassGroup(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.capacity()).isEqualTo(15);
        assertThat(result.discipline().slug()).isEqualTo("boxing");
    }

    @Test
    void getClassGroup_whenDoesNotExist_shouldThrowResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(classGroupRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classGroupService.getClassGroup(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Class group not found with id: " + id);
    }

    @Test
    void createClassGroup_withValidData_shouldSave() {
        ClassGroupRequest request = new ClassGroupRequest(
                null, disciplineId, levelId, ageGroupId, trainerId, 20,
                Map.of("en", "Kids Boxing"), Map.of("en", "Fun class"));

        when(disciplineRepository.findById(disciplineId)).thenReturn(Optional.of(discipline));
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(activeLevel));
        when(ageGroupRepository.findById(ageGroupId)).thenReturn(Optional.of(activeAgeGroup));
        when(professionalRepository.findById(trainerId)).thenReturn(Optional.of(activeTrainer));

        classGroupService.createClassGroup(request);

        ArgumentCaptor<ClassGroup> captor = ArgumentCaptor.forClass(ClassGroup.class);
        verify(classGroupRepository).save(captor.capture());
        ClassGroup saved = captor.getValue();
        assertThat(saved.getDiscipline()).isEqualTo(discipline);
        assertThat(saved.getLevel()).isEqualTo(activeLevel);
        assertThat(saved.getAgeGroup()).isEqualTo(activeAgeGroup);
        assertThat(saved.getDefaultTrainer()).isEqualTo(activeTrainer);
        assertThat(saved.getCapacity()).isEqualTo(20);
        assertThat(saved.getName()).containsEntry("en", "Kids Boxing");
        assertThat(saved.getDescription()).containsEntry("en", "Fun class");
        assertThat(saved.getIsActive()).isTrue();
    }

    @Test
    void createClassGroup_withNullDescription_shouldSaveWithNullDescription() {
        ClassGroupRequest request = new ClassGroupRequest(
                null, disciplineId, levelId, ageGroupId, trainerId, 20,
                Map.of("en", "Kids Boxing"), null);

        when(disciplineRepository.findById(disciplineId)).thenReturn(Optional.of(discipline));
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(activeLevel));
        when(ageGroupRepository.findById(ageGroupId)).thenReturn(Optional.of(activeAgeGroup));
        when(professionalRepository.findById(trainerId)).thenReturn(Optional.of(activeTrainer));

        classGroupService.createClassGroup(request);

        ArgumentCaptor<ClassGroup> captor = ArgumentCaptor.forClass(ClassGroup.class);
        verify(classGroupRepository).save(captor.capture());
        assertThat(captor.getValue().getDescription()).isNull();
    }

    @Test
    void createClassGroup_whenDisciplineNotFound_shouldThrowResourceNotFoundException() {
        ClassGroupRequest request = new ClassGroupRequest(
                null, disciplineId, levelId, ageGroupId, trainerId, 20,
                Map.of("en", "Kids Boxing"), null);

        when(disciplineRepository.findById(disciplineId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classGroupService.createClassGroup(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Discipline not found with id: " + disciplineId);

        verify(classGroupRepository, never()).save(any());
    }

    @Test
    void createClassGroup_whenLevelNotFound_shouldThrowResourceNotFoundException() {
        ClassGroupRequest request = new ClassGroupRequest(
                null, disciplineId, levelId, ageGroupId, trainerId, 20,
                Map.of("en", "Kids Boxing"), null);

        when(disciplineRepository.findById(disciplineId)).thenReturn(Optional.of(discipline));
        when(levelRepository.findById(levelId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classGroupService.createClassGroup(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Level not found with id: " + levelId);

        verify(classGroupRepository, never()).save(any());
    }

    @Test
    void createClassGroup_whenLevelIsInactive_shouldThrowIllegalArgumentException() {
        ClassGroupRequest request = new ClassGroupRequest(
                null, disciplineId, levelId, ageGroupId, trainerId, 20,
                Map.of("en", "Kids Boxing"), null);

        when(disciplineRepository.findById(disciplineId)).thenReturn(Optional.of(discipline));
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(inactiveLevel));

        assertThatThrownBy(() -> classGroupService.createClassGroup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Level 'beginner' is not active");

        verify(classGroupRepository, never()).save(any());
    }

    @Test
    void createClassGroup_whenAgeGroupNotFound_shouldThrowResourceNotFoundException() {
        ClassGroupRequest request = new ClassGroupRequest(
                null, disciplineId, levelId, ageGroupId, trainerId, 20,
                Map.of("en", "Kids Boxing"), null);

        when(disciplineRepository.findById(disciplineId)).thenReturn(Optional.of(discipline));
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(activeLevel));
        when(ageGroupRepository.findById(ageGroupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classGroupService.createClassGroup(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Age group not found with id: " + ageGroupId);

        verify(classGroupRepository, never()).save(any());
    }

    @Test
    void createClassGroup_whenAgeGroupIsInactive_shouldThrowIllegalArgumentException() {
        ClassGroupRequest request = new ClassGroupRequest(
                null, disciplineId, levelId, ageGroupId, trainerId, 20,
                Map.of("en", "Kids Boxing"), null);

        when(disciplineRepository.findById(disciplineId)).thenReturn(Optional.of(discipline));
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(activeLevel));
        when(ageGroupRepository.findById(ageGroupId)).thenReturn(Optional.of(inactiveAgeGroup));

        assertThatThrownBy(() -> classGroupService.createClassGroup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Age group 'kids' is not active");

        verify(classGroupRepository, never()).save(any());
    }

    @Test
    void createClassGroup_whenTrainerNotFound_shouldThrowResourceNotFoundException() {
        ClassGroupRequest request = new ClassGroupRequest(
                null, disciplineId, levelId, ageGroupId, trainerId, 20,
                Map.of("en", "Kids Boxing"), null);

        when(disciplineRepository.findById(disciplineId)).thenReturn(Optional.of(discipline));
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(activeLevel));
        when(ageGroupRepository.findById(ageGroupId)).thenReturn(Optional.of(activeAgeGroup));
        when(professionalRepository.findById(trainerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classGroupService.createClassGroup(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Professional not found with id: " + trainerId);

        verify(classGroupRepository, never()).save(any());
    }

    @Test
    void createClassGroup_whenTrainerIsInactive_shouldThrowIllegalArgumentException() {
        ClassGroupRequest request = new ClassGroupRequest(
                null, disciplineId, levelId, ageGroupId, trainerId, 20,
                Map.of("en", "Kids Boxing"), null);

        when(disciplineRepository.findById(disciplineId)).thenReturn(Optional.of(discipline));
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(activeLevel));
        when(ageGroupRepository.findById(ageGroupId)).thenReturn(Optional.of(activeAgeGroup));
        when(professionalRepository.findById(trainerId)).thenReturn(Optional.of(inactiveTrainer));

        assertThatThrownBy(() -> classGroupService.createClassGroup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Professional 'johndoe' is not active");

        verify(classGroupRepository, never()).save(any());
    }

    @Test
    void createClassGroup_withZeroCapacity_shouldThrowIllegalArgumentException() {
        ClassGroupRequest request = new ClassGroupRequest(
                null, disciplineId, levelId, ageGroupId, trainerId, 0,
                Map.of("en", "Kids Boxing"), null);

        assertThatThrownBy(() -> classGroupService.createClassGroup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Capacity must be greater than 0");

        verify(classGroupRepository, never()).save(any());
    }

    @Test
    void createClassGroup_withNegativeCapacity_shouldThrowIllegalArgumentException() {
        ClassGroupRequest request = new ClassGroupRequest(
                null, disciplineId, levelId, ageGroupId, trainerId, -5,
                Map.of("en", "Kids Boxing"), null);

        assertThatThrownBy(() -> classGroupService.createClassGroup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Capacity must be greater than 0");

        verify(classGroupRepository, never()).save(any());
    }

    @Test
    void updateClassGroup_whenExists_shouldUpdateFields() {
        UUID classGroupId = UUID.randomUUID();
        var existing = ClassGroup.builder()
                .id(classGroupId)
                .discipline(discipline)
                .level(activeLevel)
                .ageGroup(activeAgeGroup)
                .defaultTrainer(activeTrainer)
                .capacity(10)
                .name(Map.of("en", "Old Name"))
                .isActive(true)
                .build();

        ClassGroupRequest request = new ClassGroupRequest(
                classGroupId, disciplineId, levelId, ageGroupId, trainerId, 25,
                Map.of("en", "New Name"), Map.of("en", "Updated desc"));

        when(classGroupRepository.findById(classGroupId)).thenReturn(Optional.of(existing));
        when(disciplineRepository.findById(disciplineId)).thenReturn(Optional.of(discipline));
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(activeLevel));
        when(ageGroupRepository.findById(ageGroupId)).thenReturn(Optional.of(activeAgeGroup));
        when(professionalRepository.findById(trainerId)).thenReturn(Optional.of(activeTrainer));

        classGroupService.updateClassGroup(request);

        assertThat(existing.getCapacity()).isEqualTo(25);
        assertThat(existing.getName()).containsEntry("en", "New Name");
        assertThat(existing.getDescription()).containsEntry("en", "Updated desc");
        verify(classGroupRepository, times(1)).save(existing);
    }

    @Test
    void updateClassGroup_whenDoesNotExist_shouldThrowResourceNotFoundException() {
        UUID classGroupId = UUID.randomUUID();
        ClassGroupRequest request = new ClassGroupRequest(
                classGroupId, disciplineId, levelId, ageGroupId, trainerId, 20,
                Map.of("en", "Kids Boxing"), null);

        when(classGroupRepository.findById(classGroupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classGroupService.updateClassGroup(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Class group not found with id: " + classGroupId);

        verify(classGroupRepository, never()).save(any());
    }

    @Test
    void updateClassGroup_withNullId_shouldThrowIllegalArgumentException() {
        ClassGroupRequest request = new ClassGroupRequest(
                null, disciplineId, levelId, ageGroupId, trainerId, 20,
                Map.of("en", "Kids Boxing"), null);

        assertThatThrownBy(() -> classGroupService.updateClassGroup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Class group id is required for update");

        verify(classGroupRepository, never()).save(any());
    }

    @Test
    void softDeleteClassGroup_whenExists_shouldSetIsActiveFalse() {
        UUID classGroupId = UUID.randomUUID();
        var existing = ClassGroup.builder()
                .id(classGroupId)
                .discipline(discipline)
                .level(activeLevel)
                .ageGroup(activeAgeGroup)
                .defaultTrainer(activeTrainer)
                .capacity(10)
                .name(Map.of("en", "Kids Boxing"))
                .isActive(true)
                .build();

        when(classGroupRepository.findById(classGroupId)).thenReturn(Optional.of(existing));

        classGroupService.softDeleteClassGroup(classGroupId);

        assertThat(existing.getIsActive()).isFalse();
        verify(classGroupRepository, times(1)).save(existing);
    }

    @Test
    void softDeleteClassGroup_whenDoesNotExist_shouldThrowResourceNotFoundException() {
        UUID classGroupId = UUID.randomUUID();
        when(classGroupRepository.findById(classGroupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classGroupService.softDeleteClassGroup(classGroupId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Class group not found with id: " + classGroupId);

        verify(classGroupRepository, never()).save(any());
    }
}
