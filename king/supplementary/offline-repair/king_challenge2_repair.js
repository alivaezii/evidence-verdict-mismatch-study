'use strict';

const CHALLENGE_SHELL = '/usr/local/bin/telnet-shell.sh';

function activeChallengeEntries(config) {
  return config.split(/\r?\n/).filter(line => {
    const trimmed = line.trim();
    if (!trimmed || trimmed.startsWith('#')) return false;

    const fields = trimmed.split(/\s+/);
    return fields.length >= 7 &&
      fields.includes('/usr/sbin/telnetd') &&
      fields.includes(CHALLENGE_SHELL);
  });
}

async function verifyChallenge2Repaired(deviceIP, deps) {
  try {
    const sshReachable = await deps.isPortReachable(
      22, {host: deviceIP}
    );

    if (!sshReachable) return false;

    const result = await deps.executeSSHcommand(
      deviceIP, 'sudo cat /etc/inetd.conf'
    );

    if (result == null ||
        typeof result.stdout !== 'string' ||
        (result.stderr || '').trim() !== '') {
      return false;
    }

    return activeChallengeEntries(result.stdout).length === 0;
  } catch {
    return false;
  }
}

module.exports = {
  verifyChallenge2Repaired,
  activeChallengeEntries
};
