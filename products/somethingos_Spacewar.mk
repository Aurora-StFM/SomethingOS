#
# Copyright (C) 2026 The SomethingOS Project
# SPDX-License-Identifier: Apache-2.0
#
# Nothing Phone (1), model A063
# Device sources: official LineageOS (https://github.com/LineageOS)
#

SOMETHINGOS_DEVICE := Spacewar

# The device's LineageOS product config (hardware, kernel, vendor blobs)
$(call inherit-product, device/nothing/$(SOMETHINGOS_DEVICE)/lineage_$(SOMETHINGOS_DEVICE).mk)

# SomethingOS layer on top
$(call inherit-product, vendor/somethingos/config/common.mk)

PRODUCT_NAME := somethingos_$(SOMETHINGOS_DEVICE)
