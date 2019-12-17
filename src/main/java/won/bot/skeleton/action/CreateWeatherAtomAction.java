package won.bot.skeleton.action;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import won.bot.framework.eventbot.EventListenerContext;
import won.bot.framework.eventbot.action.EventBotActionUtils;
import won.bot.framework.eventbot.action.impl.atomlifecycle.AbstractCreateAtomAction;
import won.bot.framework.eventbot.bus.EventBus;
import won.bot.framework.eventbot.event.Event;
import won.bot.framework.eventbot.event.impl.atomlifecycle.AtomCreatedEvent;
import won.bot.framework.eventbot.event.impl.wonmessage.FailureResponseEvent;
import won.bot.framework.eventbot.listener.EventListener;
import won.bot.skeleton.WeatherDataPoint;
import won.bot.skeleton.context.SkeletonBotContextWrapper;
import won.bot.skeleton.event.CreateWeatherAtomEvent;
import won.bot.skeleton.utils.WeatherAtomModelWrapper;
import won.protocol.message.WonMessage;
import won.protocol.service.WonNodeInformationService;
import won.protocol.util.RdfUtils;
import won.protocol.util.WonRdfUtils;

import java.net.URI;

public class CreateWeatherAtomAction extends AbstractCreateAtomAction {
    private static final Logger logger = LoggerFactory.getLogger(CreateWeatherAtomAction.class);

    public CreateWeatherAtomAction(EventListenerContext ctx) {
        super(ctx);
    }

    @Override
    protected void doRun(Event event, EventListener eventListener) {
        EventListenerContext ctx = getEventListenerContext();
        if (!(ctx.getBotContextWrapper() instanceof SkeletonBotContextWrapper)
                || !(event instanceof CreateWeatherAtomEvent)) {
            logger.error(
                    "CreateEdenredAtomAction does not work without a SkeletonBotContextWrapper and CreateEdenredAtomEvent");
            throw new IllegalStateException(
                    "CreateEdenredAtomAction does not work without a SkeletonBotContextWrapper and CreateEdenredAtomEvent");
        }
        SkeletonBotContextWrapper botContextWrapper = (SkeletonBotContextWrapper) ctx.getBotContextWrapper();
        CreateWeatherAtomEvent createEdenredAtomEvent = (CreateWeatherAtomEvent) event;

        WeatherDataPoint weatherDataPoint = createEdenredAtomEvent.getWeatherDatapoint();

        /*
         * TODO if (botContextWrapper.getAtomUriForRaid(edenredAtomToCreate) != null) {
         * logger.warn("RaidAtom already exists, URI: " +
         * botContextWrapper.getAtomUriForRaid(edenredAtomToCreate)); return; }
         */

        final URI wonNodeUri = ctx.getNodeURISource().getNodeURI();
        WonNodeInformationService wonNodeInformationService = ctx.getWonNodeInformationService();
        final URI atomURI = wonNodeInformationService.generateAtomURI(wonNodeUri);
        //Dataset dataset = new WeatherAtomModelWrapper(atomURI, weatherDataPoint).copyDataset();
        Dataset dataset = generateAtomStructure(atomURI, weatherDataPoint);
        logger.info("about to publish atom with URI: " + atomURI);
        logger.debug("creating atom on won node {} with content {} ", wonNodeUri,
                StringUtils.abbreviate(RdfUtils.toString(dataset), 150));
        WonMessage createAtomMessage = ctx.getWonMessageSender().prepareMessage(createWonMessage(atomURI, dataset));
        EventBotActionUtils.rememberInList(ctx, atomURI, uriListName);
        EventBus bus = ctx.getEventBus();
        EventListener successCallback = new EventListener() {
            @Override
            public void onEvent(Event event) {
                logger.debug("atom creation successful, new atom URI is {}", atomURI);
                bus.publish(new AtomCreatedEvent(atomURI, wonNodeUri, dataset, null));
                // TODO botContextWrapper.addEdenredAtom(edenredDataPoint, atomURI);
            }
        };
        EventListener failureCallback = new EventListener() {
            @Override
            public void onEvent(Event event) {
                String textMessage = WonRdfUtils.MessageUtils
                        .getTextMessage(((FailureResponseEvent) event).getFailureMessage());
                logger.error("atom creation failed for atom URI {}, original message URI {}: {}", atomURI,
                        ((FailureResponseEvent) event).getOriginalMessageURI(), textMessage);
                // TODO botContextWrapper.removeEden(edenredAtomToCreate);
                EventBotActionUtils.removeFromList(ctx, atomURI, uriListName);
            }
        };
        EventBotActionUtils.makeAndSubscribeResponseListener(createAtomMessage, successCallback, failureCallback, ctx);
        logger.debug("registered listeners for response to message URI {}", createAtomMessage.getMessageURI());
        ctx.getWonMessageSender().sendMessage(createAtomMessage);
        logger.debug("atom creation message sent with message URI {}", createAtomMessage.getMessageURI());
        logger.debug("ATOM URI: " + atomURI.toString());
        logger.debug("ATOM FOR CITY: " + weatherDataPoint.getWeather().getName().get());
    }

    public static Dataset generateAtomStructure(URI atomURI, WeatherDataPoint weatherDataPoint){
        WeatherAtomModelWrapper atomwrapper = new WeatherAtomModelWrapper(atomURI, weatherDataPoint);
        atomwrapper.setTitle(weatherDataPoint.getWeather().getName().get());
        //atomwrapper.setDescription("");

        String description = "Cloudiness: " + weatherDataPoint.getWeather().getCloudiness().get() + "%," + " Temperature: " + weatherDataPoint.getWeather().getTemperature().get() + "°C," + " Windspeed: " + weatherDataPoint.getWeather().getWindSpeed().get() + "m/s";
        atomwrapper.setDescription(description);

        atomwrapper.addTag("WeatherData");
        atomwrapper.addTag("Vienna");


        /*
        Model model = atomwrapper.getAtomModel();
        Model defaultmodel = ModelFactory.createDefaultModel();

        Resource city = model.createResource("http:://schema.org/Weather");
        Property p1 = defaultmodel.createProperty("http://schema.org/Place");
        Property p2 = defaultmodel.createProperty("Property2");

         Resource temperature = model.createResource(p2);
        temperature.addProperty(p2, "15°C");
        city.addProperty(p2, temperature);
        atomwrapper.getAtomContentNode().addProperty(p1, city);*/
        return atomwrapper.getDataset();
    }
}
