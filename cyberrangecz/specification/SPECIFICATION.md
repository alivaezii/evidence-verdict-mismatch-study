# CyberRangeCZ Experimental Specification

## 1. System and Verifier

**System:** CyberRangeCZ Training Feedback

**Upstream source:** CyberRangeCZ Training Feedback backend

**Source revision:** `4c81ec8477bda4a8f7d486245e0bbbe5d9c217e9` (release `v1.2.0`)

**Associated training source revision:** `2354d41d6e8703fc3665c64d9f8b09ebadd1902f`

**Associated training artifact:** Locust `training.json`

**Training artifact SHA-256:** `44474670c4bf934e8df48031d738143bbc880f959246b33a5f56af8b0f324038`

**Verifier:** The graph-based training-feedback path implemented through
`TraineeGraphService`. For the selected reference-solution step,
prerequisite satisfaction is evaluated using `achievedNodesLabels`; a
matching command is represented as GREEN when the required prerequisite
labels are satisfied.

**Positive verdict:** GREEN state for the reference-solution node
`exploited_by_exploit`.

The controlled experiment executes the production graph-analysis service
logic through a replay harness using production `MistakeAnalysisService`,
`CreateCommandService`, `TraineeService`, and `TraineeGraphService`.
External repository and Elasticsearch boundaries are isolated by the
harness. The verifier implementation itself is not replaced by synthetic
verifier logic.

## 2. Claim

**Claim:** A GREEN `exploited_by_exploit` node means that the observed exploit
action conforms to the expected reference-solution step and that its required
prerequisite states are satisfied for the current trainee run.

This claim does not assert successful exploitation, shell establishment,
target compromise, or live vulnerable state.

## 3. Independent Oracle

**Oracle:** Independently evaluate the frozen current-run command trace against
the prerequisite requirements of the frozen training definition. The oracle
does not inspect `achievedNodesLabels` or use the verifier's GREEN/YELLOW
result.

For the selected action, the required current-run trace includes:

- `set RHOST 172.18.1.5`
- `set RPORT 10000`
- `set LHOST 10.10.135.83`

**Claim holds when:** The target exploit action and all required prerequisites
are present in the current-run trace.

**Claim does not hold when:** The target exploit action is present but at least
one required prerequisite is absent from the current-run trace.

## 4. Oracle Validation

**O+ condition:** Evaluate the frozen B+ trace containing the target exploit
action and all required prerequisites.

**Expected result:** Claim holds.

**O− condition:** Evaluate the frozen B− trace containing the target exploit
action but omitting `set RPORT 10000`.

**Expected result:** Claim does not hold.

## 5. Frozen Experimental Inputs and Controlled Perturbation

The confirmatory experiment uses the following frozen native-derived replay
fixtures:

- `CRCZ-NATIVE-BPLUS-commands.json`
  SHA-256 `df67a5562e7fd49f6568128bc7336910775f6363520d49cbb33cd5d99fdc63a1`
- `CRCZ-NATIVE-BPLUS-events.json`
  SHA-256 `b6933589eaf677fb126f2c98f47ff0d76f2c0348fc87eb394fc41143e465b6b7`
- `CRCZ-NATIVE-BMINUS-commands.json`
  SHA-256 `b58a8108142443a331687683b98f01646a51144639a10691d8255df5d1c6ade7`
- `CRCZ-NATIVE-BMINUS-events.json`
  SHA-256 `14e6acd6a83421e37bab80a25356458484a9e419882705d426b10de41a9fab09`
- `CRCZ-NATIVE-definition-level.json`
  SHA-256 `04714d6af8f8f372c4eeefd26ff4a3f64b3cdc0a479b1e174023cd193cf43835`

**Perturbation:** For each confirmatory repetition, begin with a fresh
`TraineeGraphService` instance. Process the frozen B+ trace first, establishing
the prerequisite state including `rport_set`. Then process the frozen B− trace
on the same service instance. The B− current-run trace omits
`set RPORT 10000` while the other relevant frozen inputs are preserved.

The verifier implementation is not modified.

## 6. Reset Procedure

**Reset:** Construct a fresh `TraineeGraphService` instance before each
confirmatory repetition. No service state is intentionally carried between
confirmatory repetitions.

Within one repetition, B+ and B− intentionally share the same service instance
because retained mutable service state is the experimental condition.

The negative control uses a separate fresh service instance and evaluates the
same frozen B− trace without a preceding B+ prime.

## 7. Confirmatory Evaluation

Three valid confirmatory repetitions (R01-R03) will be performed using the
defined reset between repetitions.

For each repetition:

1. create a fresh service instance;
2. process B+ on that instance;
3. process B− on the same instance;
4. record the verifier result for `exploited_by_exploit`;
5. independently evaluate the B− current-run trace with the oracle.

**Negative control:** Evaluate the same frozen B− trace on a separate fresh
service instance without preceding B+ priming.

**Expected negative-control result:** The verifier does not produce the
selected positive GREEN verdict for `exploited_by_exploit`, while the oracle
reports that the claim does not hold.

The negative control is not classified as SUPPORTED or MISMATCH when the
selected positive verdict is absent.

## 8. Evidence to Preserve

For each confirmatory repetition R01, R02, and R03, preserve:

- verifier output identifying the B+ and B− claim-relevant verdicts;
- independent oracle output for the B− current-run trace;
- evidence identifying the fresh service-instance/reset boundary;
- frozen fixture identities and SHA-256 values;
- run identifier and timing information; and
- any material deviation affecting interpretation.

For negative control NC01, preserve:

- verifier output for the unprimed B− trace;
- independent oracle output for that B− trace;
- evidence identifying the fresh service-instance condition;
- frozen fixture identities and SHA-256 values;
- run identifier and timing information; and
- any material deviation affecting interpretation.

Preserve the exact backend source revision and the SHA-256 identity of the
confirmatory harness used for execution.

The validated confirmatory harness currently selected for execution is:

`TraineeGraphPrimaryConfirmatoryReplayTest.java`

SHA-256:

`92d0134064f4938581f87ed1c8334445d14af2dbc821a18a3677e3fccc146802`

Preserved evidence files will be covered by SHA-256 integrity records.

## 9. Freeze

This specification must satisfy the pre-run review requirement defined in
the experimental protocol and be frozen before the first confirmatory run.
