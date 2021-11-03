#!/usr/bin/env bash


gradle assembleDebug

adb install -r ./app/build/outputs/apk/debug/app-debug.apk