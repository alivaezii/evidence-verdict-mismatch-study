# Experimental Protocol

## 1. Study Objective

This study investigates whether positive verdicts produced by automated
cybersecurity verifiers are supported by independent evidence about the
specific claims those verdicts communicate.

The study follows a three-phase design. First, a non-exhaustive exploratory
audit is used to identify one experimentally testable verifier–claim pair
in each selected system. Second, the claim, independent oracle, perturbation, and reset procedure are
defined, validated where applicable, and frozen before controlled evaluation. Third, the pair is evaluated under controlled experimental conditions.

The exploratory phase is intended to identify experimentally testable
verifier–claim pairs, not to enumerate all possible mismatches within a system. Exploration stops
once one candidate satisfying the study's eligibility criteria has been
identified for controlled evaluation.

The unit of analysis is the verifier–claim pair rather than the system as
a whole. SecGen, CyberRangeCZ, and KING are purposively selected as three
independently developed cybersecurity systems that expose automated positive
verdicts in different operational verification contexts and permit controlled,
claim-specific evaluation.

The study does not estimate the prevalence of evidence–verdict mismatches
across cybersecurity tools, nor does a finding for a selected verifier–claim
pair imply that the corresponding system as a whole is unreliable.


## 2. Study Design

The study follows the same experimental structure for each selected system:
```
┌─────────────────────────────────────────────────────────────────────┐
│ PHASE 1 — EXPLORATORY AUDIT                                         │
│                                                                     │
│ Selected system  →  Non-exhaustive audit  →  Eligible candidate     │
│                                             verifier–claim pair     │
└──────────────────────────────────┬──────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│ PHASE 2 — EXPERIMENTAL SPECIFICATION                                │
│                                                                     │
│ Define claim → Define oracle → Define perturbation + reset          │
│                              ↓                                      │
│                       Validate O+ / O−                              │
│                              ↓                                      │
│                            Freeze                                   │
└──────────────────────────────────┬──────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│ PHASE 3 — CONTROLLED EVALUATION                                     │
│                                                                     │
│ R01  →  Reset  →  R02  →  Reset  →  R03  +  Negative control        │
└──────────────────────────────────┬──────────────────────────────────┘
                                   │
                                   ▼
                     ┌───────────────────────────┐
                     │     CLASSIFICATION        │
                     │                           │
                     │ SUPPORTED                 │
                     │ MISMATCH                  │
                     │ INDETERMINATE             │
                     └───────────────────────────┘

```
The exploratory audit is not exhaustive. Once an eligible candidate has
been selected for controlled evaluation, the study does not require further
search for additional mismatches within that system.

Repeated runs assess whether the selected observation can be reproduced
under the specified experimental conditions. They are not treated as
independent mismatch discoveries or as a statistical sample of verifier
behavior.


## 3. Candidate Eligibility and Exploratory Stopping Rule

A candidate is eligible for controlled evaluation when:

1. the verifier produces an identifiable positive verdict;
2. the claim communicated by that verdict can be clearly reconstructed;
3. the claim can be evaluated using an independent oracle; and
4. the claim can be tested under a controlled perturbation relevant to the verifier's decision.

For each system, the exploratory audit stops when the first candidate meeting
all four criteria is identified.

The audit is non-exhaustive; no claim is made about additional verifier–claim
pairs that were not evaluated.

## 4. Outcome Classification

Positive-verdict observations are classified using the independent oracle:

- **SUPPORTED:** the verifier produces a positive verdict and the oracle
  confirms that the claim holds.
- **MISMATCH:** the verifier produces a positive verdict but the oracle
  shows that the claim does not hold.
- **INDETERMINATE:** a positive verdict is observed, but the available
  evidence is insufficient to determine whether the claim holds.

Runs without a positive verifier verdict are recorded but are not classified
as SUPPORTED or MISMATCH.

A mismatch is recorded only when an independent oracle directly refutes
the claim communicated by a positive verdict.

## 5. Evidence and Reproducibility

For each controlled evaluation:

- the system and verifier version are recorded;
- raw verifier and oracle evidence is preserved;
- each run has a unique identifier;
- resets and deviations are documented; and
- preserved evidence is protected by SHA-256 checksums.

Experimental specifications are frozen before confirmatory runs begin.

## 6. Validity Requirements

Before confirmatory evaluation:

- the oracle must be validated on one known-positive (O+) and one
  known-negative (O−) condition;
- the claim, oracle, perturbation, and reset procedure must be frozen; and
- the experimental specification must receive a pre-run review.

Confirmatory evaluation uses three valid repetitions with the defined reset
between runs and includes a predefined negative control.

Any deviation from the frozen procedure is documented before the affected
run is interpreted.


