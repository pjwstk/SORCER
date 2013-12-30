This directory contains platform configuration files. The Platform
configuration file is used as follows:

- Provides attributes for creating PlatformCapability classes and declaring
   what jars will be common across all service classloaders. If jars are common
   they are loaded by the CommonClassLoader, and are in the classpath of all
   child class loaders. By default Rio and Jini technology jars are common
   across all service classloaders.

   For platform capabilities that are not loaded by the common classloader,
   services that declare that dependency will have the capability loaded by
   the service's classloader.

 - Provides a manifest of the platform jars that can be loaded.

Platform configuration files are loaded when Rio starts. The contents of this
directory will be scanned at startup time. Each .xml file will be parsed for
<platform> declarations.

The structure of the documents are as follows:

<platform>
    <capability name="Foo" common="yes">
        <description>An optional description</description>
        <version>2.5</version>
        <manufacturer>An optional manufacturer</manufacturer>
        <classpath>space delimited listing of directories and/or jars</classpath>
        <path>The location on the file system where the capability is installed</path>
        <native>Any native libraries that need to be loaded</native>
        <costmodel>The resource cost model class name</costmodel>
    </capability>
</platform>
