# Project
## Environment & Tools
Ubuntu 20.04, IntelliJ IDEA 2021.3.1 (Ultimate Edition), Azul Zulu JDK 15.0.8, Apache Maven 3.8.5, Git 2.25.1

## Purpose
The purpose of this project was to demonstrate a personal understanding of building event-driven Java
Swing GUI applications, using the Model View Controller architecture, and Maven project build system.
It was also to demonstrate threadsafe synchronized concurrent processing, correct implementations of
different design patterns, and usages of the Streams API. To demonstrate this, an application needed to be
designed and built. The code should provide self-documentation, and all classes and methods needed to be commented on with Javadocs.

The concrete requirements for grade A were:

* The application needs to be an event-driven GUI application
* There needed to be a minimum of 5 concurrent processes in the application
* There needed to be at least 3 threads accessing a shared resource in a threadsafe way
* There needed to be at least 6 different Swing components in use
* There needed to be at least 4 different Swing layouts in use
* There needed to be 6 design patterns implemented - these are:
  * Object Pool pattern
  * Observer pattern
  * Producer /Consumer pattern
  * Template method pattern
  * Abstract factory pattern
  * Factory method pattern
* There needed to be 3 unique implementations of the Streams API.
* The application needs to be built using the Model-View-Controller (MVC)
  architecture
* The project has to be built using Maven, with a compiler target no higher than JDK 15.
* The project has to be able to produce a runnable JAR.

All requirements did not have to be implemented in a way that showcased best practice use,
showcasing an understanding of the requirement was more important.

## Procedures
One of the challenges of this project was to create the proper project structure using Maven. First, it was not clear
which JDK to select since the project needed to compile to JDK 15, but JDK 17 had been used before. This was resolved
by installing Azul ZULU JDK 15 and specifying 15 as compile target in the pom.xml.

The pom.xml structure was conceived from using earlier examples, removing plugins that were not relevant to this
build, adding the maven-jar-plugin, and customizing relevant tags to fit this project.

Another challenge was choosing a project idea with a suitable scope, complexity, and room for design patterns and other
requirements. Choosing a pong-based game with balls was the initial idea since it provided many logical opportunities for
implementing design patterns, and the design was iteratively updated to accommodate all the requirements.

### Constants.java
There is a package “constants”, that contains the Constants.java interface. This interface
contains constants that are used throughout the program.

### UltimateSurvivalPong.java
This is the entry point for the application. It uses the static method determineFrameSize() to
extract the screen size, and determine the size for the frame using the Toolkit class.

This dimension is then passed as arguments when instantiating GameFrame and PongModel, which
are then passed as arguments when instantiating the PongController.

The view is packed, centered, and rendered visible using a runnable lambda which is passed to
the EventQueue to be executed on the EDT.

Initially, it was difficult to know how to treat the frame size, since it was the basis on which the
"game world" would be built, and used by both model and the view. At first, it was the responsibility
of the view to calculate and access the screen size, but it was later refactored to be done immediately in
the main method of UltimateSurvivalPong and instead be passed as an argument to both the model and the view - since
they both required it.

### Controllers
In this package, there exists the PongController class, the Observer interface, and the Writers package.
At first, it was unclear whether to place the Observer interface and the writers in the Controllers package,
but since PongController is the only implementer of the Observer interface, and the writers only write
data to a file and are not a part of the model, they were placed here.

#### Observer.java
This is an interface that only provides the abstract method update() which is
required for an observer in the Observer pattern.

#### PongController.java
This is the controller of the application. It implements the ActionListener interface,
which gives the actionPerformed() method. This method is executed for each tick of the Swing timer actionTimer and
makes up the game loop. In the game loop, the controller controls all execution of backend calculations
by accessing the model and controls all updates to the view.

The controller also extends KeyAdapter, which is a KeyListener adapter class
that allows for selective implementations of only the needed methods defined in KeyListener. This
allows the controller to also control the key presses and key releases from a user of the GUI.

In the constructor, the controller is assigned as KeyListener to the view, and the startGame() method is
defined as the PlayButtonListener for the view. This together with handling the input
from the GameOver popup via the displayLoseMessage() method makes sure that all user input is handled in the controller.

