package com.coachly.adminpanel.level;

import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import com.coachly.adminpanel.level.dto.LevelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;

    @Transactional(readOnly = true)
    public List<LevelResponse> getAllLevels() {
        return levelRepository.findAll().stream()
                .map(l -> new LevelResponse(l.getId(), l.getSlug(), l.getName(), l.getIsActive()))
                .toList();
    }

    @Transactional(readOnly = true)
    public LevelResponse getLevel(UUID id) {
        return levelRepository.findById(id)
                .map(l -> new LevelResponse(l.getId(), l.getSlug(), l.getName(), l.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + id));
    }
}
