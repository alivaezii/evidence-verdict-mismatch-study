package cz.cyberrange.platform.training.feedback.service.graph;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cyberrange.platform.training.feedback.constants.GraphConstants;
import cz.cyberrange.platform.training.feedback.dto.resolver.DefinitionLevel;
import cz.cyberrange.platform.training.feedback.dto.resolver.TrainingCommand;
import cz.cyberrange.platform.training.feedback.dto.resolver.TrainingEvent;
import cz.cyberrange.platform.training.feedback.model.Graph;
import cz.cyberrange.platform.training.feedback.repository.GraphRepository;
import cz.cyberrange.platform.training.feedback.repository.TraineeRepository;
import cz.cyberrange.platform.training.feedback.service.CreateCommandService;
import cz.cyberrange.platform.training.feedback.service.MistakeAnalysisService;
import cz.cyberrange.platform.training.feedback.service.TraineeService;
import cz.cyberrange.platform.training.feedback.service.api.ElasticsearchServiceApi;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.util.ReflectionTestUtils;

class TraineeGraphSpringCausalTest {

  private static final String FIXTURE_DIR = "crcz-exact-replay/";

  private final ObjectMapper objectMapper =
      new ObjectMapper().findAndRegisterModules();


  @Test
  void causalStateIntervention() throws Exception {

    // A: B-minus on a fresh service.
    Harness fresh = newHarness();
    Graph a = executeFrozenRun(
        fresh,
        "CRCZ-NATIVE-BMINUS-events.json",
        "CRCZ-NATIVE-BMINUS-commands.json",
        9001L, 9002L, 9102L, "crcz-native-bminus");

    boolean aGreen = hasNode(
        a, "exploited_by_exploit", GraphConstants.GREEN);

    org.junit.jupiter.api.Assertions.assertFalse(
        aGreen, "A must reject incomplete B-minus");

    // B: B-plus followed by B-minus on the same service.
    Harness retained = newHarness();
    Graph primeB = executeFrozenRun(
        retained,
        "CRCZ-NATIVE-BPLUS-events.json",
        "CRCZ-NATIVE-BPLUS-commands.json",
        9001L, 9002L, 9101L, "crcz-native-bplus");

    org.junit.jupiter.api.Assertions.assertTrue(
        hasNode(primeB, "exploited_by_exploit", GraphConstants.GREEN));

    Graph b = executeFrozenRun(
        retained,
        "CRCZ-NATIVE-BMINUS-events.json",
        "CRCZ-NATIVE-BMINUS-commands.json",
        9001L, 9002L, 9102L, "crcz-native-bminus");

    boolean bGreen = hasNode(
        b, "exploited_by_exploit", GraphConstants.GREEN);

    org.junit.jupiter.api.Assertions.assertTrue(
        bGreen, "B must reproduce the stale-state GREEN");

    // C: Same B-plus prime, but clear only internal state.
    Harness cleared = newHarness();
    Graph primeC = executeFrozenRun(
        cleared,
        "CRCZ-NATIVE-BPLUS-events.json",
        "CRCZ-NATIVE-BPLUS-commands.json",
        9001L, 9002L, 9101L, "crcz-native-bplus");

    org.junit.jupiter.api.Assertions.assertTrue(
        hasNode(primeC, "exploited_by_exploit", GraphConstants.GREEN));

    @SuppressWarnings("unchecked")
    List<String> state = (List<String>)
        ReflectionTestUtils.getField(
            cleared.service, "achievedNodesLabels");

    org.junit.jupiter.api.Assertions.assertNotNull(state);
    org.junit.jupiter.api.Assertions.assertFalse(state.isEmpty());

    int removed = state.size();
    state.clear();

    org.junit.jupiter.api.Assertions.assertTrue(state.isEmpty());

    Graph c = executeFrozenRun(
        cleared,
        "CRCZ-NATIVE-BMINUS-events.json",
        "CRCZ-NATIVE-BMINUS-commands.json",
        9001L, 9002L, 9102L, "crcz-native-bminus");

    boolean cGreen = hasNode(
        c, "exploited_by_exploit", GraphConstants.GREEN);

    org.junit.jupiter.api.Assertions.assertFalse(
        cGreen,
        "C must reject B-minus after state clearing");

    System.out.println(
        "SPRING_CAUSAL_A fresh_Bminus_GREEN=" + aGreen);

    System.out.println(
        "SPRING_CAUSAL_B primed_Bminus_GREEN=" + bGreen);

    System.out.println(
        "SPRING_CAUSAL_C cleared_Bminus_GREEN=" + cGreen);

    System.out.println(
        "SPRING_CAUSAL_INTERVENTION removed_labels=" + removed
        + " result=PASS");
  }