Keypresses are handled in the keyPressed() and keyReleased() methods, by matching the keycode with the execution of the
correct paddle velocity.

Playbutton presses are handled in the startGame() method, which manipulates the GUI, calls the relevant
model operations and starts the actionTimer which drives the game.

The input from the GameOver popup is handled by matching the user choice with the execution of the
model.writeResult() using the correct subtype of AbstractResultWriter.

Initially, I had the controller define some operations which should have been in the model, for example
moving balls and moving paddles. This would be a breach of the separate concerns dictated by MVC, and this was
resolved by refactoring these responsibilities back to the model.

#### Result writers
These are classes responsible for writing the results of a game into a file. The base is the
AbstractResultWriter. This class defines how the writing should be done in the
write() method, but defers the definition of constructResultString() to subclasses. This is done according to the
template method design pattern. The writing done by the helper method writeData() does so by creating a separate thread.

ResultAndDateWriter() builds a string that includes both the result and date using the Streams API, while ResultWriter() builds
a string containing only the date.

### Models
In this package there exists the PongModel class which is the main model for the game. The model makes use
of the other classes included in the package. It includes the balls package, which
contains all classes defining the models for balls, and the factories for their creation.
There is also the Subject interface which is a part of the Observer design pattern, and
the Bound enum which only lays out the X and Y for use throughout the model.

#### Subject.java
This is an interface that provides the abstract methods required for
an observer in the Observer pattern.

#### Balls
These are the classes responsible for representing balls in the game. The base is the
AbstractBallModel which defines all properties a ball should have, such as the dimensions, position,
velocities, and randomness factors, if a ball has been missed. It provides a public
interface for accessing these fields, changing velocity, and moving the ball.

Internally the ball has methods for detecting being missed, detecting bounces, paddle hits
and randomizing its X and Y velocity - which is done in response to hitting the roof, the floor or a
paddle.

The concrete balls are made up of these classes:

* EasyBigBallModel.java
* EasyMediumBallModel.java
* EasySmallBallModel.java
* HardBallFactory.java
* HardBigBallModel.java
* HardMediumBallModel.java
* HardSmallBallModel.java

Each of these extends the AbstractBallModel and initializes itself with a size and randomness factor
found in the Constants interface. This gives the ball a "Difficulty" justifying the classifications
as hard and easy balls, as well as a size that makes them big, medium, or small.

#### Ball factories
These are the classes responsible for creating balls for the game. The base is the AbstractBallFactory
which provides a method for creating a random ball, and defers the implementation of the three factory methods
createBigBall(), createMediumBall() and createSmallBall().
It also provides a method for setting the level of the factory. This level is used to determine the difficulty of the balls produced.

The concrete factories are EasyBallFactory and HardBallFactory. These factories
provide an implementation for creating the balls, and the balls created belong to the same
difficulty family, which is Easy or Hard. This is done according to the Abstract factory pattern and the factory method pattern.

The balls are created for the game by randomizing the X and Y velocity
using the current level as a factor, and therefore determining a part of the difficulty
which is the speed of the ball. It then waits for a ball of the desired type
to be available in the ballPoolService collects this ball by passing a ConsumerTask to the
threadPoolManager (As a part of the Producer/Consumer pattern) and then
sets the position and speed of the ball, and returns it.

#### BallPoolService.java
This is the class responsible for providing pools containing balls to be
used in the game. It provides a public interface for producing, consuming, returning balls to, and checking the state of
a certain ball pool. This is done according to the Object Pool pattern, and the Producer / Consumer pattern.

The balls are contained in LinkedBlockingQueues which ensure thread-safe concurrent access since both producer and consumer threads access these at the same time. These queues are stored in a hashmap in which the keys are
generic classes extending the AbstractBallModel, and the values are LinkedBlockingQueues containing objects of a generic
type that extends AbstractBallModel. The insertion of these pools into the hashmap is done via
a private insertion method that employs the generic type T, to ensure that the passed key is of the same type
as the objects in the queue passed as a value.

