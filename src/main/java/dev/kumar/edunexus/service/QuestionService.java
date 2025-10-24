package dev.kumar.edunexus.service;

import dev.kumar.edunexus.dto.QuestionDTO;
import dev.kumar.edunexus.entity.Question;
import dev.kumar.edunexus.exception.ResourceNotFoundException;
import dev.kumar.edunexus.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    
    private final QuestionRepository questionRepo;
    
    public List<QuestionDTO> getQuestionsByLevel(UUID levelId) {
        try {
            return questionRepo.findByLevelId(levelId).stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error fetching questions for level " + levelId + ": " + e.getMessage());
            throw new RuntimeException("Failed to fetch questions");
        }
    }
    
    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        try {
            Question question = toEntity(questionDTO);
            Question savedQuestion = questionRepo.save(question);
            System.out.println("Question created with id: " + savedQuestion.getId());
            return toDTO(savedQuestion);
        } catch (Exception e) {
            System.out.println("Error creating question: " + e.getMessage());
            throw new RuntimeException("Failed to create question");
        }
    }
    
    public QuestionDTO updateQuestion(UUID questionId, QuestionDTO questionDTO) {
        Question existingQuestion = questionRepo.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        
        try {
            existingQuestion.setType(questionDTO.getType());
            existingQuestion.setQuestion(questionDTO.getQuestion());
            Question updatedQuestion = questionRepo.save(existingQuestion);
            System.out.println("Question updated with id: " + questionId);
            return toDTO(updatedQuestion);
        } catch (Exception e) {
            System.out.println("Error updating question " + questionId + ": " + e.getMessage());
            throw new RuntimeException("Failed to update question");
        }
    }
    
    public void deleteQuestion(UUID questionId) {
        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        
        try {
            questionRepo.delete(question);
            System.out.println("Question deleted with id: " + questionId);
        } catch (Exception e) {
            System.out.println("Error deleting question " + questionId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete question");
        }
    }
    
    private QuestionDTO toDTO(Question question) {
        return QuestionDTO.builder()
                .id(question.getId())
                .type(question.getType())
                .question(question.getQuestion())
                .levelId(question.getLevel().getId())
                .build();
    }
    
    private Question toEntity(QuestionDTO dto) {
        return Question.builder()
                .type(dto.getType())
                .question(dto.getQuestion())
                .level(dev.kumar.edunexus.entity.Level.builder().id(dto.getLevelId()).build())
                .build();
    }
}