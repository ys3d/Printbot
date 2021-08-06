# Telegram-Printbot
This Telegram-bot is capable of printing PDF-files recieved as a message via Telegram

## Building 
To build this bot the following things are needed:
* A system running at least Java-JDK 11 or OpenJDK 11
* Maven as a console-application or an IDE capable of running Maven
* A active internet-connection to pull several java-libraries

To set all parameters:
* Modify ``/src/main/resources/bot_example.properties`` with your parameters and rename it to ``src/main/resources/bot.properties``
* Create ``/src/main/resources/whitelist.txt`` and add your Telegram-user-id to the file. To add multiple user-ids insert one user-id per line in ``whitespace.txt``

To build the bot, run
```
mvn package 
```
in the main directory

## Running
To run the bot use
```
java -jar uber-PrintBot-<VERSION>.jar
```