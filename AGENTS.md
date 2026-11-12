# AGENTS.md

Guidance for AI coding agents (and humans) working on SomethingOS. Read this
before changing anything; the README and the wiki (`docs/`) have the
user-facing detail.

## What this repository is

SomethingOS is a LineageOS-based Android ROM with a NothingOS-inspired look,
designed for tablets as well as phones. This repository is the
**SomethingOS vendor layer**: it's checked out at `vendor/somethingos` inside a
full LineageOS source tree and adds products, overlays, fonts and
configuration on top of LineageOS. It is not a complete Android tree.

- Base: **LineageOS 24.0 (Android 17)**, release config `cp2a` (read from
  `vendor/lineage/vars/aosp_target_release`, don't hard-code it).
- Branches track Android versions: `somethingos-24.0` (default, active) and
  `somethingos-22.2` (fallback for the Tab S6 Lite only; don't develop on it).
- Devices are folders/files on the same branch, never separate branches.

## Layout

```
AndroidProducts.mk         lunch targets (PRODUCT_MAKEFILES, COMMON_LUNCH_CHOICES)
config/common.mk           shared config, inherited by every product
config/version.mk          ro.somethingos.version and build type
products/somethingos_<codename>.mk   one per device
manifests/somethingos.xml  local manifest: SomethingOS repos and forks (all devices)
manifests/<codename>.xml   local manifest: one device's device/kernel/vendor trees
rro/                       runtime resource overlays
fonts/<Font>/              one folder per font: files, OFL.txt, Android.bp
docs/                      wiki source (Home, Installation, Building, design/)
.github/                   wiki sync workflow and script
```

## You can't build or test here

A build needs a full LineageOS checkout (~300 GB), 64 GB+ RAM plus swap, and
hours of compile time. Agents normally can't build, flash or run anything.
So:

- Keep changes small and reviewable, and check XML with `xmllint --noout`.
- Verify facts against the actual LineageOS 24.0 / AOSP sources (shallow or
  sparse clones of the relevant repos on GitHub) instead of assuming how
  Android behaves; several "obvious" approaches have failed here (see
  Pitfalls).
- Hand the maintainer exact commands to build and to verify the result on
  the device (`adb shell …`), and say what output to expect.

## Conventions

- **Device-agnostic layer.** Nothing device-specific (SoC, vendor, arm64) in
  `config/common.mk` or shared overlays. Device quirks go in that device's
  `products/` file or a per-device overlay.
- **Adding a device:** product makefile inheriting the device's
  `lineage_<codename>.mk` plus `config/common.mk`, an entry in
  `AndroidProducts.mk`, and `manifests/<codename>.xml`. Update the
  supported/planned devices tables in `README.md`.
- **Products are named `somethingos_<codename>`.** LineageOS only sets
  `LINEAGE_BUILD` for `lineage_*` products, so builds `export
  LINEAGE_BUILD=<codename>` after `lunch` (documented in `docs/Building.md`).
- **Changing a LineageOS repo means forking it** to `Aurora-StFM` (same repo
  name), a `somethingos-24.0` branch based on `lineage-24.0`, and a
  `<remove-project>` + `<project>` pair in `manifests/somethingos.xml` under
  "LineageOS repos replaced by SomethingOS forks". Keep fork diffs minimal and
  clearly marked so rebasing on new LineageOS releases stays easy. Current
  forks:
  - `android_packages_overlays_Lineage`: `fonts/etc/fonts_customization.xml`
    registers the SomethingOS font families.
- **Overlays:** `runtime_resource_overlay` modules in `rro/`, product
  specific, added to `PRODUCT_PACKAGES` in `config/common.mk`.
- **Licence headers:** `SPDX-FileCopyrightText: 2026 The SomethingOS Project`
  and `SPDX-License-Identifier: Apache-2.0` on new files.
- **English:** British spelling in docs and user-facing strings (colour,
  customise, licence as a noun).

## Fonts

| Family | Font | Licence | Use |
| --- | --- | --- | --- |
| `somethingos-dot` | Doto, `wght` 900 / `ROND` 100 | OFL | Settings titles (Dot font option), dot widgets |
| `somethingos-sans` | Geist (variable `wght`) | OFL | System UI font (default) |
| `somethingos-serif` | Old Standard TT | OFL | Serif titles (NothingOS's NType) |

- Font files and their `OFL.txt` live in `fonts/<Font>/`; families are
  registered in the overlays fork, because Android reads a single
  `/product/etc/fonts_customization.xml` and LineageOS already ships it.
- `FontGeistOverlay` (static) makes Geist the UI font; `FontRobotoOverlay`
  (category `android.theme.customization.font`) switches to Roboto.
- The lock screen's dot-matrix clock face uses a **different** dot style from
  Doto; don't reuse `somethingos-dot` for it.

## Hard rules: legal and assets

- **No Nothing assets.** Never add Nothing's fonts (NDot, NType), icons,
  wallpapers, sounds, ringtones, Glyph patterns or screenshots of their UI to
  the repo or wiki. Describe NothingOS in words; recreate with original or
  open-licence assets. Sounds are original compositions in the same style,
  not recreations of Nothing's melodies.
- **Open licences only** for third-party assets, with the licence file next
  to them. Check for an OFL "Reserved Font Name" before modifying or renaming
  a font.
- **Nothing's Glyph / GlyphMatrix SDKs:** work only from their public
  documentation and observed behaviour. Their licence forbids decompiling or
  reverse-engineering; never inspect SDK internals, and never ship the SDK.
- **No "Nothing" branding** in names, package names or product names.
  Describing compatibility ("Glyph Toys made for the Phone (4a) Pro") is
  fine.

## Docs and wiki

- `docs/` is the source of the GitHub wiki. A workflow
  (`.github/workflows/wiki.yml`, `.github/scripts/sync_wiki.py`) regenerates
  the wiki on every push to `somethingos-24.0`. **Edit `docs/`, never the
  wiki**; wiki edits are overwritten.
- `docs/Home.md` is the wiki Home; its links set the sidebar order. A folder
  with a `README.md` is a section (e.g. `docs/design/`). Page names come
  from each file's first `# ` title and must be unique.
- Design decisions belong in `docs/design/<area>.md`. Record what was
  decided, how it will be built on LineageOS 24.0, and any open questions.
  When a question is answered, update the doc and remove the question.
- Keep `docs/Building.md` and `docs/Installation.md` in step with any change
  to the build or install process.

## Commits

- Author commits as the maintainer:
  `Aurora-StFM <84717132+Aurora-StFM@users.noreply.github.com>` (the GitHub
  noreply address; don't use a personal email).
- Credit the agent with a `Co-Authored-By:` trailer.
- Commit messages: short imperative subject; body explains *why*.
- Push to `somethingos-24.0`. Don't rewrite published history without the
  maintainer's explicit go-ahead.

## Pitfalls already hit

- **`overrides` doesn't let two modules install the same file.** The build
  still creates install rules for both and kati fails with "overriding
  commands for target". Fork the owning repo instead.
- **Switching a synced folder to a fork** needs
  `repo sync -c --force-sync <path>` after updating
  `.repo/local_manifests/`; a plain sync silently keeps the old repo.
- **`soong_build` needs a lot of memory** (60–70 GB peak). Builds get killed
  (exit 137) without a large swapfile.
- **ccache needs `CCACHE_DIR`.** Compile steps run sandboxed as `nobody`;
  without it every compile fails with "ccache: error: Not a directory".
- **`repo sync` from android.googlesource.com is rate-limited** for anonymous
  users (HTTP 429). Use `-j4` and retry later, or authenticate with
  `.gitcookies`.

## Working with the maintainer

The maintainer is learning Android platform development through this
project. When proposing or making a change, briefly explain what it does and
why, and give exact commands for anything they need to run on their machine.
For build or device problems, explain the cause and the fix rather than
leaving them to work it out.
