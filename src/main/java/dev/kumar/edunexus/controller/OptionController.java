package dev.kumar.edunexus.controller;

import dev.kumar.edunexus.dto.CustomResponse;
import dev.kumar.edunexus.dto.OptionDTO;
import dev.kumar.edunexus.service.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/option")
@RequiredArgsConstructor
public class OptionController {
    
    private final OptionService service;
    
    @GetMapping("/question/{questionId}")
    public CustomResponse<List<OptionDTO>> getOptionsByQuestionHandler(@PathVariable UUID questionId) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Options fetched successfully",
                service.getOptionsByQuestion(questionId)
        );
    }
    
    @PostMapping
    public CustomResponse<OptionDTO> createOptionHandler(@RequestBody OptionDTO optionDTO) {
        return new CustomResponse<>(
                HttpStatus.CREATED,
                "Option created successfully",
                service.createOption(optionDTO)
        );
    }
    
    @PutMapping("/{optionId}")
    public CustomResponse<OptionDTO> updateOptionHandler(
            @PathVariable UUID optionId,
            @RequestBody OptionDTO optionDTO
    ) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Option updated successfully",
                service.updateOption(optionId, optionDTO)
        );
    }
    
    @DeleteMapping("/{optionId}")
    public CustomResponse<Void> deleteOptionHandler(@PathVariable UUID optionId) {
        service.deleteOption(optionId);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Option deleted successfully",
                null
        );
    }
}