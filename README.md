# SomethingOS

A LineageOS-based Android ROM with a clean, monochrome, dot-matrix-inspired
look. Under the hood it's LineageOS; SomethingOS is a theming and UI layer
on top of it (overlays, fonts, launcher and SystemUI tweaks).

SomethingOS is an independent community project. It is not affiliated with,
endorsed by, or based on software from Nothing Technology Limited, and it
does not ship any of Nothing's fonts, icons, wallpapers, sounds or other assets.

> [!WARNING]
> **Not recommended for daily use.**
>
> - **Early development.** Expect bugs, missing features and breaking
>   changes between builds. Back up your data before installing.
> - **Built with heavy use of AI tools.** Much of the code and documentation
>   is written with AI assistance and reviewed by a single developer. It has
>   not had the testing or review a mature ROM gets.
> - **No Play Integrity.** Like most custom ROMs, SomethingOS does not pass
>   Google's Play Integrity checks. Banking apps, Google Wallet / contactless
>   payments, some streaming apps and some games may refuse to run.
> - **Unlocked bootloader.** Installing any custom ROM means unlocking the
>   bootloader, which may void your warranty and, on Samsung devices,
>   permanently trips Knox.

## Supported devices

**Currently supported:** two devices.

| Device | Codename | Model numbers | SoC | Base |
| --- | --- | --- | --- | --- |
| Samsung Galaxy Tab S6 Lite **2022** (Qualcomm) | `gta4xlveu` | SM-P613, SM-P619 | Snapdragon 720G | LineageOS 24.0 (Android 17), unofficial |
| Nothing Phone (1) | `Spacewar` | A063 | Snapdragon 778G+ | LineageOS 24.0 (Android 17), official |

SomethingOS targets **Android 17** via LineageOS 24.0.

The Nothing Phone (1) is officially supported by LineageOS, so its device,
kernel and vendor sources are the same ones official LineageOS builds use.

For the Tab S6 Lite, the device's
[LineageOS release on XDA](https://xdaforums.com/t/rom-unofficial-2022-model-lineageos-22-2-for-galaxy-tab-s6-lite-2022-qualcomm.4699445/)
is still 22.2 (Android 15); its maintainers' 24.0 sources are in active
development and not yet released.

If the Tab S6 Lite's Android 17 sources don't boot yet, the `somethingos-22.2`
branch is kept as a fallback (Tab S6 Lite only). It builds on the released
LineageOS 22.2 (Android 15) device sources instead.

Other Tab S6 Lite models are **not** supported. That includes the original
2020 Exynos model (SM-P610/P615) and the 2024 model, which use different hardware.

### Planned devices

Not buildable yet; listed so the plan is visible.

| Device | Codename | SoC | LineageOS status |
| --- | --- | --- | --- |
| OnePlus Nord CE 5G | `ebba` | Snapdragon 750G | official, LineageOS 24.0 sources available |
| Nokia 8 Sirocco | `A1N` | Snapdragon 835 | no LineageOS sources; would need a port |

The Nord CE 5G is officially supported by LineageOS 24.0, so adding it
should follow the same steps as the Phone (1).

The Nokia 8 Sirocco has no LineageOS device tree. The closest starting point
is the Nokia 8 (`NB1`), which has the same chip and was supported by
LineageOS up to 22.2, but porting it to the Sirocco and to Android 17 is a
much larger job.

The SomethingOS layer is kept device-agnostic so more LineageOS-supported
devices can be added later, and x86 builds are a long-term goal.

## Design

The planned look and behaviour of SomethingOS (lock screen, widgets, Quick
Settings, home screen, Glyph and more) is documented in the
**[wiki](https://github.com/Aurora-StFM/SomethingOS/wiki)**, along with
installation and build instructions. The wiki is generated automatically
from `docs/` in this repository; edit the files there.

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
docs/                     wiki source: home, installation, building
docs/design/              design notes and concept sketches
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

See **[Building](https://github.com/Aurora-StFM/SomethingOS/wiki/Building)**
in the wiki ([source](docs/Building.md)).

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
