# App drawer

![Tablet app drawer sketch, landscape and portrait](app-drawer-tablet.png)

## Layout

- **Swipe up on the home screen** to open the drawer as a **card** that
  takes the place of the app grid. The dock and the widget area stay
  visible beside it, so dock apps can still be launched while the drawer is
  open.
- The card holds **category cards**, as in NothingOS (Social, Productivity,
  Travel and so on; see [nothingos-reference.md](nothingos-reference.md)):
  - landscape: 3 columns, 3 rows visible;
  - portrait: 3 columns, 4 rows visible.
- **Search bar** pinned at the bottom of the card, with a **⋮ menu** that
  opens the app drawer and search settings.

Applies to the square grid layout; see [home-screen.md](home-screen.md).

## How to build it

- Part of the Launcher3 fork. On tablets, Launcher3 already shows the app
  drawer as a floating card over the home screen rather than full screen,
  so the card itself is mostly sizing (match the grid area) and styling.
- **Categories are the new part.** Android only offers a handful of
  developer-set app categories (games, audio, video, images, social, news,
  maps, productivity, accessibility). NothingOS has around 15 categories,
  so we need more data:
  - start from the Android category when the app sets one;
  - add a bundled list mapping common apps to categories;
  - fall back to "Others";
  - let the user move an app to another category.

## Open questions

- With more categories than fit, does the card scroll vertically?
- Tapping a category: does it expand inside the card, or open as a pop-up?
- Searching in landscape: the keyboard covers the lower part of the screen,
  including the search bar. Does the card shrink to sit above the keyboard,
  or does the search bar move to the top while typing?
