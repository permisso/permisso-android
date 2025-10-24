#!/bin/bash

# Permisso Android Release Script
# This script automates version updates and git tagging for releases

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS] <new_version>"
    echo ""
    echo "Arguments:"
    echo "  new_version    The new version number (e.g., 1.0.2, 2.1.0)"
    echo ""
    echo "Options:"
    echo "  -h, --help     Show this help message"
    echo "  -n, --dry-run  Show what would be done without making changes"
    echo "  --no-commit    Don't commit the version change"
    echo "  --no-push      Don't push changes to remote"
    echo "  --no-tag       Don't create git tag"
    echo ""
    echo "Examples:"
    echo "  $0 1.0.2                    # Release version 1.0.2"
    echo "  $0 --dry-run 1.0.2          # See what would happen"
    echo "  $0 --no-push 1.0.2          # Don't push to remote"
}

# Function to validate version format
validate_version() {
    local version=$1
    if [[ ! $version =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9]+)?$ ]]; then
        print_error "Invalid version format: $version"
        print_error "Version should be in format: major.minor.patch (e.g., 1.0.2) or major.minor.patch-suffix (e.g., 1.0.2-beta)"
        return 1
    fi
}

# Function to get current version from gradle.properties
get_current_version() {
    grep "^VERSION_NAME=" gradle.properties | cut -d'=' -f2
}

# Function to update version in gradle.properties
update_version() {
    local new_version=$1
    local current_version=$(get_current_version)

    print_info "Updating version from $current_version to $new_version"

    if [[ "$DRY_RUN" == "true" ]]; then
        print_warning "[DRY RUN] Would update VERSION_NAME in gradle.properties"
        return 0
    fi

    # Create backup
    cp gradle.properties gradle.properties.backup

    # Update version
    sed -i.tmp "s/^VERSION_NAME=.*/VERSION_NAME=$new_version/" gradle.properties
    rm gradle.properties.tmp

    print_success "Version updated in gradle.properties"
}

# Function to check if git working directory is clean
check_git_status() {
    if [[ -n $(git status --porcelain) ]]; then
        print_error "Git working directory is not clean. Please commit or stash your changes first."
        git status --short
        return 1
    fi
}

# Function to create git tag
create_git_tag() {
    local version=$1
    local tag_name="v$version"

    print_info "Creating git tag: $tag_name"

    if [[ "$DRY_RUN" == "true" ]]; then
        print_warning "[DRY RUN] Would create git tag: $tag_name"
        return 0
    fi

    if [[ "$NO_TAG" == "true" ]]; then
        print_warning "Skipping git tag creation (--no-tag specified)"
        return 0
    fi

    # Check if tag already exists
    if git tag -l | grep -q "^$tag_name$"; then
        print_error "Tag $tag_name already exists!"
        return 1
    fi

    git tag -a "$tag_name" -m "Release version $version"
    print_success "Created git tag: $tag_name"
}

# Function to commit changes
commit_changes() {
    local version=$1

    if [[ "$DRY_RUN" == "true" ]]; then
        print_warning "[DRY RUN] Would commit version change"
        return 0
    fi

    if [[ "$NO_COMMIT" == "true" ]]; then
        print_warning "Skipping commit (--no-commit specified)"
        return 0
    fi

    print_info "Committing version change"
    git add gradle.properties
    git commit -m "Bump version to $version"
    print_success "Committed version change"
}

# Function to push changes
push_changes() {
    if [[ "$DRY_RUN" == "true" ]]; then
        print_warning "[DRY RUN] Would push changes and tags to remote"
        return 0
    fi

    if [[ "$NO_PUSH" == "true" ]]; then
        print_warning "Skipping push (--no-push specified)"
        return 0
    fi

    print_info "Pushing changes to remote"
    git push origin $(git branch --show-current)

    if [[ "$NO_TAG" != "true" ]]; then
        git push origin --tags
    fi

    print_success "Pushed changes to remote"
}

# Function to restore backup on error
restore_backup() {
    if [[ -f gradle.properties.backup ]]; then
        print_warning "Restoring gradle.properties from backup"
        mv gradle.properties.backup gradle.properties
    fi
}

# Main function
main() {
    # Parse command line arguments
    DRY_RUN=false
    NO_COMMIT=false
    NO_PUSH=false
    NO_TAG=false
    NEW_VERSION=""

    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_usage
                exit 0
                ;;
            -n|--dry-run)
                DRY_RUN=true
                shift
                ;;
            --no-commit)
                NO_COMMIT=true
                shift
                ;;
            --no-push)
                NO_PUSH=true
                shift
                ;;
            --no-tag)
                NO_TAG=true
                shift
                ;;
            -*)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
            *)
                if [[ -z "$NEW_VERSION" ]]; then
                    NEW_VERSION=$1
                else
                    print_error "Multiple version arguments provided"
                    show_usage
                    exit 1
                fi
                shift
                ;;
        esac
    done

    # Check if version was provided
    if [[ -z "$NEW_VERSION" ]]; then
        print_error "No version specified"
        show_usage
        exit 1
    fi

    # Validate version format
    validate_version "$NEW_VERSION" || exit 1

    # Check if we're in a git repository
    if ! git rev-parse --git-dir > /dev/null 2>&1; then
        print_error "Not in a git repository"
        exit 1
    fi

    # Check if gradle.properties exists
    if [[ ! -f gradle.properties ]]; then
        print_error "gradle.properties not found in current directory"
        exit 1
    fi

    # Get current version
    CURRENT_VERSION=$(get_current_version)

    print_info "Current version: $CURRENT_VERSION"
    print_info "New version: $NEW_VERSION"

    # Check if new version is different
    if [[ "$CURRENT_VERSION" == "$NEW_VERSION" ]]; then
        print_warning "New version is the same as current version"
        read -p "Do you want to continue? [y/N] " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_info "Cancelled"
            exit 0
        fi
    fi

    # Check git status (only if we're going to make commits)
    if [[ "$DRY_RUN" != "true" && "$NO_COMMIT" != "true" ]]; then
        check_git_status || exit 1
    fi

    # Set up error handling
    trap restore_backup ERR

    print_info "Starting release process for version $NEW_VERSION"

    # Update version
    update_version "$NEW_VERSION"

    # Commit changes
    commit_changes "$NEW_VERSION"

    # Create git tag
    create_git_tag "$NEW_VERSION"

    # Push changes
    push_changes

    # Clean up backup
    if [[ -f gradle.properties.backup ]]; then
        rm gradle.properties.backup
    fi

    print_success "Release $NEW_VERSION completed successfully! 🎉"

    if [[ "$DRY_RUN" != "true" ]]; then
        echo ""
        print_info "Next steps:"
        echo "  • Verify the release in your repository"
        echo "  • Create a GitHub release if needed"
        echo "  • Update documentation if necessary"
    fi
}

# Run main function
main "$@"
