#!/bin/sh
# Supplementary identity-aware SecGen check.
# Success requires a running process whose executable is
# the genuine mysqld binary at the specified path.

for proc in /proc/[0-9]*; do
    [ -d "$proc" ] || continue

    executable=$(readlink "$proc/exe" 2>/dev/null) || continue

    if [ "$executable" = "/usr/sbin/mysqld" ]; then
        echo "SECGEN_MYSQL_IDENTITY_OK"
        exit 0
    fi
done

echo "SECGEN_MYSQL_IDENTITY_NOT_FOUND"
exit 1
