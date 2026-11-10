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

## Phone (1) reference: NothingOS 3.2

The Phone (1) never got NothingOS 5's Glyph app; its last Glyph settings
(NothingOS 3.2) look different:

- **Title** "Glyph Interface".
- **Top:** a card with an outline of the Phone (1)'s back and a brightness
  slider, beside two cards: **Ringtones** (current ringtone) and
  **Notifications** (current sound, or off).
- **Glyph lights** master toggle (wide pill).
- **Features:** **Flip to Glyph** ("Silence on demand", toggle) and
  **Composer** ("Create your own ringtones", a separate app).
- **Visual feedback:** **Charging meter** ("Wiggle for progress"),
  **Google Assistant** ("Visual response"), **Music visualisation** ("Give
  life to the party"; its own page with a preview animation, a toggle and
  an explanation), each a card with a toggle.
- **Turn off automatically:** **Bedtime schedule** (e.g. 22:00–07:00 on
  weekdays).
- Epilepsy and light-sensitivity warning.
- **Ringtone / notification picker:** sounds grouped into collections,
  swiped between. Each collection is a horizontal carousel of Phone (1)
  back previews that play the sound's light pattern, with the sound's name
  and position ("3/10") below, a **Sound only** toggle ("Glyph lights will
  be disabled") and **Save** at the top right.

### SomethingOS on the Phone (1)

The SomethingOS Glyph app uses the NothingOS 5 layout on every device, with
the sections filled from what the device supports. On the Phone (1):

| NothingOS 3.2 (Phone (1)) | SomethingOS Glyph app |
| --- | --- |
| Glyph lights toggle, brightness | hero and brightness at the top |
| Ringtones, Notifications pickers | Sounds tab, with the carousel picker and Sound only toggle |
| Flip to Glyph | Triggers |
| Charging meter, Music visualisation | Triggers (both already in ParanoidGlyph) |
| Google Assistant visual response | Triggers, as a generic voice assistant response, shown only when an assistant is installed |
| Bedtime schedule | a "Turn off automatically" option, ideally tied to Android's Bedtime mode instead of its own schedule |
| Composer | later; a separate tool for making patterns, possibly shared with the Glyph Builder |
| Essential Notifications, Glyph Timer, Volume indicator | added from NothingOS 5 (Timer and Essential Notifications are new work) |
| Glyph Toys | hidden: no matrix |

Sounds: the picker can only offer ringtones and patterns we're allowed to
ship (those LineageOS already includes, or our own). Nothing's ringtone
collections are not copied.

**SomethingOS sound set:** custom ringtones, notification sounds and system
sounds in the same spirit as Nothing's (short, synthetic, minimal), each
with a matching Glyph pattern.

- Original compositions in that style, not recreations: new melodies and
  rhythms, no sampling or tracing of Nothing's sounds, and our own names.
  A melody is protected even when re-recorded, so "close enough" should
  mean the same feel, not the same tune.
- Glyph patterns are made for each sound (later with the Composer).
- Released under an open licence alongside the rest of SomethingOS.

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
  and curved strips, rings and ring segments, dots, and a **Glyph Matrix**
  (below).
- Up to **5 elements** per layout, placed on a **grid** over the
  always-on display to the user's liking (moved, and possibly resized or
  rotated).
- Patterns light the elements **by their slot number (1–5)**, so every
  layout works with every pattern. Elements that can show a level (strips,
  rings) are used for charging, volume, timer and progress.
- Five slots lines up with the Nothing Phone (1), whose lights are grouped
  into five zones. Phone (1) patterns could map straight onto a GlyphLike
  layout, and a preset layout could imitate the Phone (1)'s back.

#### Glyph Matrix element

A replica of the **Nothing Phone (4a) Pro's Glyph Matrix**: a circular
matrix of 137 dots within a 13×13 grid.

- Placed like other elements but **relatively large: 2×2 or 3×3 cells**,
  and drawn **dim**.
- The 4a Pro version is the model rather than the Phone (3)'s (25×25)
  because the 4a Pro has no Glyph button: its Glyph Toys are AOD-only,
  which is exactly what GlyphLike is.
- Shows Glyph Toys (clock, battery, timer and so on), and richer
  notifications such as app icons.
- Most demanding element for battery and burn-in; the shift setting matters
  most here.

#### Third-party Glyph Toys on the matrix (future)

Goal: third-party Glyph Toys made for the 4a Pro run on the GlyphLike
matrix. From Nothing's public
[GlyphMatrix Developer Kit](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit)
documentation:

- A Glyph Toy is a service in its app with the intent action
  `com.nothing.glyph.TOY` and metadata (name, preview image, summary,
  `aod_support`). The system finds toys by that action, binds the one the
  user picks, and sends it events; 4a Pro toys must declare AOD support.
- Toys and apps draw by sending frames through the SDK to a system service
  in the package `com.nothing.thirdparty`, after registering for a device
  (`Glyph.DEVICE_25111p` for the 4a Pro, 13×13 frames).

Work needed:

1. **A system service that stands in for Nothing's**, answering the SDK's
   connection and receiving frames, then drawing them on the GlyphLike
   matrix. LineageOS's ParanoidGlyph already does this for Nothing's older
   Glyph Developer Kit on the Phone (1), so there's a precedent and possibly
   code to reuse.
2. **Toy management** in our Glyph app: list installed toys, let the user
   pick, bind the chosen one and send it AOD events.
3. **Device detection:** the SDK must believe it's on a 4a Pro. This should
   be answered only to the SDK's checks, never by changing the phone's
   model system-wide (that breaks other apps and updates). How the SDK
   detects the device isn't documented, so it has to be found by testing.

Rules for this work: build only from the public documentation and observed
behaviour. Nothing's SDK licence forbids decompiling or reverse-engineering
it, and we don't ship the SDK; third-party apps bring their own copy.

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
