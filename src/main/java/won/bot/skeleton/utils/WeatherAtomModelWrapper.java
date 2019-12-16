package won.bot.skeleton.utils;

import won.bot.skeleton.WeatherDataPoint;
import won.protocol.util.DefaultAtomModelWrapper;
import won.protocol.vocabulary.WXCHAT;

import java.net.URI;

public class WeatherAtomModelWrapper extends DefaultAtomModelWrapper {
    public WeatherAtomModelWrapper(URI atomUri, WeatherDataPoint datapoint) {
        super(atomUri);
        setDescription("Highest Temp: " + datapoint.getHighestTemperature());

        this.addSocket("#socket1", WXCHAT.ChatSocketString);
        this.setDefaultSocket("#socket1");
    }
}
