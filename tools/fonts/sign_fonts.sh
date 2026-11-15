#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 The SomethingOS Project
# SPDX-License-Identifier: Apache-2.0
#
# Sign the fonts the system font picker installs at runtime.
#
# The picker installs the copies of Geist and Roboto that are already on the
# device, so it only needs a signature for each. A signature covers the exact
# bytes of the file, so this script pulls the fonts from a connected device
# running SomethingOS, signs them with the SomethingOS font-signing key and
# writes the .sig files to fonts/signatures/. It then test-installs each font
# with `cmd font update` to prove the device accepts the signature.
#
# Rerun it whenever Geist or Roboto changes (a LineageOS update can change
# Roboto).
#
# Needs: adb (device connected, USB debugging on), openssl and fsverity
# (Ubuntu: apt install fsverity; Fedora: dnf install fsverity-utils).
#
# Usage: tools/fonts/sign_fonts.sh [path to private key]
#        (default: ~/somethingos-keys/somethingos-fonts.key)
#
# The private key never leaves your machine and must never be committed.

set -euo pipefail

KEY=${1:-$HOME/somethingos-keys/somethingos-fonts.key}
REPO=$(cd "$(dirname "$0")/../.." && pwd)
CERT_DER=$REPO/security/fsverity/somethingos-fonts.der
OUT=$REPO/fonts/signatures

# Fonts on the device that the picker installs.
FONTS=(
    /product/fonts/Geist-VF.ttf
    /system/fonts/Roboto-Regular.ttf
)

for tool in adb openssl fsverity; do
    command -v "$tool" >/dev/null || { echo "Missing tool: $tool" >&2; exit 1; }
done
[[ -r $KEY ]] || { echo "Can't read key: $KEY" >&2; exit 1; }

WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

# fsverity wants the certificate as PEM; the repo has it as DER.
openssl x509 -inform der -in "$CERT_DER" -out "$WORK/cert.pem"

# Make sure the key belongs to the certificate the ROM trusts.
if ! cmp -s <(openssl x509 -in "$WORK/cert.pem" -noout -pubkey) \
            <(openssl pkey -in "$KEY" -pubout); then
    echo "The key doesn't match security/fsverity/somethingos-fonts.der" >&2
    exit 1
fi

adb get-state >/dev/null
mkdir -p "$OUT"

for path in "${FONTS[@]}"; do
    name=$(basename "$path")
    echo "== $name"
    adb pull -q "$path" "$WORK/$name"

    # Geist should be byte-identical to the copy in this repo.
    if [[ -f $REPO/fonts/Geist/$name ]] && ! cmp -s "$WORK/$name" "$REPO/fonts/Geist/$name"; then
        echo "Warning: device's $name differs from fonts/Geist/$name; rebuild and reflash first?" >&2
    fi

    fsverity sign "$WORK/$name" "$OUT/$name.sig" --key="$KEY" --cert="$WORK/cert.pem"

    # Test-install it. Same file as the system copy, so nothing changes on screen.
    adb push -q "$WORK/$name" "$OUT/$name.sig" /data/local/tmp/
    adb shell cmd font update "/data/local/tmp/$name" "/data/local/tmp/$name.sig"
    adb shell rm "/data/local/tmp/$name" "/data/local/tmp/$name.sig"
    echo "Installed OK"
done

echo
echo "Signatures written to $OUT:"
ls -l "$OUT"
echo
echo "Remove the test installs with: adb shell cmd font clear && adb reboot"
