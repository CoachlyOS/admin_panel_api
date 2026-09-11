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
                "john_doe", "password123", "John", "Doe");

        assertThatThrownBy(() -> professionalService.registerProfessional(request))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("Username is already in use");

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
                .socials(Map.of("instagram", "https://instagram.com/johndoe"))
                .disciplines(Set.of(mmaDiscipline))
                .avatarId("avatars/test-avatar.png")
                .build();

        when(professionalRepository.findByUsernameWithDetails("john_doe")).thenReturn(Optional.of(professional));
        when(professionalRepository.countActiveSubscribersByProfessionalId(any())).thenReturn(0);
        when(storageService.resolveUrl("avatars/test-avatar.png")).thenReturn("http://localhost:8080/uploads/avatars/test-avatar.png");

        ProfessionalProfileResponse response = professionalService.getProfessional("john_doe");

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
        assertThat(response.subscriptionCount()).isEqualTo(0);
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

        when(professionalRepository.countActiveSubscribersAll()).thenReturn(Collections.emptyList());
        when(professionalRepository.findAll()).thenReturn(List.of(professional));
        when(storageService.resolveUrl("avatars/test-avatar.png")).thenReturn("http://localhost:8080/uploads/avatars/test-avatar.png");

        List<ProfessionalResponse> responseList = professionalService.getAllProfessionals();

        assertThat(responseList).hasSize(1);
        ProfessionalResponse response = responseList.get(0);
        assertThat(response.username()).isEqualTo("john_doe");
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.locale()).isEqualTo("en");
        assertThat(response.avatarUrl()).isEqualTo("http://localhost:8080/uploads/avatars/test-avatar.png");
        assertThat(response.subscriptionCount()).isEqualTo(0);
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
                "john_doe", "Johnny", "Doey", "Updated Bio", "en", Set.of("boxing"), Map.of("instagram", "https://instagram.com/johnny")
        );

        professionalService.updateProfessional(request);

        assertThat(professional.getFirstName()).isEqualTo("Johnny");
        assertThat(professional.getLastName()).isEqualTo("Doey");
        assertThat(professional.getBiography()).containsEntry("en", "Updated Bio");
        assertThat(professional.getSocials()).containsEntry("instagram", "https://instagram.com/johnny");
        assertThat(professional.getDisciplines()).containsExactly(boxingDiscipline);
        verify(professionalRepository, times(1)).save(professional);
    }
}
