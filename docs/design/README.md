# SomethingOS design

Design notes and concept sketches for the SomethingOS UI. Each document
covers one area: what it should look like, and how we plan to build it on
top of LineageOS 24.0 (Android 17).

| Area | Document |
| --- | --- |
| Lock screen layout | [lockscreen.md](lockscreen.md) |
| Lock screen widgets | [widgets.md](widgets.md) |
| Settings | [settings.md](settings.md) |
| Quick Settings and notification shade | [quick-settings.md](quick-settings.md) |
| Home screen | [home-screen.md](home-screen.md) |
| App drawer | [app-drawer.md](app-drawer.md) |
| Theme and style | [theme-and-style.md](theme-and-style.md) |
| NothingOS 5 reference notes | [nothingos-reference.md](nothingos-reference.md) |

## Principles

- **Customisability first.** Users should be able to arrange things the way
  they want, especially lock screen widgets.
- **Tablets are first-class.** Every screen needs a phone layout and a
  tablet layout (portrait and landscape). Phones follow the familiar
  single-column style.
- **Our own assets only.** SomethingOS is inspired by a monochrome,
  dot-matrix aesthetic but ships no Nothing fonts, icons, wallpapers,
  sounds or screenshots. Use open-licence or original assets.
- **Prefer existing Android machinery.** Restyle and extend what AOSP and
  LineageOS already have before writing new systems, to keep rebases on
  new Lineage releases manageable.
