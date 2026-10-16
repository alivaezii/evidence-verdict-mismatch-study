
## Purpose

This supplementary artifact contains the frozen Java confirmatory replay harness and five frozen JSON input fixtures for the CyberRangeCZ case in the accompanying empirical study.

The replay executes the upstream `TraineeGraphService` implementation while mocking repository and Elasticsearch interfaces. It is **not** an end-to-end experiment on a deployed CyberRangeCZ platform.

## Pinned upstream implementation

- Upstream repository: https://github.com/cyberrangecz/backend-training-feedback
- Upstream commit: `4c81ec8477bda4a8f7d486245e0bbbe5d9c217e9`
- Project version: `1.2.0`
- Java: `17`
- Build tool: Apache Maven (locally verified with `3.8.7`)
- Upstream license: MIT. Copyright and permission notices remain with the upstream source.

## Files supplied

- `TraineeGraphPrimaryConfirmatoryReplayTest.java` — frozen confirmatory harness
- `fixtures/CRCZ-NATIVE-BPLUS-events.json`
- `fixtures/CRCZ-NATIVE-BPLUS-commands.json`
- `fixtures/CRCZ-NATIVE-BMINUS-events.json`
- `fixtures/CRCZ-NATIVE-BMINUS-commands.json`
- `fixtures/CRCZ-NATIVE-definition-level.json`
- `SHA256SUMS` — hashes for the harness and the five fixtures

## Reproduction instructions

These commands assume this artifact repository and the upstream backend are checked out as sibling directories. Run them from a terminal with Git, Java 17, and Maven installed.

1. Clone the pinned upstream backend:

   ```bash
   git clone https://github.com/cyberrangecz/backend-training-feedback.git
   cd backend-training-feedback
   git checkout --detach 4c81ec8477bda4a8f7d486245e0bbbe5d9c217e9
   ```

2. Copy the harness and fixtures from the **artifact repository** (replace `/path/to/evidence-verdict-mismatch-study` with the actual checkout path):

   ```bash
   ARTIFACT=/path/to/evidence-verdict-mismatch-study/cyberrangecz/replay
   TEST_DIR=src/test/java/cz/cyberrange/platform/training/feedback/service/graph
   FIXTURE_DIR=src/test/resources/crcz-exact-replay
   mkdir -p "$TEST_DIR" "$FIXTURE_DIR"
   cp "$ARTIFACT/TraineeGraphPrimaryConfirmatoryReplayTest.java" "$TEST_DIR/"
   cp "$ARTIFACT"/fixtures/*.json "$FIXTURE_DIR/"
   ```

3. Optionally verify the archived artifact files **in the artifact directory**:

   ```bash
   (cd "$ARTIFACT" && sha256sum -c SHA256SUMS)
   ```

4. Run the frozen replay test:

   ```bash
   mvn -DskipChecks=true -Dtest=TraineeGraphPrimaryConfirmatoryReplayTest test
   ```

   The upstream `pom.xml` binds Spotless `apply` to the Maven `validate` phase; `-DskipChecks=true` skips that formatting action and avoids modifying tracked upstream Java source files during replay.

## Expected verifier observations

| Run | B+ prime GREEN | Perturbed B− GREEN | Yellow attempt |
| --- | --- | --- | --- |
| R01 | `true` | `true` | `false` |
| R02 | `true` | `true` | `false` |
| R03 | `true` | `true` | `false` |
| NC01 | not applied | `false` | `true` |

Each of R01–R03 constructs a fresh service instance, evaluates a known-good B+ trace, then evaluates the perturbed B− trace on the **same** service instance. B− omits `set RPORT 10000`. NC01 evaluates B− on a fresh instance without prior priming.

The archived independent oracle returns `FALSE` for the current-run prerequisite claim on B−. The Maven test replays **verifier behavior only**; it does not independently execute the oracle. Accordingly, the replay outputs should be interpreted together with the separately archived oracle evidence.

R01–R03 are repeated observations of **one** selected verifier–claim pair, not three independent cases.

## Scope and limitations

- The harness invokes the production service logic with mocked repository and Elasticsearch boundaries; it does not demonstrate effects on a deployed system or cross-trainee leakage.
- The JUnit method prints the verifier observations. `BUILD SUCCESS` alone is not proof that each expected verdict was asserted: inspect the `RUN_ID=` lines.
- A clean local checkout of the pinned upstream commit, populated with the frozen harness and fixtures, produced the expected four observation lines and `BUILD SUCCESS` on 2026-10-16 with OpenJDK 17.0.20 and Maven 3.8.7. This does not constitute independent third-party reproduction.
- The supplementary publication of this replay package does not alter the original confirmatory evidence or the `experimental-artifact-v1` tag.

## Integrity and provenance

Frozen confirmatory harness SHA-256:

`92d0134064f4938581f87ed1c8334445d14af2dbc821a18a3677e3fccc146802`

The accompanying `SHA256SUMS` file records the hashes of all six replay inputs. Run `sha256sum -c SHA256SUMS` from the replay artifact directory to verify them.

## Attribution

The original CyberRangeCZ backend is developed by its respective copyright holders, including Masaryk University and CyberSecurity Hub, z.s., and is distributed under the MIT license. The upstream copyright and permission notices must be preserved when redistributing upstream code.
 EOF 
