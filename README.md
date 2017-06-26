# add-backend
Backend challenge

Hello, there.
This is LJ, and I have just updated the project information.
I intend to add a docker container, with a minimal installation to run the application, i.e. an ubuntu, JVM and my jar file.
As soon as I have tested it, I will update this README with the steps to download my container.

We explain at the bottom of the file how to modify or build from scratch the JAR file.

Note: to address this challenge, I have used my own docker container based on ubuntu minimal image. I added SSH, Oracle JVM, JavaDB and VIM as text editor.

Cheers!

----------------------
Command Line Arguments
----------------------

Example:  java -jar flights.jar


--------------
User interface
--------------

Once the java application is launched, it will ask us for a data file:
        Introduce filename with data:
        The text file is filled with the connections between airports (one way trips)
        As for example, let's create a data file called data.txt, filled with the following info:
NUE-FRA-43
NUE-AMS-67
FRA-AMS-17
FRA-LHR-27
LHR-NUE-23

This information is already stored in data/data.txt.

The application will first store data into a local database. In case there are no format errors, a menu will be presented to the user, as follows:
Options available for search
1 - Search price for a flight
2 - Search cheapest conn between A and B
3 - Search num of flights between A and B with maximum 3 stops
4 - Search num of flights between A and B with minimum 1 stop
5 - Search num of flights between A and B with exactly 1 stop
6 - Search num of flights between A and B with exactly 2 stops
7 - Search all flights between A and B which cost is below X

We can choose any option between 1 and 7. If we choose an inexistent option, a message will be presented and the menu will appear again.

In case we choose any option, as for example, 1, a brief explanation of the steps to follow will be presented
Example:
Options available for search
1 - Search price for a flight
2 - Search cheapest conn between A and B
3 - Search num of flights between A and B with maximum 3 stops
4 - Search num of flights between A and B with minimum 1 stop
5 - Search num of flights between A and B with exactly 1 stop
6 - Search num of flights between A and B with exactly 2 stops
7 - Search all flights between A and B which cost is below X
1
Introduce complete flight info: Example NUE-LHR-BOS
FRA-LHR-NUE
50

The last line shows the cost of the trip, if exists. If it doesn't exist, a message will be presented to the user
After 3 seconds, the main menu will appear again.

-------------------------
Design Decisions & Issues
-------------------------

The application has been coded according to three main assumptions.

Design in accordance to layers:
 ----------------
| User Interface |
 ----------------
        |
   ------------
  | Data Logic |
   ------------
        |
     ------
    | Data |
     ------

Use of DDBB and standard SQL for data management, mainly for scalability and perdormance of the solution. JavaDB has been used, as it was a design requirement to embed the whole solution into a JAR file.

Error management via exceptions. We have defined a hierarchy of two exceptions, FunctionalException and TechnicalException. A more detailed design should consider a more detailed hierarchy of exceptions.

CLASSES:

FunctionalException. Our Exception for errors of functionality.
TechnicalException. Our exception for technical errors.

Data_Class. Data objects which store data sets coming from file, and being stored into DDBB.
DDBB_Data. Data objects which store data sets coming from DDBB

FileToDDBB. Class to store file into DDBB.

DDBB_Queries. Class with SQL abstraction.
DAO. Database Access abstraction.

Cli_Interface. Class to manage User Interface.

FlightsSearch. Main class.

Class Hierarchy:

                 -------------
                |FlightsSearch|
                 -------------
                        |                --------------
                        |               | DDBB_Queries |
                        |                --------------
                        |                       ^
                        |                       |
                        |                       has
                        |                       |
                        |                       |
                        |                -------------
                         - has a -----> |      DAO    |<--------
                        |                -------------          |
                        |                       ^               |
                        |                       |               |
                        |                       has             |
                        |                       |               |
                        |                       |               |
                        |                --------------         |
                         - has a -----> | Cli_Interface|        |
                        |                --------------         |
                        |                                       |
                        |                                       |
                        |                                       |
                        |                                       |
                        |                                       |
                        |                -------------          |
                         - has a -----> |   FileToDDBB |--------
                                         -------------


DDBB:
There are two classes for database management, DDBB_Queries, where every SQL is managed, and DAO, a Data Access Object that is the abstraction of the database model and access.

The database model is based on a table for point-to-point trips, and a hierarchy of up to four levels trips over it, each level base on the previous one plus the combination with the base options. This hierarchy is made up with SQL VIEWS, in order to avoid having to use physical space for consecutive iterations.

 ------       ---------       ----------       ----------       ----------
| MAIN | --- | 1 SCALE | --- | 2 SCALES | --- | 3 SCALES | --- | 4 SCALES |
 ------       ---------       ----------       ----------       ----------
   |              |               |                 |                |
   |--------------                |                 |                |
   |------------------------------                  |                |
   |------------------------------------------------                 |
    -----------------------------------------------------------------

In this way, the complexity of flights combinations and conditions is managed by a series of simple SQL sentences, and the results are agregated/evaluated by the code.
The example manages a few trips, but the architecture could manage hundreds of thousands of combinations without major issues.

Exceptions:
Management of exceptions to control errors. The errors have been clasiffied as functional and technical errors.
A functional error is linked to the use of the application. For example, improper data filled by the user will throw a functional exception.
A technical error comprises the rest of the errors than may appear.
For example, a non accessible database.

----------------------
Potential improvements
----------------------

As this is just a POC, I see many opportunities of improvement, obviously.

A suggestion would be to create dynamically-based view depth instead of statically as it is done now.
Another improvement would be to replace JavaDB with any other relational database.
It would be necessary to modify database connections, and review SQL exceptions management, in case the behaviour of the database is different than javaDB.

Exceptions management have a lot o margin of improvement.

-------------------
Extra documentation
-------------------
 
The jar file includes JAVADOC documentation for the project

---------------------
Modifying the project
---------------------

There are a few things which are included in the JAR file, but are not part of the GITHUB project.
First: JavaDB libraries.
In case you want to use the code, you should install JavaDB in your environment. I suggest to follow the steps described in here: http://docs.oracle.com/javadb/10.8.3.0/getstart/index.html.

Create a new jar:
I have followed these steps.
cd source
javac FlightsSearch.java
mv *.class ../
javadoc -d ../javadoc FlightsSearch.java

In order to embed JavaDB drivers into the project, I have decided to expand derby.jar in the project root directory.
jar xvf derby.jar .

And we can now re-create our jar file
jar cf flights.jar *.class META-INF data javadoc org

Let's add an entry point
We create a config file called manifest1, and we write there the following config parameter:
Main-Class: FlightsSearch

Once done, we modify JAR MANIFEST file:
jar ufm flights.jar manifest1

And that's all!. We can now test our new jar!
java -jar flights.jar




