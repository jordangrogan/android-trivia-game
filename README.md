# android-trivia-game
A trivia game for Android for a Pitt CS Project


# Assignment Description

[Source](http://people.cs.pitt.edu/~skhattab/cs1699/A1.html "Permalink to ")


| ----- |
| 

![/afs/cs.pitt.edu/usr0/skhattab/public/html/cs1699/index_files/image001.png][1]

**CS 1699 Special Topics in Computer Science (Section 1115)**

**Introduction to Android Programming **

Spring 2018

 

 | 

**Assignment 1 - Android Trivia Game**

 

**Deadline**: Monday 2/12 @ 11:59pm

**Submission instructions:** Submit a single zip file containing your code and resources to CourseWeb. To package your code as a single ZIP file, you may select the menu option File -> Export to Zip File in AndroidStudio.

 

**Required Knowledge: **This assignment requires knowledge of Activities, Intents, Preferences, Resources, Simple File I/O, GUI widgets, and MediaPlayer.

 

In the first assignment, we will build an app for a simple Trivia game on Android terms and definitions. The app should have at least four activities as shown in the figures below.

 

The Start Activity has three buttons and a switch to turn ON and OFF background music. You may use any mp3 file of your choice.

1.     The PLAY button opens the Play Activity.

2.     The ADD WORD button opens the AddWord Activity.

3.     The SCORE HISTORY button opens the ScoreHistory Activity.

 

The image below is an example of the Start Activity.

 

![][2]

 

The Play Activity shows a word (dex file in the picture) in a TextView and five choices (in a ListView) for the definition of the word. Both the word and the answers are selected randomly from the set of terms and definitions of the game. 

 

![][3]

 

You need to make sure that the correct answer is there and that the choices are shuffled randomly. Once the user selects an answer, a Toast message appears, the score of the player is updated, and a new question appears. Five questions are shown before the Play Activity is closed and the user is back to the Start Activity. A progress bas (at the bottom) tracks the number of questions offered.

 

| ----- |
| 

![][4]

 | 

![][5]

 | 

 

You should start the set of words in the game by using a text file that you add under res/raw folder. When the user adds a new word, the word and its definition are added into another file, which has to be loaded as well to select questions from.

 

The AddWord Activity allows the user to enter a word along with its definition to be added to the game.

![][6]

 

 

Finally, the ScoreHistory Activity shows the highest score and a log of previous attempts. Both the highest score and the attempt log should be **persistent**.

![][7]

 

 

**Hints:**

 

1.     These [instructions on preparing your development environment][8] may be useful.

2.     An example APK file can be downloaded from the assignment page on CourseWeb. You can run it online as well [here][9].

3.     You may find the command adb shell run-as edu.pitt.cs1699.triviagame ls useful in accessing files stored inside the Android device. 

1.     The adb utility can be found under the platform-tools folder under the path where your Android SDK resides.

2.     edu.pitt.cs1699.triviagame is the package name of your app

3.     ls is the UNIX ls system program.

 

[1]: http://people.cs.pitt.edu/index_files/image001.png
[2]: http://people.cs.pitt.edu/A1.fld/image001.png
[3]: http://people.cs.pitt.edu/A1.fld/image002.png
[4]: http://people.cs.pitt.edu/A1.fld/image003.png
[5]: http://people.cs.pitt.edu/A1.fld/image004.png
[6]: http://people.cs.pitt.edu/A1.fld/image005.png
[7]: http://people.cs.pitt.edu/A1.fld/image006.png
[8]: http://web.stanford.edu/class/cs193a/android-studio.shtml
[9]: https://appetize.io/app/chrhzwjjjmngb2w2chm36yvfwg?device=nexus5&scale=75&orientation=portrait&osVersion=7.1

  
