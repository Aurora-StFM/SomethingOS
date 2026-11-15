#!/usr/bin/env python3
# SPDX-FileCopyrightText: 2026 The SomethingOS Project
# SPDX-License-Identifier: Apache-2.0
"""Point LineageOS's Google Sans families at Geist.

LineageOS's fonts_customization.xml (packages/overlays/Lineage) defines about
40 named families (google-sans..., variable-...) that all use Google Sans
Flex. Settings, SystemUI and the launcher ask for these by name, so a font
overlay alone can't change them. Android also refuses duplicate family names
in this file, so they have to be rewritten in place.

This script rewrites every font in those families to Geist-VF.ttf, keeping
only the weight. Geist has no italic and none of Google Sans Flex's other
axes (opsz, GRAD, wdth, ROND, slnt), so:
  - italic entries are dropped; Android draws a slanted Geist instead;
  - families that only had italic entries get one upright entry;
  - other axes are dropped.

It's idempotent, so rerun it after rebasing the fork on a new LineageOS
release:

    tools/fonts/use_geist.py <path to packages/overlays/Lineage>/fonts/etc/fonts_customization.xml
"""

import re
import sys

FONT_FILE = "Geist-VF.ttf"
SOURCE_FILES = ("GoogleSansFlex-Regular.ttf", FONT_FILE)
MARKER = "SomethingOS: rewritten to Geist by vendor/somethingos/tools/fonts/use_geist.py"

FAMILY_RE = re.compile(
    r'(?P<indent>[ \t]*)(?P<open><family\b[^>]*\bname="(?P<name>(?:google-sans|variable-)[^"]*)"[^>]*>)'
    r'(?P<body>.*?)</family>',
    re.DOTALL,
)
FONT_RE = re.compile(r"<font\b(?P<attrs>[^>]*)>\s*(?P<file>[\w.-]+)(?P<axes>.*?)</font>", re.DOTALL)
AXIS_RE = re.compile(r"<axis\b[^>]*/>")
ATTR_RE = re.compile(r'(\w+)="([^"]*)"')


def font_weight(attrs, axes):
    for axis in AXIS_RE.findall(axes):
        a = dict(ATTR_RE.findall(axis))
        if a.get("tag") == "wght":
            return int(float(a["stylevalue"]))
    return int(attrs.get("weight", 400))


def rewrite_family(m):
    indent, name, body = m.group("indent"), m.group("name"), m.group("body")
    upright, italic = [], []
    for f in FONT_RE.finditer(body):
        if f.group("file") not in SOURCE_FILES:
            raise SystemExit(f"{name}: unexpected font file {f.group('file')}; check the script")
        attrs = dict(ATTR_RE.findall(f.group("attrs")))
        w = font_weight(attrs, f.group("axes"))
        (italic if attrs.get("style") == "italic" else upright).append(w)
    if not upright and not italic:
        raise SystemExit(f"{name}: no fonts found")
    weights = sorted(set(upright or italic))
    inner = indent + "    "
    lines = [f"{indent}<!-- {MARKER} -->", indent + m.group("open")]
    for w in weights:
        lines += [
            f'{inner}<font weight="{w}" style="normal">{FONT_FILE}',
            f'{inner}    <axis tag="wght" stylevalue="{w}"/>',
            f"{inner}</font>",
        ]
    lines.append(indent + "</family>")
    return "\n".join(lines)


def main():
    if len(sys.argv) != 2:
        raise SystemExit(__doc__)
    path = sys.argv[1]
    with open(path, encoding="utf-8") as fh:
        text = fh.read()
    # Drop markers from a previous run so reruns don't stack them.
    text = re.sub(rf"[ \t]*<!-- {re.escape(MARKER)} -->\n", "", text)
    text, count = FAMILY_RE.subn(rewrite_family, text)
    with open(path, "w", encoding="utf-8") as fh:
        fh.write(text)
    print(f"{count} families now use {FONT_FILE}")


if __name__ == "__main__":
    main()
