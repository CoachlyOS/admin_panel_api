package com.coachly.adminpanel.discipline;

import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import com.coachly.adminpanel.common.exception.DisciplineAlreadyExistsException;
import com.coachly.adminpanel.discipline.dto.DisciplineRequest;
import com.coachly.adminpanel.discipline.dto.DisciplineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisciplineServiceTest {

    @Mock
    private DisciplineRepository disciplineRepository;

    @InjectMocks
    private DisciplineService disciplineService;

    private Discipline mmaDiscipline;

    @BeforeEach
    void setUp() {
        mmaDiscipline = Discipline.builder()
                .slug("mma")
                .name(Map.of("en", "MMA", "uk", "ММА"))
                .build();
    }

    @Test
    void getAllDisciplines_shouldReturnList() {
        when(disciplineRepository.findAll()).thenReturn(List.of(mmaDiscipline));

        List<DisciplineResponse> result = disciplineService.getAllDisciplines();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).slug()).isEqualTo("mma");
        assertThat(result.get(0).name()).containsEntry("en", "MMA");
        verify(disciplineRepository, times(1)).findAll();
    }

    @Test
    void getDiscipline_whenExists_shouldReturnDiscipline() {
        when(disciplineRepository.findBySlug("mma")).thenReturn(Optional.of(mmaDiscipline));

        DisciplineResponse result = disciplineService.getDiscipline("mma");

        assertThat(result.slug()).isEqualTo("mma");
        assertThat(result.name()).containsEntry("uk", "ММА");
        verify(disciplineRepository, times(1)).findBySlug("mma");
    }

    @Test
    void getDiscipline_whenDoesNotExist_shouldThrowResourceNotFoundException() {
        when(disciplineRepository.findBySlug("boxing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disciplineService.getDiscipline("boxing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Discipline not found with slug: boxing");
    }

    @Test
    void createDiscipline_whenSlugIsUnique_shouldSave() {
        when(disciplineRepository.findBySlug("boxing")).thenReturn(Optional.empty());
        DisciplineRequest request = new DisciplineRequest("boxing", Map.of("en", "Boxing"));

        disciplineService.createDiscipline(request);

        verify(disciplineRepository, times(1)).save(any(Discipline.class));
    }

    @Test
    void createDiscipline_whenSlugAlreadyExists_shouldThrowDisciplineAlreadyExistsException() {
        when(disciplineRepository.findBySlug("mma")).thenReturn(Optional.of(mmaDiscipline));
        DisciplineRequest request = new DisciplineRequest("mma", Map.of("en", "MMA"));

        assertThatThrownBy(() -> disciplineService.createDiscipline(request))
                .isInstanceOf(DisciplineAlreadyExistsException.class)
                .hasMessageContaining("Discipline with slug mma already exists");

        verify(disciplineRepository, never()).save(any(Discipline.class));
    }

    @Test
    void updateDiscipline_whenExists_shouldUpdateName() {
        when(disciplineRepository.findBySlug("mma")).thenReturn(Optional.of(mmaDiscipline));
        DisciplineRequest request = new DisciplineRequest("mma", Map.of("en", "Mixed Martial Arts"));

        disciplineService.updateDiscipline(request);

        assertThat(mmaDiscipline.getName()).containsEntry("en", "Mixed Martial Arts");
        verify(disciplineRepository, times(1)).save(mmaDiscipline);
    }

    @Test
    void updateDiscipline_whenDoesNotExist_shouldThrowResourceNotFoundException() {
        when(disciplineRepository.findBySlug("boxing")).thenReturn(Optional.empty());
        DisciplineRequest request = new DisciplineRequest("boxing", Map.of("en", "Boxing"));

        assertThatThrownBy(() -> disciplineService.updateDiscipline(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Discipline not found with slug: boxing");

        verify(disciplineRepository, never()).save(any());
    }

    @Test
    void deleteDiscipline_whenExists_shouldDelete() {
        when(disciplineRepository.findBySlug("mma")).thenReturn(Optional.of(mmaDiscipline));

        disciplineService.deleteDiscipline("mma");

        verify(disciplineRepository, times(1)).delete(mmaDiscipline);
    }

    @Test
    void deleteDiscipline_whenDoesNotExist_shouldThrowResourceNotFoundException() {
        when(disciplineRepository.findBySlug("boxing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disciplineService.deleteDiscipline("boxing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Discipline not found with slug: boxing");

        verify(disciplineRepository, never()).delete(any());
    }
}
