# Auto-Irrigration-Mobile-App
### What is the application about?
* This app is created for my assignment in Project Manager course of RMIT university.
* Our project is about the auto-irrigration system. We will place some moisture sensors which will measure the moisture of the soil in the ground and send the data back to the server of the system. The data will be stored in a database. There are also pumps which are connected to the server and receive order from the server (activate and deactivate).
* Users can use either mobile application or web application to interact with the server of the system. Users could read the current moisture, set the the mode for the pumps (either **_Manual_**  which users have to turn on and off the pumps manually, **_Auto_** which the pumper will be automatically activated whenever the moisture level is under minimum value (can be set by users) and deactivated whenvever the moisture reaches the maximum value (can be set by users), or **_Timer_** which users could set the time and date that they want the pump to activate and set the moisture level to deactive the pumps)

### Which features is applied in the application?
* Sliding menu
* Connect to web server database, get data from database and update database
* Different Android layouts and features such as Fragment, Grid Layout, List View, Relative Layout, Linear Layout, e.t.c

### How to run the application?
* This application is designed to get and modify data of web server database. Therefore, in order to run the application, a server should be run. That server should have a database which is named 'watering'. Then, file Database.sql should be run to set up the database.
* When database is set up, the first screen of the application is a Log In screen. This screen requires users type in the IP and Port of the host that contain the database. Given right IP, Port and set up database correctly will help to run the application.
