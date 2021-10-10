#!/bin/bash

if [[ $1 = 'cassandra' ]]; then
  # Create default keyspace for single node cluster
  until cat /tmp/init.cql | cqlsh; do
    echo "cqlsh: Cassandra is unavailable - retry later"
    sleep 2
  done &

  echo "***************************************************"
  echo "init.cql:"
  cat /tmp/init.cql
  echo "***************************************************"
fi

echo "Launching original docker-entrypoint"
exec /usr/local/bin/docker-entrypoint.sh "$@"
