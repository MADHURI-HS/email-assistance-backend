package com.email.writer;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmailWriterSbApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailWriterSbApplication.class, args);

    }
    @PostConstruct
    public void checkEnv() {
        System.out.println("API KEY FROM ENV: " + System.getenv("GEMINI_API_KEY"));
    }



}
