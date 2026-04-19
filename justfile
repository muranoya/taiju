set shell := ["bash", "-ceuo", "pipefail"]

android_home := env_var_or_default("ANDROID_HOME", "/home/muraoka/Android/Sdk")
adb := android_home + "/platform-tools/adb"
emulator := android_home + "/emulator/emulator"
avd := env_var_or_default("TAIJU_AVD", "Medium_Phone_API_36.1")

app_id_debug := "net.meshpeak.taiju.debug"
main_activity := "net.meshpeak.taiju.MainActivity"

# List available recipes
default:
    @just --list

# Run linters: ktlint + Android Lint
check:
    ./gradlew ktlintCheck lint

# Auto-format Kotlin sources with ktlint
format:
    ./gradlew ktlintFormat

# Build debug APK, install on an emulator/device, and launch the app
run: _ensure-device
    ./gradlew installDebug
    {{adb}} shell am start -n {{app_id_debug}}/{{main_activity}}

# Build an unsigned release APK
release:
    ./gradlew assembleRelease
    @echo "Release APK: app/build/outputs/apk/release/"
    @echo "(unsigned — configure signingConfigs.release before distributing)"

# Run JVM unit tests
test:
    ./gradlew test

# Build a release APK. Signed if RELEASE_KEYSTORE_* env vars are set (see README), otherwise unsigned.
build-release:
    ./gradlew assembleRelease
    @echo "Release APK: app/build/outputs/apk/release/"

# Clean all build outputs
clean:
    ./gradlew clean

# Start the emulator if no device is attached, and wait for boot
_ensure-device:
    #!/usr/bin/env bash
    set -euo pipefail
    if {{adb}} devices | awk 'NR>1 && $2=="device" {found=1} END {exit !found}'; then
        echo "Device already attached."
    else
        echo "No device attached. Booting AVD '{{avd}}'..."
        nohup {{emulator}} -avd {{avd}} >/tmp/taiju-emulator.log 2>&1 &
        {{adb}} wait-for-device
        echo "Waiting for boot to complete..."
        until [ "$({{adb}} shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do
            sleep 2
        done
        echo "Boot complete."
    fi
