#
# Copyright (C) 2026 The SomethingOS Project
# SPDX-License-Identifier: Apache-2.0
#
# Samsung Galaxy Tab S6 Lite 2022 (Qualcomm, SM-P613/SM-P619)
#
# TODO: confirm the codename against the device tree used by the
# unofficial LineageOS 22.2 build. If it differs from "gta4xlve",
# rename this file and update SOMETHINGOS_DEVICE below plus the entries
# in AndroidProducts.mk.
#

SOMETHINGOS_DEVICE := gta4xlve

# The device's LineageOS product config (hardware, kernel, vendor blobs)
$(call inherit-product, device/samsung/$(SOMETHINGOS_DEVICE)/lineage_$(SOMETHINGOS_DEVICE).mk)

# SomethingOS layer on top
$(call inherit-product, vendor/somethingos/config/common.mk)

PRODUCT_NAME := somethingos_$(SOMETHINGOS_DEVICE)
