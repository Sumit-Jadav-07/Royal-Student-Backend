package com.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ServerWakeUpScheduler {

  private final HttpClient httpClient = HttpClient.newHttpClient();

  @Scheduled(fixedRate = 300000) // 🛑 5 Min ke interval pe chalega
  public void wakeUpServer() {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("https://royal-student.onrender.com/ServerWakeUp.jsp"))
          .GET()
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      System.out.println("✅ Server wake-up hit successful: " + response.statusCode());
    } catch (Exception e) {
      System.err.println("❌ Error hitting server: " + e.getMessage());
    }
  }

}
