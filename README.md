<img align="left" width="80" height="80" src="app/src/main/res/mipmap-xxhdpi/ic_launcher.png" alt="FoodIST icon">

# FoodIST: Finding food on campus

![Android CI](https://github.com/geckoflume/FoodIST/workflows/Android%20CI/badge.svg)

Android application project for Mobile and Ubiquitous Computing class @ Instituto Superior Técnico, Lisbon, Portugal.

Meant to be used with the [FoodIST REST Server](https://github.com/geckoflume/FoodIST-Server), it provides the ability to locate cafeterias, crowdsource cafeteria menus, dishes pictures and queue wait times.

More information: [https://fenix.tecnico.ulisboa.pt/disciplinas/CMov4/2019-2020/2-semestre](https://fenix.tecnico.ulisboa.pt/disciplinas/CMov4/2019-2020/2-semestre)

*Group 23*

## Features

- List and map of all dining options (cafeterias, canteens and bars) of IST
- Filter cafeterias by campus
- Campus auto-selection based on device location
- Cafeteria details: name, opening hours, map, estimated walk time and itinerary
- Ability to add, remove dishes and to fetch menus from the server
- Ability to add pictures to dishes (compressed before being sent)
- User status selection and cafeterias/opening times displayed accordingly
- Dark theme compatible
- Localization in English, French and Portuguese, Google translations for user-provided data
- Dual LRU cache system, in ram and on disk with preload if the device gets connected to wifi
- Dish, cafeteria sharing

## TODO

- Beacons detection
- Support pre-Lollipop devices (https://android.jlelse.eu/android-vector-drawables-on-pre-lollipop-crash-solution-45c0c34f0160)

## Specifications

This application is built around the MVVM (Model View View-Model) design pattern, using Room, a DAO with LiveData and ViewModels.

Static cafeteria data and opening times are stored in an SQLite database, populated from JSON arrays [1](app/src/main/assets/cafeterias.json) and [2](app/src/main/assets/opening_hours.json) (see [Generate opening_hours.json](#generate-opening_hoursjson)).

The architecture is built around the [Jetpack components collection](https://developer.android.com/jetpack) in Java, to introduce best Android practices (such as AndroidX, DataBinding, LiveData, Fragments...) and the layouts are designed with the help of [Google's Material Design components](https://material.io/develop/android/).

The multi-threading is managed by `java.util.concurrent` Executors, to support future Android versions (see [Android AsyncTask API deprecating in Android 11](https://stackoverflow.com/q/58767733/9875498)).

### Local database specification

![Database relationship diagram](database.png "Database relationship diagram")

## Prerequisites

- Android API Level >=v16
- Android Build Tools >=v29

## How to build

This project uses the Gradle build system.  
In order to benefit from Google Maps services (itineraries, times) and Google Translate, please set an API key (with Directions API, Maps SDK for Android and Cloud Translation API enabled) in [debug/res/values/google_cloud_api.xml](app/src/debug/res/values/google_cloud_api.xml) or [release/res/values/google_cloud_api.xml](app/src/release/res/values/google_cloud_api.xml) by replacing the `google_cloud_key` string:
```xml
<string name="google_cloud_key" templateMergeStrategy="preserve" translatable="false">YOUR_KEY_HERE</string>
```
To build this app, use the `gradlew build` command or use "Import Project" in Android Studio. 

### Generate opening_hours.json

By default, the opening times and cafeteria locations are the following:
| Campus    | Name             | Geo Coordinates      | Hours                                                                                                 |
|-----------|------------------|----------------------|-------------------------------------------------------------------------------------------------------|
| Alameda   | Central Bar      | 38.736606, -9.139532 | 9:00-17:00                                                                                            |
| Alameda   | Civil Bar        | 38.736988, -9.139955 | 9:00-17:00                                                                                            |
| Alameda   | Civil Cafeteria  | 38.737650, -9.140384 | 12:00-15:00                                                                                           |
| Alameda   | Sena Pastry Shop | 38.737677, -9.138672 | 8:00-19:00                                                                                            |
| Alameda   | Mechy Bar        | 38.737247, -9.137434 | 9:00-17:00                                                                                            |
| Alameda   | AEIST Bar        | 38.736542, -9.137226 | 9:00-17:00                                                                                            |
| Alameda   | AEIST Esplanade  | 38.736318, -9.137820 | 9:00-17:00                                                                                            |
| Alameda   | Chemy Bar        | 38.736240, -9.138302 | 9:00-17:00                                                                                            |
| Alameda   | SAS Cafeteria    | 38.736571, -9.137036 | 9:00-21:00                                                                                            |
| Alameda   | Math Cafeteria   | 38.735508, -9.139645 | 13:30-15:00 for Students and the Public 12:00-15:00 for Professors, Researchers, and Staff            |
| Alameda   | Complex Bar      | 38.736050, -9.140156 | 9:00-12:00, 14:00-17:00 for Students and the Public 9:00-17:00 for Professors, Researchers, and Staff |
| Taguspark | Tagus Cafeteria  | 38.737802, -9.303223 | 12:00-15:00                                                                                           |
| Taguspark | Red Bar          | 38.736546, -9.302207 | 8:00-22:00                                                                                            |
| Taguspark | Green Bar        | 38.738004, -9.303058 | 7:00-19:00                                                                                            |
| CTN       | CTN Cafeteria    | 38.812522, -9.093773 | 12:00-14:00                                                                                           |
| CTN       | CTN Bar          | 38.812522, -9.093773 | 8:30-12:00, 15:30-16:30                                                                               |

To quickly generate a new JSON file containing different hours, you can use the [generate_openingtimes.sh](generate_openingtimes.sh) script, which provides a basic yet useful assistant to help you do that painful task.
Syntax :
```shell script
./generate_openingtimes.sh
```
> Note: this script requires [jq](https://stedolan.github.io/jq/) to run.

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

- HTTP multipart/form-data without Java libs:
    - https://stackoverflow.com/a/34409142

- JPEG compression:
    - https://stackoverflow.com/a/19596231

- Set locale at runtime:
    - https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
    - https://medium.com/ironsource-tech-blog/conversion-by-translation-changing-your-android-app-language-at-runtime-5c9daebf9771
    - https://stackoverflow.com/a/9173571