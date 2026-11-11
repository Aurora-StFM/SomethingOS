# Settings

![Tablet Settings sketch with About device open](settings-tablet.png)

## Layout

Two panes on tablets: settings list on the left, the selected page on the
right. Android's Settings already does this on large screens, so the
layout itself is restyling.

- **Titles:** large page titles ("Settings", "About device"). Rendered in a
  dot-matrix font when the experimental **Dot font** toggle is on (see
  below). Needs a small Settings patch, since a static overlay can't switch
  fonts based on a setting.
- **Search:** pill-shaped search bar at the top of the list. Restyle of the
  existing one.
- **List:** entries grouped into rounded cards with dividers between rows.
  Android 16 introduced a grouped rounded style for Settings; check whether
  24.0 has it, in which case this is mostly spacing, colour and icons.
- **Profile picture (top right):** see below.

## Special features

A SomethingOS section in the main Settings list, provided by a small
SomethingOS settings app. Android lets other apps inject entries into
Settings (this is how Lineage adds its own), so no Settings fork is needed
for it. First contents:

- **Experimental → Dot font:** use the dot-matrix font for **Settings page
  titles**. That's all it changes.

The goal is to add most, if not all, of NothingOS's special features over
time, built up as the ROM matures rather than all at once:

- Experimental features (starting with Dot font)
- Gestures
- RAM Booster
- Pop-up view (see [recents.md](recents.md))
- Game Mode

## About device

A card layout replacing the standard list. Needs a custom page in our
`packages_apps_Settings` fork.

| Card | Source |
| --- | --- |
| SomethingOS version | `ro.somethingos.version`, set by `config/version.mk` |
| Update status ("Up to date") | the updater app. Needs our own update server eventually; until then, "Check for updates" |
| Device name | per-device value in `products/` |
| Device image | per-device image in `products/` |
| Android version | standard system info |
| IMEI 1 / IMEI 2 | standard system info. **Hidden** on devices without a mobile radio rather than showing "N/A" |
| See all | the full standard status page |
| Specs: SoC, cameras, RAM, storage, battery, screen | read from the device at runtime; cameras via the camera API, or a per-device value if unreliable |

## Profile picture

The circle in the top-right corner shows the **local Android user's
profile picture**, the one set for the device owner in Settings. No online
account is involved:

- signing in with a Nothing account isn't possible or advisable;
- a SomethingOS account would need servers and would bring privacy and
  security obligations, for no clear benefit.

When no picture is set, it shows a small SomethingOS logo. Tapping it opens
the user/profile settings.

## Fonts

Nothing's NDot font is proprietary and can't be shipped.

**Chosen dot font: [Doto](https://fonts.google.com/specimen/Doto)** (SIL Open
Font License), set to **weight 900** and **roundness 100** (both at their
maximum). At those settings it is nearly identical to NDot.

- Doto is a variable font with a weight axis (`wght`, 100–900) and a
  roundness axis (`ROND`, 0–100). SomethingOS ships the unmodified variable
  font (`fonts/Doto-VF.ttf`, installed to `/product/fonts`) and sets the axes
  in the font configuration, the same way LineageOS ships Google Sans Flex.
- It's registered as the font family **`somethingos-dot`** in
  `/product/etc/fonts_customization.xml`, so system apps can use it by name
  (`android:fontFamily="somethingos-dot"`, or `Typeface.create("somethingos-dot",
  Typeface.NORMAL)`). Android reads only one such file and LineageOS already
  ships it (in `packages/overlays/Lineage`), so the entry is added in our
  fork of that repo,
  [Aurora-StFM/android_packages_overlays_Lineage](https://github.com/Aurora-StFM/android_packages_overlays_Lineage)
  (branch `somethingos-24.0`).
- Ship the OFL licence text alongside the font file.
- Used for: large Settings page titles (only when the Dot font option is
  on). Also available to dot-matrix widgets and the community widget kit.
  Not the lock screen's Dot matrix clock face, which uses a different dot
  style (see [lockscreen.md](lockscreen.md#clock-faces)).

### Other fonts

NothingOS uses two more font families.

**Geist (sans-serif): `somethingos-sans`.** By Vercel, released under the
**SIL Open Font License** with no Reserved Font Name, so SomethingOS ships
Geist itself rather than a look-alike.

- NothingOS lets the user choose the **system font** in *Font and display
  size*: **Roboto** or **Geist**, each shown as an "Aa 123" preview card,
  with Cancel / Apply. SomethingOS offers the same two.
- **Geist is the default.** `FontGeistOverlay` is a static overlay (always
  on) that sets Android's UI font families to `somethingos-sans`. Its
  priority (2) is above LineageOS's Google Sans Flex overlay (1), which it
  replaces.
- **Roboto** is `FontRobotoOverlay`, a switchable font overlay (category
  `android.theme.customization.font`) that sets them back to `sans-serif`.
  The picker enables it for Roboto and disables it for Geist. Until the
  picker exists it can be tested with
  `adb shell cmd overlay enable org.somethingos.overlay.font.roboto`.
- Known gap: Android 16+ also uses separate "variable-display/headline/…"
  font families built on Google Sans Flex for some Material 3 text. The
  overlays don't change those; parts of the UI may stay in Google Sans
  Flex until they're remapped.

**NType (serif): Old Standard TT, `somethingos-serif`.** NType is Nothing's
own serif, used for titles such as the Glyph app's, "App drawer & Search"
and "Nothing Machines (1)". It's proprietary; **Old Standard TT** (SIL
OFL 1.1) is a close open-licence match (regular, italic and bold).

All three families are registered in our fork of `packages/overlays/Lineage`
(`fonts/etc/fonts_customization.xml`); the font files live in
`vendor/somethingos/fonts/<Font>/` with their licences.
