package com.coachly.adminpanel.level;

import com.coachly.adminpanel.level.dto.LevelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/level")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
public class LevelController {

    private final LevelService levelService;

    @GetMapping("/all")
    public List<LevelResponse> getAllLevels() {
        return levelService.getAllLevels();
    }

    @GetMapping("/{id}")
    public LevelResponse getLevel(@PathVariable UUID id) {
        return levelService.getLevel(id);
    }
}
