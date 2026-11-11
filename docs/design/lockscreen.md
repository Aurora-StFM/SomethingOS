# Lock screen

![Tablet lock screen sketch, landscape and portrait](lockscreen-tablet.png)

## Layouts

- **Tablet, landscape:** two columns. Left: large clock, widget grid under
  it, with room for an expanded widget area below. Right: notifications.
- **Tablet, portrait:** one centred column. Large clock, widget grid, then
  notifications. The large clock stays large when notifications arrive.
- **Phone:** the familiar single-column style: large clock, widget row,
  notifications.

All three layouts share the same clock and the same widget grid.

## How to build it

- **Clock:** a lock screen clock plugin (the mechanism Pixels use for their
  clock styles). Possibly shippable from `vendor/somethingos` with an
  allowlist overlay and no SystemUI fork; to be confirmed against the 24.0
  source. Most self-contained piece, so build it first.
- **Layouts:** SystemUI describes the lock screen with "blueprints".
  - Landscape builds on the existing split-shade lock screen, which already
    puts the clock left and notifications right.
  - Portrait and phone get a SomethingOS blueprint that keeps the large
    clock above the widgets and notifications.
  - Needs our `frameworks_base` fork.
- **Widgets:** see [widgets.md](widgets.md).

## Clock faces

The user picks a clock face in a **Clock face** sheet (lock screen edit
mode, Cancel / Done at the top), with the lock screen previewed above it.
Based on NothingOS 5's options.

### Faces

| Face | Description |
| --- | --- |
| Serif | large high-contrast serif digits, date above (the default) |
| Rounded bold | heavy, rounded geometric digits |
| Info line | monospace text line: day, date and month in pills, the time with a block cursor, then temperature and weather condition |
| Dot matrix | large dot-matrix digits, with some dots dimmed for depth |
| Analogue | wide, oval watch face with fine tick marks and a red second hand |
| Thin | thin sans-serif digits with a small weather line below |
| Segmented | blocky digits built from square grid segments |
| Dot info | small dot-matrix time with date and weather lines below, in the dot font |
| Extended | very wide, bold rounded digits |
| Wireframe | thin, angular outline digits |
| Pixel | chunky rounded pixel-style digits |

### Options

- **Size:** large (expanded) or small (compact).
- **Alignment:** anchor the clock and notifications to the **centre** or the
  **left** of the screen. On tablets in landscape this applies within the
  left column.
- **Colour:** "default" (no tint) or one of a row of colours taken from the
  wallpaper, scrollable for more.

### How to build it

- All faces ship in one SomethingOS clock plugin; SystemUI's clock registry
  supports several faces from one plugin.
- The face carousel, size and colour choices map onto what the Wallpaper &
  style app's clock picker already offers for clock plugins (face list,
  size, colour from the wallpaper palette). Restyle it as the sheet above.
- **Alignment** isn't a stock option: it's a setting read by our lock screen
  layouts (see Layouts above).
- **Fonts:** every face needs an open-licence font or custom-drawn digits;
  none of Nothing's fonts can be used. The Dot matrix face does **not**
  use the same dot font as the Settings titles: its dots are spaced
  differently horizontally and partly overlap, so Doto doesn't match it.
  It needs another dot font or custom-drawn dot digits. The other faces
  need suitable OFL fonts, chosen when each face is built.
- Faces showing weather use the same weather provider as the widgets (see
  [widgets.md](widgets.md)).

## PIN entry

![Tablet PIN entry sketch, landscape and portrait](pin-entry-tablet.png)

- **Landscape:** the screen splits in two.
  - **Left:** the lock screen (clock and widgets) stays where it was, and it
    and the wallpaper are blurred.
  - **Right:** a lock icon, "Enter PIN" and the number pad.
- **Portrait:** the clock and widgets slide up and out of view, the
  wallpaper is blurred, and the lock icon, "Enter PIN" and number pad sit
  centred on the screen.

Sizes and proportions in the sketch are provisional.

### How to build it

- SystemUI 24.0's PIN screen ("bouncer") already has a side-by-side layout
  for large screens in landscape (`BouncerOverlayLayout.BESIDE_USER_SWITCHER`):
  the PIN pad on one side, and the user switcher or empty space on the
  other. We show the blurred lock screen in that other half instead.
- Portrait uses the standard centred layout; the clock and widgets sliding
  up is an animation on the lock screen side as the PIN screen appears.
- The number pad is locked to the right in landscape for now. Stock
  Android lets the user double-tap the empty side to move the PIN pad there
  for one-handed use; we disable that.
- The blur uses Android's window blur, which depends on the device's GPU
  support. Check that it's enabled on the Tab S6 Lite; otherwise fall back
  to a dimmed, non-blurred lock screen.
