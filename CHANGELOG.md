# Changelog

## Pending changes

### Added

- [#579](https://github.com/bumble-tech/appyx/pull/579) – Expose `AndroidLifecycle` in `PlatformLifecycleRegistry` for Android


### Fixed
- 
- [#589](https://github.com/bumble-tech/appyx/pull/589) – Fix visibility resolution for elements that do not match parent's size
- [#586](https://github.com/bumble-tech/appyx/issues/586) – Fix `AppyxComponent` wrongly applying width and height modifier to children composables
- [#584](https://github.com/bumble-tech/appyx/pull/584) – Fix applying offset twice in `AppyxComponent`
- [#585](https://github.com/bumble-tech/appyx/pull/585) – Fix drag vs align
- [#571](https://github.com/bumble-tech/appyx/pull/571) – Avoid `MotionController` recreation


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
