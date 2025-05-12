# Releasing

## Automatic

1. Create a PR with the following changes:
    1. Update the version in `gradle.properties`.
    2. Create a new section with the version name, including all changes from the pending section.
2. Create a GitHub release with a tag name matching the version and fill in the release notes using the pending changes section.
3. The library will be published to the staging repository automatically via GitHub Actions.
4. [Proceed to closing the staging repository](#closing-staging-repository).

If any issues arise, you can manually launch the release from the GitHub Actions tab.

## Manual

This process should be used if there are issues with automatic publication.

1. Create a PR with the following changes:
    1. Update the version in `gradle.properties`.
    2. Create a new section with the version name, including all changes from the pending section.
2. Refer to [.github/workflows/release.yml](.github/workflows/release.yml) for the manual release process.
    - Check the [vanniktech.github.io plugin documentation](https://vanniktech.github.io/gradle-maven-publish-plugin/central/#secrets) to learn how to use Gradle properties instead of environment variables.
3. Create a GitHub release with the version's tag name and fill in the release notes using the pending changes section.
4. Cancel the `Release` GitHub Action, as it will fail because the version has already been released.
5. [Proceed to closing the staging repository](#closing-staging-repository).

## Closing the Staging Repository

1. Open [Sonatype Central](https://central.sonatype.com/publishing) and sign in with the Sonatype credentials.
2. Click `Deployments` and then `Publish` once validated.

For more information, refer to [the official documentation](https://central.sonatype.org/publish/publish-portal-guide/#component-validation).
