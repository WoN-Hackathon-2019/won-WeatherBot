# Web of Needs Bot Weatherdata

This Repository contains a bot that creates a Atoms containing a citys weather information.

The Bot Skeleton is a [Spring Boot Application](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html).

## Running the bot

### Prerequisites

- [Openjdk 8](https://adoptopenjdk.net/index.html) - the method described here does **not work** with the Oracle 8 JDK!
- Maven framework set up

### Before starting: 
Create a free API-Key at Openweatherdata.org

### On the command line

```
cd bot-skeleton
export WON_NODE_URI="https://hackathonnode.matchat.org/won"
mvn clean package
java -jar target/bot.jar
```
Now go to [What's new](https://hackathon.matchat.org/owner/#!/overview) to find your bot, connect and [create an atom](https://hackathon.matchat.org/owner/#!/create) to see the bot in action.

### In Intellij Idea
1. Create a run configuration for the class `won.bot.skeleton.SkeletonBotApp`
2. Add the environment variables

  * `WON_NODE_URI` pointing to your node uri (e.g. `https://hackathonnode.matchat.org/won` without quotes)
  * `APIKey` with the API-Key from Openweatherdata.org as value
  
  to your run configuration.
  
3. Run your configuration

If you get a message indicating your keysize is restricted on startup (`JCE unlimited strength encryption policy is not enabled, WoN applications will not work. Please consult the setup guide.`), refer to [Enabling Unlimited Strength Jurisdiction Policy](https://github.com/open-eid/cdoc4j/wiki/Enabling-Unlimited-Strength-Jurisdiction-Policy) to increase the allowed key size.

##### Optional Parameters for both Run Configurations:
- `WON_KEYSTORE_DIR` path to folder where `bot-keys.jks` and `owner-trusted-certs.jks` are stored (needs write access and folder must exist) 

## Setting up
- Download or clone this repository
- Add config files

Please refer to the general [Bot Readme](https://github.com/researchstudio-sat/webofneeds/blob/master/webofneeds/won-bot/README.md) for more information on Web of Needs Bot applications.

## Functions
### Atoms created
#### Atoms for cities:
* Salzburg
* Innsbruck
* Vienna
* Linz
* Paris
* Graz
* London
#### There is also an interactive atom created
You can use /weather <cityName> to get information about the cities current weather.
 
 
In case the command is malformed, the city does not exist or other errors it tries to respond with helpful messages so you can debug your command/bot setup.


