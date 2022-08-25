# Changelog

## Pending changes

- [#122](https://github.com/bumble-tech/appyx/pull/122) - **Breaking change**: `ChildEntry.ChildMode` is removed, now nodes are always created when a nav model changes (previously default behaviour)
- [#99](https://github.com/bumble-tech/appyx/pull/99) – **Breaking change**: Removed `IntegrationPointAppyxProvider` and made `ActivityIntegrationPoint`'s constructor private. Use `ActivityIntegrationPoint.createIntegrationPoint`. This uses a weak reference to keep track of the integration points, and will not introduce memory leaks.
- [#122](https://github.com/bumble-tech/appyx/pull/122) - **Added**: New `ChildEntry.KeepMode` that allows to destroy nodes that are currently not visible on the screen
- [#132](https://github.com/bumble-tech/appyx/pull/132) - **Added**: New `NodeComponentActivity` to extend when wanting to work with ComponentActivity as your base activity, eg when migrating from a project built from the Jetpack Compose template
- [#119](https://github.com/bumble-tech/appyx/pull/119) - **Fixed**: Lifecycle observers are invoked in incorrect order (child before parent)
- [#62](https://github.com/bumble-tech/appyx/pull/62) - **Fixed**: Node is marked with stable annotation making some of the composable functions skippable
- [#129](https://github.com/bumble-tech/appyx/pull/129) - **Updated**: Removed sealed interface from operations to allow client to define their own

## 1.0-alpha06 – 26 Aug 2022

- [#96](https://github.com/bumble-tech/appyx/pull/96) – **Breaking change**: Removed `InteractorTestHelper`. Please use Node tests instead of Interactor tests.
- [#99](https://github.com/bumble-tech/appyx/pull/99) – **Breaking change**: Modified package of `NodeConnector` and `Connectable`
- [#99](https://github.com/bumble-tech/appyx/pull/99) – **Added**: Source<T>.rx2() to convert Source<T> to io.reactivex.Observable<T>
- [#107](https://github.com/bumble-tech/appyx/pull/107) – **Fixed**: Back press handlers are not properly registered on lifecycle events

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
