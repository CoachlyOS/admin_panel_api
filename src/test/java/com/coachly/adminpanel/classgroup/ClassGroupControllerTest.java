package com.coachly.adminpanel.classgroup;

import com.coachly.adminpanel.agegroup.dto.AgeGroupResponse;
import com.coachly.adminpanel.classgroup.dto.ClassGroupDisciplineResponse;
import com.coachly.adminpanel.classgroup.dto.ClassGroupRequest;
import com.coachly.adminpanel.classgroup.dto.ClassGroupResponse;
import com.coachly.adminpanel.classgroup.dto.ClassGroupTrainerResponse;
import com.coachly.adminpanel.common.GlobalExceptionHandler;
import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import com.coachly.adminpanel.level.dto.LevelResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClassGroupControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ClassGroupService classGroupService;

    @InjectMocks
    private ClassGroupController classGroupController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(classGroupController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllClassGroups_shouldReturnList() throws Exception {
        UUID id = UUID.randomUUID();
        UUID disciplineId = UUID.randomUUID();
        UUID levelId = UUID.randomUUID();
        UUID ageGroupId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();

        ClassGroupResponse response = new ClassGroupResponse(
                id,
                new ClassGroupDisciplineResponse(disciplineId, "boxing", Map.of("en", "Boxing")),
                new LevelResponse(levelId, "beginner", Map.of("en", "Beginner"), true),
                new AgeGroupResponse(ageGroupId, "kids", Map.of("en", "Kids"), true),
                new ClassGroupTrainerResponse(trainerId, "johndoe", "John", "Doe", true),
                20,
                Map.of("en", "Kids Boxing"),
                null,
                true,
                Instant.now(),
                Instant.now()
        );

        when(classGroupService.getAllClassGroups()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/class-group/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].discipline.slug").value("boxing"))
                .andExpect(jsonPath("$[0].level.slug").value("beginner"))
                .andExpect(jsonPath("$[0].ageGroup.slug").value("kids"))
                .andExpect(jsonPath("$[0].defaultTrainer.username").value("johndoe"))
                .andExpect(jsonPath("$[0].capacity").value(20))
                .andExpect(jsonPath("$[0].name.en").value("Kids Boxing"))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    void getClassGroup_whenExists_shouldReturnResponse() throws Exception {
        UUID id = UUID.randomUUID();
        ClassGroupResponse response = new ClassGroupResponse(
                id,
                new ClassGroupDisciplineResponse(UUID.randomUUID(), "boxing", Map.of("en", "Boxing")),
                new LevelResponse(UUID.randomUUID(), "beginner", Map.of("en", "Beginner"), true),
                new AgeGroupResponse(UUID.randomUUID(), "kids", Map.of("en", "Kids"), true),
                new ClassGroupTrainerResponse(UUID.randomUUID(), "johndoe", "John", "Doe", true),
                15,
                Map.of("en", "Kids Boxing"),
                Map.of("en", "Fun"),
                true,
                Instant.now(),
                Instant.now()
        );

        when(classGroupService.getClassGroup(id)).thenReturn(response);

        mockMvc.perform(get("/api/class-group/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.capacity").value(15))
                .andExpect(jsonPath("$.description.en").value("Fun"));
    }

    @Test
    void getClassGroup_whenNotFound_shouldReturn404() throws Exception {
        UUID id = UUID.randomUUID();
        when(classGroupService.getClassGroup(id))
                .thenThrow(new ResourceNotFoundException("Class group not found with id: " + id));

        mockMvc.perform(get("/api/class-group/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Class group not found with id: " + id));
    }

    @Test
    void createClassGroup_withValidData_shouldReturn200() throws Exception {
        ClassGroupRequest request = new ClassGroupRequest(
                null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                20, Map.of("en", "Kids Boxing"), null);

        doNothing().when(classGroupService).createClassGroup(any());

        mockMvc.perform(post("/api/class-group/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(classGroupService, times(1)).createClassGroup(any());
    }

    @Test
    void createClassGroup_withNullDisciplineId_shouldReturn400() throws Exception {
        ClassGroupRequest request = new ClassGroupRequest(
                null, null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                20, Map.of("en", "Kids Boxing"), null);

        mockMvc.perform(post("/api/class-group/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(classGroupService, never()).createClassGroup(any());
    }

    @Test
    void createClassGroup_withZeroCapacity_shouldReturn400() throws Exception {
        ClassGroupRequest request = new ClassGroupRequest(
                null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                0, Map.of("en", "Kids Boxing"), null);

        mockMvc.perform(post("/api/class-group/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(classGroupService, never()).createClassGroup(any());
    }

    @Test
    void createClassGroup_withEmptyName_shouldReturn400() throws Exception {
        ClassGroupRequest request = new ClassGroupRequest(
                null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                20, Map.of(), null);

        mockMvc.perform(post("/api/class-group/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(classGroupService, never()).createClassGroup(any());
    }

    @Test
    void createClassGroup_whenLevelIsInactive_shouldReturn400() throws Exception {
        UUID levelId = UUID.randomUUID();
        ClassGroupRequest request = new ClassGroupRequest(
                null, UUID.randomUUID(), levelId, UUID.randomUUID(), UUID.randomUUID(),
                20, Map.of("en", "Kids Boxing"), null);

        doThrow(new IllegalArgumentException("Level 'beginner' is not active"))
                .when(classGroupService).createClassGroup(any());

        mockMvc.perform(post("/api/class-group/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Level 'beginner' is not active"));
    }

    @Test
    void createClassGroup_whenDisciplineNotFound_shouldReturn404() throws Exception {
        ClassGroupRequest request = new ClassGroupRequest(
                null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                20, Map.of("en", "Kids Boxing"), null);

        doThrow(new ResourceNotFoundException("Discipline not found with id: " + request.disciplineId()))
                .when(classGroupService).createClassGroup(any());

        mockMvc.perform(post("/api/class-group/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Discipline not found with id: " + request.disciplineId()));
    }

    @Test
    void updateClassGroup_withValidData_shouldReturn200() throws Exception {
        UUID classGroupId = UUID.randomUUID();
        ClassGroupRequest request = new ClassGroupRequest(
                classGroupId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                25, Map.of("en", "Updated Name"), Map.of("en", "Updated desc"));

        doNothing().when(classGroupService).updateClassGroup(any());

        mockMvc.perform(patch("/api/class-group/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(classGroupService, times(1)).updateClassGroup(any());
    }

    @Test
    void updateClassGroup_whenNotFound_shouldReturn404() throws Exception {
        UUID classGroupId = UUID.randomUUID();
        ClassGroupRequest request = new ClassGroupRequest(
                classGroupId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                25, Map.of("en", "Updated Name"), null);

        doThrow(new ResourceNotFoundException("Class group not found with id: " + classGroupId))
                .when(classGroupService).updateClassGroup(any());

        mockMvc.perform(patch("/api/class-group/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Class group not found with id: " + classGroupId));
    }

    @Test
    void deleteClassGroup_whenExists_shouldSoftDelete() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(classGroupService).softDeleteClassGroup(id);

        mockMvc.perform(delete("/api/class-group/{id}", id))
                .andExpect(status().isOk());

        verify(classGroupService, times(1)).softDeleteClassGroup(id);
    }

    @Test
    void deleteClassGroup_whenNotFound_shouldReturn404() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("Class group not found with id: " + id))
                .when(classGroupService).softDeleteClassGroup(id);

        mockMvc.perform(delete("/api/class-group/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Class group not found with id: " + id));
    }
}
