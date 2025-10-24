package dev.kumar.edunexus.service;

import dev.kumar.edunexus.dto.OptionDTO;
import dev.kumar.edunexus.entity.Option;
import dev.kumar.edunexus.exception.ResourceNotFoundException;
import dev.kumar.edunexus.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionService {
    
    private final OptionRepository optionRepo;
    
    public List<OptionDTO> getOptionsByQuestion(UUID questionId) {
        try {
            return optionRepo.findByQuestionId(questionId).stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error fetching options for question " + questionId + ": " + e.getMessage());
            throw new RuntimeException("Failed to fetch options");
        }
    }
    
    public OptionDTO createOption(OptionDTO optionDTO) {
        try {
            Option option = toEntity(optionDTO);
            Option savedOption = optionRepo.save(option);
            System.out.println("Option created with id: " + savedOption.getId());
            return toDTO(savedOption);
        } catch (Exception e) {
            System.out.println("Error creating option: " + e.getMessage());
            throw new RuntimeException("Failed to create option");
        }
    }
    
    public OptionDTO updateOption(UUID optionId, OptionDTO optionDTO) {
        Option existingOption = optionRepo.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));
        
        try {
            existingOption.setText(optionDTO.getText());
            existingOption.setAnswer(optionDTO.isAnswer());
            Option updatedOption = optionRepo.save(existingOption);
            System.out.println("Option updated with id: " + optionId);
            return toDTO(updatedOption);
        } catch (Exception e) {
            System.out.println("Error updating option " + optionId + ": " + e.getMessage());
            throw new RuntimeException("Failed to update option");
        }
    }
    
    public void deleteOption(UUID optionId) {
        Option option = optionRepo.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));
        
        try {
            optionRepo.delete(option);
            System.out.println("Option deleted with id: " + optionId);
        } catch (Exception e) {
            System.out.println("Error deleting option " + optionId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete option");
        }
    }
    
    private OptionDTO toDTO(Option option) {
        return OptionDTO.builder()
                .id(option.getId())
                .text(option.getText())
                .isAnswer(option.isAnswer())
                .questionId(option.getQuestion().getId())
                .build();
    }
    
    private Option toEntity(OptionDTO dto) {
        return Option.builder()
                .text(dto.getText())
                .isAnswer(dto.isAnswer())
                .question(dev.kumar.edunexus.entity.Question.builder().id(dto.getQuestionId()).build())
                .build();
    }
}