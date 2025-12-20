package com.email.writer.app;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.WebClient;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
public class EmailGeneratorService {

    private final WebClient webClient;



    @Value("${gemini.api.url}")
  private String geminiApiUrl;

  @Value("${gemini.api.key}")
  private String geminiApiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();

    }

  public String generateEmailReply(EmailRequest emailRequest) {
    String prompt  = buildPrompt(emailRequest);

    Map<String, Object> requestBody = Map.of(
      "contents", new Object[] {
        Map.of("parts", new Object[]{
          Map.of("text", prompt)
        })
      }
    );
   String response  = webClient.post()
           .uri(geminiApiUrl + "?key=" + geminiApiKey)
           .header("Content-Type", "application/json")
           .bodyValue(requestBody)
           .retrieve()
           .bodyToMono(String.class)
           .block();
      System.out.println("CALLING URL = " + geminiApiUrl + "?key=" + geminiApiKey);



      return extractResponseContent(response);

    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        }catch(Exception e){
            return "Error processing request: " + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
    StringBuilder prompt = new StringBuilder();
        prompt.append("Write ONE short and simple email reply. ");
        prompt.append("Format exactly like this:\n");
        prompt.append("Hi [Sender Name],\n\n");
        prompt.append("[One short friendly reply sentence].\n\n");
        prompt.append("Do NOT generate a subject line. Do NOT give multiple options. Only one email reply.");
        prompt.append("\nOriginal email: ");



        if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()){
      prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
    }
    prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
    return prompt.toString();
  }

}