The ball pools are accessed for producing, returning, or consuming by the public accessor methods which take class objects as parameters.
These class objects are then used as keys for getting the right ball pool. When producing a ball, it is done
through the reflection layer by instantiating a new object of the passed class.

Initially, there was some difficulty in implementing this class both in terms of generic programming, and the use of the
reflection layer. The difficulty originated from keeping all the different pools inside a map and wanting to access all these
through single methods. This required the use of generics to allow the processing of different types, and reflection
to instantiate objects from class objects. At first, the balls couldn't be instantiated and trying to do so
resulted in the program hanging without any error message. The problem was resolved by
making the constructors of the balls public so that they could be found when executing the .getConstructors() method
on a class object. The generic structure issues were resolved by using wildcard generics for the types
in the hash map, to allow for the insertion of different classes/objects extending AbstractBallModel, and the use of type parameters
and casts for the accessor and mutator methods to ensure that the type of ball requested was the type of
ball delivered/produced.

The getAmountOfAvailableBalls() method uses the Streams API to generate
its returning map.

#### ConsumerTask.java and ProducerTask.java
These are the classes responsible for producing/consuming balls from/for the ball pools in BallPoolService. For ConsumerTasks,
this is done by implementing the Callable interface with the type AbstractBallModel. The overridden call() method returns the result of
the ballPoolService.consumeBall(this.ballType), which is a ball of this.ballType. For ProducerTasks, this is done by
implementing the Runnable interface. The overridden call to run() calls ballPoolService.produceBall(this.ballType).

The ConsumerTasks and ProducerTasks produce and consume resources (balls) according to the Producer/Consumer pattern.
Instances of these classes are passed to the ThreadPoolManager for execution.

#### PaddleModel.java
This is the class responsible for representing a paddle in the game. It defines all properties a
paddle should have, such as the dimensions, position, Y velocity, and baseline speed. It provides a public
interface for accessing/mutating these fields and moving the paddle.

#### PongModel.java
This is the class responsible for containing all game data and executing operations upon this data.
It provides a public interface for accessing/mutating the state of the game, which is used by the PongController
for driving the game. Since the game model is separated from the view, the game can exist and be played in the
backend with or without a corresponding GUI. The simulation is separated from the visualization of the simulation.

The model contains the bounds member, which contains the max X and Y values for the game. All components in the game exist within this coordinate grid.
The model has two paddles, a collection of balls, a ballPoolService, a threadPoolManager, two ball factories, and a collection of observers.
The ball factories deliver balls to the game, and these factories in turn use the ballPoolService for access to ball objects.
The threadPoolManager is used for scheduling ball-producing/consuming tasks.

The PongModel implements the Subject interface, which means it has an observable state. This state is the elapsed
seconds in the game and the current level. All observers are notified for each elapsed second, via the notifyObservers() method. This is done
according to the Observer pattern. Currently, in the game, the only observer is the PongController which implements the Observer
interface. The controller pushes the elapsed seconds and current level to the view, and checks for the nextLevel() in the model upon being notified.

When executing setInitialState(), there is a call to ensureBallSupply() which is a method
that ensures that the ball pools have a sufficient amount of balls, before any balls can be added to the game. If the ball amount is too small for any pool, new
ProducerTasks are instantiated and passed to the threadPoolManager for execution. To ensure that these
ProducerTasks are completed before moving forward with ball creation, the awaitProducerFutures() method is called,
which waits until all future producer tasks are completed. The producer tasks are stored in completedProducerTasks, which is a List
that is shared with the threadPoolManager, which inserts the futures into it after execution.

Balls that are missed in the game, are collected in the collectMissedBalls() method, which makes use of the
Streams API for filtering balls by the wasMissed boolean flag.

The PongModel has a hardMode flag, which decides if the game is played in hard mode. The method checkIfNextLevel()
checks if a certain amount of seconds has elapsed, and if true, makes a call to nextLevel()
which increments the level. If the level is higher than 5, hardMode is set to true. Hardmode impacts
the game by ensuring two balls are added for each level, and that these balls come from the
hardBallFactory. The method addBallsToGame() is responsible for adding balls into the game,
and it checks if hardMode has been enabled, and depending on its value, sets the current level to the right factory
and calls for ball-creation.

