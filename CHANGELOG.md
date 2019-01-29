# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
[Unreleased]: https://bitbucket.org/atlassian/gradle-release/branches/compare/master%0Drelease-0.4.3

### Added
- Infer GitHub remote for POM SCM section. Unblock [JPERF-375].

[JPERF-375]: https://ecosystem.atlassian.net/browse/JPERF-375

## [0.4.3] - 2018-09-20
[0.4.3]: https://bitbucket.org/atlassian/gradle-release/branches/compare/release-0.4.3%0Drelease-0.4.2

### Fixed
- Fix Kotlin compilation. Fix [JPERF-118].

[JPERF-118]: https://ecosystem.atlassian.net/browse/JPERF-118

## [0.4.2] - 2018-09-20
[0.4.2]: https://bitbucket.org/atlassian/gradle-release/branches/compare/release-0.4.2%0Drelease-0.4.1

### Fixed
- Support anonymous access to `atlassian-public`. Fix [JPERF-116].

[JPERF-116]: https://ecosystem.atlassian.net/browse/JPERF-116

## [0.4.1] - 2018-09-06
[0.4.1]: https://bitbucket.org/atlassian/gradle-release/branches/compare/release-0.4.1%0Drelease-0.4.0

### Fixed
- License check overriding `check` tasks dependencies see [JPERF-58](https://ecosystem.atlassian.net/browse/JPERF-58)

## [0.4.0] - 2018-09-03
[0.4.0]: https://bitbucket.org/atlassian/gradle-release/branches/compare/release-0.4.0%0Drelease-0.3.0

### Added
- Sign releases.

### Changed
- Release to atlassian-public maven repository.

## [0.3.0] - 2018-08-27
[0.3.0]: https://bitbucket.org/atlassian/gradle-release/branches/compare/release-0.3.0%0Drelease-0.2.0

### Removed
- Stop publishing to snapshot repositories.
- Stop depending on snapshot repositories.

## [0.2.0] - 2018-08-09
### Added
- Add more licenses to allowedLicenses list
- Run `verifyLicensing` before `check` task
### Fixed
- Add description to  `verifyLicensing` task

## [0.1.0] - 2018-08-08
### Added
- Bundle license in JAR file
- Add `verifyLicensing` task

## [0.0.2] - 2018-08-02
### Added
- [README.md](README.md)

## [0.0.1] - 2018-08-02
### Added
- Atlassian private repository configuration
- Release and publishing configuration


