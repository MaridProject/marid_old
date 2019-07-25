package org.marid.test.logging;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class TestLogExtension implements BeforeAllCallback {

  @Override
  public void beforeAll(ExtensionContext context) {
    final var logManager = LogManager.getLogManager();
    final var logger = Logger.getLogger("");

    if (Arrays.stream(logger.getHandlers()).noneMatch(h -> h instanceof TestLogHandler)) {
      logManager.reset();
      logger.addHandler(new TestLogHandler());
    }
  }
}
