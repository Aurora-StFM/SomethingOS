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
- The category cards **scroll vertically** inside the card when there are
  more than fit.
- **Search bar** pinned at the bottom of the card, with a **⋮ menu** that
  opens the app drawer and search settings. When the keyboard opens, the
  card stays where it is and the **search bar jumps to the top** of the
  card, above the results, so the keyboard doesn't cover it.

### Opening a category

As in NothingOS:

- Each category card shows three large icons plus a small cluster of the
  rest.
- Tapping a card fades and blurs the drawer behind, and the category opens
  as an overlay: a large title (e.g. "Others") rising into place from the
  card, with all its apps below in a grid, icons with labels.
- Tapping outside it, or going back, reverses the animation and returns to
  the drawer at the same scroll position.
- On tablets the overlay fills the drawer card's area, so the dock and
  widget area stay in view (dimmed).

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
- **Opening a category** behaves much like opening a Launcher3 folder (an
  animated container growing from the icon), so it can reuse the folder
  open/close animation with a blurred background.

## App drawer & Search settings

Opened from the ⋮ menu on the search bar. Modelled on NothingOS's page:

- **Drawer layout:**
  - **Default:** a suggestions row, then all apps in a plain alphabetical
    grid.
  - **Smart:** a suggestions row, then the category cards.
- **Show app suggestions:** the suggestions row at the top.
- **Show hidden icons:** whether apps the user has hidden appear in the
  drawer.
- **Search:**
  - **Swipe up to start search:** open the keyboard straight away when the
    drawer opens.
  - **Search results settings:** what search covers (apps, settings,
    contacts, files and so on).
  - **Default search engine:** which engine handles web searches.

### How to build it

- **Default layout** is Launcher3's stock app drawer.
- **App suggestions:** Launcher3's suggestions row is normally filled by
  Google's on-device prediction service, which LineageOS doesn't have. We'd
  write a simple on-device predictor from recent and frequent app usage.
- **Hidden apps:** Lineage's launcher (Trebuchet) may already support hiding
  apps; check its 24.0 source before writing our own.
- **Search scope:** stock Launcher3 search only finds apps. Searching
  settings, contacts and files means adding a search provider for each.
- **Search engine:** web searches open in the browser using the chosen
  engine; no account or Google service involved.
