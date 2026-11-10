# Glyph

Devices with Glyph lights (currently the Nothing Phone (1)) get a
SomethingOS **Glyph** app, modelled on the Glyph page introduced in
NothingOS 5.0: a live preview of the device's Glyph lights at the top,
then the Glyph features grouped below.

## What LineageOS already has

LineageOS's Phone (1) sources (`hardware/nothing`) ship **ParanoidGlyph**,
originally from AOSPA, as `ParanoidGlyphPhone1`, plus a `GlyphAdapter`. It
already drives the lights for:

- incoming calls and notifications (per-app patterns);
- charging level and power sharing;
- volume level;
- flip to Glyph (turn the phone face down);
- music visualiser;
- third-party apps using Nothing's Glyph developer kit;
- Quick Settings tiles for the Glyph and the Glyph torch.

## Plan

- Keep ParanoidGlyph's services and light control as the engine; they are
  device-specific and already work.
- Build a new SomethingOS Glyph app as the front end: NothingOS 5.0-style
  layout, our styling, dot-font title, and the light preview.
- Add NothingOS 5.0 features on top over time.
- Only ship light patterns that LineageOS already distributes or that we
  make ourselves; don't copy new patterns or sounds from NothingOS.
- Hide the app on devices without Glyph lights.

## Open questions

- Which NothingOS 5.0 Glyph features are wanted first? Screenshots of the
  NothingOS 5.0 Glyph app will help decide the layout.
