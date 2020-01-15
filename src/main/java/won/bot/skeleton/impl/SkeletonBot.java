package won.bot.skeleton.impl;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Locale;

import at.apf.easycli.CliEngine;
import at.apf.easycli.annotation.Command;
import at.apf.easycli.exception.CommandNotFoundException;
import at.apf.easycli.exception.MalformedCommandException;
import at.apf.easycli.impl.EasyEngine;
import org.apache.jena.query.Dataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import won.bot.framework.bot.base.EventBot;
import won.bot.framework.eventbot.EventListenerContext;
import won.bot.framework.eventbot.action.BaseEventBotAction;
import won.bot.framework.eventbot.action.impl.trigger.ActionOnTriggerEventListener;
import won.bot.framework.eventbot.action.impl.trigger.BotTrigger;
import won.bot.framework.eventbot.action.impl.trigger.BotTriggerEvent;
import won.bot.framework.eventbot.action.impl.wonmessage.OpenConnectionAction;
import won.bot.framework.eventbot.behaviour.ExecuteWonMessageCommandBehaviour;
import won.bot.framework.eventbot.bus.EventBus;
import won.bot.framework.eventbot.event.AtomCreationFailedEvent;
import won.bot.framework.eventbot.event.Event;
import won.bot.framework.eventbot.event.impl.command.connect.ConnectCommandEvent;
import won.bot.framework.eventbot.event.impl.command.connect.ConnectCommandResultEvent;
import won.bot.framework.eventbot.event.impl.command.connect.ConnectCommandSuccessEvent;
import won.bot.framework.eventbot.event.impl.command.connectionmessage.ConnectionMessageCommandEvent;
import won.bot.framework.eventbot.event.impl.command.create.CreateAtomCommandEvent;
import won.bot.framework.eventbot.event.impl.wonmessage.CloseFromOtherAtomEvent;
import won.bot.framework.eventbot.event.impl.wonmessage.ConnectFromOtherAtomEvent;
import won.bot.framework.eventbot.event.impl.wonmessage.MessageFromOtherAtomEvent;
import won.bot.framework.eventbot.filter.impl.AtomUriInNamedListFilter;
import won.bot.framework.eventbot.filter.impl.CommandResultFilter;
import won.bot.framework.eventbot.filter.impl.NotFilter;
import won.bot.framework.eventbot.listener.EventListener;
import won.bot.framework.eventbot.listener.impl.ActionOnEventListener;
import won.bot.framework.eventbot.listener.impl.ActionOnFirstEventListener;
import won.bot.framework.extensions.matcher.MatcherBehaviour;
import won.bot.framework.extensions.matcher.MatcherExtension;
import won.bot.framework.extensions.matcher.MatcherExtensionAtomCreatedEvent;
import won.bot.framework.extensions.serviceatom.ServiceAtomBehaviour;
import won.bot.framework.extensions.serviceatom.ServiceAtomExtension;
import won.bot.skeleton.WAC.APIException;
import won.bot.skeleton.WAC.Weather;
import won.bot.skeleton.WeatherDataPoint;
import won.bot.skeleton.action.CreateWeatherAtomAction;
import won.bot.skeleton.action.MatcherExtensionAtomCreatedAction;
import won.bot.skeleton.context.SkeletonBotContextWrapper;
import won.bot.skeleton.event.CreateWeatherAtomEvent;
import won.protocol.exception.WonMessageBuilderException;
import won.protocol.message.WonMessage;
import won.protocol.message.builder.WonMessageBuilder;
import won.protocol.util.DefaultAtomModelWrapper;
import won.protocol.util.WonRdfUtils;
import won.protocol.vocabulary.WXCHAT;

public class SkeletonBot extends EventBot implements MatcherExtension, ServiceAtomExtension {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private int registrationMatcherRetryInterval;
    private MatcherBehaviour matcherBehaviour;
    private ServiceAtomBehaviour serviceAtomBehaviour;

    // bean setter, used by spring
    public void setRegistrationMatcherRetryInterval(final int registrationMatcherRetryInterval) {
        this.registrationMatcherRetryInterval = registrationMatcherRetryInterval;
    }

    @Override
    public ServiceAtomBehaviour getServiceAtomBehaviour() {
        return serviceAtomBehaviour;
    }

    @Override
    public MatcherBehaviour getMatcherBehaviour() {
        return matcherBehaviour;
    }

