<img width="2171" height="724" alt="banner" src="https://github.com/user-attachments/assets/49f00da0-d77e-4312-87d0-c00b2f58051d" />


<h1 align="center">Evidence–Verdict Mismatches</h1>

<p align="center"><strong>An Empirical Study of Automated Cybersecurity Verification</strong></p>

<p align="center">
  <img src="https://img.shields.io/badge/SYSTEMS-3-16324F?style=for-the-badge" alt="3 Systems">
  <img src="https://img.shields.io/badge/CONFIRMATORY_RUNS-9-C66A22?style=for-the-badge" alt="9 Confirmatory Runs">
  <img src="https://img.shields.io/badge/ARTIFACT-R7--v1-2E6F57?style=for-the-badge" alt="Artifact v1">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/EVIDENCE-SHA--256-16324F?style=flat-square" alt="SHA-256 Evidence">
  <img src="https://img.shields.io/badge/OUTCOME-MISMATCH-C66A22?style=flat-square" alt="Mismatch">
  <img src="https://img.shields.io/badge/STATUS-EXPERIMENTS_COMPLETE-2E6F57?style=flat-square" alt="Experiments Complete">
</p>

---

## Overview

This repository contains the experimental artifact for an empirical study of **evidence–verdict mismatches in automated cybersecurity verification**.

The study examines whether positive verdicts produced by automated cybersecurity verifiers are supported by independent evidence about the specific claims communicated by those verdicts.

| System | Verification Context | Confirmatory Runs |
|:---|:---|:---:|
| **SecGen** | Post-provisioning validation | 3 |
| **CyberRangeCZ** | Automated training feedback | 3 |
| **KING** | Security remediation verification | 3 |

The unit of analysis is the **verifier–claim pair**.

---

## Experimental Design

Each selected verifier–claim pair follows a controlled evaluation structure:

```text
Verifier–Claim Pair
        │
        ▼
Independent Oracle
      ┌─┴─┐
      O+  O−
        │
        ▼
Controlled Evaluation
 R01 → R02 → R03
        │
        ▼
 Negative Control
        │
        ▼
SUPPORTED / MISMATCH / INDETERMINATE
```

Before confirmatory evaluation, the semantic claim, independent oracle, controlled perturbation, reset procedure, and evidence requirements are defined in a system-specific experimental specification.

---

## Results

| System | R01 | R02 | R03 | Negative Control |
|:---|:---:|:---:|:---:|:---:|
| **SecGen** | MISMATCH | MISMATCH | MISMATCH | ✓ |
| **CyberRangeCZ** | MISMATCH | MISMATCH | MISMATCH | ✓ |
| **KING** | MISMATCH | MISMATCH | MISMATCH | ✓ |

**9 / 9 confirmatory repetitions reproduced an evidence–verdict mismatch under their specified experimental conditions.**

A `MISMATCH` is recorded only when the verifier produces the selected positive verdict and an independent claim-specific oracle shows that the corresponding claim does not hold.

Complete run metadata is available in [`RUNS.csv`](RUNS.csv).

---


## Supplementary Mechanism and Repair Evaluations

The R7 supplementary experiments extend the original confirmatory
study with targeted mechanism analysis, controlled interventions,
and scenario-specific repair evaluations.

These experiments are separate from the nine original confirmatory
repetitions and are **not included in the 9/9 count**.

| System | Investigated mechanism | Supplementary evaluation |
|:---|:---|:---|
| **SecGen** | Substring-based process identification accepts a decoy rather than establishing executable identity | Original-versus-repaired framework checks, process-identity oracle, positive and negative controls |
| **CyberRangeCZ** | Mutable trainee-graph state persists across sequential evaluations within a reused service instance | State inspection, state-clearing causal intervention, Spring scope and Spring-managed causal tests |
| **KING** | Fixed-port reachability is used as a proxy for removal of the challenge-specific Telnet shell | Raspberry Pi hardware comparison, port-relocation intervention, independent behavioral oracle and configuration-aware repair |

### Observed repair outcomes

| System | Unsafe or adversarial condition | Original verifier | Repair or intervention |
|:---|:---|:---|:---|
| **SecGen** | MySQL stopped; sleep-based process decoy | False positive | Decoy rejected |
| **CyberRangeCZ** | Negative graph evaluated after positive graph using retained service state | False positive (`GREEN`) | Clearing retained state restores `GREEN=false` |
| **KING** | Challenge Telnet shell moved from TCP/23 to TCP/2323 | False positive | Configuration-aware prototype rejects the reported fix |

The evaluated repairs and interventions address the **specific mechanisms
tested**, not every possible verifier weakness. CyberRangeCZ experiments
are service-level tests with mocked dependencies; SecGen evaluates a
privileged process-identity prototype; KING includes paired comparisons
on physical hardware but not a complete HTTP/database integration test.

→ [SecGen supplementary evidence](secgen/supplementary/)

