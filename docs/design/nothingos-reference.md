# NothingOS 5 reference notes

Written observations from a screen recording of NothingOS 5 (Android 17) on
a phone, to refer back to when designing SomethingOS. The recording itself
is not stored in this repository, since it shows Nothing's UI and assets and
personal data. Everything here is described in words; SomethingOS
recreates the look with its own assets.

## Home screen

- Wallpaper-tinted translucent widgets (Translucent style, see
  [theme-and-style.md](theme-and-style.md)) on a grid with the same cell
  shapes as the lock screen: circles, pills, rounded squares.
- Examples on screen: a vertical pill clock (hours above minutes), a wide
  Away Time bar with "Away 10h 59m", a Bluetooth device battery square, a
  cut-out photo (Collector), a date-and-weather square in the dot font, an
  analogue clock, a pill timer ("3h 15m"), round single-app icons with
  labels.
- **Folders as large widgets:** a 2×2 folder ("Social") shows a grid of round
  app icons that can be tapped directly. Small round folders ("Google",
  "System", "Stuff") show a single dot-matrix glyph.
- **Dock:** five round monochrome icons, no background bar.
- Icons are monochrome (themed); the user's icon pack is applied.

## App drawer

- Apps grouped automatically into **category cards**, two per row:
  Social, Utilities, Multimedia Tools, Entertainment, Productivity, Travel,
  Education, Shopping, Business, Games, Lifestyle, Finance, News & Reading,
  Health & Fitness, Others.
- Each card: title, three large icons, plus a 2×2 cluster of small icons for
  the rest (tap to expand the category).
- Search bar pinned at the bottom with a menu button; tapping it opens the
  keyboard and filters apps.
- Opens with a fade over the home screen.

## Quick Settings and notifications

- Fixed header row: edit (pencil, outlined pill), "N apps are active" pill,
  settings, power.
- Scrolls under the header when tiles overflow; the media card scrolls away
  first. See [quick-settings.md](quick-settings.md).
- Sideways swipe switches between Quick Settings and notifications.
- Active tiles are white circles with black icons; inactive tiles are dark
  grey with white icons. Large tiles: a round icon at the left end of a pill
  with a title and status.
- Notifications: grouped under a "Silent" heading; collapsed cards with a
  round app icon, title, age ("2d") and expand chevron. Ongoing ones (screen
  recording) are full-colour cards. Round buttons below for history and
  notification settings.

## Recents

- Single row of large app cards, swiped horizontally, with the app name and
  icon in a pill above each card and a Screenshot button below.

## Settings

- **Top level:** large dot-font "SETTINGS" title that collapses as you
  scroll, pill search bar, profile avatar circle at the top right.
- Entries grouped into rounded dark cards with thin dividers; icons are
  simple outlined glyphs; secondary text in grey (e.g. "N Telenor",
  "CMF Watch 3 Pro · 100%").
- Group order: Network and internet / Connected devices; Glyph Interface,
  Intelligence toolkit, Lock screen, Customisation; Apps, Notifications,
  Battery, Storage, Sound and vibration, Modes, Display; Accessibility,
  Security and privacy, Location, Safety and emergency, Passwords and
  passkeys, Accounts and backup, Digital Wellbeing, Parental controls,
  Google; System, Special features, Tips and feedback, About phone.
- **Sub-pages** (Lock screen, Display) use the same large dot-font title,
  then small grey section headers ("What to show", "When to show",
  "Brightness", "Appearance") above grouped cards. Toggles sit at the right
  of each row. A "Looking for other settings?" card at the bottom links to
  related pages.

## About phone

- Large dot-font OS name and version ("NOTHING OS 5.0") in a card with "Up
  to date" at the bottom.
- Device name card below it, and a tall card with a product render of the
  phone beside both.
- Spec grid of 2×3 tiles, each with a small outlined icon, a label and a
  value: Processor, Camera, RAM, Storage, Battery, Screen.
- "Device details" section: Android version, IMEI 1 / IMEI 2 side by side,
  See all.
- "Legal and regulatory" section: User guide, Legal information, Regulatory
  labels, Safety information, Recycle devices.
- Compare with the SomethingOS version in [settings.md](settings.md).
