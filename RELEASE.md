# Release Scripts

This directory contains scripts to automate the release process for the Permisso Android SDK.

## Scripts

### 1. `release.sh` (Full-featured)

A comprehensive release script with multiple options and safety checks.

**Usage:**

```bash
./release.sh [OPTIONS] <new_version>
```

**Options:**

- `-h, --help`: Show help message
- `-n, --dry-run`: Show what would be done without making changes
- `--no-commit`: Don't commit the version change
- `--no-push`: Don't push changes to remote
- `--no-tag`: Don't create git tag

**Examples:**

```bash
# Standard release
./release.sh 1.0.2

# Dry run to see what would happen
./release.sh --dry-run 1.0.2

# Release without pushing to remote
./release.sh --no-push 1.0.2

# Just update version, don't create tag
./release.sh --no-tag 1.0.2
```

## Prerequisites

- Git repository with clean working directory
- `gradle.properties` file exists in the current directory
- Proper git configuration (user.name and user.email)

## Version Format

The scripts expect semantic versioning format: `major.minor.patch` (e.g., `1.0.2`)

Optional suffixes are supported: `1.0.2-beta`, `1.0.2-rc1`

## Safety Features

The full `release.sh` script includes:

- Git working directory clean check
- Version format validation
- Existing tag detection
- Backup and restore functionality
- Colored output for better readability
- Comprehensive error handling

## Troubleshooting

**"Git working directory is not clean"**

- Commit or stash your changes before running the release script

**"Tag already exists"**

- The version you're trying to release has already been tagged
- Use a different version number or delete the existing tag if needed

**"gradle.properties not found"**

- Make sure you're running the script from the project root directory
- Ensure the `gradle.properties` file exists

## Manual Release Process

If you prefer to do releases manually:

1. Update `VERSION_NAME` in `gradle.properties`
2. Commit: `git commit -am "Bump version to X.X.X"`
3. Tag: `git tag -a vX.X.X -m "Release version X.X.X"`
4. Push: `git push origin main && git push origin --tags`
