# SomethingOS

A LineageOS-based Android ROM with a clean, monochrome, dot-matrix-inspired
look. Under the hood it's LineageOS; SomethingOS is a theming and UI layer
on top of it (overlays, fonts, launcher and SystemUI tweaks).

SomethingOS is an independent community project. It is not affiliated with,
endorsed by, or based on software from Nothing Technology Limited, and it
does not ship any of Nothing's fonts, icons, wallpapers, sounds or other assets.

> **Status: early development.** Nothing here is ready for daily use yet.

## Supported devices

**Currently supported:** only one device.

| Device | Model numbers | SoC | Base |
| --- | --- | --- | --- |
| Samsung Galaxy Tab S6 Lite **2022** (Qualcomm) | SM-P613, SM-P619 | Snapdragon 720G | LineageOS 24.0 (Android 17), unofficial |

SomethingOS targets **Android 17** via LineageOS 24.0. The device's
[LineageOS release on XDA](https://xdaforums.com/t/rom-unofficial-2022-model-lineageos-22-2-for-galaxy-tab-s6-lite-2022-qualcomm.4699445/)
is still 22.2 (Android 15); its maintainers' 24.0 sources are in active
development and not yet released.

If the Android 17 sources don't boot yet, the `somethingos-22.2` branch is kept
as a fallback. It builds on the released LineageOS 22.2 (Android 15) device
sources instead.

Other Tab S6 Lite models are **not** supported. That includes the original
2020 Exynos model (SM-P610/P615) and the 2024 model, which use different hardware.

**More devices are planned.** The SomethingOS layer is kept device-agnostic
so it can be added to other LineageOS-supported devices later, and x86
builds are a long-term goal.

## Repository layout

This repository is checked out at `vendor/somethingos` in a LineageOS tree.

```
AndroidProducts.mk        lunch targets
config/common.mk          shared config, inherited by every product
config/version.mk         version string and properties
products/                 one product makefile per supported device
rro/                      runtime resource overlays (theming)
manifests/somethingos.xml local manifest: SomethingOS repos, shared by all devices
manifests/<codename>.xml  local manifest: one device's device/kernel/vendor trees
```

Devices live side by side in this layout rather than on separate branches.
Branches track Android versions (`somethingos-24.0`, `somethingos-22.2`).

### Adding a device

1. Add `products/somethingos_<codename>.mk`, inheriting the device's
   `lineage_<codename>.mk` and `vendor/somethingos/config/common.mk`
   (copy an existing product file).
2. List it in `AndroidProducts.mk` (`PRODUCT_MAKEFILES` and
   `COMMON_LUNCH_CHOICES`).
3. Add `manifests/<codename>.xml` with the device's trees.

Anything that applies to only one device goes in its product file, never in
`config/common.mk`. If a device tree needs patching, fork it into its own
repository and point the device manifest at the fork.

## Building

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

The device, kernel and vendor trees come from the
[gta4xlve-dev](https://github.com/gta4xlve-dev) project, the same sources as the
unofficial LineageOS build. The device codename is `gta4xlveu`; one build covers
both the Wi-Fi (SM-P613) and LTE (SM-P619) models.

## Roadmap

- [x] Repository and product scaffolding
- [ ] Device trees wired into the manifest; first booting build
- [ ] Monochrome default theme with a red accent
- [ ] Dot-matrix display font and system font config
- [ ] Boot animation, wallpapers and UI sounds
- [ ] Dot-matrix lockscreen clock
- [ ] SomethingOS launcher (Launcher3 fork, tablet-aware)
- [ ] SystemUI styling: quick settings, notifications, volume panel
- [ ] Additional devices
- [ ] x86 builds

## License

Apache License 2.0. See [LICENSE](LICENSE).
