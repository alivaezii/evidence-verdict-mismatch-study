package cz.cyberrange.platform.training.feedback.service.graph;

import cz.cyberrange.platform.training.feedback.repository.GraphRepository;
import cz.cyberrange.platform.training.feedback.service.TraineeService;
import cz.cyberrange.platform.training.feedback.service.api.ElasticsearchServiceApi;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TraineeGraphSpringScopeTest {

  private AnnotationConfigApplicationContext createContext() {
    AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext();

    context.registerBean(
        GraphRepository.class,
        () -> mock(GraphRepository.class));

    context.registerBean(
        TraineeService.class,
        () -> mock(TraineeService.class));

    context.registerBean(
        ElasticsearchServiceApi.class,
        () -> mock(ElasticsearchServiceApi.class));

    context.register(TraineeGraphService.class);
    context.refresh();

    return context;
  }

  @SuppressWarnings("unchecked")
  private List<String> state(TraineeGraphService service) {
    Object value = ReflectionTestUtils.getField(
        service, "achievedNodesLabels");
    assertNotNull(value);
    return (List<String>) value;
  }

  @Test
  void springSingletonRetainsInstanceState() {

    try (AnnotationConfigApplicationContext first =
             createContext();
         AnnotationConfigApplicationContext second =
             createContext()) {

      TraineeGraphService a =
          first.getBean(TraineeGraphService.class);

      TraineeGraphService b =
          first.getBean(TraineeGraphService.class);

      TraineeGraphService independent =
          second.getBean(TraineeGraphService.class);

      assertSame(a, b);
      assertNotSame(a, independent);

      assertTrue(state(a).isEmpty());
      assertTrue(state(independent).isEmpty());

      state(a).add("STATE_SCOPE_TEST_MARKER");

      assertTrue(
          state(b).contains("STATE_SCOPE_TEST_MARKER"));

      assertFalse(
          state(independent).contains("STATE_SCOPE_TEST_MARKER"));

      System.out.println(
          "SPRING_SCOPE same_context_same_instance="
              + (a == b));

      System.out.println(
          "SPRING_SCOPE retained_in_same_context="
              + state(b).contains("STATE_SCOPE_TEST_MARKER"));

      System.out.println(
          "SPRING_SCOPE isolated_across_contexts="
              + !state(independent).contains("STATE_SCOPE_TEST_MARKER"));
    }
  }
}