→ [CyberRangeCZ supplementary evidence](cyberrangecz/supplementary/)

→ [KING supplementary evidence](king/supplementary/)

---


---

## Systems

### SecGen

**Context:** Post-provisioning security validation
**Upstream:** https://github.com/cliffe/SecGen
**Source revision:** `bdc76d30958e00f7202f56490108cf60bb9fa4d6`

→ [`Experimental specification`](secgen/specification/SPECIFICATION.md)

→ [`Evidence`](secgen/evidence/)

→ [`Supplementary evaluation`](secgen/supplementary/)

### CyberRangeCZ

**Context:** Automated cybersecurity training feedback
**Upstream:** https://github.com/cyberrangecz
**Source revision:** `4c81ec8477bda4a8f7d486245e0bbbe5d9c217e9`

→ [`Experimental specification`](cyberrangecz/specification/SPECIFICATION.md)

→ [`Evidence`](cyberrangecz/evidence/)

→ [`Reproducible replay`](cyberrangecz/replay/)

→ [`Supplementary evaluation`](cyberrangecz/supplementary/)

### KING

**Context:** Automated security-remediation verification
**Upstream:** https://github.com/lksptz/KING-Projekt
**Source revision:** `83b6d8f8a98e2c12462db62adaf88e95eb616114`

→ [`Experimental specification`](king/specification/SPECIFICATION.md)

→ [`Evidence`](king/evidence/)

→ [`Supplementary evaluation`](king/supplementary/)

---

## Repository Structure

```text
evidence-verdict-mismatch-study/
├── README.md
├── PROTOCOL.md
├── PRE_RUN_REVIEW.md
├── RUNS.csv
├── MANIFEST.sha256
├── secgen/
│   ├── specification/
│   ├── evidence/
│   └── supplementary/
├── cyberrangecz/
│   ├── specification/
│   ├── evidence/
│   ├── replay/
│   └── supplementary/
├── king/
│   ├── specification/
│   ├── evidence/
│   └── supplementary/
└── templates/
```

| Path | Purpose |
|:---|:---|
| [`PROTOCOL.md`](PROTOCOL.md) | Experimental protocol and outcome classification |
| [`RUNS.csv`](RUNS.csv) | Original confirmatory run index |
| [`MANIFEST.sha256`](MANIFEST.sha256) | Preserved evidence integrity records |
| `*/specification/` | System-specific experimental specifications |
| `*/evidence/` | Original confirmatory evidence |
| [`cyberrangecz/replay/`](cyberrangecz/replay/) | Reproducible service-level replay |
| `*/supplementary/` | Mechanism, intervention and repair evidence, with dedicated README and checksums |

---

## Reproducibility and Evidence Integrity

Verify the original preserved evidence:

```bash
sha256sum -c MANIFEST.sha256
```

Verify the supplementary evidence independently:

```bash
for system in secgen cyberrangecz king; do
  (
    cd "$system/supplementary" || exit 1
    sha256sum -c SHA256SUMS
  ) || exit 1
done
```

The original manifest and the system-specific supplementary manifests
have **different scopes**. The original confirmatory dataset remains
preserved; supplementary experiments are documented separately.

Experimental implementation revisions, selected conditions, oracle
observations, controls, and limitations are documented in the
corresponding system directories.

---

## Artifact Versions

### R7 supplementary artifact

**Tag:** [`experimental-artifact-r7-v1`](https://github.com/alivaezii/evidence-verdict-mismatch-study/tree/experimental-artifact-r7-v1)

**Commit:** `e3c90528bbccccf488f8383ce68bb7f90f8d891e`
**Scope:** Original evidence plus three-system supplementary causal,
mechanism and repair evaluations.

```bash
git fetch --tags
git checkout experimental-artifact-r7-v1
sha256sum -c MANIFEST.sha256
```

Run the supplementary integrity checks above from this checkout.

### Original experimental artifact

**Tag:** [`experimental-artifact-v1`](https://github.com/alivaezii/evidence-verdict-mismatch-study/tree/experimental-artifact-v1)

**Commit:** `3f7b5f749599e8bba819b96e677073f490d31e53`

This original release remains separately available and unchanged.
The newer R7 artifact is an extension, not a replacement of its
frozen confirmatory evidence.

## Scope of Interpretation

The study uses purposively selected verifier–claim pairs and controlled
experimental conditions. The 9/9 result describes repeatability within
those conditions, **not the prevalence of mismatches across cybersecurity
tools or deployments**.

Supplementary interventions strengthen the causal interpretation of
the selected mechanisms. They do not establish production-wide repair
effectiveness, complete platform-level verification, or general immunity
to adversarial adaptations.

---

<p align="center"><strong>Evidence–Verdict Mismatches in Automated Cybersecurity Verification</strong></p>
<p align="center">SecGen · CyberRangeCZ · KING</p>
