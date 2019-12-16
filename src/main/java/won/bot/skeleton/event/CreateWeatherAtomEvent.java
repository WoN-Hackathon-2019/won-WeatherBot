package won.bot.skeleton.event;

import won.bot.framework.eventbot.event.BaseEvent;
import won.bot.skeleton.WeatherDataPoint;

public class CreateWeatherAtomEvent extends BaseEvent {
    WeatherDataPoint dataPoint;

    public CreateWeatherAtomEvent(WeatherDataPoint datapoint) {
        this.dataPoint = datapoint;
    }

    public WeatherDataPoint getWeatherDatapoint() {
        return this.dataPoint;
    }
}
