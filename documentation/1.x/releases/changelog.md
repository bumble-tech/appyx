# Changelog

## Pending changes

-

---

## 1.3.0

- [#425](https://github.com/bumble-tech/appyx/pull/425) – **Fixed**: Up navigation should be properly propagated from Appyx to RIBs
- [#384](https://github.com/bumble-tech/appyx/issues/384) – **Updated**: Compose BOM version and activity-compose version

<div style="text-align: center"><small>22 Jun 2023</small></div>

---

## 1.2.0

- [#393](https://github.com/bumble-tech/appyx/pull/393) – **Breaking change**: Removed non-lazy implementations from `NodeCustomisationDirectory`
- [#393](https://github.com/bumble-tech/appyx/pull/393) – **Breaking change**: Removed `NodeCustomisationDirectoryImpl#put(vararg values: T)` due to potential uncaught bugs. Please call the reified `put` extension function instead.

<div style="text-align: center"><small>11 Apr 2023</small></div>

---

## 1.1.2

- [#391](https://github.com/bumble-tech/appyx/pull/391) – **Added**: Introduced `putLazy` and `putSubDirectoryLazy` functions within `NodeCustomisationDirectoryImpl` via `LazyMutableNodeCustomisationDirectory`

<div style="text-align: center"><small>04 Apr 2023</small></div>

---

## 1.1.1

- [#386](https://github.com/bumble-tech/appyx/pull/386) – **Updated**: RIBs version to 0.39.0

<div style="text-align: center"><small>31 Mar 2023</small></div>

---

## 1.1.0

- [#383](https://github.com/bumble-tech/appyx/pull/383) – **Changed**: Removed deprecated methods in Node, ParentNode and CombinedHandler classes
- [#376](https://github.com/bumble-tech/appyx/issues/376) – **Changed**: Androidx lifecycle version updated to 2.6.1.
- [#376](https://github.com/bumble-tech/appyx/issues/376) – **Updated**: Kotlin and Compose compiler version updated to 1.8.10 to align kotlin version used in androidx lifecycle
- [#375](https://github.com/bumble-tech/appyx/issues/375) – **Fixed**: SaveableStateHolder does no longer save state for destroyed elements

<div style="text-align: center"><small>27 Mar 2023</small></div>

---

## 1.0.5

- [#370](https://github.com/bumble-tech/appyx/pull/370) – **Added**: `RetainedInstanceStore` has two new functions which allows a developer to check whether an object is retained. These are: `isRetainedByStoreId` and `isRetained`.

<div style="text-align: center"><small>04 Mar 2023</small></div>

---

## 1.0.4

- [#361](https://github.com/bumble-tech/appyx/pull/361) – **Added**: Introduced `RetainedInstanceStore`. This provides developers the ability to retain objects between configuration changes.
- [#336](https://github.com/bumble-tech/appyx/pull/336) – **Updated**: ChildAware API does not enforce Node subtypes only anymore, making it possible to use interfaces as public contracts for child nodes.

<div style="text-align: center"><small>20 Feb 2023</small></div>

---

## 1.0.3

- [#325](https://github.com/bumble-tech/appyx/pull/325) – **Fixed**: Crash when using PermanentChild API in View testing

<div style="text-align: center"><small>23 Jan 2023</small></div>

---

## 1.0.2

- [#287](https://github.com/bumble-tech/appyx/pull/287) – **Added**: Introduced a new `rememberCombinedHandler` implementation that takes an immutable list to avoid non-skippable compositions. The previous implementation is now deprecated.
- [#287](https://github.com/bumble-tech/appyx/pull/287) – **Added**: `ImmutableList` has been added to avoid non-skippable compositions.
- [#289](https://github.com/bumble-tech/appyx/issues/289) – **Added**: Introduced `interop-rx3` for RxJava 3 support. This has identical functionality to `interop-rx2`.
- [#298](https://github.com/bumble-tech/appyx/pulls/298) – **Updated**: ChildView documentation. `TransitionDescriptor` generics has been renamed to `NavTarget` and `State`
- [#307](https://github.com/bumble-tech/appyx/pull/307) - **Added**: `Spotlight.current()` method to observe currently active `NavTarget`.
- [#314](https://github.com/bumble-tech/appyx/pull/314) – **Fixed**: Lifecycle is properly destroyed for suspended nodes.

<div style="text-align: center"><small>10 Jan 2023</small></div>

---

## 1.0.1

- [#268](https://github.com/bumble-tech/appyx/pull/268) – **Fixed**: `PermanentChild` now does not crash in UI tests with `ComposeTestRule`.
- [#276](https://github.com/bumble-tech/appyx/pull/276) – **Fixed**: Back press handlers order is fixed for RIBs-Appyx integration.
- [#272](https://github.com/bumble-tech/appyx/pull/272) – **Changed**: `attachWorkflow` renamed to `attachChild`. `executeWorkflow` renamed to `executeAction`.
- [#272](https://github.com/bumble-tech/appyx/pull/272) – **Added**: `NodeReadyObserver` plugin to observe when the `Node` is ready

<div style="text-align: center"><small>22 Nov 2022</small></div>

---

## 1.0.0

- [#247](https://github.com/bumble-tech/appyx/pull/247) – **Added**: Added `EmptyNavModel` to core for cases in which a `ParentNode` only uses `PermanentChild`. The `DummyNavModel` test class is deprecated.
- [#250](https://github.com/bumble-tech/appyx/pull/250) – **Updated**: Jetpack Compose to 1.3.0

<div style="text-align: center"><small>31 Oct 2022</small></div>

---

## 1.0.0-rc02

- [#231](https://github.com/bumble-tech/appyx/pull/231) – **Fixed**: Changing transition handler at runtime does not redraw children
- [#239](https://github.com/bumble-tech/appyx/pull/239) – **Fixed**: Fixed an issue with desynchronisation between NavModel and children's restoration process
- [#218](https://github.com/bumble-tech/appyx/pull/218) – **Updated**: `androidx.core:core-ktx` to 1.9.0.

<div style="text-align: center"><small>21 Oct 2022</small></div>

---

## 1.0.0-rc01

- [#214](https://github.com/bumble-tech/appyx/pull/214) – **Breaking change**: `AppyxViewTestRule` stops supporting automatic launching activity. Activities should be started explicitly in tests.
- [#197](https://github.com/bumble-tech/appyx/pull/197) – **Breaking change**: `ParentNodeView` does not implement plugin anymore. `Node` instance is retrieved via `LocalComposition`. `AppyxParentViewTestRule` and `AbstractParentNodeView` have been removed.
- [#196](https://github.com/bumble-tech/appyx/pull/196) – **Breaking change**: `InteropBuilder` now should be supplied with Appyx `IntegrationPointProvider` to attach it at the same time Appyx Node is created.
- [#185](https://github.com/bumble-tech/appyx/issues/185) – **Breaking change**: `Activity` must implement `IntegrationPointProvider` and create `IntegrationPoint` manually. Weak references usage has been removed.
- [#173](https://github.com/bumble-tech/appyx/pull/173) – **Breaking change**: `ActivityStarter` and `PermissionRequester` now exposes coroutine based API instead of `minimal.reactive`.
- [#200](https://github.com/bumble-tech/appyx/pull/200) – **Breaking change**: Reordered the parameters for `ParentNode<NavTarget>.Child` and `fun <N : Node> NodeHost` to meet Compose guidelines.
- [#43](https://github.com/bumble-tech/appyx/pull/43) – **Updated**: Jetpack Compose to `1.2.1` and Kotlin to `1.7.10`.
- [#168](https://github.com/bumble-tech/appyx/pull/168) – **Updated**: Kotlin coroutines to `1.6.4`.
- [#171](https://github.com/bumble-tech/appyx/pull/171) – **Updated**: RIBs to `0.36.1`.
- [#212](https://github.com/bumble-tech/appyx/pull/212) – **Updated**: `Node` parent property is now public instead of private.
- [#174](https://github.com/bumble-tech/appyx/issues/174) – **Fixed**: `IntegrationPointExample` does not work with "do not keep activities"
- [#180](https://github.com/bumble-tech/appyx/pull/180) – **Added**: Ensure that `super.onSaveInstanceState()` was called to restore Node's state correctly

<div style="text-align: center"><small>13 Oct 2022</small></div>

---

## 1.0-alpha09

- [#151](https://github.com/bumble-tech/appyx/issues/151) - **Breaking change**: Renamed `Routing` to `NavTarget`. All related namings are affected (`RoutingElement`, `RoutingKey`, etc.)
- [#158](https://github.com/bumble-tech/appyx/issues/158) - **Breaking change**: Renamed `TransitionState` to `State` in all NavModel impls. Renamed `STASHED_IN_BACK_STACK` to `STASHED`.
- [#146](https://github.com/bumble-tech/appyx/issues/146) - **Breaking change**: Removed `FragmentIntegrationPoint`. Clients should use `ActivityIntegrationPoint.getIntegrationPoint(context: Context)` to get integration point from Fragment
- [#160](https://github.com/bumble-tech/appyx/issues/160) - **Breaking change**: Renamed `navmodel-addons` to `navmodel-samples` and stopped publishing the binary. If you feel we should add any of the samples to the main codebase, please let us know!
- [#138](https://github.com/bumble-tech/appyx/pull/138) - **Fixed**: `androidx.appcompat:appcompat` from is exposed via `api` within `com.bumble.appyx:core`. This prevents potential compilation bugs.
- [#143](https://github.com/bumble-tech/appyx/issues/143) - **Fixed**: Correctly exposed transitive dependencies that are part of the libraries ABI
- [#162](https://github.com/bumble-tech/appyx/pull/162) - **Fixed**: `NodeTestHelper`'s `moveTo` function can now move to `Lifecycle.State.DESTROYED`. The node itself has safeguards to prevent moving from destroyed state, and moving to destroyed is a valid test case.
- [#145](https://github.com/bumble-tech/appyx/issues/145) - **Updated**: `SpotlightSlider` now uses offset modifier with lambda
- [#159](https://github.com/bumble-tech/appyx/pull/159) - **Added**: `NodeHost` now takes modifier parameter to decorate the view of a root node
- [#162](https://github.com/bumble-tech/appyx/pull/162) - **Added**: `disposeOnDestroyPlugin` extension has been added to interop-rx2. This will allow Rx2 code to be easily disposed when the node it belongs to is destroyed.
- [#161](https://github.com/bumble-tech/appyx/pull/161) - **Added**: Operations helpers

<div style="text-align: center"><small>22 Sep 2022</small></div>

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
