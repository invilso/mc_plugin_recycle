#!/usr/bin/env python3
"""Auto-increment the project version in pom.xml (format v<number>)."""

from __future__ import annotations

import argparse
import pathlib
import re
import sys
import xml.etree.ElementTree as ET

NAMESPACE = {"m": "http://maven.apache.org/POM/4.0.0"}


def extract_project_version(pom_path: pathlib.Path) -> str:
    tree = ET.parse(pom_path)
    root = tree.getroot()
    version_element = root.find("m:version", NAMESPACE)
    if version_element is None or version_element.text is None:
        raise ValueError("Cannot find project <version> in pom.xml")
    return version_element.text.strip()


def compute_next(version: str) -> str:
    match = re.fullmatch(r"v(\d+)", version)
    if not match:
        raise ValueError(f"Version '{version}' must use v<number> format")
    return f"v{int(match.group(1)) + 1}"


def replace_version(pom_text: str, old: str, new: str) -> str:
    pattern = re.compile(rf"(<version>\s*){re.escape(old)}(\s*</version>)", re.MULTILINE)
    updated, count = pattern.subn(rf"\1{new}\2", pom_text, count=1)
    if count != 1:
        raise ValueError("Failed to update pom.xml version")
    return updated


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("pom", nargs="?", default="pom.xml", help="Path to pom.xml")
    parser.add_argument("--dry-run", action="store_true", help="Print versions without modifying pom.xml")
    args = parser.parse_args()

    pom_path = pathlib.Path(args.pom)
    pom_text = pom_path.read_text(encoding="utf-8")

    current = extract_project_version(pom_path)
    new_version = compute_next(current)

    if args.dry_run:
        print(f"Current version: {current}")
        print(f"Next version:    {new_version}")
        return 0

    updated_text = replace_version(pom_text, current, new_version)
    pom_path.write_text(updated_text, encoding="utf-8")
    print(f"Bumped version: {current} -> {new_version}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
