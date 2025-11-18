package dev.kumar.edunexus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    
    @Value("${gemini.api.key:}")
    private String apiKey;
    
    private final WebClient webClient;
    
    public GeminiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }
    
    public String generateCompletionFeedback(int xpEarned, boolean isCompleted, int dayStreak) {
        String prompt = String.format(
            "Generate encouraging feedback (max 30 words) for a student who %s a level, earned %d XP, and has a %d-day streak.",
            isCompleted ? "completed" : "attempted", xpEarned, dayStreak
        );
        
        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );
        
        try {
            Map<String, Object> response = webClient.post()
                    .uri("/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            return extractTextFromResponse(response);
        } catch (Exception e) {
            return isCompleted ? 
                String.format("Great job! You earned %d XP and maintained your %d-day streak!", xpEarned, dayStreak) :
                "Keep practicing! Every attempt brings you closer to mastery.";
        }
    }
    
    public String generateHint(String question, List<String> options, List<String> correctAnswers) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Analyze the question carefully and consider which option best fits the context.";
        }
        
        String prompt = String.format(
            "Provide a brief hint (max 15 words) for this question without revealing the answer: %s\nOptions: %s",
            question, String.join(", ", options)
        );
        
        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );
        
        try {
            Map<String, Object> response = webClient.post()
                    .uri("/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            return extractTextFromResponse(response);
        } catch (WebClientResponseException e) {
            // Log the specific API error for future debugging, then return fallback
            System.err.println("Gemini API Error Status: " + e.getStatusCode());
            System.err.println("Gemini API Error Body: " + e.getResponseBodyAsString());
            return "Focus on key terms in the question and eliminate unlikely options.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Focus on key terms in the question and eliminate unlikely options.";
        }
    }
    
    private String extractTextFromResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            return "Consider the context and eliminate incorrect options systematically.";
        }
    }
}