The game is over if no balls exist in the game, and this state is determined by isGameOver().

#### ThreadPoolManager.java
This class is responsible for managing and providing access to the two ThreadPoolExecutors. These are used
for queuing runnable or callable tasks for execution. The class provides the public execute() method as
dictated by the Executor interface. This method is used for queueing runnable tasks, which in the current implementation
is ProducerTasks. It also provides the public executeCallable() method which is used for queueing
callable tasks, which return a future result. The tasks are queued into BlockingQueues owned by each
ThreadPoolExecutor, the submitted tasks are stored in the tail of the queue, and tasks are removed for
execution from the head. This makes up an implementation of the producer/consumer pattern where the ProducerTasks
and ConsumerTasks act as producer threads (produce tasks), and the pool threads act as consumer threads (consume tasks).

Initially, it was difficult to decide which interfaces the ThreadPoolManager should implement for a logical
functionality as a thread pool manager. The Executor interface provided a method for submitting runnables, but no methods
for submitting Callables. This was resolved by implementing a custom method which is the executeCallable() method.

### Views

#### GameFrame.java
This is the class responsible for containing the Swing GUI of the application. The class itself is a JFrame and
contains Swing components which are 5 JPanels, 5 JLabels, 1 JButton, 1 JCheckbox, and 1 JOptionPane. This frame contains
all the panels, and these panels contain smaller components. These are initialized and built using the
initUI() method. The class provides a public interface for accessing/mutating the graphical representations
of the game components in the model, such as the paddles, balls, and elapsed seconds as well as repainting the game panel.
This allows the PongController to push updates to the view components when something has changed in the model, repaint,
and in that way drive the visualization of the game. The GameFrame also provides public methods for adding listeners to the play button and key inputs - this
allows the controller to assume control of the user input both in terms of button clicks and key input. There is also the
public displayGameOverMessage() method which takes user input upon game over and transfers it to the controller for handling.

#### Panels
The view makes use of three panels extending JPanel, which are GamePanel, InfoPanel, and ScorePanel.
GamePanel overrides the JComponent.paint() method to drive the game animation. It updates the positions
of the paddles, as well as the balls by using the Graphics.fillRect() and Graphics.fillOval() methods
with the latest size and position information supplied by the controller. The InfoPanel is used to contain the game information such as the instructions,
the controls, the play button, and the checkbox for the game. The ScorePanel is used to contain and display game information which is the time survived and the current level.
This information is supplied by the controller upon changes in the model.

## How the application should be executed
The application can be executed by running the UltimateSurvivalPong.java main method from IntelliJ IDEA, or by compiling a runnable JAR
using "mvn clean verify" - and then executing the JAR using JDK 15. The game is started by pressing the "Play" button
and is terminated by pressing the X button to close the window. There are instructions in the GUI for how to play the game.

## Discussion
### Fulfillment of purpose
The purpose has been fully fulfilled and the concrete requirement for grade A has been met.

#### Event-driven GUI application
The application is entirely driven by the user, the user starts the game, exits the game, and makes choices
via user input, and controls the paddles.


#### Concurrent processes in the application
There are at least 5 concurrent processes in the application:

* The main thread
* The EDT thread
* Multiple Producer Threads
* Multiple Consumer Threads
* The threads spawned when writing game results to file

The amount of active concurrent processes vary, but there are 5 different types of threads
in use throughout the program cycle, and it can be argued that there are at least 9 concurrent processes active at all times
after the game has been started from the UltimateSurvivalPong::main method - The EDT, and the 8 core threads residing in the
ThreadPoolExecutors of ThreadPoolManagers.

#### Threadsafe concurrent access of shared resources
At least 3 threads are accessing a shared resource in a threadsafe way since the ProducerTasks mutate the different
ball pools inside BallPoolService by being placed in the BlockingQueue of the producerThreadPool 5 at the time. These 5
producer tasks can be run concurrently by the thread pool since there are up to 16 threads in the pool available to run a task.

