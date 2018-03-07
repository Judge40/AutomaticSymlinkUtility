# Automatic Symlink Utility
[![License MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## About
Automatic Symlink Utility is a small utility which automatically manages
symbolic links based on pre-defined values.

The motivation for this utility came from a personal desire to keep application
configurations and game saves organized in such a way as to make them easy to
access and backup. Existing solutions either consisted of manual symbolic link
creation or full backup solutions which created their own non-customizable
structure.

Automatic Symlink Utility is intended to offer a middle ground, once the desired
symbolic links are defined the utility will move all of the relevant files in to
the defined folder locations and create links in the original location. Allowing
full control over where and how your configuration and save files are stored.

## Features
Automatic Symlink Utility uses an XML file to define the symbolic links to
create, the schema provides the ability to create the symbolic links
conditionally based on the presence of a given file or folder. The context path
should generally point to the application related to the files being linked to,
so that links are only recreated for applications and games that are currently
installed.

An example XML file is shown below. This includes context path examples to
create links only when certain applications, plugins, expansions or games are
installed. For further information refer to the [schema](symlinks.xsd).

```xml
<symlinks>
    <group name="application1" contextPath="C:\app1">
        <symlink contextPath="C:\app1\plugin.exe">
            <linkPath>C:\app1\plugin.config</linkPath>
            <targetPath>D:\Configs\app1\plugin.config</targetPath>
        </symlink>
        <symlink>
            <linkPath>C:\app1\app1.config</linkPath>
            <targetPath>D:\Configs\app1\app1.config</targetPath>
        </symlink>
    </group>
    <group name="game1">
        <symlink contextPath="C:\game1\expansion1">
            <linkPath>C:\game1\expansion1.ini</linkPath>
            <targetPath>D:\Configs\game1\expansion1.ini</targetPath>
        </symlink>
        <symlink>
            <linkPath>C:\User\Games\game1.sav</linkPath>
            <targetPath>D:\Saves\game1\saves\game1.sav</targetPath>
        </symlink>
    </group>
    <symlink contextPath="C:\game2">
        <linkPath>C:\User\Games\game2\saves</linkPath>
        <targetPath>D:\Saves\game2\saves</targetPath>
    </symlink>
    <symlink>
        <linkPath>C:\game3\saves\campaign.sav</linkPath>
        <targetPath>D:\Saves\game3\hard_campaign.sav</targetPath>
    </symlink>
</symlinks>
```

## Versioning
This project uses Semantic Versioning, see [SemVer](http://semver.org) for
further details.

## License
This project is licensed under the [MIT License (MIT)](LICENSE).
