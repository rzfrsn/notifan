package com.notifan.notifan.simulator;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.notifan.notifan.config.ApplicationProperties;
import com.notifan.notifan.notification.EventType;

import lombok.RequiredArgsConstructor;

/**
 * Dev/demo tool: publishes real events through the actual Kafka pipeline, so the full
 * flow (routing, rate limiting, dedup, delivery) can be triggered and narrated live.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/simulate")
public class SimulatorController {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ApplicationProperties applicationProperties;

  @PostMapping("/events")
  public String simulate(@RequestParam EventType eventType, @RequestParam Optional<String> recipientUsername) {
    TestUser recipient = resolveUser(recipientUsername);
    TestUser actor = randomUserExcept(recipient);

    String json = EventBuilder.generate(eventType, actor.id(), recipient.id());
    kafkaTemplate.send(applicationProperties.getPlatformEventsTopic(), recipient.id().toString(), json);

    return "Sent %s as %s -> %s".formatted(eventType, actor.username(), recipient.username());
  }

  private TestUser resolveUser(Optional<String> username) {
    return username
        .flatMap(name -> TestUsers.TEST_USERS.stream()
            .filter(u -> u.username().equalsIgnoreCase(name))
            .findFirst())
        .orElseGet(this::randomUser);
  }

  private TestUser randomUser() {
    List<TestUser> users = TestUsers.TEST_USERS;
    return users.get(ThreadLocalRandom.current().nextInt(users.size()));
  }

  private TestUser randomUserExcept(TestUser excluded) {
    TestUser candidate;

    do {
      candidate = randomUser();
    } while (candidate.equals(excluded) && TestUsers.TEST_USERS.size() > 1);

    return candidate;
  }
}
