###Gradle Release Plugin

Gradle plugin that helps with SCM and release management for 
[Performance Tools](https://bitbucket.org/account/user/atlassian/projects/PT) modules.

### Verifying licenses
Execute `./gradlew verifyLicensing`  to  scan dependencies and verify if all the licenses are valid.

### Version management
SCM is an ultimate source of truth about project version and you will not find it hardcoded in the source code.
To get the current version of the project from Git execute `./gradlew currentVersion`.

### Releasing
To release a new version of a module that uses the plugin execute `./gradlew release`.
You should release new version only from *master* branch.
To publish a new version execute `./gradlew publish`.  

#### Marking new version
If you wish to mark a new *major* / *minor* version simply execute ` ./gradlew markNextVersion -Prelease.version=**version**`. 
For example to start a new version `0.2.0` execute ` ./gradlew markNextVersion -Prelease.version=0.2.0`.

For more information please refer to [axion-release-plugin docs](http://axion-release-plugin.readthedocs.io/en/latest/index.html) 
and [Maven Publish Plugin docs](https://docs.gradle.org/current/userguide/publishing_maven.html)

#### Repositories
The plugin automatically configures Atlassian *maven-private* repository and Atlassian *maven-private-snapshot* repository 
for *SNAPSHOT* releases. The credentials are read from `atlassian_private_username` and `atlassian_private_password` env
variables with a fallback to *Maven* `settings.xml` file.



##License
Copyright (c) 2018 Atlassian and others.
Apache 2.0 licensed, see [LICENSE.txt](LICENSE.txt) file.


