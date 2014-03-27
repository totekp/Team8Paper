Developer Documentation

Sometimes called design documentation, developer documentation enables a new team member, or a future maintenance and upgrade development team to understand your software. Developer documentation should include the following elements in some form. For each element you should include not only the chosen design, but the rationale for choosing that design over alternatives.

Architecture

Architecture is the highest, most abstract level of design. It should give a representation of how your application will interact with external entities and how it is organized. We talk about architecture all the time with standard terms like: Client-Server, Layered, Event-Driven, or Pipe and Filter. These are commonly called Architectural Styles. Your application may use one or more of these to help you achieve an optimal design for your system. Be sure to break down components as necessary. For instance a thin client might have just two boxes at the top (Client and Server) , but the Server may actually have a complex architecture of its own.

It is important to provide both a static and dynamic view of your architecture. A static view might, for example, be expressed with a UML Package diagram. It can also be a "box and arrow" diagram where the boxes represent major components and the arrows represent relationships between those components. A dynamic view could be a system-level sequence diagram or a textual description of how functionality is realized at the architectural level. Each component should be identified as to what functionality it provides and how it interoperates with other components to form a working system. The dynamic description can be a textual walkthrough of the architecture along with a scenario, or it can be a sequence diagram. Your goal for the dynamic architecture is to understand how the components in the static architecture work together at runtime. Usually one scenario walkthough is sufficient to do this.

Detailed Design

Decompose the high-level architecture into lower level classes and document their dynamic behavior. If a non-OO application, document the static and dynamic behavior of the application. Typically, for UML this would be a class diagram and any of: state diagram, sequence diagram, collaboration diagram, or activity diagram.

For web applications, this might include a page navigation graph, along with summaries of the scripts running on each page.

Note that you don't need to provide detailed UML diagrams of your classes, unless they add information that can't be gleaned from documentation that lives in the source code itself. Low-level design details are best kept in the code in the form of Javadoc comments or similar. Javadoc package documentation is an excellent place to put architectural documentation. Also, if your in-source documentation is sufficiently detailed, you may not even need external design documents at all -- the architecture could be presented in the index page of the Javadocs.

Data Storage

Many systems involve the management of persistent data, either through files or a database. How the data is organized is another aspect of design, and its expression is called a data model. If you have taken a database course, then you are already familiar with Entity-Relationship (ER) diagrams. These contain virtually the same concepts as do UML class diagrams, and the latter can be used if you are unfamiliar with the former. Your design documentation should include one of these visual descriptions of how any persistent data will be stored.

If your application uses flat files or XML data files, then the format for those files should be presented and documented.

User Interface

Many modern systems are interactive. That is, the user interacts with the system using some form of display screen using which data can be entered and results presented. Well-designed UIs are essential to successful systems because they are the most visible part of the system. As such, it is important to get early and frequent feedback from users about their reactions. This can take the form of a prototype, but if this is infeasible, then mocked up screen shots can be substituted. Your design documentation should include a description of your user interface design. You should explain what the viewer is looking at for each screen. Even command line apps have a UI and you should describe the command line options. If you are making a library rather than an application, then explain your API. Developer documentation should include a sory board, page-flow graph, or some other sort of high-level overview of all the visual "screens" in the application and how they fit together (e.g., which pages link to a page and how).

User Documentation

User documentation should explain the core concepts and metaphors of the application and discuss how to use each feature in a step-by-step manner. User documentation can be delivered as a user manual, a menu-driven help system for a desktop GUI application, as a Unix man mage, or as a collection of help pages on a web site.