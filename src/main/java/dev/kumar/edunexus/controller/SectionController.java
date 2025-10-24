package dev.kumar.edunexus.controller;

import dev.kumar.edunexus.dto.CustomResponse;
import dev.kumar.edunexus.dto.SectionDTO;
import dev.kumar.edunexus.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/section")
@RequiredArgsConstructor
public class SectionController {
    
    private final SectionService service;
    
    @GetMapping("/course/{courseId}")
    public CustomResponse<List<SectionDTO>> getSectionsByCourseHandler(@PathVariable UUID courseId) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Sections fetched successfully",
                service.getSectionsByCourse(courseId)
        );
    }
    
    @PostMapping
    public CustomResponse<SectionDTO> createSectionHandler(@RequestBody SectionDTO sectionDTO) {
        return new CustomResponse<>(
                HttpStatus.CREATED,
                "Section created successfully",
                service.createSection(sectionDTO)
        );
    }
    
    @PutMapping("/{sectionId}")
    public CustomResponse<SectionDTO> updateSectionHandler(
            @PathVariable UUID sectionId,
            @RequestBody SectionDTO sectionDTO
    ) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Section updated successfully",
                service.updateSection(sectionId, sectionDTO)
        );
    }
    
    @DeleteMapping("/{sectionId}")
    public CustomResponse<Void> deleteSectionHandler(@PathVariable UUID sectionId) {
        service.deleteSection(sectionId);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Section deleted successfully",
                null
        );
    }
}