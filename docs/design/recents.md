# Recents and multitasking

![Tablet recents sketch, landscape and portrait](recents-tablet.png)

## Layout

- Recent apps as a **2×2 grid of cards**: wide cards in landscape, tall
  cards in portrait, matching the screen's shape.
- With more than four recent apps, the grid **always scrolls horizontally**,
  in both orientations.
- Each card has a **header pill** at the top left: app icon, app name and a
  chevron. Tapping it opens a drop-down menu:
  - **App info**
  - **Split screen**
  - **Pop-Up View** (floating window)
  - **Clear** (close the app)

## Split screen and Pop-Up View

Split screen is started from the card's drop-down menu, as in NothingOS:

1. Tap the header pill on a recents card and choose **Split screen**.
2. That app moves into its half of the screen as a placeholder showing its
   icon (top half in portrait; on tablets in landscape, the left half).
3. The other recents cards stay visible, with a bar at the bottom: "Choose
   another app to use split screen" and **Cancel**.
4. Scroll the recents and tap a second app; both open side by side.

### Pop-Up View

Based on NothingOS 5:

- Chosen from the card's drop-down menu (App info, Pop-Up View, Clear;
  Split screen when the app supports it).
- The app opens as a **floating window** with rounded corners over the home
  screen or another app.
- A slim **top bar** on the window: **–** (minimise) on the left, a drag
  handle in the middle, **×** (close) on the right.
- Drag the bar to move the window; drag a corner to resize it.
- **Minimise** shrinks it into a small floating preview at the screen edge.
  Tapping the preview shows close and mute buttons; tapping again restores
  the window.

## Taskbar / dock

The dock is **only visible on the home screen**. Inside apps there is no
taskbar; navigation uses the normal gesture bar.

## How to build it

- Part of the Launcher3 fork (Launcher3 also draws recents, via its
  "Quickstep" part).
- **Grid:** on tablets Launcher3 already shows recents as a grid (one large
  card plus smaller ones in two rows). The even 2×2 grid is a change to that
  layout.
- **Split screen flow:** this is Launcher3's own "split select" mode
  (first app parked, pick a second, instruction bar with Cancel), so it's
  mostly restyling.
- **Drop-down menu:** Launcher3 already has a per-card menu opened from the
  app icon, with App info and Split screen among its entries. Restyle it as
  the pill drop-down; Clear replaces swiping a card away (or works alongside
  it).
- **Pop-Up View:** maps onto Android's floating window support (freeform /
  desktop windowing on large screens), which already provides a caption
  bar, moving and resizing. The minimised preview is closest to Android's
  picture-in-picture-style window shrinking and needs the most new work.
  Check what LineageOS 24.0 enables on the Tab S6 Lite and the Phone (1).
- **No taskbar in apps:** Launcher3 shows a taskbar in apps on tablets by
  default; we turn it off and keep the gesture bar, like on phones.
