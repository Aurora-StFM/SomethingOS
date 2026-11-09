# Home screen

![Tablet home screen sketch, landscape and portrait](home-screen-tablet.png)

## Layout styles

A setting lets the user choose between two layouts:

- **Square grid (SomethingOS default on tablets):** a fixed 5×5 grid of
  square cells in the centre of the screen, identical in both orientations.
- **Adaptive (standard Android):** Launcher3's normal behaviour, where cells
  stretch to fill the screen.

### Square grid

- Cells stay square, so circles, pills and rounded squares keep the right
  proportions, matching the lock screen widget grid. Community widgets can
  rely on a cell always being the same shape.
- The leftover space on the long side of the screen is used, not left empty:
  - **Dock:** along the bottom in portrait, a vertical column on the right in
    landscape.
  - **"Empty space" widgets:** two dedicated widget slots, above the grid in
    portrait and to the left of it in landscape.
- The dock and widget slots stay on the same physical edge of the tablet
  when it's rotated (portrait top → landscape left, portrait bottom →
  landscape right). The dock order follows: W X Y Z left to right in
  portrait becomes W X Y Z bottom to top in landscape.

## How to build it

- Part of the Launcher3 fork. Launcher3 already computes its layout from a
  device profile and has selectable grid options (the "App grid" choice in
  Wallpaper & style), so the square grid is a new layout mode alongside the
  stock one rather than a rewrite.
- The "empty space" slots are a small separate widget area, built as a
  mini-grid of the same square cells, so placed widgets reflow when the
  orientation changes.

## Open questions

- **Which way round in landscape?** Following the physical edge means the
  dock is on the right when rotated one way and on the left when rotated
  the other. Do we follow the physical edge both ways, or always put the
  dock on the right?
- **Slot shapes:** the second widget slot is tall in landscape but wide in
  portrait, so a widget placed there changes shape on rotation. Most
  widgets can resize, but some won't look right. Alternative: make both
  slots square.
- **Per page or fixed?** Are the "empty space" widgets the same on every
  home screen page, like the dock, or does each page have its own?
- **Phones:** does the square grid apply to phones too, or tablets only?
