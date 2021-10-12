#!/usr/bin/env bash
docker-compose down
rm -rf .data
docker-compose build --no-cache
docker-compose up