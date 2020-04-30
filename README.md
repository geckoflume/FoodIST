# FoodIST: Finding food on campus
Android application project for Mobile and Ubiquitous Computing class @ Instituto Superior Técnico, Lisbon, Portugal.

Meant to be used with the [FoodIST REST Server](https://github.com/geckoflume/FoodIST-Server), it provides ability to locate cafeterias, crowdsource cafeteria menus, dishes pictures and queue wait times.

More information: [https://fenix.tecnico.ulisboa.pt/disciplinas/CMov4/2019-2020/2-semestre](https://fenix.tecnico.ulisboa.pt/disciplinas/CMov4/2019-2020/2-semestre)

*Group 23*

## Features

- List of all dining options (cafeterias, canteens and bars) of IST
- Filter cafeterias by campus
- Campus autoselection based on device location
- Cafeteria details: name, opening hours, map, estimated walk time and itinerary
- Mockup static dishes
- User status selection and cafeterias/opening times displayed accordingly
- Dark theme compatible

## TODO

- Server data fetching
- Ability to add dishes/meals
- Beacons detection
- Caching

## Specifications

This application is built around the MVVM (Model View View-Model) design pattern, using Room, a DAO with LiveData and ViewModels.

Static cafeteria data are stored in a SQLite database, populated from a [JSON array](app/src/main/assets/cafeterias.json).

The architecture is build around the [Jetpack components collection](https://developer.android.com/jetpack) in Java, to introduce best Android practices (such as AndroidX, DataBinding, LiveData, Fragments...) and the layouts are designed with the help of [Google's Material Design components](https://material.io/develop/android/).

### Local database specification

![Database relationship diagram](database.png "Database relationship diagram")

## Prerequisites

- Android API Level >=v16
- Android Build Tools >=v29

## How to build

This project use the Gradle build system.
In order to benefit from Google Maps services (itineraries, times), please set your key in [debug/res/values/google_maps_api.xml](app/src/debug/res/values/google_maps_api.xml) or in [release/res/values/google_maps_api.xml](app/src/release/res/values/google_maps_api.xml) by replacing the `google_maps_key` string:
```xml
<string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">YOUR_KEY_HERE</string>
```
To build it, use the `gradlew build` command or use "Import Project" in Android Studio. 

## Valuable resources:

- Coordinates type in SQLite:
	- https://stackoverflow.com/a/46732273
	- https://sqlite.org/datatype3.html
	- https://stackoverflow.com/a/12504340
	- https://abrignoni.blogspot.com/2018/08/android-nike-run-app-geolocation-sqlite.html

- SQLite:
	- https://stackoverflow.com/q/2493331
	- https://github.com/android/architecture-components-samples/blob/master/PersistenceContentProviderSample/app/src/main/java/com/example/android/contentprovidersample/data/SampleDatabase.java
	- https://codelabs.developers.google.com/codelabs/android-room-with-a-view

- Cards:
	- https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/
	- https://medium.com/@droidbyme/android-cardview-with-recyclerview-90cfeda6a4d4

- Map directions, zooming:
	- https://stackoverflow.com/a/14828739
	- https://medium.com/@haydar_ai/better-way-to-get-the-item-position-in-androids-recyclerview-820667d435d4
	- https://github.com/Vysh01/android-maps-directions

- How to store dates (for opening hours):
	- https://codeblog.jonskeet.uk/2017/04/23/all-about-java-util-date/
	- https://medium.com/androiddevelopers/room-time-2b4cf9672b98
	- https://stackoverflow.com/a/38922755
	- https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html
    - https://medium.com/androiddevelopers/room-time-2b4cf9672b98