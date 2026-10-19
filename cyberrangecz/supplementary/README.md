# CyberRangeCZ Supplementary Causal Tests

## Scope

This supplement provides service-level state inspection,
controlled interventions, and Spring lifecycle tests for the
CyberRangeCZ trainee-graph evidence-verdict mismatch.

It extends the original confirmatory replay documented in
`../replay/` without modifying the frozen evidence.

## Tested implementation

- CyberRangeCZ revision:
  `4c81ec8477bda4a8f7d486245e0bbbe5d9c217e9`
- Original TraineeGraphService implementation
- JUnit / Maven test execution
- Spring-managed service instances in targeted tests
- Mocked external dependencies

## Tests and observations

### State Audit

`TraineeGraphStateAuditTest.java`

Directly inspects `achievedNodesLabels`.

Observed:
- Before B+: 0 retained labels
- After B+: 7 retained labels
- After B- in reused service: 14 accumulated labels

This demonstrates state retention across sequential
evaluations within the tested service instance.

### Causal Intervention

`TraineeGraphCausalInterventionTest.java`

Three controlled conditions:

- Fresh B-: GREEN=false
- B+ followed by B- without clearing state: GREEN=true
- B+ followed by clearing retained labels, then B-:
  GREEN=false

The intervention supports a causal explanation involving
retained service state.

### Spring Scope

`TraineeGraphSpringScopeTest.java`

Shows that the service behaves as a singleton within the
tested Spring context and retains a marker across repeated
retrievals. A separate Spring context does not inherit it.

### Spring-managed Causal Replay

`TraineeGraphSpringCausalTest.java`

Repeats the fresh/reused/cleared comparison using a
Spring-created service and mocked dependencies.

Observed GREEN decisions:
false / true / false.

## Reproduction

Place the four Java test sources in the test source tree of
the specified CyberRangeCZ revision.

Run individually using Maven with the environment and test
configuration described in `../replay/README.md`.

Example:

`mvn -DskipChecks=true -Dtest=TraineeGraphSpringCausalTest test`

Review the corresponding log in `evidence/` and compare
its recorded test outcomes.

## Limitations

These are targeted service-level experiments, not a complete
deployed cyber-range test with real external infrastructure.

The use of mocks isolates the service logic and supports the
tested causal mechanism, but does not establish its frequency
in production deployments or across different service
configurations.

The tested examples are purposively selected, not a random
sample from all possible challenges or system states.
