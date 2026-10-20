'use strict';

const Challenges = require(process.env.HOME +
  '/KING-Projekt/api/models/Challenges');

const {verifyChallenge2Repaired} = require(
  process.env.HOME +
  '/king-supplementary-20261020/offline-repair/king_challenge2_repair'
);

const reachability = require(
  process.env.HOME +
  '/KING-Projekt/node_modules/is-port-reachable'
).default;

async function main() {
  const host = '10.0.0.139';

  const originalVerdict = await Challenges.verifyChallenge2(host);

  const repairedVerdict = await verifyChallenge2Repaired(host, {
    isPortReachable: reachability,
    executeSSHcommand: (ip, command) =>
      Challenges.executeSSHcommand(ip, command)
  });

  console.log('HOST=' + host);
  console.log('ORIGINAL_VERDICT=' + originalVerdict);
  console.log('REPAIRED_VERDICT=' + repairedVerdict);

  if (originalVerdict !== false || repairedVerdict !== false) {
    throw new Error('Unexpected unsafe-baseline verdict');
  }

  console.log('HARDWARE_NEGATIVE_COMPARISON=PASS');
}

main().catch(err => {
  console.error(err);
  process.exitCode = 1;
});
