CSC3003S Capstone Project: MapUCT - An interactive, improved map of the University of Cape Town
Chantal Yang, Noosrat Hossain, Kiaan Pillay

Date: 22 September 2014.

Installation Requirements:
- The UCT Map app has been designed and compiled using Android SDK 20, but has backwards compatibility to SDK 15. 
- Permission must be granted to the app to use GPS  and the Internet via mobile data or Wi-fi.

Description:
One of several Capstone Projects for CSC3002S, MapUCT is an interactive maps app that allows users to find 
their way around UCT and has features such as filtering map icons and finding routes to locations. 
This was implemented in the form of an Android app.

Functionality:
- Overview of Map with Colour-coded buildings, icons for points of interest, parking.
- Map orientated westwards on startup, pinch to zoom and panning gestures. 
- Filter buildings by faculty and points of interest.
- Details on Demand: Display Information about Buildings and Points of Interest
- Display Routes from specified location to target locations.

Original design by Victour Soudien, Nabeelah Harris and Zahraa Mathews for UCT CSC4000W Visual thinking and Visualisation.
http://people.cs.uct.ac.za/~mkuttel/UCTMapVisualization.html

Getting an API key to use with Google Maps API*:
1. Setup Google Developer Account at console.developers.google.com
2. Once you get the API key, copy-paste it into the AndroidManifest.XML

*Note: This only needs to be done if the targeted device is an emulator.

Setting up Android Studio:

1. Download Android Studio from https://developer.android.com/sdk/installing/studio.html
2. Configure Android Studio to use Google Play Services and Android SDK 19: Go to "Tools" > "Android" > "SDK Manager" 
   and install the following packages:
	- Android 4.4.2 (API 19)
	- Google Play Services
	- Android SDK Build-tools (Revision 19)

Configuring Geny Motion to work with Google Play Services:
http://stackoverflow.com/questions/20121883/how-to-install-google-play-services-in-a-genymotion-vm-with-no-drag-and-drop-su


Use Genymotion Emulator*

1. Download Geny Motion Emulator from https://cloud.genymotion.com/page/launchpad/download/
2. Click the "Add" button along the toolbar at the top of the screen and choose any device thats compatible with API 19.
	eg.Samsung Galaxy S4 - 4.4.2 - API 19 - 1080x1920
3. Start the device in Geny Motion by clicking the play button

*Alternatively, any Android device can be used to run the app by connecting it to the PC
via a cable. This device will be chosen as a target device instead of the emulator.  

Intstructions for running in Android Studio:

1. Open Android Studio
2. Choose "Import Project" then select the project* file and click ok
3. Click the run app button in Android Studio (shift+f10)
4. Choose target device from list of devices


Java classes: app > src > main > java > maps > com > uctmap
		- Buildings.java
		- CustomerOverlay.java
		- DirectionsJSONParser.java
		- Icon.java
		- MainActivity.java
		- Parking.java
		- MapFilterFragment.java
		- MapInformationFragment.java
		- MapRoutesFragment.java
		- NavigationDrawerFragment.java

*Note: The project file MUST be opened and NOT the top level directory.

Android SDK: API 20

Google Maps API V2
