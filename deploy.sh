#!/usr/bin/env bash

function run() {
    echo "$@"
    "$@" || exit 1
}

run ./gradlew clean
run ./gradlew build
run ./gradlew deployCli
run ./gradlew nativePackage_dmg