package com.coachly.adminpanel.agegroup;

import com.coachly.adminpanel.agegroup.dto.AgeGroupResponse;
import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgeGroupService {

    private final AgeGroupRepository ageGroupRepository;

    @Transactional(readOnly = true)
    public List<AgeGroupResponse> getAllAgeGroups() {
        return ageGroupRepository.findAll().stream()
                .map(a -> new AgeGroupResponse(a.getId(), a.getSlug(), a.getName(), a.getIsActive()))
                .toList();
    }

    @Transactional(readOnly = true)
    public AgeGroupResponse getAgeGroup(UUID id) {
        return ageGroupRepository.findById(id)
                .map(a -> new AgeGroupResponse(a.getId(), a.getSlug(), a.getName(), a.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException("Age group not found with id: " + id));
    }
}
