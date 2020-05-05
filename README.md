Slightly V1.0

Developed by
Rafael José López Buelvas

Description: HTML Parser. It makes easier Coding Java Web Apps.

Run:
mvn jetty:run
or try Importing the project in IntelliJ as a Maven Project, and execute the jetty plugin task jetty:run.

External Libs:
JSoup -> HTML Document Elements Navigator
Nashorn -> Java 8 Tool to execute Javascript from Java.
Apache Commons -> Just Some Utils to make some validations.
Log4J -> Logging Tool.
JUnit -> For Java Testing.

Classes
-biz.netcentric.script.ScriptExecutor
 * A Nashorn Class Implementation to Execute and send attributes to Javascript
-biz.netcentric.model.Person
 * The provided Model
-biz.netcentric.parser.HTMLParser
 * The Class with the analyzer and the process methods.
- biz.netcentric.service.ParseService
 * Intermediates between the servlet and the parser.
- biz.netcentric.servlet.HTMLParserServlet
 * The servlet
- biz.netcentric.exceptions.SlightlyException
 * This is a class that represents all the exceptions for showing the errors to the user

Tests
- biz.netcentric.parser.HTMLParserTest
 * This contains some methods that prove the execution of the html parser.

Process.

The Servlet recieves the client request, and calls the service, the service is an intermediary,
it recieves the information from the servlet and print the output to html, calling the HTMLParser,
this class parse the html document and iterate it, it also has the tags analayzer,
and calls the executor to execute the javascript when its necessary.

Design.

I think it's a simple design solution, i structured some layers to improve the distribution of the
responsabilities of each one, to gain more cohesion and less coupling, for example the ScriptExecutor
may be reused by other clasess, because it has his behavior defined. I prefer to make simple
implementations, adding complex solutions only when it's necessary.

Optional Local Scope.
To reduce the scope of a variable to local, i can use the Memento pattern, developing the Memento,
Originator and Caretaker class. So at the begining of the execution i can safe the global object in
the memento implementation, after that i send a new value to the Javascript, then, when the execution
ends, i restore to the global Object i had at first.

Optional Inclussion
It's possible to add a new evaluator it look for the attribute "data-attribute" in all tags,
if there's one, i can simply call a Javascript function that loads the template into the tag.
It's important to recieve the template id to include the proper, the template must be defined
with the <template> tag.

