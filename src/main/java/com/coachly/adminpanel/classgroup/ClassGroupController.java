package com.coachly.adminpanel.classgroup;

import com.coachly.adminpanel.classgroup.dto.ClassGroupRequest;
import com.coachly.adminpanel.classgroup.dto.ClassGroupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/class-group")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
public class ClassGroupController {

    private final ClassGroupService classGroupService;

    @GetMapping("/all")
    public List<ClassGroupResponse> getAllClassGroups() {
        return classGroupService.getAllClassGroups();
    }

    @GetMapping("/{id}")
    public ClassGroupResponse getClassGroup(@PathVariable UUID id) {
        return classGroupService.getClassGroup(id);
    }

    @PostMapping("/add")
    public void createClassGroup(@Valid @RequestBody ClassGroupRequest request) {
        classGroupService.createClassGroup(request);
    }

    @PatchMapping("/update")
    public void updateClassGroup(@Valid @RequestBody ClassGroupRequest request) {
        classGroupService.updateClassGroup(request);
    }

    @DeleteMapping("/{id}")
    public void deleteClassGroup(@PathVariable UUID id) {
        classGroupService.softDeleteClassGroup(id);
    }
}
