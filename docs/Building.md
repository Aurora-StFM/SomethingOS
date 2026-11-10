# Building

How to build SomethingOS from source. SomethingOS is built exactly like
LineageOS 24.0, with SomethingOS's own manifests and lunch targets on top.

The steps below assume a **Fedora** host and build inside an **Ubuntu
container** with Distrobox. LineageOS's build instructions and package
lists assume Ubuntu, so the container saves translating them to Fedora. On
an Ubuntu host, skip step 1 and run everything directly.

## What you need

- **Disk:** about **400 GB free**: roughly 250–300 GB for the source and
  one device's build output, plus 50–100 GB for the compiler cache. An SSD
  makes a big difference.
- **RAM:** **32 GB** at minimum; more is better. With 32 GB, also have
  swap or zram (Fedora enables zram by default).
- **CPU:** any modern multi-core CPU. The first build takes a few hours;
  later builds are much faster thanks to the cache.
- **Internet:** the first sync downloads around 100 GB.

Check what you have:

```bash
df -h ~       # free disk space where the source will live
free -h       # RAM and swap
nproc         # CPU threads
```

## 1. Create the build container (Fedora)

```bash
sudo dnf install distrobox podman
distrobox create --name lineage --image ubuntu:24.04
distrobox enter lineage
```

Distrobox shares your home folder with the container, so the source tree
lives in your normal home and is visible from Fedora too. Everything from
here on runs **inside the container**.

## 2. Install the build tools

```bash
sudo apt update
sudo apt install bc bison build-essential ccache curl flex g++-multilib \
  gcc-multilib git git-lfs gnupg gperf imagemagick protobuf-compiler \
  python3-protobuf lib32readline-dev lib32z1-dev libdw-dev libelf-dev lz4 \
  libsdl1.2-dev libssl-dev libxml2 libxml2-utils lzop pngcrush rsync \
  schedtool squashfs-tools xsltproc zip zlib1g-dev python-is-python3

mkdir -p ~/bin
curl https://storage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
chmod a+x ~/bin/repo
```

`repo` is Google's tool for managing the hundreds of git repositories an
Android tree is made of. Make sure `~/bin` is on your `PATH` (log out of
the container and back in if `repo` isn't found).

Set your git identity (used for the commits `repo` makes locally):

```bash
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
git lfs install
```

## 3. Turn on the compiler cache

```bash
echo 'export USE_CCACHE=1' >> ~/.bashrc
echo 'export CCACHE_EXEC=/usr/bin/ccache' >> ~/.bashrc
source ~/.bashrc
ccache -M 50G
```

ccache keeps compiled results, so rebuilding after small changes takes
minutes instead of hours.

## 4. Get the source

```bash
mkdir -p ~/android/somethingos && cd ~/android/somethingos
repo init -u https://github.com/LineageOS/android.git -b lineage-24.0 --git-lfs --no-clone-bundle
mkdir -p .repo/local_manifests

# Add SomethingOS and your device's trees
BASE=https://raw.githubusercontent.com/Aurora-StFM/SomethingOS/somethingos-24.0/manifests
DEVICE=Spacewar        # or gta4xlveu
curl -o .repo/local_manifests/somethingos.xml "$BASE/somethingos.xml"
curl -o ".repo/local_manifests/$DEVICE.xml" "$BASE/$DEVICE.xml"

repo sync -c -j4 --no-clone-bundle
```

- `repo init` downloads the list of repositories LineageOS 24.0 consists
  of.
- The two files in `.repo/local_manifests` add SomethingOS (checked out at
  `vendor/somethingos`) and your device's device, kernel and vendor trees.
- `repo sync` downloads everything. This takes hours the first time. If it
  fails partway, just run it again; it carries on where it stopped.
- Keep `-j` low (around 4). Google's Android source server rate-limits
  anonymous downloads; with more parallel downloads the sync fails with
  `HTTP 429` / `RESOURCE_EXHAUSTED` errors. If that happens, wait 10–15
  minutes and run `repo sync` again. If it keeps happening, sign in at
  <https://android.googlesource.com/new-password> and run the command it
  shows (it saves a login cookie to `~/.gitcookies`), which raises the
  limit.

Set `DEVICE` to the codename from the
[supported devices table](../README.md#supported-devices).

## 5. Build

```bash
cd ~/android/somethingos
source build/envsetup.sh
source vendor/lineage/vars/aosp_target_release
lunch somethingos_$DEVICE-$aosp_target_release-userdebug
export LINEAGE_BUILD=$DEVICE
m bacon
```

- `envsetup.sh` adds the build commands (`lunch`, `m` and so on) to your
  shell.
- `aosp_target_release` is LineageOS's Android release name (`cp2a` for
  24.0); reading it from the file means the command keeps working when it
  changes.
- `lunch` selects what to build: the SomethingOS product for your device,
  `userdebug` (a normal build with debugging tools).
- `LINEAGE_BUILD` tells LineageOS's build which device this is. LineageOS
  only sets it automatically for products named `lineage_<device>`;
  without it, the output zip's name and some helper commands leave the
  device out.
- `m bacon` builds the flashable zip.

The result is in `out/target/product/$DEVICE/`: a `lineage-24.0-…-UNOFFICIAL-$DEVICE.zip`
plus the boot and recovery images needed to install it. (The zip is still
named after LineageOS for now.)

## Device notes

- **Nothing Phone (1)** (`Spacewar`): official LineageOS device; the most
  likely to build and boot first.
- **Galaxy Tab S6 Lite 2022** (`gta4xlveu`): the device, kernel and vendor
  trees come from the [gta4xlve-dev](https://github.com/gta4xlve-dev)
  project, the same sources as the unofficial LineageOS build. Its 24.0
  sources are unreleased work in progress. One build covers both the Wi-Fi
  (SM-P613) and LTE (SM-P619) models.
