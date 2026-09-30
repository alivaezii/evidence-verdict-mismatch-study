<p align="center">
  ![Uploading banner.png…]()
</p>

<h1 align="center">Evidence–Verdict Mismatches</h1>

<p align="center"><strong>An Empirical Study of Automated Cybersecurity Verification</strong></p>

<p align="center">
  <img src="https://img.shields.io/badge/SYSTEMS-3-16324F?style=for-the-badge" alt="3 Systems">
  <img src="https://img.shields.io/badge/CONFIRMATORY_RUNS-9-C66A22?style=for-the-badge" alt="9 Confirmatory Runs">
  <img src="https://img.shields.io/badge/ARTIFACT-v1-2E6F57?style=for-the-badge" alt="Artifact v1">
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

## Systems

### SecGen

**Context:** Post-provisioning security validation  
**Upstream:** https://github.com/cliffe/SecGen  
**Source revision:** `bdc76d30958e00f7202f56490108cf60bb9fa4d6`

→ [`Experimental specification`](secgen/specification/SPECIFICATION.md)  
→ [`Evidence`](secgen/evidence/)

### CyberRangeCZ

**Context:** Automated cybersecurity training feedback  
**Upstream:** https://github.com/cyberrangecz  
**Source revision:** `4c81ec8477bda4a8f7d486245e0bbbe5d9c217e9`

→ [`Experimental specification`](cyberrangecz/specification/SPECIFICATION.md)  
→ [`Evidence`](cyberrangecz/evidence/)

### KING

**Context:** Automated security-remediation verification  
**Upstream:** https://github.com/lksptz/KING-Projekt  
**Source revision:** `83b6d8f8a98e2c12462db62adaf88e95eb616114`

→ [`Experimental specification`](king/specification/SPECIFICATION.md)  
→ [`Evidence`](king/evidence/)

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
│   └── evidence/
├── cyberrangecz/
│   ├── specification/
│   └── evidence/
├── king/
│   ├── specification/
│   └── evidence/
└── templates/
```

| File | Purpose |
|:---|:---|
| [`PROTOCOL.md`](PROTOCOL.md) | Experimental protocol and outcome classification |
| [`RUNS.csv`](RUNS.csv) | Central index of experimental runs |
| [`MANIFEST.sha256`](MANIFEST.sha256) | SHA-256 integrity records for preserved evidence |
| `*/specification/` | Frozen system-specific experimental specifications |
| `*/evidence/` | Preserved experimental evidence |

---

## Evidence Integrity

```bash
sha256sum -c MANIFEST.sha256
```

A successful verification reports `OK` for every file listed in the manifest.

---

## Artifact Snapshot

**Tag:** `experimental-artifact-v1`  
**Commit:** `3f7b5f749599e8bba819b96e677073f490d31e53`

```bash
git checkout experimental-artifact-v1
sha256sum -c MANIFEST.sha256
```

---

<p align="center"><strong>Evidence–Verdict Mismatches in Automated Cybersecurity Verification</strong></p>
<p align="center">SecGen · CyberRangeCZ · KING</p>
