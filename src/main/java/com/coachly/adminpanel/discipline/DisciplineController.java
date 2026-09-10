package com.coachly.adminpanel.discipline;

import com.coachly.adminpanel.discipline.dto.DisciplineRequest;
import com.coachly.adminpanel.discipline.dto.DisciplineResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discipline")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
public class DisciplineController {

    private final DisciplineService disciplineService;

    @GetMapping("/all")
    public List<DisciplineResponse> getAllDisciplines() {
        return disciplineService.getAllDisciplines();
    }

    @GetMapping("/{disciplineSlug}")
    public DisciplineResponse getDiscipline(@PathVariable String disciplineSlug) {
        return disciplineService.getDiscipline(disciplineSlug);
    }

    @PostMapping("/add")
    public void createDiscipline(@Valid @RequestBody DisciplineRequest disciplineRequest) {
        disciplineService.createDiscipline(disciplineRequest);
    }

    @PatchMapping("/update")
    public void updateDiscipline(@Valid @RequestBody DisciplineRequest disciplineRequest) {
        disciplineService.updateDiscipline(disciplineRequest);
    }

    @DeleteMapping("/{disciplineSlug}")
    public void deleteDiscipline(@PathVariable String disciplineSlug) {
        disciplineService.deleteDiscipline(disciplineSlug);
    }
}
