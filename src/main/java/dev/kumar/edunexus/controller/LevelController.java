package dev.kumar.edunexus.controller;

import dev.kumar.edunexus.dto.CustomResponse;
import dev.kumar.edunexus.dto.LevelDTO;
import dev.kumar.edunexus.service.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/level")
@RequiredArgsConstructor
public class LevelController {
    
    private final LevelService service;
    
    @GetMapping("/unit/{unitId}")
    public CustomResponse<List<LevelDTO>> getLevelsByUnitHandler(@PathVariable UUID unitId) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Levels fetched successfully",
                service.getLevelsByUnit(unitId)
        );
    }
    
    @PostMapping
    public CustomResponse<LevelDTO> createLevelHandler(@RequestBody LevelDTO levelDTO) {
        return new CustomResponse<>(
                HttpStatus.CREATED,
                "Level created successfully",
                service.createLevel(levelDTO)
        );
    }
    
    @PutMapping("/{levelId}")
    public CustomResponse<LevelDTO> updateLevelHandler(
            @PathVariable UUID levelId,
            @RequestBody LevelDTO levelDTO
    ) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Level updated successfully",
                service.updateLevel(levelId, levelDTO)
        );
    }
    
    @DeleteMapping("/{levelId}")
    public CustomResponse<Void> deleteLevelHandler(@PathVariable UUID levelId) {
        service.deleteLevel(levelId);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Level deleted successfully",
                null
        );
    }
    
    @PostMapping("/{levelId}/complete")
    public CustomResponse<Void> completeLevelHandler(
            @PathVariable UUID levelId,
            @RequestHeader("Authorization") String token,
            @RequestParam int xpEarned
    ) {
        service.completeLevel(levelId, token, xpEarned);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Level completed successfully",
                null
        );
    }
}