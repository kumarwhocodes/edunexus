package dev.kumar.edunexus.service;

import dev.kumar.edunexus.dto.UnitDTO;
import dev.kumar.edunexus.entity.Unit;
import dev.kumar.edunexus.exception.ResourceNotFoundException;
import dev.kumar.edunexus.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitService {
    
    private final UnitRepository unitRepo;
    
    public List<UnitDTO> getUnitsBySection(UUID sectionId) {
        try {
            return unitRepo.findBySectionId(sectionId).stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error fetching units for section " + sectionId + ": " + e.getMessage());
            throw new RuntimeException("Failed to fetch units");
        }
    }
    
    public UnitDTO createUnit(UnitDTO unitDTO) {
        try {
            Unit unit = toEntity(unitDTO);
            Unit savedUnit = unitRepo.save(unit);
            System.out.println("Unit created with id: " + savedUnit.getId());
            return toDTO(savedUnit);
        } catch (Exception e) {
            System.out.println("Error creating unit: " + e.getMessage());
            throw new RuntimeException("Failed to create unit");
        }
    }
    
    public UnitDTO updateUnit(UUID unitId, UnitDTO unitDTO) {
        Unit existingUnit = unitRepo.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + unitId));
        
        try {
            existingUnit.setNumber(unitDTO.getNumber());
            existingUnit.setGuidance(unitDTO.getGuidance());
            Unit updatedUnit = unitRepo.save(existingUnit);
            System.out.println("Unit updated with id: " + unitId);
            return toDTO(updatedUnit);
        } catch (Exception e) {
            System.out.println("Error updating unit " + unitId + ": " + e.getMessage());
            throw new RuntimeException("Failed to update unit");
        }
    }
    
    public void deleteUnit(UUID unitId) {
        Unit unit = unitRepo.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + unitId));
        
        try {
            unitRepo.delete(unit);
            System.out.println("Unit deleted with id: " + unitId);
        } catch (Exception e) {
            System.out.println("Error deleting unit " + unitId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete unit");
        }
    }
    
    private UnitDTO toDTO(Unit unit) {
        return UnitDTO.builder()
                .id(unit.getId())
                .number(unit.getNumber())
                .guidance(unit.getGuidance())
                .sectionId(unit.getSection().getId())
                .build();
    }
    
    private Unit toEntity(UnitDTO dto) {
        return Unit.builder()
                .number(dto.getNumber())
                .guidance(dto.getGuidance())
                .section(dev.kumar.edunexus.entity.Section.builder().id(dto.getSectionId()).build())
                .build();
    }
}