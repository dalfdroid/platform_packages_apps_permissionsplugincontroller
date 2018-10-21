rm -r ~/Projects/Heimdall/tmp/*

mv src/com ~/Projects/Heimdall/tmp/
mv res ~/Projects/Heimdall/tmp/

cp -r ~/Projects/Heimdall/PermissionsPluginController/app/src/main/java/com src/
cp -r ~/Projects/Heimdall/PermissionsPluginController/app/src/main/res .

rm src/com/android/permissionsplugin/PermissionsPlugin.java

cp PackageManagerBridge.java src/com/android/permissionsplugin/permissionsplugincontroller/PackageManagerBridge.java
