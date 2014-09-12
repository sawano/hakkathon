#Mini hAkkathon

##Intro
Get to know the basics of Akka in a fun and playful way.


##Preparations:

- Java 8 compatible JDK
- Maven
- The source code will be available from GitHub so Git or a Git client will help but is not necessary
 

##Points of interest

- Javadoc in `Racecourse` explains game basics
- Choose template based on your difficulty level (i.e. L1, L2, L3)
- Interface `DriverApplication`
- Interface `RacingStrategy`
- Akka documentation http://doc.akka.io/docs/akka/2.3.5/java.html is very helpful
- You can use the DriverActorTest to test your creation
- The examples uses the `akka-message-java` library for defining actor behavior. The interested reader can find out more at https://github.com/sawano/akka-message-java


##Project modules
- The `driver` module is where you will work
- The `model` module contains common objects
- The `racecourse` module contains the 'server' implementation
- The `web` module contains a simple web interface to the racecourse server

For a quick introduction to Akka, watch the first 6 minutes of this presentation: http://parleys.com/play/5302014de4b0c04daeac9df5

##Running the code
To start the server you simply run the main method in `ScoreBoardController`

To test your driver implementation you can use the `DriverActorTest`. Just replace the actor class with your implementation.

If you want ot place your server outside your local subnet then edit the 'hostname' and 'server-ip' values in 
`application-racecourse.conf` and `application.conf` respectively and change the way racecourse lookup is performed (see javadoc).

