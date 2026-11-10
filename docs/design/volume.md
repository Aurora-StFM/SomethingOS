# Volume panel

## Position

The panel appears **next to the physical volume rocker**, and stays on the
same side of the screen when the tablet is turned 180°:

- In each orientation (portrait or landscape), the panel sits on the edge
  where the rocker is when the device is held the normal way up.
- Turned 180°, the rocker is physically on the opposite side, but the panel
  stays where it would have been, so its position on screen never changes
  within an orientation.

## Styling

To follow, from NothingOS screenshots.

## How to build it

- SystemUI's volume dialog, restyled.
- Positioning is a SystemUI change: place the dialog on the rocker's edge
  for the current orientation, ignoring 180° rotation. The rocker's
  position is a per-device value (in `products/`), since it differs
  between devices.
