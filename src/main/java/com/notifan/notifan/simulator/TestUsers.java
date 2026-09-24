package com.notifan.notifan.simulator;

import java.util.List;
import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUsers {
  public static final TestUser BOB = new TestUser(UUID.randomUUID(), "Bob");
  public static final TestUser ALICE = new TestUser(UUID.randomUUID(), "Alice");
  public static final TestUser SIMON = new TestUser(UUID.randomUUID(), "SIMON");
  public static final TestUser TERESA = new TestUser(UUID.randomUUID(), "TERESA");

  public static final List<TestUser> TEST_USERS = List.of(BOB, ALICE, SIMON, TERESA);
}
