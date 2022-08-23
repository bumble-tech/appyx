# Changelog

## Pending changes

- [#96](https://github.com/bumble-tech/appyx/pull/96) – **Breaking change**: Removed `InteractorTestHelper`. Please use Node tests instead of Interactor tests.

## 1.0-alpha05 – 19 Aug 2022

- [#83](https://github.com/bumble-tech/appyx/issues/83) – **Breaking change**: `RoutingSource` renamed to `NavModel`. All subclasses, fields, package names, etc., any mentions of the word follow suit.
- [#91](https://github.com/bumble-tech/appyx/pull/91) – **Fixed**: Spotlight next and previous operations crash fix 

## 1.0-alpha04 – 18 Aug 2022

- [#39](https://github.com/bumble-tech/appyx/pull/39) – **Added**: Workflows implementation to support deeplinks
- [#32](https://github.com/bumble-tech/appyx/pull/32) – **Added**: `BackPressHandler` plugin that allows to control back press behaviour via `androidx.activity.OnBackPressedCallback`
- [#59](https://github.com/bumble-tech/appyx/issues/59) – **Added**: interface for `ParentNodeView<>`
- [#32](https://github.com/bumble-tech/appyx/issues/69) – **Added**: Jetpack Compose Navigation code sample
- [#81](https://github.com/bumble-tech/appyx/issues/81) – **Added**: Support integration point for multiple roots
- [#65](https://github.com/bumble-tech/appyx/pull/65) – **Added**: `InteropBuilderStub` and `InteropSimpleBuilderStub` testing util classes
- [#47](https://github.com/bumble-tech/appyx/issues/47) – **Updated**: The `customisations` module is now pure Java/Kotlin.
- [#85](https://github.com/bumble-tech/appyx/issues/85) – **Updated**: Improved `InteropView` error messaging when `Activity` does not implement `IntegrationPointAppyxProvider`
- [#88](https://github.com/bumble-tech/appyx/issues/88) – **Updated**: Moved `TestUpNavigationHandler` to `testing-unit-common`


## 1.0-alpha03 – 2 Aug 2022

- [#38](https://github.com/bumble-tech/appyx/pull/38) – **Added**: JUnit5 support


## 1.0-alpha02 – 19 Jul 2022

- [#19](https://github.com/bumble-tech/appyx/pull/19) – **Fixed**: Do not allow setting `Node.integrationPoint` on non-root nodes
- [#23](https://github.com/bumble-tech/appyx/pull/21) – **Fixed**: Integration point attached twice crash when using live literals
- [#14](https://github.com/bumble-tech/appyx/issues/14) – **Fixed**: Transition interruptions bug
- [#23](https://github.com/bumble-tech/appyx/pull/23) – **Added**: Unit test support
- [#26](https://github.com/bumble-tech/appyx/issues/26) – **Added**: Publish snapshot versions
- [#9](https://github.com/bumble-tech/appyx/pull/9) – **Migrated**: [app-tree-utils](https://github.com/badoo/app-tree-utils) into this repository


## 1.0-alpha01 – 4 July

- Initial release
