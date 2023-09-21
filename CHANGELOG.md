# Changelog

## Pending changes

-

## 2.0.0-alpha06

### Changed

- [#594](https://github.com/bumble-tech/appyx/pull/594) Reverted JVM bytecode target to JDK11 instead of 17


<div style="text-align: center"><small>15 Sep 2023</small></div>

---

## 2.0.0-alpha05

### Added

- [#579](https://github.com/bumble-tech/appyx/pull/579) – Expose `AndroidLifecycle` in `PlatformLifecycleRegistry` for Android
- [#601](https://github.com/bumble-tech/appyx/pull/601) – Support iOS platform for  **:appyx-navigation** & adding sample iOS targets in **:demos:appyx-navigation** & **:demos:appyx-interactions**  

### Fixed
 
- [#584](https://github.com/bumble-tech/appyx/pull/584) – Fix applying offset twice in `AppyxComponent`
- [#585](https://github.com/bumble-tech/appyx/pull/585) – Fix drag vs align
- [#571](https://github.com/bumble-tech/appyx/pull/571) – Avoid `MotionController` recreation
- [#587](https://github.com/bumble-tech/appyx/pull/587) – Fix `DraggableChildren` and rename it to `AppyxComponent`
- [#588](https://github.com/bumble-tech/appyx/pull/588) – Set bounds on all new motion controllers
- [#589](https://github.com/bumble-tech/appyx/pull/589) – Fix visibility resolution for elements that do not match parent's size
- [#591](https://github.com/bumble-tech/appyx/pull/591) – Flush output cache when onCreate is called in NodeConnector
- [#592](https://github.com/bumble-tech/appyx/pull/592) – Fix Backstack3D Motion Controller

<div style="text-align: center"><small>13 Sep 2023</small></div>

---

## 2.0.0-alpha04

### Fixed

- [#575](https://github.com/bumble-tech/appyx/pull/575) - Make customisations lazy to improve performance

<div style="text-align: center"><small>31 Aug 2023</small></div>

---

## 2.0.0-alpha03

### Added

- [#551](https://github.com/bumble-tech/appyx/pull/551) - Support Multiplatform in **:appyx-navigation**
- [#565](https://github.com/bumble-tech/appyx/pull/565) - Adds `AngularPosition` via restoring `Promoter` example

### Fixed

- [#560](https://github.com/bumble-tech/appyx/issues/560) – Don't remove destroyed elements on update automatically

### Changed

- [#562](https://github.com/bumble-tech/appyx/issues/562) – Implement custom alignment to remove screen size knowledge when offsetting element in MotionController
- [#562](https://github.com/bumble-tech/appyx/pull/565) – Remove UiContext parameter from MotionProperty and provide BoxScope via composition local

<div style="text-align: center"><small>17 Aug 2023</small></div>

---

## 2.0.0-alpha02

### Added

- [#539](https://github.com/bumble-tech/appyx/pull/539) – Position alignment
- [#538](https://github.com/bumble-tech/appyx/pull/538) – Availability to observe MotionProperties from children UI

### Fixed

- [#530](https://github.com/bumble-tech/appyx/issues/530) – Backstack Parallax motion controller bug

<div style="text-align: center"><small>9 Aug 2023</small></div>

---

## 2.0.0-alpha01

- Initial release

<div style="text-align: center"><small>6 Jul 2023</small></div>
