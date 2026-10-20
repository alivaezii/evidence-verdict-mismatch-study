'use strict';

const assert = require('node:assert/strict');
const {verifyChallenge2Repaired} =
  require('./king_challenge2_repair');

const entry23 =
  'telnet stream tcp nowait backdoor /usr/sbin/telnetd ' +
  'telnetd -E /usr/local/bin/telnet-shell.sh';

const entry2323 = entry23.replace(/^telnet /, '2323 ');

const scenarios = [
  ['BASELINE_UNSAFE', true, entry23, false],
  ['GENUINE_FIX', true, '# ' + entry23, true],
  ['RELOCATED_TELNET', true, entry2323, false],
  ['SSH_UNAVAILABLE', false, '# ' + entry23, false],
  ['SSH_ERROR', true, null, false]
];

async function main() {
  for (const [name, ssh, config, expected] of scenarios) {
    const deps = {
      isPortReachable: async (port, options) => {
        assert.equal(port, 22);
        assert.equal(options.host, 'offline-king-target');
        return ssh;
      },
      executeSSHcommand: async (host, command) => {
        assert.equal(host, 'offline-king-target');
        assert.equal(command, 'sudo cat /etc/inetd.conf');
        return config === null ?
          null : {stdout: config, stderr: ''};
      }
    };

    const verdict = await verifyChallenge2Repaired(
      'offline-king-target', deps
    );

    assert.equal(verdict, expected);

    console.log(
      `SCENARIO=${name} REPAIRED_VERDICT=${verdict} ` +
      `EXPECTED=${expected} RESULT=PASS`
    );
  }

  console.log('KING_OFFLINE_REPAIR_RESULT=PASS');
}

main().catch(err => {
  console.error(err);
  process.exitCode = 1;
});
