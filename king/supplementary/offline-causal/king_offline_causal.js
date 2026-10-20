'use strict';

const assert = require('node:assert/strict');
const Module = require('node:module');
const path = require('node:path');

const project = path.join(process.env.HOME, 'KING-Projekt');
const challengesPath = path.join(project, 'api/models/Challenges.js');

const scenarios = [
  {
    name: 'BASELINE_UNSAFE',
    ports: {22: true, 23: true, 2323: false},
    expectedVerdict: false,
    claimHolds: false
  },
  {
    name: 'GENUINE_FIX',
    ports: {22: true, 23: false, 2323: false},
    expectedVerdict: true,
    claimHolds: true
  },
  {
    name: 'RELOCATED_TELNET',
    ports: {22: true, 23: false, 2323: true},
    expectedVerdict: true,
    claimHolds: false
  },
  {
    name: 'SSH_UNAVAILABLE',
    ports: {22: false, 23: false, 2323: false},
    expectedVerdict: false,
    claimHolds: false
  }
];

async function run() {
  const originalLoad = Module._load;
  let activeScenario = null;
  let calls = [];

  try {
    Module._load = function(request, parent, isMain) {
      if (request === 'is-port-reachable') {
        return {
          default: async (port, options) => {
            assert.equal(options.host, 'offline-king-target');
            assert.ok(Object.hasOwn(activeScenario.ports, port));
            calls.push(port);
            return activeScenario.ports[port];
          }
        };
      }
      return originalLoad.apply(this, arguments);
    };

    const Challenges = require(challengesPath);
    Module._load = originalLoad;

    for (const scenario of scenarios) {
      activeScenario = scenario;
      calls = [];

      const verdict = await Challenges.verifyChallenge2(
        'offline-king-target'
      );

      assert.equal(verdict, scenario.expectedVerdict);
      assert.deepEqual(calls, [23, 22]);

      const mismatch = verdict && !scenario.claimHolds;

      console.log(
        `SCENARIO=${scenario.name} ` +
        `SSH22=${scenario.ports[22]} ` +
        `TELNET23=${scenario.ports[23]} ` +
        `TELNET2323=${scenario.ports[2323]} ` +
        `VERDICT=${verdict} ` +
        `CLAIM_HOLDS=${scenario.claimHolds} ` +
        `MISMATCH=${mismatch} ` +
        `PROBES=${calls.join(',')}`
      );
    }

    console.log('KING_OFFLINE_CAUSAL_RESULT=PASS');
  } finally {
    Module._load = originalLoad;
  }
}

run().catch(error => {
  console.error(error);
  process.exitCode = 1;
});
