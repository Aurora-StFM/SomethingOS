#
# Copyright (C) 2026 The SomethingOS Project
# SPDX-License-Identifier: Apache-2.0
#
# SomethingOS common configuration.
#
# Every SomethingOS product inherits this file on top of its device's
# LineageOS product config. Keep it device-agnostic: nothing Qualcomm-,
# Samsung- or arm64-specific belongs here. Device quirks go in the
# per-device product makefile under products/.
#

# Versioning
include vendor/somethingos/config/version.mk

# Overlays
PRODUCT_PACKAGES += \
    SomethingFrameworkOverlay

# Fonts (vendor/somethingos/fonts). Registered as font families in
# fonts_customization.xml, which lives in our fork of packages/overlays/Lineage
# (see manifests/somethingos.xml):
#   somethingos-dot     Doto (dot-matrix)
#   somethingos-sans    Geist (system UI font)
#   somethingos-serif   Old Standard TT
PRODUCT_PACKAGES += \
    Doto-VF.ttf \
    Geist-VF.ttf \
    OldStandard-Regular.ttf \
    OldStandard-Italic.ttf \
    OldStandard-Bold.ttf

# System UI font: Geist by default; FontRobotoOverlay switches to Roboto
PRODUCT_PACKAGES += \
    FontGeistOverlay \
    FontRobotoOverlay
