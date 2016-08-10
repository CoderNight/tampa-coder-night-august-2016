#Bot Trust

This is an entry for CoderNight Meetup August 2016 as described at https://code.google.com/codejam/contest/975485/dashboard.

#Goal

I wanted to see if I could crank out an Android based solution in an evening. Mission accomplished if you ignore that the result is very rudimentary with lots of very poor coding style and only manual animation.

# Algorithm

The algorithm itself not very complicated and is implemented in the run method of the simulateStep Runnable in the file BotTrust/app/src/main/java/org/tbadg/bottrust/MainActivity.java . The basic idea is to simultaneously walk three lists: one with the color sequence of button pushes, and a pair of lists with the button numbers of each bot's button pushes.

#How to Run

There's a pre-built APK in ./BotTrust/app/build/outputs/apk/app-debug.apk .

#How to Build

The project can also be built from source with any recent version of Android Studio. You may need to tweak the Gradle and Android SDK versions to match what you have installed.

#How to View Without Building or Installing

There are a pair of video files in BotTrust/work. One shows a simulation of a single case running in standard speed. The other shows a batch simulation for all of the small cases; it runs at a much quicker speed.
