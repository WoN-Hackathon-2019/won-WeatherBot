package won.bot.skeleton;

import won.bot.skeleton.WAC.Weather;

public class WeatherDataPoint {
    private float highestTemperature;
    private Weather weather;

    /*public WeatherDataPoint(float highestTemperature) {
        this.highestTemperature = highestTemperature;
    }*/
    public WeatherDataPoint(Weather weather) {
        this.weather = weather;
    }

    /*public float getHighestTemperature() {
        return highestTemperature;
    }*/
    public Weather getWeather() {
        return weather;
    }
}
