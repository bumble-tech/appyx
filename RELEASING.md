# Releasing

## Automatic

1. Create PR with following changes:
    1. Update version in `gradle/publication.gradle`.
    2. Create a new section with version name taking all changes from pending section.
2. Create GitHub release with a tag name of the version and fill notes from pending changes section.
3. Library is published to staging repository automatically with GitHub Action.
4. Proceed to 'Closing staging repository'.

In case of any issues you can launch Release manually from GitHub Actions tab.

## Manual

Should be used in case if there are issues with automatic publication.

1. Create PR with following changes:
    1. Update version in `gradle/publication.gradle`.
    2. Create a new section with version name taking all changes from pending section.
2. `./gradle publishAppyxReleasePublicationToOSSRHRepository --no-parallel -Psigning.keyId=$KEY_ID -Psigning.password=$PASS -Psigning.secretKeyRingFile=$FILE -Psonatype.username=$NAME -Psonatype.password=$PASS`
    1. `signing` properties are related to signing information.
    2. `sonatype` properties are your username and password from `oss.sonatype.org`.
    3. `--no-parallel` is required to avoid creation of multiple staging repositories.
3. Create GitHub release with a tag name of the version and fill notes from pending changes section.
4. Cancel Release GitHub Action as it will fail now because version is already released.
5. Proceed to 'Closing staging repository'.

## Closing staging repository

1. Open `https://s01.oss.sonatype.org` and sign in with the sonatype credentials.
2. Click `Staging Repositories`.
3. Select the repository (assuming publish succeeded) and click the close button.
4. Select the repository again and click release.