In PongModel.nextLevel(), calls are made to both ensureBallSupply() and addBallsToGame() at the same time. This makes it so that
there can be 5 ProducerTasks and 1 or 2 ConsumerTasks competing for access in a certain ball pool concurrently. This concurrent
access/mutation is threadsafe because the ball pools are LinkedBlockingQueues, which ensures that they can be concurrently mutated in a threadsafe way.

In PongModel.setInitialState(), after calling PongModel.ensureBallSupply() to produce balls, the futures of these tasks are awaited by a call to the PongModel.awaitProducerFutures()
method if any ProducerTasks were created. This ensures that the ProducerTasks are finished before ConsumerTasks are issued from the PongModel.addBallsToGame() method.
This ensures threadsafe concurrent access. This wouldn't be necessary, however - since the ball pools are LinkedBlockingQueues, which
on their own ensures threadsafe concurrent access. However - this ensures that none of the ball pools are empty the first time the
.addBallsToGame() is called.

#### Swing components and Swing layouts
There are at least 6 different swing components in use since there are uses of JFrame, JPanel,
JOption, JLabel, JButton, and JCheckbox. There are also at least 4 Swing layouts in use, since
Border, Box, flow, and grid layouts are in use.

#### 6 Design patterns
##### Object Pool pattern
This pattern is implemented in the BallPoolService class since it contains multiple pools
of instantiated objects ready for use in the game. It also provides a public interface
for accessing, producing, and returning these objects to and from the pool.

##### Observer pattern
This pattern is implemented between the PongModel and PongController classes through
the implementation of the Subject/Observer interfaces.

PongModel acts as the subject, to which the PongController is added as an Observer. When a change
of state occurs (A second passes in the game), the PongController is notified, and can in turn
pull new data from the model and push this data to the view. One can also argue that
there is an Observer pattern implemented between the GameFrame and the PongController
via a push mechanism, since the PongController pushes updates to the view - however there
is no straightforward interface implementation here, and it is more the behavior that could be likened
to an observer pattern.

##### Producer/Consumer pattern
This pattern is implemented between the ball pools in ballPoolService, the ProducerTasks, and the ConsumerTasks.
The ball pools are implemented as queues and produced ball objects are added to the tail of the queue, while consumed balls
are taken from the head of the queue - as the pattern dictates. The responsibility of producing and consuming is
divided between the concrete producer actors and consumer actors.

The pattern is also implemented between the ThreadPoolExecutors, their pool threads and the
Producer/Consumertasks. In this relationship, both the ProducerTasks and ConsumerTasks act as producers and
produce tasks that are submitted into the queue structures inside the ThreadPoolExecutors. These tasks
are then consumed by the pool threads.

##### Template method pattern
This pattern is implemented through the classes in the “writers” package. The template method
is the AbstractResultWriter.write() method, which is divided into the steps openFileOutputStream(),
constructResultString() and writeData(). A default implementation is provided for the
openFileOutputStream() and writeData() methods, while the constructResultString() implementation
is deferred to subclasses.

The classes ResultAndDateWriter() and ResultWriter() implement this method in different ways, resulting in some different result strings but a very similar shared behavior for writing to file without code
duplication.

##### Abstract factory pattern and factory method pattern
This pattern is implemented between the AbstractBallFactory and the concrete EasyBallFactory and HardBallFactory,
as well as the AbstractBallModel and all concrete subclasses of this.

AbstractBallFactory is an abstract factory, that provides methods for creating groups of products from the same product families.
These products are balls of different sizes extending the AbstractBallModel class. The two different families
are the Hard balls and the Easy balls. The product creation methods in AbstractBallFactory defer the implementation to
subclasses, which can then be implemented to return the correct type of product (ball).

Each of the creation methods in the AbstractBallFactory, createSmallBall(), createMediumBall() and
createBigBall() provides an interface for creating objects and allows subclasses to alter the type of objects
that will be created - therefore this constitutes an implementation of the factory method pattern as well. The
abstract factory pattern can in a way be seen as a grouping of related factory method patterns, which produces
products that belong together (Hard balls belong with other hard balls, easy balls belong with other easy balls).

