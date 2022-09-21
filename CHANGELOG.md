# Changelog


## Pending changes

- [#151](https://github.com/bumble-tech/appyx/issues/151) - **Breaking change**: Renamed `Routing` to `NavTarget`. All related namings are affected (`RoutingElement`, `RoutingKey`, etc.)
- [#151](https://github.com/bumble-tech/appyx/issues/158) - **Breaking change**: Renamed `TransitionState` to `State` in all NavModel impls. Renamed `STASHED_IN_BACK_STACK` to `STASHED`.
- [#146](https://github.com/bumble-tech/appyx/issues/146) - **Breaking change**: Removed `FragmentIntegrationPoint`. Clients should use `ActivityIntegrationPoint.getIntegrationPoint(context: Context)` to get integration point from Fragment 
- [#146](https://github.com/bumble-tech/appyx/issues/160) - **Breaking change**: Renamed `navmodel-addons` to `navmodel-samples` and stopped publishing the binary. If you feel we should add any of the samples to the main codebase, please let us know! 
- [#138](https://github.com/bumble-tech/appyx/pull/138) - **Fixed**: `androidx.appcompat:appcompat` from is exposed via `api` within `com.bumble.appyx:core`. This prevents potential compilation bugs.
- [#143](https://github.com/bumble-tech/appyx/issues/143) - **Fixed**: Correctly exposed transitive dependencies that are part of the libraries ABI
- [#145](https://github.com/bumble-tech/appyx/issues/145) - **Updated**: `SpotlightSlider` now uses offset modifier with lambda
- [#159](https://github.com/bumble-tech/appyx/pull/159) - **Added**: `NodeHost` now takes modifier parameter to decorate the view of a root node

---

## 1.0-alpha08

- [#140](https://github.com/bumble-tech/appyx/issues/140) - **Breaking change**: Added `testing-ui-activity` module to avoid needing to add `testing-ui` as a debug implementation as part of instrumentation testing. See the linked issue for more details
- [#139](https://github.com/bumble-tech/appyx/pull/139) - **Fixed**: `IntegrationPoint` memory leak created by `ActivityIntegrationPoint`

<div style="text-align: center"><small>12 Sep 2022</small></div>

---

## 1.0-alpha07

- [#122](https://github.com/bumble-tech/appyx/pull/122) - **Breaking change**: `ChildEntry.ChildMode` is removed, now nodes are always created when a nav model changes (previously default behaviour)
- [#99](https://github.com/bumble-tech/appyx/pull/99) – **Breaking change**: Removed `IntegrationPointAppyxProvider` and made `ActivityIntegrationPoint`'s constructor private. Use `ActivityIntegrationPoint.createIntegrationPoint`. This uses a weak reference to keep track of the integration points, and will not introduce memory leaks.
- [#122](https://github.com/bumble-tech/appyx/pull/122) - **Added**: New `ChildEntry.KeepMode` that allows to destroy nodes that are currently not visible on the screen
- [#132](https://github.com/bumble-tech/appyx/pull/132) - **Added**: New `NodeComponentActivity` to extend when wanting to work with `ComponentActivity` as your base activity, eg when migrating from a project built from the Jetpack Compose template
- [#119](https://github.com/bumble-tech/appyx/pull/119) - **Fixed**: Lifecycle observers are invoked in incorrect order (child before parent)
- [#62](https://github.com/bumble-tech/appyx/pull/62) - **Fixed**: Node is marked with stable annotation making some of the composable functions skippable
- [#129](https://github.com/bumble-tech/appyx/pull/129) - **Updated**: Removed sealed interface from operations to allow client to define their own
- [#133](https://github.com/bumble-tech/appyx/pull/133) - **Updated**: `NodeView` interface and `ParentNode` marked as stable improving amount of skippable composables

<div style="text-align: center"><small>9 Sep 2022</small></div>

---

## 1.0-alpha06

- [#96](https://github.com/bumble-tech/appyx/pull/96) – **Breaking change**: Removed `InteractorTestHelper`. Please use Node tests instead of Interactor tests.
- [#99](https://github.com/bumble-tech/appyx/pull/99) – **Breaking change**: Modified package of `NodeConnector` and `Connectable`
- [#99](https://github.com/bumble-tech/appyx/pull/99) – **Added**: Source<T>.rx2() to convert Source<T> to io.reactivex.Observable<T>
- [#107](https://github.com/bumble-tech/appyx/pull/107) – **Fixed**: Back press handlers are not properly registered on lifecycle events

<div style="text-align: center"><small>26 Aug 2022</small></div>

---

## 1.0-alpha05

- [#83](https://github.com/bumble-tech/appyx/issues/83) – **Breaking change**: `RoutingSource` renamed to `NavModel`. All subclasses, fields, package names, etc., any mentions of the word follow suit.
- [#91](https://github.com/bumble-tech/appyx/pull/91) – **Fixed**: Spotlight next and previous operations crash fix 

<div style="text-align: center"><small>19 Aug 2022</small></div>

---

## 1.0-alpha04

- [#39](https://github.com/bumble-tech/appyx/pull/39) – **Added**: Workflows implementation to support deeplinks
- [#32](https://github.com/bumble-tech/appyx/pull/32) – **Added**: `BackPressHandler` plugin that allows to control back press behaviour via `androidx.activity.OnBackPressedCallback`
- [#59](https://github.com/bumble-tech/appyx/issues/59) – **Added**: interface for `ParentNodeView<>`
- [#32](https://github.com/bumble-tech/appyx/issues/69) – **Added**: Jetpack Compose Navigation code sample
- [#81](https://github.com/bumble-tech/appyx/issues/81) – **Added**: Support integration point for multiple roots
- [#65](https://github.com/bumble-tech/appyx/pull/65) – **Added**: `InteropBuilderStub` and `InteropSimpleBuilderStub` testing util classes
- [#47](https://github.com/bumble-tech/appyx/issues/47) – **Updated**: The `customisations` module is now pure Java/Kotlin.
- [#85](https://github.com/bumble-tech/appyx/issues/85) – **Updated**: Improved `InteropView` error messaging when `Activity` does not implement `IntegrationPointAppyxProvider`
- [#88](https://github.com/bumble-tech/appyx/issues/88) – **Updated**: Moved `TestUpNavigationHandler` to `testing-unit-common`

<div style="text-align: center"><small>18 Aug 2022</small></div>

---

## 1.0-alpha03

- [#38](https://github.com/bumble-tech/appyx/pull/38) – **Added**: JUnit5 support

<div style="text-align: center"><small>2 Aug 2022</small></div>

---

## 1.0-alpha02

- [#19](https://github.com/bumble-tech/appyx/pull/19) – **Fixed**: Do not allow setting `Node.integrationPoint` on non-root nodes
- [#23](https://github.com/bumble-tech/appyx/pull/21) – **Fixed**: Integration point attached twice crash when using live literals
- [#14](https://github.com/bumble-tech/appyx/issues/14) – **Fixed**: Transition interruptions bug
- [#23](https://github.com/bumble-tech/appyx/pull/23) – **Added**: Unit test support
- [#26](https://github.com/bumble-tech/appyx/issues/26) – **Added**: Publish snapshot versions
- [#9](https://github.com/bumble-tech/appyx/pull/9) – **Migrated**: [app-tree-utils](https://github.com/badoo/app-tree-utils) into this repository

<div style="text-align: center"><small>19 Jul 2022</small></div>

---

## 1.0-alpha01

- Initial release

<div style="text-align: center"><small>4 Jul 2022</small></div>
