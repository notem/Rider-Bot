#### Warning:

```dif
- This is abandonware.
- Code quality is poor!
- Documentation is non-existant.
```

<hr>

# Rider-Bot

### Features
+ Create 'looking for group' entries quickly
+ All entries viewable in dedicated channel, ordered by age
+ Entries track members via mentions and allow option size limits
+ Groups are automatically removed after they expire (configurable)
+ Minutes since last update displayed on the entry
+ Users are automatically assigned a role

#### Prerequisites
+ Java 8
+ Maven

### Setup
1) Clone this repository
2) Use maven to compile the application with dependencies
3) Launch the bot with java, a configuration file should have been created
4) Add your Discord-bot token to the configuration file
5) Run the bot application (preferably in terminal window to view the basic log output)

### Dependencies

+ [JDA](https://github.com/DV8FromTheWorld/JDA) - 3.3.1_290
+ [Unirest](https://github.com/Mashape/unirest-java) - 1.4.9
