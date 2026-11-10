# Building

You need a machine that can build LineageOS 24.0. See LineageOS's own build
guides for host requirements and how to install `repo`.

```bash
# 1. Get the LineageOS 24.0 source
mkdir somethingos && cd somethingos
repo init -u https://github.com/LineageOS/android.git -b lineage-24.0 --git-lfs
mkdir -p .repo/local_manifests

# 2. Add SomethingOS and your device's trees
BASE=https://raw.githubusercontent.com/Aurora-StFM/SomethingOS/somethingos-24.0/manifests
DEVICE=gta4xlveu
curl -o .repo/local_manifests/somethingos.xml "$BASE/somethingos.xml"
curl -o ".repo/local_manifests/$DEVICE.xml" "$BASE/$DEVICE.xml"
repo sync -c -j$(nproc)

# 3. Build
source build/envsetup.sh
lunch somethingos_$DEVICE-<release>-userdebug
m bacon
```

`<release>` is the Android 17 release config name used by LineageOS 24.0.
The available names match the files under `build/release/release_configs/`
in your checkout, and `lunch` lists the valid ones if you pass a wrong one.

Set `DEVICE` to the codename from the [supported devices table](../README.md#supported-devices)
(`gta4xlveu` or `Spacewar`).

The Tab S6 Lite's device, kernel and vendor trees come from the
[gta4xlve-dev](https://github.com/gta4xlve-dev) project, the same sources as the
unofficial LineageOS build; one build covers both the Wi-Fi (SM-P613) and LTE
(SM-P619) models.
