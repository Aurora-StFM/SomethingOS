#!/usr/bin/env python3
#
# Copyright (C) 2026 The SomethingOS Project
# SPDX-License-Identifier: Apache-2.0
#
# Mirror docs/ into the GitHub wiki.
#
# Usage: sync_wiki.py <docs dir> <wiki checkout> <owner/repo> <branch>
#
# Structure:
# - docs/Home.md is the wiki's Home page. Its links decide the order of the
#   top-level sections in the sidebar.
# - Every other Markdown file becomes a page named after its first "# "
#   title. A folder's README.md is that section's index page (e.g.
#   docs/design/README.md is the "Design" page); the folder's other files
#   are listed under it in the sidebar, in the order the index links them.
# - GitHub wikis are flat (page names must be unique), so the hierarchy is
#   shown through the sidebar and the index pages.
# - Links between docs are rewritten to wiki links; images stay in the main
#   repository and are linked by absolute URL; other repository files link
#   to GitHub.
# - Every page the script doesn't generate is removed, so the wiki always
#   matches the docs. Edit docs/ in the repository, not the wiki.

import posixpath
import re
import sys
from pathlib import Path

LINK = re.compile(r"(!?)\[([^\]]*)\]\(([^)\s]+)\)")


def title_of(md: Path) -> str:
    for line in md.read_text(encoding="utf-8").splitlines():
        if line.startswith("# "):
            return line[2:].strip()
    return md.stem


def slug(title: str) -> str:
    return re.sub(r"[^A-Za-z0-9()]+", "-", title).strip("-")


def main() -> None:
    docs, wiki, repo, branch = Path(sys.argv[1]), Path(sys.argv[2]), sys.argv[3], sys.argv[4]
    root = docs.as_posix().rstrip("/")
    raw = f"https://github.com/{repo}/raw/{branch}"
    blob = f"https://github.com/{repo}/blob/{branch}"

    # Relative path (from docs/) -> wiki page name.
    pages = {}
    for md in sorted(docs.rglob("*.md")):
        rel = md.relative_to(docs).as_posix()
        pages[rel] = "Home" if rel == "Home.md" else slug(title_of(md))
    if len(set(pages.values())) != len(pages):
        sys.exit("Two docs share the same title; wiki page names must be unique.")

    def resolve(src_rel: str, target: str) -> str:
        """Path of a link target relative to docs/, normalised."""
        return posixpath.normpath(posixpath.join(posixpath.dirname(src_rel), target))

    def links_in(rel: str):
        """Doc pages linked from a doc, in order, without duplicates."""
        seen = []
        text = (docs / rel).read_text(encoding="utf-8")
        for bang, _, target in LINK.findall(text):
            if bang or re.match(r"[a-z]+://|#|mailto:", target):
                continue
            path = resolve(rel, target.partition("#")[0])
            if path in pages and path not in seen and path != rel:
                seen.append(path)
        return seen

    def convert(rel: str) -> str:
        lines = (docs / rel).read_text(encoding="utf-8").splitlines()
        # The wiki shows the page name as the title, so drop the first heading.
        if lines and lines[0].startswith("# "):
            lines = lines[1:]
            while lines and not lines[0].strip():
                lines = lines[1:]

        def rewrite(match: re.Match) -> str:
            bang, text, target = match.groups()
            if re.match(r"[a-z]+://|#|mailto:", target):
                return match.group(0)
            path, _, anchor = target.partition("#")
            anchor = f"#{anchor}" if anchor else ""
            full = resolve(rel, path)
            if bang:
                return f"![{text}]({raw}/{root}/{full})"
            if full in pages:
                if text == path or text == posixpath.basename(path):
                    text = pages[full].replace("-", " ")
                return f"[{text}]({pages[full]}{anchor})"
            # Anything else in the repository (README, manifests, ...).
            return f"[{text}]({blob}/{posixpath.normpath(posixpath.join(root, full))}{anchor})"

        return LINK.sub(rewrite, "\n".join(lines)) + "\n"

    # Remove old pages, keep the wiki's git data.
    for old in wiki.glob("*.md"):
        old.unlink()
    for rel, name in pages.items():
        (wiki / f"{name}.md").write_text(convert(rel), encoding="utf-8")

    # Sidebar: Home, then top-level sections in the order Home links them,
    # each folder index followed by its pages.
    def section_children(index_rel: str):
        folder = posixpath.dirname(index_rel)
        children = [p for p in links_in(index_rel) if posixpath.dirname(p) == folder]
        children += [p for p in pages if posixpath.dirname(p) == folder
                     and p != index_rel and p not in children]
        return children

    def label(rel: str) -> str:
        return pages[rel].replace("-", " ")

    top = links_in("Home.md") if "Home.md" in pages else []
    top += [p for p in pages if p not in top and p != "Home.md"
            and ("/" not in p or p.endswith("/README.md"))]
    sidebar = ["**[Home](Home)**", ""]
    for rel in top:
        if "/" in rel and not rel.endswith("/README.md"):
            continue  # nested pages are listed under their section
        sidebar.append(f"- [{label(rel)}]({pages[rel]})")
        if rel.endswith("/README.md"):
            sidebar += [f"  - [{label(c)}]({pages[c]})" for c in section_children(rel)]
    (wiki / "_Sidebar.md").write_text("\n".join(sidebar) + "\n", encoding="utf-8")

    (wiki / "_Footer.md").write_text(
        f"This wiki is generated from [`{root}/`](https://github.com/{repo}/tree/{branch}/{root}) "
        "in the repository. Edit the files there; changes made directly in the "
        "wiki are overwritten.\n",
        encoding="utf-8",
    )


if __name__ == "__main__":
    main()
