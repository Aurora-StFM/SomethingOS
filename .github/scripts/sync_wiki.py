#!/usr/bin/env python3
#
# Copyright (C) 2026 The SomethingOS Project
# SPDX-License-Identifier: Apache-2.0
#
# Mirror docs/design/ into the GitHub wiki.
#
# Usage: sync_wiki.py <docs dir> <wiki checkout> <owner/repo> <branch>
#
# - Each Markdown file becomes a wiki page named after its first "# " title
#   (docs/design/README.md becomes the Home page).
# - Links between docs files are rewritten to wiki page links.
# - Images stay in the main repository and are linked by absolute URL, so
#   the wiki doesn't need its own copies.
# - Every page the script doesn't generate is removed, so the wiki always
#   matches the docs exactly. Edit the docs in the repository, not the wiki.

import re
import sys
from pathlib import Path

LINK = re.compile(r"(!?)\[([^\]]*)\]\(([^)\s]+)\)")


def page_name(md: Path) -> str:
    if md.name == "README.md":
        return "Home"
    for line in md.read_text(encoding="utf-8").splitlines():
        if line.startswith("# "):
            title = line[2:].strip()
            return re.sub(r"[^A-Za-z0-9()]+", "-", title).strip("-")
    return md.stem


def main() -> None:
    docs, wiki, repo, branch = Path(sys.argv[1]), Path(sys.argv[2]), sys.argv[3], sys.argv[4]
    pages = {md.name: page_name(md) for md in sorted(docs.glob("*.md"))}
    raw = f"https://github.com/{repo}/raw/{branch}/{docs.as_posix()}"
    source = f"https://github.com/{repo}/tree/{branch}/{docs.as_posix()}"

    def rewrite(match: re.Match) -> str:
        bang, text, target = match.groups()
        if re.match(r"[a-z]+://|#|mailto:", target):
            return match.group(0)
        path, _, anchor = target.partition("#")
        anchor = f"#{anchor}" if anchor else ""
        if bang:
            return f"![{text}]({raw}/{path})"
        if path in pages:
            if text == path:
                text = pages[path].replace("-", " ")
            return f"[{text}]({pages[path]}{anchor})"
        return f"[{text}]({source}/{path}{anchor})"

    # Remove old pages, keep the wiki's git data.
    for old in wiki.glob("*.md"):
        old.unlink()

    for md_name, name in pages.items():
        lines = (docs / md_name).read_text(encoding="utf-8").splitlines()
        # The wiki shows the page name as the title, so drop the first heading.
        if lines and lines[0].startswith("# "):
            lines = lines[1:]
            while lines and not lines[0].strip():
                lines = lines[1:]
        body = LINK.sub(rewrite, "\n".join(lines)) + "\n"
        (wiki / f"{name}.md").write_text(body, encoding="utf-8")

    # Sidebar: pages in the order the docs index (README.md) lists them,
    # then any pages it doesn't mention.
    order = []
    index = docs / "README.md"
    if index.exists():
        for _, _, target in LINK.findall(index.read_text(encoding="utf-8")):
            path = target.partition("#")[0]
            if path in pages and pages[path] not in order and pages[path] != "Home":
                order.append(pages[path])
    order += [n for n in pages.values() if n not in order and n != "Home"]
    sidebar = ["**[Home](Home)**", ""]
    sidebar += [f"- [{name.replace('-', ' ')}]({name})" for name in order]
    (wiki / "_Sidebar.md").write_text("\n".join(sidebar) + "\n", encoding="utf-8")

    (wiki / "_Footer.md").write_text(
        f"This wiki is generated from [`docs/design`]({source}) in the repository. "
        "Edit the files there; changes made directly in the wiki are overwritten.\n",
        encoding="utf-8",
    )


if __name__ == "__main__":
    main()
