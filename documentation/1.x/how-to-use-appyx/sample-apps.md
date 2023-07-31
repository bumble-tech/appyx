{% include-markdown "../deprecation.md" %}

# Appyx sample apps

## Where to find the sample apps

1. Go to the [GitHub project](https://github.com/bumble-tech/appyx)
2. Fork and check out the code locally
3. Import the project to Android Studio

You can find the pre-built sample app apks here:

- [Latest release](https://github.com/bumble-tech/appyx/releases)
- [Latest 1.x](https://github.com/bumble-tech/appyx/actions/runs/${POST_MERGE_RUN_ID}#artifacts)

## Showcase app

The `:app` module showcases **Appyx** itself with multiple levels of navigation, NavModel demos, etc. See it in action, then check the related code how it works.

## Appyx + Jetpack Compose Navigation example 

The `:samples:navigation-compose` module demonstrates how to use Appyx within Google's Jetpack Compose Navigation library.
This example may be useful if you need to migrate to Appyx gradually.

## Appyx + Hilt example

Coming soon!

Meanwhile:

- Our draft PR: [#115](https://github.com/bumble-tech/appyx/pull/115) (Feel free to provide feedback!)
- [https://github.com/jbreitfeller-sfix/appyx-playground](https://github.com/jbreitfeller-sfix/appyx-playground) another approach on this topic
