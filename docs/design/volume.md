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

Based on NothingOS 5.

**Collapsed** (what appears when pressing the volume buttons):

- A round **ringer mode** button on top (ring / vibrate / mute); tapping it
  cycles through the modes.
- Below it, a tall dark **pill slider** for the active stream. The track is
  a column of dots; the filled part is a solid white bar. The stream's icon
  (e.g. a music note) sits at the bottom of the bar.
- A **‹** chevron at the bottom of the pill expands the panel.

**Expanded:**

- The ringer modes become a horizontal pill of three buttons (mute,
  vibrate, ring) with the active one highlighted.
- Below, a card with **one tall dot-track slider per stream** side by side
  (media, alarm, notification, ring/call), each with its icon underneath.
- **SEE MORE** at the bottom opens Sound and vibration settings.
- A separate round **Live Caption** button sits below the card.

## How to build it

- SystemUI's volume dialog, restyled. Android's dialog already has a
  ringer mode button, a main slider and an expand action showing all
  streams, so the structure matches; the dot-track sliders and pill shapes
  are new styling.
- Positioning is a SystemUI change: place the dialog on the rocker's edge
  for the current orientation, ignoring 180° rotation. The rocker's
  position is a per-device value (in `products/`), since it differs
  between devices.
