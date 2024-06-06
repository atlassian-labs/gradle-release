# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
[Unreleased]: https://github.com/atlassian-labs/gradle-release/compare/release-0.7.3...master

## Added
- Allowlist alternate spellings of Apache, MIT and LGPL licenses.

## [0.8.0] - 2023-01-27
[0.8.0]: https://github.com/atlassian-labs/gradle-release/compare/release-0.7.3...release-0.8.0

### Added
- Support Gradle 7. Fix [JPERF-941].

[JPERF-941]: https://ecosystem.atlassian.net/browse/JPERF-941

## [0.7.3] - 2021-01-07
[0.7.3]: https://github.com/atlassian-labs/gradle-release/compare/release-0.7.2...release-0.7.3

### Fixed
- Find license problems in more configurations, like `api` or `implementation`. Fix [JPERF-713].

[JPERF-713]: https://ecosystem.atlassian.net/browse/JPERF-713

## [0.7.2] - 2020-11-17
[0.7.2]: https://github.com/atlassian-labs/gradle-release/compare/release-0.7.1...release-0.7.2

### Fixed
- Fix repo URL for `gradleRelease.atlassianPrivateMode = true`.

## [0.7.1] - 2020-11-04
[0.7.1]: https://github.com/atlassian-labs/gradle-release/compare/release-0.6.0...release-0.7.1

### Changed
- Release to Atlassian proxy of Maven Central. Fix [JPERF-677].

[JPERF-677]: https://ecosystem.atlassian.net/browse/JPERF-677

## 0.7.0
This release didn't happen due to a bug. The number 0.7.0 is "burned".

## [0.6.0] - 2019-03-26
[0.6.0]: https://github.com/atlassian-labs/gradle-release/compare/release-0.5.1...release-0.6.0

### Added
- Enable support for Atlassian private repository.

## [0.5.1] - 2019-03-23
[0.5.1]: https://github.com/atlassian-labs/gradle-release/compare/release-0.5.0...release-0.5.1

### Fixed
- Bump `axion-release` to get better transitive dependencies.

## [0.5.0] - 2019-01-30
[0.5.0]: https://github.com/atlassian-labs/gradle-release/compare/release-0.4.3...release-0.5.0

### Added
- Infer GitHub remote for POM SCM section. Unblock [JPERF-375].

[JPERF-375]: https://ecosystem.atlassian.net/browse/JPERF-375

## [0.4.3] - 2018-09-20
[0.4.3]: https://github.com/atlassian-labs/gradle-release/compare/release-0.4.2...release-0.4.3

### Fixed
- Fix Kotlin compilation. Fix [JPERF-118].

[JPERF-118]: https://ecosystem.atlassian.net/browse/JPERF-118

## [0.4.2] - 2018-09-20
[0.4.2]: https://github.com/atlassian-labs/gradle-release/compare/release-0.4.1...release-0.4.2

### Fixed
- Support anonymous access to `atlassian-public`. Fix [JPERF-116].

[JPERF-116]: https://ecosystem.atlassian.net/browse/JPERF-116

## [0.4.1] - 2018-09-06
[0.4.1]: https://github.com/atlassian-labs/gradle-release/compare/release-0.4.0...release-0.4.1

### Fixed
- License check overriding `check` tasks dependencies see [JPERF-58].

[JPERF-58]: https://ecosystem.atlassian.net/browse/JPERF-58

## [0.4.0] - 2018-09-03
[0.4.0]: https://github.com/atlassian-labs/gradle-release/compare/release-0.3.0...release-0.4.0

### Added
- Sign releases.

### Changed
- Release to atlassian-public maven repository.

## [0.3.0] - 2018-08-27
[0.3.0]: https://github.com/atlassian-labs/gradle-release/compare/release-0.2.0...release-0.3.0

### Removed
- Stop publishing to snapshot repositories.
- Stop depending on snapshot repositories.

## [0.2.0] - 2018-08-09
[0.2.0]: https://github.com/atlassian-labs/gradle-release/compare/release-0.1.0...release-0.2.0

### Added
- Add more licenses to allowedLicenses list
- Run `verifyLicensing` before `check` task

### Fixed
- Add description to  `verifyLicensing` task

## [0.1.0] - 2018-08-08
[0.1.0]: https://github.com/atlassian-labs/gradle-release/compare/release-0.0.2...release-0.1.0

### Added
- Bundle license in JAR file
- Add `verifyLicensing` task

## [0.0.2] - 2018-08-02
[0.0.2]: https://github.com/atlassian-labs/gradle-release/compare/release-0.0.1...release-0.0.2

### Added
- [README.md](README.md)

## 0.0.1 - 2018-08-02

### Added
- Atlassian private repository configuration
- Release and publishing configuration


