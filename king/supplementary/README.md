# KING Challenge 2 — Supplementary Causal and Repair Evidence

## Scope

This supplementary package investigates an evidence–verdict mismatch
in KING Challenge 2 on a Raspberry Pi running Debian Linux.

The original verifier checks whether SSH (TCP/22) is reachable and
Telnet (TCP/23) is unreachable. Its success verdict does not establish
that the challenge-specific insecure Telnet shell is no longer usable
on another port.

The experiments use the unchanged KING source implementation and a
separate configuration-aware repair prototype.

## Source and test environment

- Original KING source revision:
  `83b6d8f8a98e2c12462db62adaf88e95eb616114`
- Target: Raspberry Pi, Debian Linux, ARM64.
- Challenge service:
  `/usr/local/bin/telnet-shell.sh`
- Challenge shell SHA-256:
  `b4e5ee9fabd39839f97541e4dc4c7c34478589b765f0777d0b46da33f8d399a5`
- Controlled endpoints: TCP/22, TCP/23 and TCP/2323.
- Artifact collection date: 2026-10-20.

## Causal mechanism

The original `Challenges.verifyChallenge2` evaluates:

    SSH(22) reachable AND Telnet(23) unreachable

A port relocation from 23 to 2323 preserves access to the
challenge-specific Telnet shell while making the original verifier
report success.

The observed mismatch concerns the challenge-specific remediation
claim, not a general claim that all forms of remote access are absent.

## Experiment matrix

| Condition | Original verdict | Repair verdict | Independent observation |
| --- | --- | --- | --- |
| Unsafe baseline: Telnet on 23 | false | false | Challenge shell accessible on 23 |
| Positive control: challenge entry disabled | true | true | No listeners on 23 or 2323 |
| Adversarial intervention: Telnet on 2323 | true | false | Challenge shell accessible on 2323 |

`true` indicates a reported successful fix.

The adversarial condition is a false positive for the original
verifier. The repair rejects this particular port-relocation mismatch
while retaining the two control outcomes.

## Hardware evidence

- `hardware-baseline/`: initial SSH inspection, active inetd entry,
  listeners and challenge shell identity.
- `hardware-oracle-baseline/`: interactive challenge-specific Telnet
  shell and command-execution observation on port 23.
- `hardware-negative-comparison/`: original and repaired verifier
  outcomes on the unsafe baseline.
- `hardware-positive-comparison/`: both verifier outcomes under
  temporary service disablement, endpoint reachability observations,
  and subsequent restoration.
- `hardware-adversarial/`: relocation to TCP/2323, paired verifier
  outcomes, independent behavioral oracle and restoration.

The behavioral oracle verifies challenge-specific shell accessibility
and a benign command execution independently of the verifier verdict.

The original baseline configuration was restored after each hardware
intervention. Restored configurations were compared with
`/etc/inetd.conf.king-baseline`; the final comparison exited with 0.

## Offline tests

- `offline-causal/`: controlled replay calling the original KING
  verifier implementation with mocked reachability measurements.
- `offline-repair/`: fixture-based tests of the separate repair
  prototype, including unsafe, genuine-fix, relocated-service,
  unavailable-SSH and SSH-error cases.

Offline fixtures are not independent hardware observations.

## Repair prototype

`offline-repair/king_challenge2_repair.js` implements a
configuration-aware verifier that checks SSH availability and reads
`/etc/inetd.conf` through KING's existing SSH-command pathway.

It rejects success if an active configuration entry refers to the
known challenge-specific Telnet daemon and shell script.

The prototype is not a general Telnet detection mechanism. It may miss
alternative configuration sources, service managers, aliases, or
runtime states inconsistent with configuration text. It also depends
on access to the remote configuration.

A production-grade repair would require additional runtime and
behavioral validation, authenticated result handling, and a defined
threat model.

## Interpretation and limitations

- The direct original function and standalone repair were compared
  under matching hardware configurations.
- These supplementary repair tests do not demonstrate end-to-end
  HTTP, session, database, or deployment integration.
- The hardware positive control establishes closure of the
  experiment's selected endpoints, not universal absence of Telnet.
- The hardware adversarial test demonstrates one controlled
  port-relocation mechanism; it does not establish prevalence across
  other hosts or configurations.
- The experiments do not modify the KING source repository.
- Earlier confirmatory observations are preserved separately under
  `king/evidence/`.

## Integrity

Each experimental directory contains a local `SHA256SUMS` manifest.
The supplementary root `SHA256SUMS` covers all files under this
directory except itself.

Verify from this directory with:

    sha256sum -c SHA256SUMS

No hardware IP addresses or SSH private keys are required to inspect
the archived results.
