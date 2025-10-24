package dev.kumar.edunexus.service;

import dev.kumar.edunexus.dto.SectionDTO;
import dev.kumar.edunexus.entity.Section;
import dev.kumar.edunexus.exception.ResourceNotFoundException;
import dev.kumar.edunexus.repository.CourseRepository;
import dev.kumar.edunexus.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionService {
    
    private final SectionRepository sectionRepo;
    private final CourseRepository courseRepo;
    
    public List<SectionDTO> getSectionsByCourse(UUID courseId) {
        if (!courseRepo.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        try {
            return sectionRepo.findByCourseId(courseId).stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error fetching sections for course " + courseId + ": " + e.getMessage());
            throw new RuntimeException("Failed to fetch sections");
        }
    }
    
    public SectionDTO createSection(SectionDTO sectionDTO) {
        if (!courseRepo.existsById(sectionDTO.getCourseId())) {
            throw new ResourceNotFoundException("Course not found with id: " + sectionDTO.getCourseId());
        }
        
        try {
            Section section = toEntity(sectionDTO);
            Section savedSection = sectionRepo.save(section);
            System.out.println("Section created with id: " + savedSection.getId());
            return toDTO(savedSection);
        } catch (Exception e) {
            System.out.println("Error creating section: " + e.getMessage());
            throw new RuntimeException("Failed to create section");
        }
    }
    
    public SectionDTO updateSection(UUID sectionId, SectionDTO sectionDTO) {
        Section existingSection = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        
        try {
            existingSection.setSectionName(sectionDTO.getSectionName());
            Section updatedSection = sectionRepo.save(existingSection);
            System.out.println("Section updated with id: " + sectionId);
            return toDTO(updatedSection);
        } catch (Exception e) {
            System.out.println("Error updating section " + sectionId + ": " + e.getMessage());
            throw new RuntimeException("Failed to update section");
        }
    }
    
    public void deleteSection(UUID sectionId) {
        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        
        try {
            sectionRepo.delete(section);
            System.out.println("Section deleted with id: " + sectionId);
        } catch (Exception e) {
            System.out.println("Error deleting section " + sectionId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete section");
        }
    }
    
    private SectionDTO toDTO(Section section) {
        return SectionDTO.builder()
                .id(section.getId())
                .sectionName(section.getSectionName())
                .courseId(section.getCourse().getId())
                .build();
    }
    
    private Section toEntity(SectionDTO dto) {
        return Section.builder()
                .sectionName(dto.getSectionName())
                .course(dev.kumar.edunexus.entity.Course.builder().id(dto.getCourseId()).build())
                .build();
    }
}