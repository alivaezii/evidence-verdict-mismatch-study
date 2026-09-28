# Evidence–Verdict Mismatches in Automated Cybersecurity Verification

[![Systems](https://img.shields.io/badge/systems-3-blue)](#systems-under-study)
[![Artifact](https://img.shields.io/badge/artifact-reproducibility-informational)](#repository-structure)

Experimental artifact repository for an empirical study of whether positive
verdicts produced by automated cybersecurity verifiers are supported by
independent evidence about the specific claims those verdicts communicate.

## Study Design

The study follows three phases for each selected system:

1. **Exploratory audit** : identify one eligible verifier–claim pair through
   a non-exhaustive audit.
2. **Experimental specification** : define and validate the claim-specific
   oracle, perturbation, and reset procedure, then freeze the specification.
3. **Controlled evaluation** : perform three confirmatory repetitions and a
   predefined negative control.

The unit of analysis is the **verifier–claim pair**.

The study does not estimate the prevalence of evidence–verdict mismatches
across cybersecurity tools.

## Systems Under Study

### SecGen

Upstream source:
https://github.com/cliffe/SecGen

### CyberRangeCZ

Upstream project:
https://github.com/cyberrangecz

The specific source repository and revision used for the selected verifier
will be recorded during the experimental specification phase.

### KING

Upstream source:
https://github.com/lksptz/KING-Projekt

The specific source revision used in the study will be recorded during the
experimental specification phase.


## Repository Structure

```text

evidence-verdict-mismatch-study/
│
├── README.md
├── PROTOCOL.md
├── PRE_RUN_REVIEW.md
├── RUNS.csv
├── MANIFEST.sha256
│
├── secgen/
│   ├── specification/
│   └── evidence/
│
├── cyberrangecz/
│   ├── specification/
│   └── evidence/
│
└── king/
    ├── specification/
    └── evidence/

```
### Artifact Roles

- `PROTOCOL.md` : frozen pre-exploration experimental protocol.
- `PRE_RUN_REVIEW.md` : pre-run review record for confirmatory evaluation.
- `RUNS.csv` : central index of controlled experimental runs.
- `MANIFEST.sha256` : SHA-256 integrity manifest for preserved evidence.
- `*/specification/` : frozen system-specific experimental specifications.
- `*/evidence/` :  preserved evidence from controlled evaluations.

## Experimental Outcomes

Positive-verdict observations are classified as:

- **SUPPORTED**
- **MISMATCH**
- **INDETERMINATE**

A mismatch is recorded only when an independent claim-specific oracle
directly refutes the claim communicated by a positive verifier verdict.
