package org.marid.spring.scope;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marid.spring.LoggingPostProcessor;
import org.marid.test.logging.TestLogExtension;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.PreDestroy;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Tag("normal")
@ExtendWith({TestLogExtension.class, SpringExtension.class})
@ContextConfiguration(initializers = {ResettableScopeTest.Initializer.class})
class ResettableScopeTest {

  private static final ResettableScope SCOPE = new ResettableScope("testScope");
  private static final AtomicInteger COUNTER = new AtomicInteger();
  private static final AtomicInteger DEPENDENT_COUNTER = new AtomicInteger();

  @Autowired
  private ObjectFactory<Runnable> incrementor;

  @Autowired
  private ObjectFactory<Runnable> dependentIncrementor;

  @Autowired
  private ObjectFactory<ChildDestroyableBean> destroyableBean;

  @Test
  void test() throws ScopeResetException {
    final var oldIncrementor = incrementor.getObject();
    final var oldDependentIncrementor = dependentIncrementor.getObject();

    final var destroyableBean = this.destroyableBean.getObject();

    SCOPE.reset();

    final var newIncrementor = incrementor.getObject();
    final var newDependentIncrementor = dependentIncrementor.getObject();

    assertNotSame(oldIncrementor, newIncrementor);
    assertNotSame(oldDependentIncrementor, newDependentIncrementor);

    assertEquals(List.of("child", "parent"), destroyableBean.log);

    final var incrementor = this.incrementor.getObject();
    final var dependentIncrementor = this.dependentIncrementor.getObject();

    assertSame(newIncrementor, incrementor);
    assertSame(newDependentIncrementor, dependentIncrementor);
  }

  @Configuration
  @Import({LoggingPostProcessor.class})
  static class Context {

    private static final AtomicInteger INCREMENT_BASE = new AtomicInteger();

    @Bean
    @Scope("test")
    Runnable dependentIncrementor() {
      final int base = INCREMENT_BASE.incrementAndGet();
      return () -> DEPENDENT_COUNTER.addAndGet(base);
    }

    @Bean
    @Scope("test")
    Runnable incrementor(Runnable dependentIncrementor) {
      return () -> {
        dependentIncrementor.run();
        COUNTER.incrementAndGet();
      };
    }

    @Bean
    @Scope("test")
    ChildDestroyableBean bean() {
      return new ChildDestroyableBean();
    }
  }

  static class Initializer implements ApplicationContextInitializer<GenericApplicationContext> {

    @Override
    public void initialize(@NonNull GenericApplicationContext applicationContext) {
      applicationContext.getDefaultListableBeanFactory().registerScope("test", SCOPE);
    }
  }

  static class DestroyableBean {

    final LinkedList<String> log = new LinkedList<>();

    @PreDestroy
    public void close() {
      log.add("parent");
    }
  }

  static class ChildDestroyableBean extends DestroyableBean {

    @PreDestroy
    public void close2() {
      log.add("child");
    }
  }
}
