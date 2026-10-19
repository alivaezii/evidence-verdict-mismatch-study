# SecGen Supplementary Verification Study

## Scope

This supplement evaluates a process-identity-aware verifier
against the original SecGen-generated MySQL check.

The original verifier searches for the substring
`/usr/sbin/mysqld` in the output of `ps -ef | grep mysqld`.

The repaired prototype checks the executable identity
reported by `/proc/<pid>/exe`.

The original verifier and framework source files were not
modified for these supplementary experiments.

## Environment

- SecGen upstream revision: `bdc76d30958e00f7202f56490108cf60bb9fa4d6`
- Guest operating system: Debian GNU/Linux 9
- Provisioning: Vagrant / VirtualBox
- Test target: `mysql_stretch_test`
- Host Ruby: 2.7.8
- Repaired identity check executed with passwordless sudo

## Experimental results

| Condition | Original verifier | Repaired verifier |
|---|---|---|
| Genuine MySQL running | PASS | PASS |
| MySQL stopped, no decoy | FAIL | FAIL |
| MySQL stopped, sleep-based decoy | PASS (false positive) | FAIL (correct rejection) |

The framework-level adversarial test used a decoy invoked
through `/tmp/usr/sbin/mysqld-decoy`, with the actual executable
identified independently as `/bin/sleep`.

The repaired verifier rejected this decoy while the original
verifier accepted it.

## Artifact contents

- `repair/mysql_identity_check.sh`: identity-aware guest check
- `repair/mysql_stretch_identity_repair.rb`: alternate verifier
  using the original SecGen `test_local_command` API
- `evidence/negative-baseline/`: original negative control
- `evidence/positive-baseline/`: original positive control
- `evidence/framework-repair-negative/`: repaired negative control
- `evidence/framework-repair-positive/`: repaired positive control
- `evidence/framework-adversarial/`: paired adversarial comparison

Each evidence directory contains its own SHA256SUMS file.
The supplementary root SHA256SUMS inventories all files
except itself.

## Reproduction

1. Provision the compatible SecGen Debian 9 guest according
   to the main artifact documentation.
2. Copy `mysql_identity_check.sh` into the guest at
   `/tmp/secgen_mysql_identity_check.sh` and make it executable.
3. Place `mysql_stretch_identity_repair.rb` alongside the
   generated original verifier so its relative imports resolve.
4. Execute both verifiers with the original SecGen runtime:

   `RBENV_VERSION=2.7.8 bundle exec ruby <verifier-path>`

5. Evaluate the stopped, genuine-MySQL and decoy conditions.
6. Independently inspect process executable identity and
   restore the original guest state between conditions.

## Limitations

This is a scenario-specific prototype, not an upstream SecGen
security patch. The identity-aware check requires privileges
sufficient to inspect `/proc/<pid>/exe`; an unprivileged execution
did not recognize the genuine MySQL process in this environment.

The original `test_local_command` API performs substring
matching against stdout or stderr and does not enforce the
remote command exit status. The supplementary verifier reuses
that API, so its general limitations remain.

These observations support the tested conditions, not a general
claim that all process spoofing or verifier weaknesses are fixed.
