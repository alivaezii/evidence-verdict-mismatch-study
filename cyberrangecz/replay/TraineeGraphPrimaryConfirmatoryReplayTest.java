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

class TraineeGraphPrimaryConfirmatoryReplayTest {

  private static final String FIXTURE_DIR = "crcz-exact-replay/";

  private final ObjectMapper objectMapper =
      new ObjectMapper().findAndRegisterModules();

  @Test
  void crcZP001ThreeCleanStartRepetitionsThenNegativeControl()
      throws Exception {

    for (int repetition = 1; repetition <= 3; repetition++) {

      // Mandatory clean start for every independent confirmatory repetition.
      Harness h = newHarness();

      Graph prime =
          executeFrozenRun(
              h,
              "CRCZ-NATIVE-BPLUS-events.json",
              "CRCZ-NATIVE-BPLUS-commands.json",
              9001L,
              9002L,
              9101L,
              "crcz-native-bplus");

      boolean primeGreen =
          hasNode(
              prime,
              "exploited_by_exploit",
              GraphConstants.GREEN);

      // Same service instance intentionally retained here.
      // This is the preregistered stale-state condition.
      Graph perturbed =
          executeFrozenRun(
              h,
              "CRCZ-NATIVE-BMINUS-events.json",
              "CRCZ-NATIVE-BMINUS-commands.json",
              9001L,
              9002L,
              9102L,
              "crcz-native-bminus");

      boolean perturbedGreen =
          hasNode(
              perturbed,
              "exploited_by_exploit",
              GraphConstants.GREEN);

      boolean perturbedYellow =
          hasYellowAttempt(
              perturbed,
              "exploited_by_exploit");

      System.out.println(
          "RUN_ID=CYBERRANGECZ-CRCZ-V001-P001-CONF-R0"
              + repetition
              + " reset=fresh_service"
              + " prime_green="
              + primeGreen
              + " verifier_green="
              + perturbedGreen
              + " yellow_attempt="
              + perturbedYellow
              + " input=FROZEN_NATIVE_BMINUS");
    }

    // Negative control: fresh service, no prime run.
    Harness negativeControlHarness = newHarness();

    Graph negativeControl =
        executeFrozenRun(
            negativeControlHarness,
            "CRCZ-NATIVE-BMINUS-events.json",
            "CRCZ-NATIVE-BMINUS-commands.json",
            9001L,
            9002L,
            9102L,
            "crcz-native-bminus");

    boolean negativeControlGreen =
        hasNode(
            negativeControl,
            "exploited_by_exploit",
            GraphConstants.GREEN);

    boolean negativeControlYellow =
        hasYellowAttempt(
            negativeControl,
            "exploited_by_exploit");

    System.out.println(
        "RUN_ID=CYBERRANGECZ-CRCZ-V001-P001-CONF-NC01"
            + " reset=fresh_service_no_prime"
            + " verifier_green="
            + negativeControlGreen
            + " yellow_attempt="
            + negativeControlYellow
            + " input=FROZEN_NATIVE_BMINUS");
  }

  private Harness newHarness() {

    GraphRepository graphRepository =
        mock(GraphRepository.class);

    TraineeRepository traineeRepository =
        mock(TraineeRepository.class);

    ElasticsearchServiceApi elasticsearch =
        mock(ElasticsearchServiceApi.class);

    MistakeAnalysisService mistakeAnalysisService =
        new MistakeAnalysisService(
            new PathMatchingResourcePatternResolver());

    ReflectionTestUtils.invokeMethod(
        mistakeAnalysisService,
        "init");

    CreateCommandService createCommandService =
        new CreateCommandService(
            mistakeAnalysisService);

    TraineeService traineeService =
        new TraineeService(
            traineeRepository,
            createCommandService);

    TraineeGraphService service =
        new TraineeGraphService(
            graphRepository,
            traineeService,
            elasticsearch);

    when(graphRepository.save(any(Graph.class)))
        .thenAnswer(
            invocation ->
                invocation.getArgument(0));

    return new Harness(
        service,
        elasticsearch);
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