    @Override
    protected void initializeEventListeners() {
        EventListenerContext ctx = getEventListenerContext();
        if (!(getBotContextWrapper() instanceof SkeletonBotContextWrapper)) {
            logger.error(getBotContextWrapper().getBotName() + " does not work without a SkeletonBotContextWrapper");
            throw new IllegalStateException(
                            getBotContextWrapper().getBotName() + " does not work without a SkeletonBotContextWrapper");
        }
        EventBus bus = getEventBus();
        SkeletonBotContextWrapper botContextWrapper = (SkeletonBotContextWrapper) getBotContextWrapper();
        // register listeners for event.impl.command events used to tell the bot to send
        // messages
        ExecuteWonMessageCommandBehaviour wonMessageCommandBehaviour = new ExecuteWonMessageCommandBehaviour(ctx);
        wonMessageCommandBehaviour.activate();






        //harald-written: EasyCli - parses the commands received by the chat, see https://github.com/WoN-Hackathon-2019/easycli
        CliEngine engine = new EasyEngine();
        engine.register(new Object() {
            @Command("/weather")
                String getWeather(String s){
                String response = "";
                Weather w = null;
                try {
                    w = new Weather(s);
                    // produces the response sent back in the chat
                    response = String.format(Locale.US,"In **%s%s** weather is: \n" +
                                    "      ~ Description: %s, %s \n" +
                                    "      ~ Temperature: %s \n" +
                                    "      ~ Pressure: %s \n" +
                                    "      ~ Wind: %s, %s \n" +
                                    "      ~ Cloud Cover: %s%%  \n" +
                                    "Information: lat: %.2f, lon: %.2f, information received at %s GMT, local timezone GMT%s, CityID %s",
                            (w.getName().isPresent() ? w.getName().get() : s),                                              // City Name
                            (w.getCountry().isPresent() ? ", " + w.getCountry().get() : ""),                                // Country Code
                            (w.getWeatherGroup().isPresent() ? w.getWeatherGroup().get() : "value unavailable"),            // Group of weather parameters (Rain, Snow, Extreme etc.)
                            (w.getWeatherDescription().isPresent() ? w.getWeatherDescription().get() : "value unavailable"),// Weather condition within the group
                            (w.getTemperature().isPresent() ?                                                               // Temperature
                                    String.format("%.1f°C", w.getTemperature().get()) :
                                    "value not found"),
                            (w.getPressure().isPresent() ?                                                                  // Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data) -> hPa
                                    String.format("%d hPa", w.getPressure().get()) :
                                    "value not found"),
                            (w.getWindSpeed().isPresent() ?
                                    String.format("%.1f m/s", w.getWindSpeed().get()) : "value not found"),                // Wind speed -> meter/sec
                            (w.getWindDirection().isPresent() ?                                                             // Wind direction -> degrees (meteorological)
                                    String.format("%d degrees", w.getWindDirection().get()) :
                                    "value not found"),
                            (w.getCloudiness().isPresent() ? String.format("%d", w.getCloudiness().get()) : "value not found"),
                            (w.getCoordsLatitude().isPresent() ? w.getCoordsLatitude().get().doubleValue() : 0),            // Latitude
                            (w.getCoordsLongitude().isPresent() ? w.getCoordsLongitude().get().doubleValue() : 0),          // Longitude
                            (w.getDataReceivingTime().isPresent() ?                                                         // Data collected at (GMT)
                                    new SimpleDateFormat("dd.MM.yyyy hh:mm").format(w.getDataReceivingTime().get()) :
                                    "No Date Found!"),
                            (w.getTimezone().isPresent() ?                                                                  // Timezone of City
                                    String.format(Locale.US,"%+.1f", ((float) w.getTimezone().get()) / 3600) :
                                    "No timezone found!"),
                            (w.getCityId().isPresent() ? w.getCityId().get().intValue() : "not found")
                    );
                    if(s.equals("Venus")){
                        response = "At Venus there currently is dry weather with clouds at 462 °C. Occasionally Lead sulfide snow does fall.";
                    }
                } catch (APIException e) {
                    e.printStackTrace();
                    response = "This city has not been found! Please try again with another city name" +
                                e.getMessage().split(System.lineSeparator(), 2)[0]; // Get only first line from stacktrace as it contains the most valuable information
                }
                return response;

            }
        });


        //harald-written: Create new Atom:
        //harald-written: Create a new atom URI
        URI wonNodeUri = ctx.getNodeURISource().getNodeURI();
        URI atomUri = ctx.getWonNodeInformationService().generateAtomURI(wonNodeUri);

        //harald-written: Set atom data- here only shown for commonly used (hence 'default') properties
        DefaultAtomModelWrapper atomWrapper = new DefaultAtomModelWrapper(atomUri);
        atomWrapper.setTitle("Ask me for weather information");
        atomWrapper.setDescription("Use \"/weather <cityName>\" for information about the weather in that city!");
        atomWrapper.addTag("weather");
        atomWrapper.addTag("request");
        atomWrapper.addTag("api");
        atomWrapper.addTag("OpenWeatherData");
        atomWrapper.addTag("current");
        //harald-written: an atom must have at least one socket
        atomWrapper.addSocket("#chatSocket", WXCHAT.ChatSocketString);
        //harald-written: publish command
        CreateAtomCommandEvent createCommand = new CreateAtomCommandEvent(atomWrapper.getDataset(), "atom_uris");
        ctx.getEventBus().publish(createCommand);
        //harald-written: this code accepts any incoming connection request and replies with "Accepting connection"
        getEventListenerContext().getEventBus().subscribe(
                ConnectFromOtherAtomEvent.class,
                new ActionOnEventListener(
                        ctx,
                        "open-reactor",
                        new OpenConnectionAction(ctx, "Accepting Connection ")
                )
        );
        //harald-written: this code responds to any message either with appropriate weather data (using aboves EasyCli), with an error explaining what went wrong or with an eastergg (if you ask for weather at Venus)'
        getEventListenerContext().getEventBus().subscribe(
                MessageFromOtherAtomEvent.class,
                new ActionOnEventListener(
                        ctx,
                        "message-reactor",
                        new BaseEventBotAction(ctx) {
                            @Override
                            protected void doRun(Event event, EventListener eventListener) throws Exception {
                                WonMessage msg = ((MessageFromOtherAtomEvent) event).getWonMessage();
                                String chatMessage = WonRdfUtils.MessageUtils.getTextMessage(msg);
                                //System.out.println(chatMessage);
                                String response = "";
                                try {
                                    response = engine.parse(chatMessage).toString();
                                    System.out.println(response);
                                } catch (CommandNotFoundException e){
                                    response = "COMMAND NOT FOUND! Use \"/weather <cityName>\" for information about the weather in that city!";
                                } catch (MalformedCommandException e){
                                    response = "COMMAND MALFORMED! Use \"/weather <cityName>\" for information about the weather in that city!";
                                }
                                System.out.println(response);
                                ConnectionMessageCommandEvent createCommand = new ConnectionMessageCommandEvent(((MessageFromOtherAtomEvent) event).getCon(), response);
                                getEventListenerContext().getEventBus().publish(createCommand);


                            }
                        }
                )
        );



















        // activate ServiceAtomBehaviour
        serviceAtomBehaviour = new ServiceAtomBehaviour(ctx);
        serviceAtomBehaviour.activate();
        // set up matching extension
        // as this is an extension, it can be activated and deactivated as needed
        // if activated, a MatcherExtensionAtomCreatedEvent is sent every time a new
        // atom is created on a monitored node
        matcherBehaviour = new MatcherBehaviour(ctx, "BotSkeletonMatchingExtension", registrationMatcherRetryInterval);
        matcherBehaviour.activate();
        // create filters to determine which atoms the bot should react to
        NotFilter noOwnAtoms = new NotFilter(
                        new AtomUriInNamedListFilter(ctx, ctx.getBotContextWrapper().getAtomCreateListName()));
        // filter to prevent reacting to serviceAtom<->ownedAtom events;
        NotFilter noInternalServiceAtomEventFilter = getNoInternalServiceAtomEventFilter();
        bus.subscribe(ConnectFromOtherAtomEvent.class, noInternalServiceAtomEventFilter, new BaseEventBotAction(ctx) {
            @Override
            protected void doRun(Event event, EventListener executingListener) {
                EventListenerContext ctx = getEventListenerContext();
                ConnectFromOtherAtomEvent connectFromOtherAtomEvent = (ConnectFromOtherAtomEvent) event;
                try {
                    String message = "Hello i am the BotSkeleton i will send you a message everytime an atom is created...";
                    final ConnectCommandEvent connectCommandEvent = new ConnectCommandEvent(
                                    connectFromOtherAtomEvent.getRecipientSocket(),
                                    connectFromOtherAtomEvent.getSenderSocket(), message);
                    ctx.getEventBus().subscribe(ConnectCommandSuccessEvent.class, new ActionOnFirstEventListener(ctx,
                                    new CommandResultFilter(connectCommandEvent), new BaseEventBotAction(ctx) {
                                        @Override
                                        protected void doRun(Event event, EventListener executingListener) {
                                            ConnectCommandResultEvent connectionMessageCommandResultEvent = (ConnectCommandResultEvent) event;
                                            if (!connectionMessageCommandResultEvent.isSuccess()) {
                                                logger.error("Failure when trying to open a received Request: "
                                                                + connectionMessageCommandResultEvent.getMessage());
                                            } else {
                                                logger.info(
                                                                "Add an established connection " +
                                                                                connectCommandEvent.getLocalSocket()
                                                                                + " -> "
                                                                                + connectCommandEvent.getTargetSocket()
                                                                                +
                                                                                " to the botcontext ");
                                                botContextWrapper.addConnectedSocket(
                                                                connectCommandEvent.getLocalSocket(),
                                                                connectCommandEvent.getTargetSocket());
                                            }
                                        }
                                    }));
                    ctx.getEventBus().publish(connectCommandEvent);
                } catch (Exception te) {
                    logger.error(te.getMessage(), te);
                }
            }
        });
        // listen for the MatcherExtensionAtomCreatedEvent
        bus.subscribe(MatcherExtensionAtomCreatedEvent.class, new MatcherExtensionAtomCreatedAction(ctx));
        bus.subscribe(CloseFromOtherAtomEvent.class, new BaseEventBotAction(ctx) {
            @Override
            protected void doRun(Event event, EventListener executingListener) {
                EventListenerContext ctx = getEventListenerContext();
                CloseFromOtherAtomEvent closeFromOtherAtomEvent = (CloseFromOtherAtomEvent) event;
                URI targetSocketUri = closeFromOtherAtomEvent.getSocketURI();
                URI senderSocketUri = closeFromOtherAtomEvent.getTargetSocketURI();
                logger.info("Remove a closed connection " + senderSocketUri + " -> " + targetSocketUri
                                + " from the botcontext ");
                botContextWrapper.removeConnectedSocket(senderSocketUri, targetSocketUri);
            }
        });

        bus.subscribe(CreateWeatherAtomEvent.class, new ActionOnEventListener(ctx, new CreateWeatherAtomAction(ctx)));

        createWeatherAtoms(bus);

        BotTrigger botTrigger = new BotTrigger(ctx, Duration.ofMinutes(1));
        botTrigger.activate();

        bus.subscribe(BotTriggerEvent.class, new ActionOnTriggerEventListener(ctx, botTrigger, new BaseEventBotAction(ctx) {
            @Override
            protected void doRun(Event event, EventListener eventListener) throws Exception {
                createWeatherAtoms(bus);
            }
        }));

    }

    private void createWeatherAtoms(EventBus bus) {
        String[] cities = new String[] { "Vienna", "London", "Paris", "Graz", "Linz", "Innsbruck", "Salzburg" };

        try {

            for(String city : cities) {
                Weather weather = new Weather(city);
                bus.publish(new CreateWeatherAtomEvent(new WeatherDataPoint(weather)));
            }

        }
        catch (APIException e) {
            throw new RuntimeException(e);
        }
    }

    private WonMessage createWonMessage(URI connectionURI, String message) throws WonMessageBuilderException {
        Dataset connectionRDF = getEventListenerContext().getLinkedDataSource().getDataForResource(connectionURI);
        URI targetSocket = WonRdfUtils.ConnectionUtils.getTargetSocketURIFromConnection(connectionRDF, connectionURI);
        URI socket = WonRdfUtils.ConnectionUtils.getSocketURIFromConnection(connectionRDF, connectionURI);
        return WonMessageBuilder
                .connectionMessage()
                .sockets()
                /**/.sender(socket)
                /**/.recipient(targetSocket)
                .content()
                /**/.text(message)
                .build();
    }
}
