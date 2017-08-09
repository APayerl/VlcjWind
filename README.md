# VlcjWind #

VlcjWind (Wind as in Windowed) is a wrapper library for Vlcj.<br />
It is used to simplify the need of having multiple video windows at the same time for the same file.<br />
It is using VLCJ:s DirectMediaPlayer and if I have not missed anything this should be compatible with JavaFx

## Gradle ##

How to import the project is listed here:<br />
https://jitpack.io/#APayerl/VlcjWind <br /> and then choosing prefered release.

<br />

Import the project by editing build.gradle:

	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
	
Then add the dependency also to the build.gradle:

	dependencies {
		...
		compile 'com.github.APayerl:VlcjWind:0.1'
	}

## Maven ##

How to import the project is listed here:<br />
https://jitpack.io/#APayerl/VlcjWind<br />and then choosing prefered release.
<br /><br />
Import the project by adding to build file:

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
	
Then add the dependency:

	<dependency>
	    <groupId>com.github.APayerl</groupId>
	    <artifactId>VlcjWind</artifactId>
	    <version>0.1</version>
	</dependency>

## Credits ##

For information about the VLCJ project and how to use VLCJ read on the original developers webpage:<br />
http://capricasoftware.co.uk/#/projects/vlcj

For information about VLC check their webpage:<br />
https://www.videolan.org/vlc/

## License ##

VlcjWind is provided under the GPL, version 3 or later.
No other licens possible since Vlcj is GPLv3+.
