# Architecture

Team8Paper is a rich web application crafted with Javascript and JQuery on the client side,
Scala on the server side, and MongoDB on the persistence side. From a high level, it has a client-server architecture
with client sending requests to the server by making HTTP calls. JSON, REST, and non-blocking are three attributes emphasized
in the design. Input and outputs are JSON when possible, routes follow REST for easy communication, and server and client handles
requests asynchronously so that the application exceeds at scaling.

Our web application is built on top of Play! a web framework which handles resources and runs our application. All our code are loaded by Play!.
On the client, we use Play!'s scala html templates. Javascript and CSS resources are stored in assets or public folders. The assets folders
provides better management by using Google's closure compiler to optimize the javascript code in the directory. LESS, which is compatible with CSS,
and offers more features is the default used. LESS files are compiled to .min.css files by Play!. Libraries such as JQuery and Bootstrap which are
already optimized, and images are stored in the public directory or linked externally, for static assets.

On the server, we depend on two libraries in addition to Play! which are Reactive Mongo and filters.
Filters is a dependency used to create application wide conditionals to handle requests.
Our application has 3 filters found in Global.scala and they run in order mentioned. The HttpsFilter ensures client is connected to
application vs HTTPS on production. Next, the Api1Filter handles url paths beginning with /api1 with a json output to be
consistent with valid /api1 calls instead of html responses such as 404 Not Found or Forbidden when accessing other
parts of application. Lastly, the GzipFilter saves network bandwidth by compressing responses. ReactiveMongo is the sole database driver
used for persisting users, papers, and all models used. We use a play-plugin which is a wrapper over ReactiveMongo for easier
interoperability with Play's JSON models rather than dealing with converting between ReactiveMongo's BSON objects, case classes, and Play's
JSON objects. With the play plugin, the data flows between case classes and JSON, and between JSON and BSON. An important feature of Reactive Mongo
is that it is an asychronous database driver. It is non-blocking, meaning calls with ReactiveMongo do no block other processes. Results are returned.