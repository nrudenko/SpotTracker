SpotTracker
===========

Example of working with maps

Application draw rect polygon around you position on map. Corners of this rect are distant from your location by 20 meters. 
There are some "hotspots" in the corners of this rectangle. Each hotspot have zone with 10 meters radius(drawed by red circle around marker).  
When you reach nearest "hotspot zone" application will notify you with built-in notification.

Note then accuracy of you location depends from GPS signal quality. And if you in the building, signal can has low accuracy. 
Best result will be received in outdoor.

For build application you can use shell/bat script ``` gradlew ``` in the project root directory
  
Example for shell (execute from root dir):
   * ./gradlew assembleDebug - build apk and store it's to /build/outputs/apk dir 
   * ./gradle installDebug - will build and install apk to connected device
   
NOTICE: for correct assembling you must have installed Android SDK (https://developer.android.com/sdk/installing/index.html)




###LINK FOR GETTING ANDROID BUILD:

![Image of qr](http://chart.apis.google.com/chart?chs=200x200&cht=qr&chld=|1&chl=https%3A%2F%2Fgithub.com%2Fnrudenko%2FSpotTracker%2Fblob%2Fmaster%2FSpotTracker.apk)

https://github.com/nrudenko/SpotTracker/blob/master/SpotTracker.apk





