# Developer Documentation

Team8Paper is a rich web application crafted in Javascript and JQuery on the client side, Scala on the server side, and MongoDB on the persistence side. These technologies were chosen due to familiarity by the 3 members in our team, and they are high performance technologies used to build modern web applications. From a high level, Team8Paper has a client-server architecture
with clients sending requests to the server by making HTTP calls. The server uses an MVC architecture with packages controllers, models, and views organizing the source code. Between the client side and server side, JSON, REST, and non-blocking are three attributes emphasized in the design due to their simplicity. Input and outputs are JSON when possible, routes follow REST for easy communication, and server and client handles requests asynchronously so that the application exceeds at scaling. There exists two other packages which are services and util. Services contain functions that are closely tied to application such as a holding a database adapter. Util contains pure functions that help to refactor duplicate parts of code and are usually widely applicable such as generating UUIDs and ObjectIds.

# Architecture

Our web application is built on top of Play!, a web framework, which manages resources and runs our application. All our code are loaded by Play!. On the client, we use Play!'s scala html templates. Javascript and CSS resources are stored in assets or public folders. The assets folders
provides better management by using Google's closure compiler to optimize the javascript code in the directory. LESS, which is compatible with CSS,
and offers more features is the default used. LESS files are compiled to .min.css files by Play!. Libraries such as JQuery and Bootstrap which are already optimized, and images are stored in the public directory or linked externally, for static assets.

On the server, we depend on two libraries in addition to Play! which are Reactive Mongo and filters. Filters is a dependency used to create application wide conditionals to handle requests. Our application has 3 filters found in Global.scala and they run in order mentioned. The HttpsFilter ensures client is connected to application vs HTTPS on production. Next, the Api1Filter handles url paths beginning with /api1 with a json output to be consistent with valid /api1 calls instead of html responses such as 404 Not Found or Forbidden when accessing other parts of application. Lastly, the GzipFilter saves network bandwidth by compressing responses. ReactiveMongo is the sole database driver used for persisting users, papers, and all models used. We use a play-plugin which is a wrapper over ReactiveMongo for easier interoperability with Play's JSON models rather than dealing with converting between ReactiveMongo's BSON objects, case classes, and Play's JSON objects. With the play plugin, the data flows between case classes and JSON, and between JSON and BSON. An important feature of Reactive Mongo is that it is an asynchronous database driver. It is non-blocking, meaning calls with ReactiveMongo do no block other processes allowing maximum processor efficiency.

On the persistence side, data are stored as BSON, similar more typed and compressed version of JSON, documents in MongoDB, a NoSQL database. Using a NoSQL database is simple and fits our design. We have two collections. The user collection stores user information. Then the paper collection stores individual papers, which is the central feature of our application. 

# Detailed Design
- TODO
Decompose the high-level architecture into lower level classes and document their dynamic behavior. If a non-OO application, document the static and dynamic behavior of the application. Typically, for UML this would be a class diagram and any of: state diagram, sequence diagram, collaboration diagram, or activity diagram.

For web applications, this might include a page navigation graph, along with summaries of the scripts running on each page.

Note that you don't need to provide detailed UML diagrams of your classes, unless they add information that can't be gleaned from documentation that lives in the source code itself. Low-level design details are best kept in the code in the form of Javadoc comments or similar. Javadoc package documentation is an excellent place to put architectural documentation. Also, if your in-source documentation is sufficiently detailed, you may not even need external design documents at all -- the architecture could be presented in the index page of the Javadocs.

# Data Storage
Data is persisted to MongoDB, a NoSQL database.
- Please see DataStorage.md

Many systems involve the management of persistent data, either through files or a database. How the data is organized is another aspect of design, and its expression is called a data model. If you have taken a database course, then you are already familiar with Entity-Relationship (ER) diagrams. These contain virtually the same concepts as do UML class diagrams, and the latter can be used if you are unfamiliar with the former. Your design documentation should include one of these visual descriptions of how any persistent data will be stored.

If your application uses flat files or XML data files, then the format for those files should be presented and documented.

# User Interface
- Please see UserInterface.md

Many modern systems are interactive. That is, the user interacts with the system using some form of display screen using which data can be entered and results presented. Well-designed UIs are essential to successful systems because they are the most visible part of the system. As such, it is important to get early and frequent feedback from users about their reactions. This can take the form of a prototype, but if this is unfeasible, then mocked up screen shots can be substituted. Your design documentation should include a description of your user interface design. You should explain what the viewer is looking at for each screen. Even command line apps have a UI and you should describe the command line options. If you are making a library rather than an application, then explain your API. Developer documentation should include a sory board, page-flow graph, or some other sort of high-level overview of all the visual "screens" in the application and how they fit together (e.g., which pages link to a page and how).

# User Documentation
- Embedded in web application. Usage should be intuitive with different levels of usage from beginner to advanced. Answers can be found by poking around the application or by reading short descriptions associated with user actions.
