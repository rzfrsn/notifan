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

  /**
   * Publishes a simulated {@code eventType} to the platform events Kafka topic.
   * <p>
   * If {@code recipientUsername} is absent (or doesn't match a known test user), a random
   * {@link TestUser} is picked as recipient. The actor is always a random test user distinct
   * from the recipient.
   *
   * @param eventType         the type of event to simulate
   * @param recipientUsername optional username of the recipient test user
   * @return a human-readable summary of what was sent, and by/to whom
   */
  @PostMapping("/events")
  public String simulate(@RequestParam EventType eventType, @RequestParam Optional<String> recipientUsername) {
    TestUser recipient = resolveUser(recipientUsername);
    TestUser actor = randomUserExcept(recipient);

    publish(eventType, actor, recipient);

    return "Sent %s as %s -> %s".formatted(eventType, actor.username(), recipient.username());
  }

  /**
   * Fires {@code count} {@code eventType} events at the same recipient back-to-back — meant to
   * demo the sliding-window rate limiter ({@code application.rate-limit}) live: with the default
   * {@code max-requests: 10}, a burst of 15 should land as ~10 delivered and ~5 RATE_LIMITED,
   * visible on the Grafana dashboard as it happens.
   * <p>
   * All events share the same Kafka partitioning key (the recipient), so they're consumed
   * in order by a single consumer — the SENT/RATE_LIMITED split comes out deterministic.
   *
   * @param eventType         the type of event to simulate
   * @param recipientUsername optional username of the recipient test user (random if absent)
   * @param count             how many events to fire at that recipient
   * @return a human-readable summary of the burst
   */
  @PostMapping("/burst")
  public String burst(
      @RequestParam EventType eventType,
      @RequestParam Optional<String> recipientUsername,
      @RequestParam int count
    ) {
    TestUser recipient = resolveUser(recipientUsername);

    for (int i = 0; i < count; i++) {
      publish(eventType, randomUserExcept(recipient), recipient);
    }

    return "Fired %d %s events at %s".formatted(count, eventType, recipient.username());
  }

  // --- Helpers below — simulator only, not worth extracting to a separate class ---
  // Build/send the Kafka event, and pick test users to stand in as actor/recipient.

  private void publish(EventType eventType, TestUser actor, TestUser recipient) {
    String json = EventBuilder.generate(eventType, actor.id(), recipient.id());
    kafkaTemplate.send(applicationProperties.getPlatformEventsTopic(), recipient.id().toString(), json);
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
