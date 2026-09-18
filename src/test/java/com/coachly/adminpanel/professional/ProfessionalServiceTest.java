package com.coachly.adminpanel.professional;

import com.coachly.adminpanel.common.exception.UsernameAlreadyExistsException;
import com.coachly.adminpanel.common.storage.StorageService;
import com.coachly.adminpanel.discipline.Discipline;
import com.coachly.adminpanel.discipline.DisciplineRepository;
import com.coachly.adminpanel.professional.dto.ProfessionalProfileResponse;
import com.coachly.adminpanel.professional.dto.ProfessionalResponse;
import com.coachly.adminpanel.professional.dto.RegisterProfessionalRequest;
import com.coachly.adminpanel.professional.dto.UpdateProfessionalRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessionalServiceTest {

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DisciplineRepository disciplineRepository;

    @Mock
    private StorageService storageService;

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
    void registerProfessional_withExistingUsername_shouldThrowUsernameAlreadyExistsException() {
        when(professionalRepository.findByUsername("john_doe")).thenReturn(Optional.of(new Professional()));
        RegisterProfessionalRequest request = new RegisterProfessionalRequest(
                "john_doe", "password123", "John", "Doe", null, null, null, null);

        assertThatThrownBy(() -> professionalService.registerProfessional(request))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("Username is already in use");

        verify(professionalRepository, never()).save(any());
    }

    @Test
    void registerProfessional_withFullOptionalFields_shouldPopulateProfile() {
        when(professionalRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(disciplineRepository.findAllBySlugIn(any())).thenReturn(Set.of(boxingDiscipline));
        when(passwordEncoder.encode("password123")).thenReturn("encoded");

        RegisterProfessionalRequest request = new RegisterProfessionalRequest(
                "john_doe", "password123", "John", "Doe",
                "uk", Map.of("en", "Bio", "uk", "Біо"), Set.of("boxing"), Map.of("instagram", "johndoe"));

        professionalService.registerProfessional(request);

        org.mockito.ArgumentCaptor<Professional> captor = org.mockito.ArgumentCaptor.forClass(Professional.class);
        verify(professionalRepository).save(captor.capture());
        Professional saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("john_doe");
        assertThat(saved.getPassword()).isEqualTo("encoded");
        assertThat(saved.getLocale()).isEqualTo("uk");
        assertThat(saved.getBiography()).containsEntry("en", "Bio").containsEntry("uk", "Біо");
        assertThat(saved.getDisciplines()).containsExactly(boxingDiscipline);
        assertThat(saved.getSocials()).containsEntry("instagram", "johndoe");
        assertThat(saved.getIsActive()).isTrue();
    }

    @Test
    void getProfessional_shouldReturnProfileWithDisciplines() {
        Professional professional = Professional.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .locale("en")
                .biography(Map.of("en", "Bio"))
                .socials(Map.of("instagram", "https://instagram.com/johndoe"))
                .disciplines(Set.of(mmaDiscipline))
                .avatarId("avatars/test-avatar.png")
                .build();

        when(professionalRepository.findByIdWithDetails(any())).thenReturn(Optional.of(professional));
        when(storageService.resolveUrl("avatars/test-avatar.png")).thenReturn("http://localhost:8080/uploads/avatars/test-avatar.png");

        ProfessionalProfileResponse response = professionalService.getProfessional(UUID.randomUUID());

        assertThat(response.username()).isEqualTo("john_doe");
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.locale()).isEqualTo("en");
        assertThat(response.biography()).containsEntry("en", "Bio");
        assertThat(response.socials()).containsEntry("instagram", "https://instagram.com/johndoe");
        assertThat(response.avatarUrl()).isEqualTo("http://localhost:8080/uploads/avatars/test-avatar.png");
        
        assertThat(response.disciplines()).hasSize(1);
        assertThat(response.disciplines().get(0).slug()).isEqualTo("mma");
        assertThat(response.disciplines().get(0).name()).containsEntry("en", "MMA");
        assertThat(response.appointments()).isEmpty();
    }

    @Test
    void getAllProfessionals_shouldReturnAllProfessionals() {
        Professional professional = Professional.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .locale("en")
                .biography(Map.of("en", "Bio"))
                .socials(Map.of("instagram", "https://instagram.com/johndoe"))
                .disciplines(Set.of(mmaDiscipline))
                .avatarId("avatars/test-avatar.png")
                .build();

        when(professionalRepository.findAllWithDisciplines()).thenReturn(List.of(professional));
        when(storageService.resolveUrl("avatars/test-avatar.png")).thenReturn("http://localhost:8080/uploads/avatars/test-avatar.png");

        List<ProfessionalResponse> responseList = professionalService.getAllProfessionals();

        assertThat(responseList).hasSize(1);
        ProfessionalResponse response = responseList.get(0);
        assertThat(response.username()).isEqualTo("john_doe");
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.locale()).isEqualTo("en");
        assertThat(response.avatarUrl()).isEqualTo("http://localhost:8080/uploads/avatars/test-avatar.png");
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
        when(disciplineRepository.findAllBySlugIn(any())).thenReturn(Set.of(boxingDiscipline));

        UpdateProfessionalRequest request = new UpdateProfessionalRequest(
                "john_doe", "Johnny", "Doey", "en", Map.of("en", "Updated Bio"), Set.of("boxing"),
                Map.of("instagram", "https://instagram.com/johnny"), null
        );

        professionalService.updateProfessional(request);

        assertThat(professional.getFirstName()).isEqualTo("Johnny");
        assertThat(professional.getLastName()).isEqualTo("Doey");
        assertThat(professional.getBiography()).containsEntry("en", "Updated Bio");
        assertThat(professional.getSocials()).containsEntry("instagram", "https://instagram.com/johnny");
        assertThat(professional.getDisciplines()).containsExactly(boxingDiscipline);
        verify(professionalRepository, times(1)).save(professional);
    }

    @Test
    void updateProfessional_shouldFullyReplaceBiographyWhenPresent() {
        Professional professional = Professional.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .biography(new HashMap<>(Map.of("en", "Old bio", "uk", "Старе біо")))
                .build();

        when(professionalRepository.findByUsername("john_doe")).thenReturn(Optional.of(professional));

        UpdateProfessionalRequest request = new UpdateProfessionalRequest(
                "john_doe", null, null, null, Map.of("en", "New bio"), null, null, null
        );

        professionalService.updateProfessional(request);

        assertThat(professional.getBiography())
                .containsOnly(org.assertj.core.data.MapEntry.entry("en", "New bio"));
        verify(professionalRepository, times(1)).save(professional);
    }

    @Test
    void updateProfessional_shouldLeaveBiographyUntouchedWhenNull() {
        Professional professional = Professional.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .biography(new HashMap<>(Map.of("en", "Existing", "uk", "Існуюче")))
                .build();

        when(professionalRepository.findByUsername("john_doe")).thenReturn(Optional.of(professional));

        UpdateProfessionalRequest request = new UpdateProfessionalRequest(
                "john_doe", null, null, null, null, null, null, null
        );

        professionalService.updateProfessional(request);

        assertThat(professional.getBiography()).containsEntry("en", "Existing").containsEntry("uk", "Існуюче");
        verify(professionalRepository, times(1)).save(professional);
    }

    @Test
    void updateProfessional_shouldRoundTripIsActiveBothDirections() {
        Professional professional = Professional.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .build();

        when(professionalRepository.findByUsername("john_doe")).thenReturn(Optional.of(professional));

        professionalService.updateProfessional(new UpdateProfessionalRequest(
                "john_doe", null, null, null, null, null, null, false));
        assertThat(professional.getIsActive()).isFalse();

        professionalService.updateProfessional(new UpdateProfessionalRequest(
                "john_doe", null, null, null, null, null, null, true));
        assertThat(professional.getIsActive()).isTrue();
    }

    @Test
    void updateProfessional_shouldLeaveIsActiveUntouchedWhenAbsent() {
        Professional professional = Professional.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .build();

        when(professionalRepository.findByUsername("john_doe")).thenReturn(Optional.of(professional));

        professionalService.updateProfessional(new UpdateProfessionalRequest(
                "john_doe", "Johnny", null, null, null, null, null, null));

        assertThat(professional.getIsActive()).isTrue();
        verify(professionalRepository, times(1)).save(professional);
    }
}
