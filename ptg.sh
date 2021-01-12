#!/usr/bin/env bash

export ARGS=$(echo "$@")

mvn -q exec:java -Dexec.args="$ARGS"