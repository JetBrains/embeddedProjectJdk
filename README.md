This plugin allows you to put the `jdk.table.xml` file (stored in `<IDE_INSTALLATION_DIRECTORY>/config/options/config/options/jdk.table.xml` by default), into the project folder and commit to VCS. If the JDK defined in the per project `<PROJECT_DIR>/.idea/jdk.table.xml` is not found or invalid, then the plugin will it automatically.

You can also define OS-dependent `jdk.table.*.xml` files like so:

* Windows: `jdk.table.win.xml`
* Linux: `jdk.table.lin.xml`
* MacOS: `jdk.table.mac.xml`
