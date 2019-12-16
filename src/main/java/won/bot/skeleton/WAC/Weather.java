import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OptionalDataException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Optional;


public class Weather {


    private static HttpURLConnection connection;
    private static String jsonResponse;

//    id 	City identification
    Optional<Integer> CityId;
//    dt 	Data receiving time
//    unix, UTC
    Optional<Date> DataReceivingTime;
//    name 	City name


//    coord
//        lat 	City geo location, latitude
//        lon 	City geo location, longitude
    Optional<Float> CoordsLatitude;
    Optional<Float> CoordsLongitude;

//    sys
//        message 	System parameter, do not use it
//        country 	Country code (GB, JP etc.)
//        sunrise 	Sunrise time 	unix, UTC
//        sunset 	Sunset time 	unix, UTC
    Optional<String> Country;
    Optional<Date> SunriseTime;
    Optional<Date> SunsetTime;
//    main
//        temp 	Temperature -> Celsius
//        humidity 	Humidity -> %
//        temp_min 	Minimum temperature at the moment. This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally) -> Celsius
//        temp_max 	Maximum temperature at the moment. This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally) -> Celsius
//        pressure 	Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data) -> hPa
//        sea_level 	Atmospheric pressure on the sea level -> hPa
//        grnd_level 	Atmospheric pressure on the ground level -> hPa
    Optional<Float> Temperature;
    Optional<Integer> Humidity;
    Optional<Float> TempMin;
    Optional<Float> TempMax;
    Optional<Integer> Pressure;
    Optional<Integer> PressureSeaLevel;
    Optional<Integer> PressureGroundLevel;
//    wind
//        speed 	Wind speed 	meter/sec
//        deg 	Wind direction -> degrees (meteorological)
//        gust 	Wind gust -> meter/sec
    Optional<Float> WindSpeed;
    Optional<Integer> WindDirection;
    Optional<Float> WindGust;

//    clouds
//        all 	Cloudiness 	% 	% 	%
    Optional<Integer> Cloudiness;

//    weather (more info Weather condition codes)
//        id 	Weather condition id
//        main 	Group of weather parameters (Rain, Snow, Extreme etc.) 	- 	- 	-
//        description 	Weather condition within the group 	- 	- 	-
//        icon 	Weather icon id 	- 	- 	-
    Optional<Integer> WeatherId;
    Optional<String> WeatherGroup;
    Optional<String> WeatherDescription;
    Optional<Integer> WeatherIconId;
//    rain
//        1h 	Precipitation volume for last hour 	mm 	mm 	mm
//        3h 	Precipitation volume for last 3 hours 	mm 	mm 	mm
    Optional<Integer> Rain1h;
    Optional<Integer> Rain3h;
//    snow
//        1h 	Snow volume for last hour 	mm 	mm 	mm
//        3h 	Snow volume for last 3 hours 	mm 	mm 	mm
    Optional<Integer> Snow1h;
    Optional<Integer> Snow3h;



    Weather(){

    }

    public Optional<Date> getDataReceivingTime() {
        return DataReceivingTime;
    }
    public Optional<Float> getCoordsLatitude() {
        return CoordsLatitude;
    }
    public Optional<Float> getCoordsLongitude() {
        return CoordsLongitude;
    }
    public Optional<String> getCountry() {
        return Country;
    }
    public Optional<Date> getSunriseTime() {
        return SunriseTime;
    }
    public Optional<Date> getSunsetTime() {
        return SunsetTime;
    }
    public Optional<Float> getTemperature() {
        return Temperature;
    }
    public Optional<Integer> getHumidity() {
        return Humidity;
    }
    public Optional<Float> getTempMin() {
        return TempMin;
    }
    public Optional<Float> getTempMax() {
        return TempMax;
    }
    public Optional<Integer> getPressure() {
        return Pressure;
    }
    public Optional<Integer> getPressureSeaLevel() {
        return PressureSeaLevel;
    }
    public Optional<Integer> getPressureGroundLevel() {
        return PressureGroundLevel;
    }
    public Optional<Float> getWindSpeed() {
        return WindSpeed;
    }
    public Optional<Integer> getWindDirection() {
        return WindDirection;
    }
    public Optional<Float> getWindGust() {
        return WindGust;
    }
    public Optional<Integer> getCloudiness() {
        return Cloudiness;
    }
    public Optional<Integer> getWeatherId() {
        return WeatherId;
    }
    public Optional<String> getWeatherGroup() {
        return WeatherGroup;
    }
    public Optional<String> getWeatherDescription() {
        return WeatherDescription;
    }
    public Optional<Integer> getWeatherIconId() {
        return WeatherIconId;
    }
    public Optional<Integer> getRain1h() {
        return Rain1h;
    }
    public Optional<Integer> getRain3h() {
        return Rain3h;
    }
    public Optional<Integer> getSnow1h() {
        return Snow1h;
    }
    public Optional<Integer> getSnow3h() {
        return Snow3h;
    }





    private String APIHTTPRequest(){
        // Connect to API and retrieve data (as String)
        // using java.net.HttpURLConnection
        BufferedReader reader;
        String line = "";
        StringBuffer responseContent = new StringBuffer();

        try {

            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?id=2172797&units=Metric&appid=" + System.getenv("APIKey"));
            connection = (HttpURLConnection) url.openConnection();


            //Request Setup
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            //System.out.println(status);

            if (status > 299) {
                reader = new BufferedReader( new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();
            } else {
                reader = new BufferedReader(( new InputStreamReader((connection.getInputStream()))));
                while ((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();
            }
            System.out.println(responseContent.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
    private void ParseJSON() throws APIException {
        JSONObject jo;
        try {
            jo = new JSONObject(jsonResponse);
        }
        catch (JSONException jex){
            throw new APIException(jex);
        }

        if (!jo.get("cod").equals("200")){
            throw new APIException("ResponseCode: " + jo.get("cod") + ", Message: " + jo.get("message") );
        }
        jo.get("");

    }






}
