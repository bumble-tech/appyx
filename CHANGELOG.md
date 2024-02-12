# Changelog

## Pending changes

### API breaking changes

- [#677](https://github.com/bumble-tech/appyx/pull/677) – Moved Builder, SimpleBuilder, Interactor to separate module

### Fixed

- [#670](https://github.com/bumble-tech/appyx/pull/670) - Fixes ios lifecycle
- [#673](https://github.com/bumble-tech/appyx/pull/673) – Fix canHandeBackPress typo

### Enhancement

- [#679](https://github.com/bumble-tech/appyx/pull/679) – Simple api for backStackNode and spotlightNode

---

## 2.0.0-alpha10

{==

Please refer to [2.0.0-alpha10 – Migration guide](2.0.0-alpha10.md)

==}

### API breaking changes

- [#630](https://github.com/bumble-tech/appyx/pull/630) – Pass initial state into Spotlights visualisations
- [#642](https://github.com/bumble-tech/appyx/pull/642) – Renamings
- [#643](https://github.com/bumble-tech/appyx/pull/643) – Unify AppyxComponent composable between appyx-navigation and appyx-interactions modules
- [#651](https://github.com/bumble-tech/appyx/pull/651) - Keep only one instance of SaveStateMap typealias and moved it to `com.bumble.appyx.utils.multiplatform` package 
- [#652](https://github.com/bumble-tech/appyx/pull/652) - KSP processor renamed from `mutable-ui-processor` to `appyx-processor`
- [#654](https://github.com/bumble-tech/appyx/pull/654) - Renamings 
- [#657](https://github.com/bumble-tech/appyx/pull/657) - Rename ParentNode & Node to Node and LeafNode 
- [#644](https://github.com/bumble-tech/appyx/pull/644) – Refactor AppyxComponent and application of draggable modifier 

### Fixed

- [#638](https://github.com/bumble-tech/appyx/pull/638) - Fix visibility issue for bottom element in BackStackParallax

<div style="text-align: center"><small>24 Jan 2024</small></div>

---

## 2.0.0-alpha09

### API breaking changes

- [#618](https://github.com/bumble-tech/appyx/pull/618) – Do not create `PermanentAppyxComponent` inside `ParentNode`. Provide it via constructor to ParentNode
- [#612](https://github.com/bumble-tech/appyx/pull/612) – Rename `MotionController` to `Visualisation`
- [#617](https://github.com/bumble-tech/appyx/pull/617) – Unify inside & outside position alignment

### Changed

- [#611](https://github.com/bumble-tech/appyx/pull/611) – Lower position and rotation animation default round-off thresholds
- [#620](https://github.com/bumble-tech/appyx/pull/620) – Updated Compose to 1.5.3 & Kotlin to 1.9.10

### Added

- [#615](https://github.com/bumble-tech/appyx/pull/615) – Material navigation helpers

<div style="text-align: center"><small>19 Oct 2023</small></div>

---

## 2.0.0-alpha08

### Fixed

- [#608](https://github.com/bumble-tech/appyx/pull/608) – Setting default value to `NodeCustomisationDirectory` in `IosNodeHost`

<div style="text-align: center"><small>4 Oct 2023</small></div>

---

## 2.0.0-alpha07

### Added

- [#601](https://github.com/bumble-tech/appyx/pull/601) – iOS support and target apps
- [#599](https://github.com/bumble-tech/appyx/pull/599) Added isContinuous flag to `GestureFactory`

<div style="text-align: center"><small>4 Oct 2023</small></div>

---

## 2.0.0-alpha06

### Changed

- [#594](https://github.com/bumble-tech/appyx/pull/594) Reverted JVM bytecode target to JDK11 instead of 17

<div style="text-align: center"><small>15 Sep 2023</small></div>

---

## 2.0.0-alpha05

### Added

- [#579](https://github.com/bumble-tech/appyx/pull/579) – Expose `AndroidLifecycle` in `PlatformLifecycleRegistry` for Android

### Fixed
 
- [#584](https://github.com/bumble-tech/appyx/pull/584) – Fix applying offset twice in `AppyxComponent`
- [#585](https://github.com/bumble-tech/appyx/pull/585) – Fix drag vs align
- [#571](https://github.com/bumble-tech/appyx/pull/571) – Avoid `MotionController` recreation
- [#587](https://github.com/bumble-tech/appyx/pull/587) – Fix `DraggableChildren` and rename it to `AppyxComponent`
- [#588](https://github.com/bumble-tech/appyx/pull/588) – Set bounds on all new motion controllers
- [#589](https://github.com/bumble-tech/appyx/pull/589) – Fix visibility resolution for elements that do not match parent's size
- [#591](https://github.com/bumble-tech/appyx/pull/591) – Flush output cache when `onCreate` is called in `NodeConnector`
- [#592](https://github.com/bumble-tech/appyx/pull/592) – Fix `Backstack3D` `MotionController`

<div style="text-align: center"><small>13 Sep 2023</small></div>

---

## 2.0.0-alpha04

### Fixed

- [#575](https://github.com/bumble-tech/appyx/pull/575) - Make customisations lazy to improve performance

<div style="text-align: center"><small>31 Aug 2023</small></div>

---

## 2.0.0-alpha03

### API breaking changes

- [#562](https://github.com/bumble-tech/appyx/issues/562) – Implement custom alignment to remove screen size knowledge when offsetting element in `MotionController`
- [#562](https://github.com/bumble-tech/appyx/pull/565) – Remove `UiContext` parameter from `MotionProperty` and provide `BoxScope` via composition local

### Added

- [#551](https://github.com/bumble-tech/appyx/pull/551) - Support Multiplatform in **:appyx-navigation**
- [#565](https://github.com/bumble-tech/appyx/pull/565) - Adds `AngularPosition` via restoring `Promoter` example

### Fixed

- [#560](https://github.com/bumble-tech/appyx/issues/560) – Don't remove destroyed elements on update automatically

<div style="text-align: center"><small>17 Aug 2023</small></div>

---

## 2.0.0-alpha02

### Added

- [#539](https://github.com/bumble-tech/appyx/pull/539) – Position alignment
- [#538](https://github.com/bumble-tech/appyx/pull/538) – Availability to observe `MotionProperties` from children UI

### Fixed

- [#530](https://github.com/bumble-tech/appyx/issues/530) – Backstack Parallax motion controller bug

<div style="text-align: center"><small>9 Aug 2023</small></div>

---

## 2.0.0-alpha01

- Initial release

<div style="text-align: center"><small>6 Jul 2023</small></div>
