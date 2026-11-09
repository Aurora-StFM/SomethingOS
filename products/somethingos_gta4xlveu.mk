#
# Copyright (C) 2026 The SomethingOS Project
# SPDX-License-Identifier: Apache-2.0
#
# Samsung Galaxy Tab S6 Lite 2022 (Qualcomm, SM-P613/SM-P619)
# Device sources: https://github.com/gta4xlve-dev
#

SOMETHINGOS_DEVICE := gta4xlveu

# The device's LineageOS product config (hardware, kernel, vendor blobs)
$(call inherit-product, device/samsung/$(SOMETHINGOS_DEVICE)/lineage_$(SOMETHINGOS_DEVICE).mk)

# SomethingOS layer on top
$(call inherit-product, vendor/somethingos/config/common.mk)

PRODUCT_NAME := somethingos_$(SOMETHINGOS_DEVICE)
