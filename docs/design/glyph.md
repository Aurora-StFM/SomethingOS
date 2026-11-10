# Glyph

Devices with Glyph lights (currently the Nothing Phone (1)) get a
SomethingOS **Glyph** app, modelled on the Glyph page introduced in
NothingOS 5.0: a live preview of the device's Glyph lights at the top,
then the Glyph features grouped below.

## Layout (from NothingOS 5)

- **Title** "Glyph" with a settings gear at the top right.
- **Hero:** an illustration of the back of the phone with its Glyph lights,
  which light up to preview what's being changed. Below it, a segmented
  **brightness** slider.
- **System**
  - **Essential Notifications** ("Filter what's important"): rules for which
    apps and contacts light the Glyph and keep a light on until seen. First
    use shows a three-step introduction; afterwards a list of rules (per
    app or contact, each with a toggle) and a **New rule** button.
- **Tools** (two-up cards with toggles)
  - **Always-on Glyph Toy** (e.g. a digital clock) — needs a Glyph matrix.
  - **Glyph Timer** (visual countdown).
- **Triggers**
  - **Flip to Glyph** (silence on demand by turning the phone face down).
  - **Volume indicator** (audio level at a glance).
  - **Glyph Progress** (third-party integrations, e.g. delivery or ride
    progress).
- A short epilepsy and light-sensitivity warning at the bottom.
- A floating **bottom bar with two tabs**:
  - **Glyph** (the page above);
  - **Sounds**: Notifications / Ringtones switch; a choice between
    **Generative** (Glyph patterns generated automatically per app) and
    **Classic**; a default engine choice; a note that ringtones have
    matching Glyph patterns.

On the Phone (1), which has light strips but no matrix, matrix-only tools
(Glyph Toys) are hidden or adapted.

## GlyphLike: Pseudo Glyph (devices without Glyph lights)

Working name: **GlyphLike** (may change).

Idea: on phones with an OLED/AMOLED screen but no Glyph lights, draw
Glyph-style light patterns on the **always-on display** instead.

- OLED pixels that are off use no power, so a few lit dot-matrix shapes on
  a black screen are cheap, which is why AOD works at all.
- Uses:
  - notification patterns when a notification arrives;
  - a persistent small light for Essential Notifications until seen;
  - charging level, Glyph Timer countdown, volume indicator and Glyph
    Progress as dot-matrix bars or rings.
- Not possible: Flip to Glyph (the screen faces down).
- Not for LCD screens, where black pixels still use the backlight. The
  Galaxy Tab S6 Lite has an LCD, so it won't get Pseudo Glyph.

How it fits:

- One Glyph app and one set of patterns, with two outputs: real Glyph
  lights, or the AOD. The app shows whichever the device has.
- AOD is drawn by SystemUI while the screen is "dozing". Constraints to
  design around:
  - the AOD normally only redraws about once a minute, so animations need
    Android's notification "pulse" (a brief brighter wake) or a short
    wake while they play;
  - keep the lit area small for battery;
  - shift positions slightly over time to avoid burn-in.

### Glyph Builder

Users design their own GlyphLike layout instead of getting a fixed one:

- A **library of Glyph elements** to choose from: shapes such as straight
  and curved strips, rings and ring segments, dots and small dot-matrix
  blocks.
- Up to **5 elements** per layout, placed on a **grid** over the
  always-on display to the user's liking (moved, and possibly resized or
  rotated).
- Patterns light the elements **by their slot number (1–5)**, so every
  layout works with every pattern. Elements that can show a level (strips,
  rings) are used for charging, volume, timer and progress.
- Five slots lines up with the Nothing Phone (1), whose lights are grouped
  into five zones. Phone (1) patterns could map straight onto a GlyphLike
  layout, and a preset layout could imitate the Phone (1)'s back.

Decided:

- **Grid:** covers the **whole screen, edge to edge**.
- **Fewer than five elements:** a GlyphLike setting chooses between
  skipping the missing slots and merging them into the nearest element.
- **Burn-in protection:** a GlyphLike setting, **on by default**, that
  shifts the whole layout slightly over time.
- **Future:** sharing layouts as files, so the community can make and swap
  their own.

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

### Mapping to ParanoidGlyph

| NothingOS 5 feature | ParanoidGlyph today |
| --- | --- |
| Essential Notifications | per-app notification patterns; rules and "keep lit until seen" are new |
| Flip to Glyph | yes |
| Volume indicator | yes |
| Glyph Progress | third-party service (Glyph developer kit) |
| Glyph Timer | new |
| Always-on Glyph Toy | matrix only; not on the Phone (1) |
| Sounds / ringtone patterns | ringtone and notification patterns exist; Generative is new |
| Charging, music visualiser | yes (not shown on NothingOS's page; keep them) |
