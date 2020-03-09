![alt text](https://github.com/Bash-/connect-four-game-java/blob/master/src/img/ConnectFour0.5.jpg?raw=true "Connect Four Game")

### Connect Four Project 
#### 2014-2015
###### By [Bas Hendrikse](https://github.com/Bash-) & [Martijn Gemmink](https://github.com/Martinootje)

Run the following classes the use the program:
- src/control/ConnectFour.java  (To open a client)
- src/server/control/ServerStarter.java (To start a server)

--Make sure the text file Leaderboard.txt and LeaderBoardTest.txt are present in the root directory of the project--

To play an offline game do the following:
- Run ConnectFour.java
- Fill in the username field for user 1
- Fill in the playertype field for user 1
- Fill in the username field for user 2
- Fill in the playertype field for user 2
- Press Offline Game

To play an online (networked) game do the following:
- Run ServerStarter.java
- Fill in a port to start the server on
- Run ConnectFour.java
- Fill in the username field for user 1
- Fill in the playertype field for user 1
- Fill in the IP address of the server (localhost if the server runs on the same machine)
- Fill in the same port as filled in at the server
- Press Online Game
- Optionally a delay for moves can be filled in the delay field, the delay is in milliseconds
- Repeat step 3 till the step before this step to open another client and start a game with two players (so fill in the fields for user 1 for both Clients)

Original repository URL: https://github.com/Bash-/connect-four-game-java
