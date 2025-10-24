package dev.kumar.edunexus.controller;

import dev.kumar.edunexus.dto.CustomResponse;
import dev.kumar.edunexus.dto.UnitDTO;
import dev.kumar.edunexus.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/unit")
@RequiredArgsConstructor
public class UnitController {
    
    private final UnitService service;
    
    @GetMapping("/section/{sectionId}")
    public CustomResponse<List<UnitDTO>> getUnitsBySectionHandler(@PathVariable UUID sectionId) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Units fetched successfully",
                service.getUnitsBySection(sectionId)
        );
    }
    
    @PostMapping
    public CustomResponse<UnitDTO> createUnitHandler(@RequestBody UnitDTO unitDTO) {
        return new CustomResponse<>(
                HttpStatus.CREATED,
                "Unit created successfully",
                service.createUnit(unitDTO)
        );
    }
    
    @PutMapping("/{unitId}")
    public CustomResponse<UnitDTO> updateUnitHandler(
            @PathVariable UUID unitId,
            @RequestBody UnitDTO unitDTO
    ) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Unit updated successfully",
                service.updateUnit(unitId, unitDTO)
        );
    }
    
    @DeleteMapping("/{unitId}")
    public CustomResponse<Void> deleteUnitHandler(@PathVariable UUID unitId) {
        service.deleteUnit(unitId);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Unit deleted successfully",
                null
        );
    }
}