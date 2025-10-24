package dev.kumar.edunexus.controller;

import dev.kumar.edunexus.dto.CustomResponse;
import dev.kumar.edunexus.dto.QuestionDTO;
import dev.kumar.edunexus.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {
    
    private final QuestionService service;
    
    @GetMapping("/level/{levelId}")
    public CustomResponse<List<QuestionDTO>> getQuestionsByLevelHandler(@PathVariable UUID levelId) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Questions fetched successfully",
                service.getQuestionsByLevel(levelId)
        );
    }
    
    @PostMapping
    public CustomResponse<QuestionDTO> createQuestionHandler(@RequestBody QuestionDTO questionDTO) {
        return new CustomResponse<>(
                HttpStatus.CREATED,
                "Question created successfully",
                service.createQuestion(questionDTO)
        );
    }
    
    @PutMapping("/{questionId}")
    public CustomResponse<QuestionDTO> updateQuestionHandler(
            @PathVariable UUID questionId,
            @RequestBody QuestionDTO questionDTO
    ) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Question updated successfully",
                service.updateQuestion(questionId, questionDTO)
        );
    }
    
    @DeleteMapping("/{questionId}")
    public CustomResponse<Void> deleteQuestionHandler(@PathVariable UUID questionId) {
        service.deleteQuestion(questionId);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Question deleted successfully",
                null
        );
    }
}