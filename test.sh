#!/usr/bin/env bash
docker ps -a
docker rm -f $(docker ps --all --quiet)

docker-compose build --no-cache
docker-compose up -d

echo "Starting cassandra container, will wait for 10 seconds..."
sleep 10

echo "Running tests"
sbt test
