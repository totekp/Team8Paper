[![Build Status](https://travis-ci.org/nanop/Team8Paper.png?branch=master)](https://travis-ci.org/nanop/Team8Paper)

live server -> http://team8paper.herokuapp.com

# Installation and Running from source

1. Clone project repo `git clone git@github.com:nanop/Team8Paper.git`
2. Install sbt `http://www.scala-sbt.org/`
3. If using local database, install and run Mongodb `(optional) http://www.mongodb.org/downloads`
4. `cd` to project root, run command `sbt` and you should get a console
5. In console, run command `run` and web app will start on localhost:9000
6. Visit localhost:9000 in a browser to view the application. Changes to source code will show on reload.
7. Application configurations are located at `./conf/application.conf` (database URI, secret, etc), and additional configurations are found here http://www.playframework.com/documentation/2.2.x/ProductionConfiguration

# Product Vision

We are building a web application called “Team 8 Paper” that brings the convenience and usefulness of paper, a material used since ancient times, to the digital era. Our product is a note-taking web application that allows users to easily take notes like a text editor, and easily organize elements like using a bulletin board or sticky notes. Usage of the application will be similar to a blank sheet of paper, allowing users to write almost anywhere on the screen as opposed to writing from top to bottom in a text editor. As a web application, users can share, view, and manage their papers on any browser connected to the internet. Keeping up with modern mediums of communication, users can also embed media from sources such as Youtube and image urls. Each sheet may be labeled with tags for easy organization. Papers may also be linked with other papers. Finally, a history of changes will be stored so users can view when each element in a sheet was created and updated. 
	
When using our product, users begin by creating a paper. Each paper contains elements with information about the position on the paper, data, kind such as text, paper  reference, image, html, video, etc, and other attributes. Each paper optionally contains tags for simple organization and retrieval. Elements may also be optionally grouped together on a paper, with a group model. In real life, a group could be a group of sticky notes with text and different colors with color representing one group. However, elements on a paper may be grouped in multiple groups similar to data in an overlapping Venn Diagram. A digital representation also enables powerful visualizations and transformations with papers, elements, tags, groups, and time. In short, organizing, analyzing, and making inferences with your data is easy. You never have to worry about damage, copying, or reorganizing your physical paper things again. 

As a user, the only basic steps to use our product are to create a paper, and then click anywhere on the paper to create an element. Secondary usage are moving elements around on the paper, creating paper tags, creating element groups, linking papers, and more. Potential features are implementing tools to visualize and group your data in unique ways such as including a fancy tag cloud. The goal of Team 8 Paper is to bring paper into a new era, and the product emphasizes simplicity, expressivity, and the widely used invention from thousands of years ago called paper.
