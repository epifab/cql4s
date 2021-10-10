#!/usr/bin/env bash
docker ps -a
docker rm -f $(docker ps --all --quiet)

docker-compose up -d

sbt test
