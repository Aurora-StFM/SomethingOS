# Theme and style

The user picks one of three styles in a **Theme & Style** page, with a live
preview of the home screen above the options.

| Style | Look | Colour options |
| --- | --- | --- |
| **Translucent** | frosted, wallpaper-tinted surfaces for widgets, icons and folders | a few accent colours taken from the wallpaper |
| **Classic** | solid black surfaces, fixed accents (e.g. blue Away Time bar, yellow clock dot) | none |
| **Dynamic** | Android's wallpaper-based colour system (Material You) | preset palettes |

Also on the page:

- **Dark theme** toggle.
- **Icon pack** picker.
- **Apply icons to Home screen only.**

## How to build it

- **Dynamic** is mostly free: Android and the Wallpaper & style app already
  generate these palettes.
- **Translucent and Classic** are one SomethingOS setting that the launcher,
  folders and our own widgets read. Third-party widgets only follow the
  Dynamic colours, since they don't know about our setting.
- **Icon packs** and **Apply icons to Home screen only** are launcher
  features and come with the launcher fork.
