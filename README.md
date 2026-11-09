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
| Samsung Galaxy Tab S6 Lite **2022** (Qualcomm) | SM-P613, SM-P619 | Snapdragon 720G | [Unofficial LineageOS 22.2](https://xdaforums.com/t/rom-unofficial-2022-model-lineageos-22-2-for-galaxy-tab-s6-lite-2022-qualcomm.4699445/) |

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
manifests/somethingos.xml local manifest for repo
```

## Building

You need a machine that can build LineageOS 22.2. See LineageOS's own build
guides for host requirements and how to install `repo`.

```bash
# 1. Get the LineageOS 22.2 source
mkdir somethingos && cd somethingos
repo init -u https://github.com/LineageOS/android.git -b lineage-22.2 --git-lfs
mkdir -p .repo/local_manifests

# 2. Add SomethingOS and the device trees
curl -o .repo/local_manifests/somethingos.xml \
  https://raw.githubusercontent.com/Aurora-StFM/SomethingOS/somethingos-22.2/manifests/somethingos.xml
repo sync -c -j$(nproc)

# 3. Build
source build/envsetup.sh
lunch somethingos_gta4xlve-bp1a-userdebug
m bacon
```

The release name in the `lunch` target (`bp1a` above) must match the one used
by LineageOS 22.2 and the device tree. Check the device tree if `lunch` complains.

The device trees in `manifests/somethingos.xml` still need filling in, and the
device codename (`gta4xlve`) still needs to be confirmed against them.

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
