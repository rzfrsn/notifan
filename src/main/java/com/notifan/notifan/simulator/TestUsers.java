package com.notifan.notifan.simulator;

import java.util.List;
import java.util.UUID;

import lombok.experimental.UtilityClass;

/**
 * Fixed pool of {@link TestUser}s the event simulator picks actors/recipients from.
 * IDs are regenerated on each app startup, so they're only stable within a running instance.
 */
@UtilityClass
public class TestUsers {
  public static final TestUser BOB = new TestUser(UUID.randomUUID(), "Bob");
  public static final TestUser ALICE = new TestUser(UUID.randomUUID(), "Alice");
  public static final TestUser SIMON = new TestUser(UUID.randomUUID(), "SIMON");
  public static final TestUser TERESA = new TestUser(UUID.randomUUID(), "TERESA");
  public static final TestUser DAVID = new TestUser(UUID.randomUUID(), "David");
  public static final TestUser EMMA = new TestUser(UUID.randomUUID(), "Emma");
  public static final TestUser FRANK = new TestUser(UUID.randomUUID(), "Frank");
  public static final TestUser GRACE = new TestUser(UUID.randomUUID(), "Grace");
  public static final TestUser HENRY = new TestUser(UUID.randomUUID(), "Henry");
  public static final TestUser ISABEL = new TestUser(UUID.randomUUID(), "Isabel");

  public static final List<TestUser> TEST_USERS = List.of(
      BOB, ALICE, SIMON, TERESA, DAVID, EMMA, FRANK, GRACE, HENRY, ISABEL
    );
}
