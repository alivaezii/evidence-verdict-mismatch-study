# Experimental Specification

## 1. System and Verifier

**System:** SecGen (`mysql_stretch_compatible` module)  
**Upstream source:** https://github.com/cliffe/SecGen  
**Source revision:** `bdc76d30958e00f7202f56490108cf60bb9fa4d6`  
**Verifier:** `test_local_command('mysqld process running?', 'ps -ef | grep mysqld', '/usr/sbin/mysqld')`  
**Positive verdict:** `PASSED` for the `mysqld process running?` check.

## 2. Claim

**Claim:** A real `mysqld` executable is running on the target system at verification time.


## 3. Independent Oracle

**Oracle:** Identify candidate `mysqld` processes and resolve `/proc/<PID>/exe` to determine the executable actually running for each candidate process.

**Genuine `mysqld` executable:** `/usr/sbin/mysqld`, as installed by the `mariadb-server-core-10.1` package on the target system.

**Claim holds when:** At least one running candidate process resolves to the genuine `mysqld` executable.

**Claim does not hold when:** No running candidate process resolves to the genuine `mysqld` executable.

## 4. Oracle Validation

**O+ condition:** Start the genuine MySQL/MariaDB server process on the target system.

**Expected result:** The oracle resolves the server process through `/proc/<PID>/exe` to the genuine `mysqld` executable, and the claim holds.

**O− condition:** Ensure that no genuine `mysqld` executable is running on the target system.

**Expected result:** The oracle identifies no running process whose `/proc/<PID>/exe` resolves to the genuine `mysqld` executable, and the claim does not hold.

## 5. Controlled Perturbation

**Perturbation:** Stop the genuine `mysqld` service and run a controlled decoy process whose process-list representation contains `/usr/sbin/mysqld`, while its actual executable is not `mysqld`. The oracle will independently verify the decoy's executable identity through `/proc/<PID>/exe`.

## 6. Reset Procedure

**Reset:** Terminate any decoy process created by the experiment, remove experiment-created decoy artifacts, and verify that no experiment-created process remains before establishing the required starting condition for the next run.

## 7. Confirmatory Evaluation

Three valid confirmatory repetitions (R01–R03) will be performed using the defined perturbation, with the defined reset between repetitions.

**Negative control:** With the genuine `mysqld` not running and no experiment-created decoy present, execute the same SecGen verifier and oracle.

**Expected negative-control result:** The verifier does not produce the positive `PASSED` verdict for the `mysqld process running?` check, and the oracle reports that the claim does not hold.


## 8. Evidence to Preserve

For each run, preserve:

- the complete SecGen verifier output;
- the process-list output used by the verifier;
- oracle and relevant process-state evidence captured immediately before and immediately after execution of the SecGen verifier;
- the oracle output resolving `/proc/<PID>/exe`;
- evidence of the relevant `mysqld` and decoy process state;
- run start and completion times;
- a unique run identifier;
- reset evidence for the preceding reset, where applicable;
- any deviation from the frozen specification; and
- SHA-256 hashes for preserved evidence files.

## 9. Freeze

This specification must satisfy the pre-run review requirement defined in
the experimental protocol and be frozen before the first confirmatory run.