SpotTracker
===========

Example of working with maps

Application draws rect polygon around your position on map. Corners of this rect are distant from your location by 20 meters. 
There are some "hotspots" in the corners of this rectangle. Each hotspot has zone with 10 meters radius(drawn by red circle around marker).  
When you reach the nearest "hotspot zone" application will notify you with built-in notification.

Note that accuracy of your location depends on GPS signal quality. And if you are in the building or another place where GPS signal can have low accuracy then there is high possibility to get bad results. Best result will be received in outdoor. 

For build application you can use shell/bat script ``` gradlew ``` in the project root directory
 
Example for shell (execute from root dir):
   ./gradlew assembleDebug - build apk and store it's to /build/outputs/apk dir 
   ./gradle installDebug - will build and install apk to connected device
   
NOTICE: for correct assembling you must have Android SDK installed (https://developer.android.com/sdk/installing/index.html)


###LINK FOR GETTING ANDROID BUILD:

![Image of qr](http://chart.apis.google.com/chart?chs=200x200&cht=qr&chld=|1&chl=https://github.com/nrudenko/SpotTracker/raw/master/SpotTracker.apk)

https://github.com/nrudenko/SpotTracker/raw/master/SpotTracker.apk





