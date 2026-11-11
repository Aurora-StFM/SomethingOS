# Installation

> [!NOTE]
> **There are no SomethingOS builds to install yet.** This page will get
> per-device instructions once the first builds are released.

> [!WARNING]
> SomethingOS is **not recommended for daily use**. Installing it erases
> your device, needs an unlocked bootloader (which may void your warranty,
> and on Samsung devices permanently trips Knox), and apps that rely on
> Play Integrity, such as banking apps and Google Wallet, may not work.

## What installation will look like

SomethingOS installs the same way as LineageOS, so the steps will follow
each device's LineageOS installation guide:

1. Back up everything on the device.
2. Unlock the bootloader.
3. Flash a recovery (the LineageOS recovery for that device).
4. Factory reset, then install the SomethingOS zip from the recovery.
5. Optionally install Google apps.

## Installing your own build

If you've built SomethingOS yourself (see [Building](Building.md)), install
it with the images **from your own build**, found in
`out/target/product/<codename>/`. Your build is signed with its own test
keys, and only the recovery from the same build trusts them; mixing in
official LineageOS images makes the install fail with "Signature
verification failed".

Tested on the **Nothing Phone (1)**:

1. Make sure the phone runs the latest stock Android 15 update, calls and
   SMS work, and all Google accounts are removed. Back up everything.
2. Install `adb` and `fastboot` on the computer (`sudo dnf install
   android-tools` on Fedora).
3. Enable **OEM unlocking** and **USB debugging** in Developer options,
   then unlock the bootloader (this erases the phone):
   ```bash
   adb -d reboot bootloader
   fastboot flashing unlock
   ```
4. Back in the bootloader (power off, then hold Volume Down + Power), flash
   the build's recovery:
   ```bash
   fastboot flash vendor_boot vendor_boot.img
   fastboot reboot bootloader
   fastboot flash boot boot.img
   ```
5. Boot **Recovery mode**, choose **Factory reset → Format data**, then
   **Apply update → Apply from ADB**, and on the computer:
   ```bash
   adb -d sideload lineage-24.0-*-Spacewar.zip
   ```
   (Stopping at 47% on the computer is normal.) Then **Reboot system now**.
6. Check it's SomethingOS: `adb shell getprop ro.somethingos.version`.

For other devices, follow the same idea using that device's LineageOS
install guide:

- [Nothing Phone (1)](https://wiki.lineageos.org/devices/Spacewar/install/)
- Galaxy Tab S6 Lite 2022: the instructions in the
  [unofficial LineageOS thread on XDA](https://xdaforums.com/t/rom-unofficial-2022-model-lineageos-22-2-for-galaxy-tab-s6-lite-2022-qualcomm.4699445/)
