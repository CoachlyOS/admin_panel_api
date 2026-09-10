package com.coachly.adminpanel.professional;

import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import com.coachly.adminpanel.common.exception.UsernameAlreadyExistsException;
import com.coachly.adminpanel.discipline.Discipline;
import com.coachly.adminpanel.discipline.DisciplineRepository;
import com.coachly.adminpanel.discipline.dto.DisciplineResponse;
import com.coachly.adminpanel.professional.dto.ProfessionalProfileResponse;
import com.coachly.adminpanel.professional.dto.RegisterProfessionalRequest;
import com.coachly.adminpanel.professional.dto.UpdateProfessionalRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessionalServiceTest {

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DisciplineRepository disciplineRepository;

    @InjectMocks
    private ProfessionalService professionalService;

    private Discipline mmaDiscipline;
    private Discipline boxingDiscipline;

    @BeforeEach
    void setUp() {
        mmaDiscipline = Discipline.builder()
                .slug("mma")
                .name(Map.of("en", "MMA"))
                .build();
        boxingDiscipline = Discipline.builder()
                .slug("boxing")
                .name(Map.of("en", "Boxing"))
                .build();
    }

    @Test
    void registerProfessional_withDisciplines_shouldSaveWithDisciplines() {
        when(professionalRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(disciplineRepository.findBySlug("mma")).thenReturn(Optional.of(mmaDiscipline));
        when(disciplineRepository.findBySlug("boxing")).thenReturn(Optional.of(boxingDiscipline));

        RegisterProfessionalRequest request = new RegisterProfessionalRequest(
                "john_doe", "password123", "John", "Doe", Set.of("mma", "boxing")
        );

        professionalService.registerProfessional(request);

        ArgumentCaptor<Professional> captor = ArgumentCaptor.forClass(Professional.class);
        verify(professionalRepository, times(1)).save(captor.capture());

        Professional saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("john_doe");
        assertThat(saved.getPassword()).isEqualTo("encoded_password");
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");
        assertThat(saved.getDisciplines()).containsExactlyInAnyOrder(mmaDiscipline, boxingDiscipline);
    }

    @Test
    void registerProfessional_withExistingUsername_shouldThrowUsernameAlreadyExistsException() {
        when(professionalRepository.findByUsername("john_doe")).thenReturn(Optional.of(new Professional()));
        RegisterProfessionalRequest request = new RegisterProfessionalRequest(
                "john_doe", "password123", "John", "Doe", null
        );

        assertThatThrownBy(() -> professionalService.registerProfessional(request))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("Username is already in use");

        verify(professionalRepository, never()).save(any());
    }

    @Test
    void registerProfessional_withNonExistingDiscipline_shouldThrowResourceNotFoundException() {
        when(professionalRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(disciplineRepository.findBySlug("invalid")).thenReturn(Optional.empty());

        RegisterProfessionalRequest request = new RegisterProfessionalRequest(
                "john_doe", "password123", "John", "Doe", Set.of("invalid")
        );

        assertThatThrownBy(() -> professionalService.registerProfessional(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Discipline not found with slug: invalid");

        verify(professionalRepository, never()).save(any());
    }

    @Test
    void getProfessional_shouldReturnProfileWithDisciplines() {
        Professional professional = Professional.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .locale("en")
                .biography(Map.of("en", "Bio"))
                .disciplines(Set.of(mmaDiscipline))
                .build();

        when(professionalRepository.findByUsername("john_doe")).thenReturn(Optional.of(professional));

        ProfessionalProfileResponse response = professionalService.getProfessional("john_doe");

        assertThat(response.username()).isEqualTo("john_doe");
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.locale()).isEqualTo("en");
        assertThat(response.biography()).containsEntry("en", "Bio");
        
        assertThat(response.disciplines()).hasSize(1);
        assertThat(response.disciplines().get(0).slug()).isEqualTo("mma");
        assertThat(response.disciplines().get(0).name()).containsEntry("en", "MMA");
    }

    @Test
    void updateProfessional_shouldUpdateDetailsAndDisciplines() {
        Professional professional = Professional.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .locale("en")
                .biography(new HashMap<>())
                .disciplines(new HashSet<>(List.of(mmaDiscipline)))
                .build();

        when(professionalRepository.findByUsername("john_doe")).thenReturn(Optional.of(professional));
        when(disciplineRepository.findBySlug("boxing")).thenReturn(Optional.of(boxingDiscipline));

        UpdateProfessionalRequest request = new UpdateProfessionalRequest(
                "john_doe", "Johnny", "Doey", "Updated Bio", "en", Set.of("boxing")
        );

        professionalService.updateProfessional(request);

        assertThat(professional.getFirstName()).isEqualTo("Johnny");
        assertThat(professional.getLastName()).isEqualTo("Doey");
        assertThat(professional.getBiography()).containsEntry("en", "Updated Bio");
        assertThat(professional.getDisciplines()).containsExactly(boxingDiscipline);
        verify(professionalRepository, times(1)).save(professional);
    }
}
