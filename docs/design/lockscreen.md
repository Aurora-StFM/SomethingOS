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
