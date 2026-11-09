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
