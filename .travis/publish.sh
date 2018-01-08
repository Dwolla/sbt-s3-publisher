#!/usr/bin/env bash

set -o errexit -o nounset

if [[ "$PUBLISH" == "true" ]]; then
  sbt "^^${SBT_VERSION}" publish
fi