#### Streams API
There are 4 unique implementations of the Streams API. It is used to initialize an immutable map in the PongModel constructor,
to parse the local date, replace dashes with underscores, and build a date-string in ResultAndDateWriter.constructResultString(),
it is used to build a new <<? extends Abstractballmodel>, Integer>>-map from the ballPools map in
BallPoolService.getAmountOfAvailableBalls(), and it is used to filter for balls having been missed
and constructing a new Arraylist of these balls in PongModel.collectMissedBalls().

#### MVC
The application has been built using the Model-View-Controller (MVC) since the game simulation (the model/backend) is completely
separated from the visualization (the view/GUI) and all communication between these entities are handled by the controller. All user input
is also handled by the controller.

#### Maven, JDK 15
The project is completely built using a Maven project structure and build system, and the game compiles
to JDK 15.

#### Runnable JAR
The game can be compiled and run from an executable JAR file.

### Suitability of the implementation
#### Class design
Overall, the class design of the BallPoolService is very complex, with extensive generics
use and reflection. Using Class objects as keys for the ballPools hashmap introduced much unnecessary
complexity, resulting in having to use wildcard generics <? extends AbstractballModel> as
the key for the hashmap, resulting in lacking type safety in the structure. (Example: The class object
EasySmallBallmodel.class and a LinkedBlockingQueue<HardBigBallModel> could be inserted as a
key/value pair in the structure, which would cause unwanted behavior in the game or breakage since the types
don't match). The insertBallPool() method was a strategy to improve this, but there can be no guarantee that this
method is used for inserting key/value pairs.

Using wildcard generics also resulted in having to generify the accessor methods, declare generic types,
and employ various downcasts to ensure the delivery of the correct objects and the satisfaction of the compiler.

A much easier design would have been to declare a shared enum with the different ball types and let each ball pool be its own declared instance field, instead of being encapsulated inside a hashmap.

Each ball pool could be accessed through shared mutator/accessor methods that would take these ball type enum values
and access/mutate the corresponding ball pool. Using an Enum would remove much of the unneeded generics' complexity as well as reflection.

Another idea could be to design a wrapper class for each pool with it having methods for accessing, producing, and returning its pool.
This would make the code very easy but would clutter up the class structure with many more classes.

Writing a class with generics and reflection provided a good learning opportunity to dive into some of the more
complex Java topics, which was good since much was learned about its workings. It can however be said that the
implementation was not so suitable, and the design is flawed resulting in unchecked casts which had to be suppressed, and odd type handling.
Much more needs to be learned to properly design generic classes and use the reflection API.

Another example of design flaws could be that there should be an encapsulating class for paddle information,
instead of manually handling Xs, Ys, Widths, heights, etc. GameFrame.createPaddle1(), createPaddle2() takes
an arbitrary array "PaddleInfo" which contains the information, while GameFrame.updatePaddles() takes 4 parameters
with the corresponding types of data. This is bad and should be made more coherent. Perhaps the PaddleModels
themselves could be passed from the model -> controller -> view instead of breaking out their positional data and
passing it over, for it then to be used when constructing a Rectangle. The use of the Rectangle component
feels a bit redundant overall since it is only used as a container of integers.

PongModel could have used the same Dimension object containing the game width and height, as the view does. But
it was instead converted into the bounds map to find an application for the Streams API.

The PongModel class especially has many hard dependencies, since many classes are instantiated and created
in the PongModel constructor. This is a big design issue and could be much improved using
dependency injection. The instantiations of different classes would have to be moved to the
main method, where they get the correct parameters and are then passed into the PongModel. Since many
classes in the model's package require a reference to the PongModel, they would also have to be redesigned
to not require the model as a construction parameter and instead be set by setter methods later.

The PongModel.executeCallable() has the return type Future<?>. Using wildcard generics
, in this case, is unnecessary, since all futures returned from this method would be Future<AbstractBallModel>. This
is because the consumerThreadPool ThreadPoolExecutor was designed to only handle ConsumerTasks and not any
callables. If the ThreadPoolExecutor was for general use, however, there could be no guarantees on what futures
it would return.

Abstractresultwriter.writeData() should not be modifying a search string by adding a new line as it does now
since the overall design of the AbstractResultWriter suggests that the entire string construction is the responsibility
of the constructResultString method. It is not honest about what each method does and lies to the client.

The instance fields in abstractBallFactory should be private and have accessor methods instead of being protected and directly accessed,
however, it would lead to a much more cluttered interface and much boilerplate code. The two members' sideLength and
randomnessFactor should however definitely be made private and have accessors/mutators instead of being the only two
members being inherited via protected.

It was a bit unclear where to place the “writers” package, since it did not seem to be related to the overall backend processing,
but rather extract data from the backend and print it to a file. It was placed in the “controllers” package, but a more active
choice could be considered to improve the structure.

The Rectangles representing the paddles could be placed in the GamePanel class instead of residing in the GameFrame
since these paddles are only relevant for the game taking place in the GamePanel. The accessors/mutators would then
have to chain their operations to the relevant operations residing inside the GamePanel instead, which would increase
the amount of boilerplate and interface clutter - but could be a more logical design.

#### Factories and balls
The subclasses of AbstractBallFactory do not instantiate ball objects, as the definition of Factories
dictates. This fact may serve as conflicting information about what the class does. Perhaps it should be named
a ball service or something other than a factory, ideally. On the other hand,
the factories act exactly like a factory for clients, since it provides ball objects, and should function fine
as long as clients don't have any acute reason for requiring that the balls are not reused, and are new instances.

To more closely adhere to the definition of a factory, a class that instantiates objects, this would have to be changed.
But to find a use for the Object Pool pattern, the factories were designed to instead get ball objects from
a pool and deliver them to clients, instead of creating them on the spot.

To more closely adhere to the abstract factory pattern, the abstract factory should be implemented as an interface,
which can enforce implementers to define the required creation methods. The AbstractBallFactory was made into an
abstract class, however, to reduce code duplication since the two concrete factories are very similar. The creation methods
could have been defined from an interface anyhow - resulting in the best of both worlds, minimizing duplicate code and also
being more scalable and enforcing method definitions.

AbstractBallModel was also made an abstract class to minimize code duplication, but the behavior of a ball
could have been defined by an interface instead of being inherited down. This would be designed according to the
composition over inheritance philosophy which would be more scalable and flexible.

The implementations of createSmallBall(), createMediumBall() and createBigBall() in EasyBallFactory and HardBallFactory
suffers from much code duplication, since many steps in their algorithms are identical. This could be a use case for the template
method pattern, where much of the method could have a general implementation,
and specific steps related to extracting a ball of the correct type, and casting could be deferred as a step implemented
by each subclass.

#### Regarding object pool/service/manager
BallPoolService got its name since it is more of a retainer/distribution service than it is a manager. It does not have a full life
cycle management, since it transfers ownership of ball objects through its public interface.

#### Thread pools, concurrency
Two ThreadPoolExecutors were used for the simultaneous execution of both ProducerTasks and ConsumerTasks, which would
result in more acute concurrent access to thread-safe structures. It was also done to prevent queued ConsumerTasks
from blocking later ProducerTasks in case the ConsumerTask would have been blocked due to a ball pool being empty. This could result in a deadlock
if the tasks never timed out from the ExecutorPoolService, and would likely break the game if they timed out.

ThreadPoolManager could implement a more extensive interface that inherently allows for the submission of callables
as well as runnables, and more advanced controls, such as terminating the thread pool, tweaking settings, etc. However,
there was no need for these functionalities in this simple application

#### Graphics
Since the Swing timer is used to drive the game loop, it was not possible to ensure updating each 1000/60 seconds, since
it only takes an integer value as a delay. Because of this - there is a slight lag in the animation.

To make a more smooth and properly designed game, a real game loop implementation with a dedicated thread handling
the game loop could be implemented - but it was deemed non-essential due to a lack of time.

In my implementation, the paint() method was overridden. This is not according to best practices - and instead the paintComponent()-method should have been overridden.

#### Overall
The game could be made more fun with another ruleset - there is an easy strategy to survive in the game for long, and that is to only
ensure the first ball stays in the game. Since it is so slow, it's pretty easy to survive for long. The GUI could also be made to 
look more interesting, but this was skipped due to time constraints. 