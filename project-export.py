import os
from pathlib import Path

OUTPUT_FILE = "PROJECT_CONTEXT_FOR_AI.txt"

ALLOWED_EXTENSIONS = {
    ".java",
    ".kt",
    ".xml",
    ".yml",
    ".yaml",
    ".properties",
    ".sql",
    ".md",
    ".txt",
    ".json",
    ".sh",
    ".bat",
    ".csv"
}

IGNORED_DIRS = {
    ".idea",
    ".gradle",
    ".settings",
    ".metadata",
    ".vscode",
    "target",
    "build",
    "dist",
    "out",
    "bin",
    "node_modules"
}


def build_tree(root_dir):
    lines = []

    for root, dirs, files in os.walk(root_dir):
        dirs[:] = sorted(
            [d for d in dirs if d not in IGNORED_DIRS]
        )

        level = root.replace(root_dir, "").count(os.sep)

        indent = "    " * level
        lines.append(f"{indent}{os.path.basename(root)}/")

        sub_indent = "    " * (level + 1)

        for file in sorted(files):
            lines.append(f"{sub_indent}{file}")

    return "\n".join(lines)


def should_include(filepath):
    filename = Path(filepath).name
    extension = Path(filepath).suffix.lower()

    important_files = {
        "pom.xml",
        "Dockerfile",
        "docker-compose.yml",
        "docker-compose.yaml",
        "README.md",
        ".gitignore",
        ".gitattributes",
        "lombok.config",
        "sonar-project.properties"
    }

    if filename in important_files:
        return True

    return extension in ALLOWED_EXTENSIONS


def detect_category(rel_path):
    path = rel_path.replace("\\", "/")

    if path.startswith(".github/workflows"):
        return "CI_CD_PIPELINE"

    if path.startswith("src/main/java"):
        return "JAVA_SOURCE"

    if path.startswith("src/test/java"):
        return "TEST_SOURCE"

    if path.startswith("src/main/resources"):
        return "APPLICATION_CONFIGURATION"

    if "adr" in path.lower():
        return "ADR"

    if "docs" in path.lower():
        return "DOCUMENTATION"

    if path.endswith("pom.xml"):
        return "MAVEN"

    if "docker" in path.lower():
        return "DOCKER"

    return "OTHER"


def export_project(root_dir="."):
    root_dir = os.path.abspath(root_dir)

    files_exported = 0

    with open(OUTPUT_FILE, "w", encoding="utf-8") as out:

        out.write("=" * 120 + "\n")
        out.write("COMPLETE PROJECT CONTEXT FOR AI\n")
        out.write("=" * 120 + "\n\n")

        out.write(f"ROOT_FOLDER: {root_dir}\n\n")

        out.write("=" * 120 + "\n")
        out.write("PROJECT_STRUCTURE\n")
        out.write("=" * 120 + "\n\n")

        out.write(build_tree(root_dir))
        out.write("\n\n")

        for root, dirs, files in os.walk(root_dir):

            dirs[:] = [
                d for d in dirs
                if d not in IGNORED_DIRS
            ]

            for file in sorted(files):

                if file == OUTPUT_FILE:
                    continue

                filepath = os.path.join(root, file)

                if not should_include(filepath):
                    continue

                rel_path = os.path.relpath(filepath, root_dir)

                try:
                    with open(
                        filepath,
                        "r",
                        encoding="utf-8",
                        errors="ignore"
                    ) as f:

                        content = f.read()

                    out.write("\n\n")
                    out.write("=" * 120 + "\n")
                    out.write(f"START_FILE: {rel_path}\n")
                    out.write(f"CATEGORY: {detect_category(rel_path)}\n")
                    out.write("=" * 120 + "\n\n")

                    out.write(content)

                    out.write("\n\n")
                    out.write("=" * 120 + "\n")
                    out.write(f"END_FILE: {rel_path}\n")
                    out.write("=" * 120 + "\n")

                    files_exported += 1

                except Exception as e:

                    out.write("\n\n")
                    out.write("=" * 120 + "\n")
                    out.write(f"ERROR_READING_FILE: {rel_path}\n")
                    out.write(str(e))
                    out.write("\n")
                    out.write("=" * 120 + "\n")

    print("=" * 80)
    print("EXPORT COMPLETED")
    print("=" * 80)
    print(f"FILES EXPORTED: {files_exported}")
    print(f"OUTPUT: {OUTPUT_FILE}")


if __name__ == "__main__":
    export_project(".")