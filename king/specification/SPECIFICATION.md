# KING Experimental Specification

## 1. System and Verifier

**System:** KING (Challenge 2)

**Upstream source:** https://github.com/lksptz/KING-Projekt

**Source revision:** `83b6d8f8a98e2c12462db62adaf88e95eb616114`

**Verifier:** `verifyChallenge2(deviceIP)` in `api/models/Challenges.js`. The verifier tests TCP reachability of ports 23 and 22 and returns a positive result iff SSH on port 22 is reachable and Telnet on port 23 is not reachable:

`sshReachable && !telnetReachable`

**Positive verdict:** The KING interface reports the literal message `Fix erfolgreich durchführt!` when challenge verification returns a positive result.

## 2. Claim

**Claim:** The insecure Telnet-based remote shell introduced by KING Challenge 2 has been successfully fixed and is no longer remotely usable.

The claim is limited to the Challenge 2 Telnet-based shell. It does not assert that all remote-command access has been removed; SSH on port 22 is expected to remain available.

## 3. Independent Oracle

**Oracle:** A behavioral Telnet test from the independent WSL host determines whether the original Challenge 2 Telnet shell remains remotely usable. The oracle is independent of the native KING verifier and does not use the verifier result to determine `claim_holds`.

For a configured Challenge 2 Telnet endpoint, the behavioral test establishes a Telnet session and checks for:

1. the challenge banner `Willkommen in der Telnet Shell!`;
2. the challenge flag `FLAG{t3ln3t_1s_b4d}`; and
3. successful execution of the predefined harmless command
   `printf 'KING_ORACLE_OK\n'`, producing `KING_ORACLE_OK`.

For the controlled perturbation, this behavioral oracle is applied to TCP port 2323, the pre-specified alternate endpoint to which the unchanged Challenge 2 Telnet service is moved.

**Claim holds when:** Under the predefined experimental configuration, the Challenge 2 Telnet shell is not remotely usable on either experimental Telnet endpoint (TCP ports 23 and 2323), while SSH on TCP port 22 remains reachable as expected.

**Claim does not hold when:** The behavioral oracle establishes that the original Challenge 2 Telnet shell remains remotely usable by observing the challenge identity and successful execution of the predefined marker command.

## 4. Oracle Validation

**O+ condition:** Genuinely remove the active Challenge 2 Telnet service exposure while retaining SSH connectivity. Apply the oracle checks to both predefined experimental Telnet endpoints, TCP ports 23 and 2323, and confirm that the Challenge 2 Telnet shell is not remotely usable on either endpoint.

**Expected result:** `claim_holds = TRUE`.

**O− condition:** Restore the Challenge 2 baseline configuration with the original Telnet shell exposed on TCP port 23 and apply the behavioral oracle to TCP port 23.

**Expected result:** The challenge banner, challenge flag, and `KING_ORACLE_OK` command result are observed; `claim_holds = FALSE`.

## 5. Controlled Perturbation

**Perturbation:** Change only the service selector for the Challenge 2 Telnet service from TCP port 23 to TCP port 2323, preserving the same Telnet daemon, account, `/usr/local/bin/telnet-shell.sh`, challenge banner, challenge flag, and interactive shell behavior. Restart `inetutils-inetd`.

**Expected perturbation state:** SSH on TCP port 22 remains reachable; no listener is present on TCP port 23; TCP port 2323 exposes the unchanged Challenge 2 Telnet service. The relevant `inetd` configuration entry and listener state for TCP ports 22, 23, and 2323 will be captured before invoking the verifier.

## 6. Reset Procedure

**Reset:** Restore `/etc/inetd.conf` from `/etc/inetd.conf.king-baseline`, restart `inetutils-inetd`, and verify that SSH on TCP port 22 and the original Challenge 2 Telnet service on TCP port 23 are reachable and that no Challenge 2 Telnet service is exposed on TCP port 2323.

## 7. Confirmatory Evaluation

Three valid confirmatory repetitions will be performed using the defined reset between repetitions.

For each repetition:

1. establish and verify the reset state;
2. apply the controlled perturbation;
3. verify and record the perturbation state;
4. invoke the native KING Challenge 2 verifier and record its output;
5. apply the predefined behavioral oracle to TCP port 2323;
6. classify the verifier–claim pair using the native verdict and `claim_holds`;
7. restore and verify the reset state.

A predefined negative control will also be executed. For the negative control, the Challenge 2 Telnet service will be genuinely removed rather than moved to TCP port 2323, while SSH remains reachable. The native verifier will then be invoked and the independent oracle will verify that the Challenge 2 Telnet shell is not remotely usable.

## 8. Evidence to Preserve

For each confirmatory repetition R01, R02, and R03, preserve exactly:

- `reset-before.txt`
- `perturbation-state.txt`
- `verifier.txt`
- `oracle-after-verifier.txt`
- `reset-after.txt`

For negative control NC01, preserve exactly:

- `control-state.txt`
- `verifier.txt`
- `oracle-after-verifier.txt`
- `reset-after.txt`

`perturbation-state.txt` will include the relevant `inetd` service entry and listener state for TCP ports 22, 23, and 2323.

`oracle-after-verifier.txt` will preserve the behavioral Telnet observation, including the challenge banner, challenge flag, and result of the predefined `KING_ORACLE_OK` command when the shell is reachable.

Each evidence record will include the run identifier and relevant timing information. Any material deviation affecting interpretation of an official run will be recorded.

## 9. Freeze

This specification must satisfy the pre-run review requirement defined in the experimental protocol and be frozen before the first confirmatory run.