  private final java.util.List<
      org.springframework.context.annotation.AnnotationConfigApplicationContext>
      contexts = new java.util.ArrayList<>();

  @org.junit.jupiter.api.AfterEach
  void closeContexts() {
    for (int i = contexts.size() - 1; i >= 0; i--) {
      contexts.get(i).close();
    }
    contexts.clear();
  }

  private Harness newHarness() {

    GraphRepository graphRepository = mock(GraphRepository.class);
    TraineeRepository traineeRepository = mock(TraineeRepository.class);
    ElasticsearchServiceApi elasticsearch =
        mock(ElasticsearchServiceApi.class);

    MistakeAnalysisService mistakeAnalysisService =
        new MistakeAnalysisService(
            new PathMatchingResourcePatternResolver());

    ReflectionTestUtils.invokeMethod(
        mistakeAnalysisService, "init");

    CreateCommandService createCommandService =
        new CreateCommandService(mistakeAnalysisService);

    TraineeService traineeService =
        new TraineeService(
            traineeRepository, createCommandService);

    when(graphRepository.save(any(Graph.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    org.springframework.context.annotation.AnnotationConfigApplicationContext
        context =
            new org.springframework.context.annotation.AnnotationConfigApplicationContext();

    contexts.add(context);

    context.registerBean(
        GraphRepository.class, () -> graphRepository);

    context.registerBean(
        TraineeService.class, () -> traineeService);

    context.registerBean(
        ElasticsearchServiceApi.class, () -> elasticsearch);

    // Spring constructs the unmodified production service.
    context.register(TraineeGraphService.class);
    context.refresh();

    TraineeGraphService service =
        context.getBean(TraineeGraphService.class);

    org.junit.jupiter.api.Assertions.assertSame(
        service, context.getBean(TraineeGraphService.class));

    org.junit.jupiter.api.Assertions.assertEquals(
        TraineeGraphService.class, service.getClass());

    System.out.println(
        "SPRING_BEAN singleton=true production_class=true");

    return new Harness(service, elasticsearch);
  }

  private Graph executeFrozenRun(
      Harness h,
      String eventsFile,
      String commandsFile,
      long definitionId,
      long instanceId,
      long runId,
      String sandboxId)
      throws Exception {

    List<TrainingEvent> events =
        read(
            eventsFile,
            new TypeReference<List<TrainingEvent>>() {});

    List<TrainingCommand> commands =
        read(
            commandsFile,
            new TypeReference<List<TrainingCommand>>() {});

    List<DefinitionLevel> levels =
        read(
            "CRCZ-NATIVE-definition-level.json",
            new TypeReference<List<DefinitionLevel>>() {});

    when(
            h.elasticsearch.getTrainingEventsByTrainingRunId(
                definitionId,
                instanceId,
                runId))
        .thenReturn(events);

    when(
            h.elasticsearch.getTrainingCommandsBySandboxId(
                sandboxId))
        .thenReturn(commands);

    return h.service.createTraineeGraphCloudInstance(
        definitionId,
        instanceId,
        runId,
        levels);
  }

  private <T> T read(
      String filename,
      TypeReference<T> type)
      throws IOException {

    try (InputStream input =
        Objects.requireNonNull(
            getClass()
                .getClassLoader()
                .getResourceAsStream(
                    FIXTURE_DIR + filename),
            "Missing frozen fixture: " + filename)) {

      return objectMapper.readValue(
          input,
          type);
    }
  }

  private boolean hasNode(
      Graph graph,
      String label,
      String color) {

    return graph.getSubGraphs().stream()
        .flatMap(
            sg ->
                sg.getNodes().stream())
        .anyMatch(
            n ->
                label.equals(n.getLabel())
                    && color.equals(n.getColor()));
  }

  private boolean hasYellowAttempt(
      Graph graph,
      String targetLabel) {

    return graph.getSubGraphs().stream()
        .flatMap(
            sg ->
                sg.getNodes().stream())
        .anyMatch(
            n ->
                GraphConstants.YELLOW.equals(n.getColor())
                    && n.getLabel().contains(targetLabel));
  }

  private static class Harness {

    private final TraineeGraphService service;
    private final ElasticsearchServiceApi elasticsearch;

    private Harness(
        TraineeGraphService service,
        ElasticsearchServiceApi elasticsearch) {

      this.service = service;
      this.elasticsearch = elasticsearch;
    }
  }
}
