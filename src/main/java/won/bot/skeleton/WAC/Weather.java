import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
//    dt 	Data receiving time -> unix, UTC
    Optional<Date> DataReceivingTime;
//    timezone          Shift in seconds from UTC
    Optional<Integer> Timezone;
//    name 	City name
    Optional<String> Name;
//    cod Internal parameter (probably success/error sign)
    Optional<Integer> COD;
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
    Optional<String> WeatherIcon;
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



    Weather() throws APIException {
        jsonResponse = APIHTTPRequest();
        ParseJSON();
    }
    Weather(String city, String country) throws APIException {
        jsonResponse = APIHTTPRequest(city, country);
        ParseJSON();
    }
    Weather(String city) throws APIException {
        jsonResponse = APIHTTPRequest(city);
        ParseJSON();
    }
    Weather(Integer cityId) throws APIException {
        jsonResponse = APIHTTPRequest(cityId);
        ParseJSON();
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
    public Optional<String> getWeatherIcon() {
        return WeatherIcon;
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
        return APIHTTPRequester("id=2172797");
    }
    private String APIHTTPRequest(String city, String country){
        return APIHTTPRequester("q=" + city + "," + country);
    }
    private String APIHTTPRequest(String city){
        return APIHTTPRequester("q=" + city);
    }
    private String APIHTTPRequest(Integer cityId){
        return APIHTTPRequester("id=" + cityId);
    }


    private String APIHTTPRequester(String location){
        // Connect to API and retrieve data (as String)
        // using java.net.HttpURLConnection
        BufferedReader reader;
        String line = "";
        StringBuffer responseContent = new StringBuffer();

        try {

            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?" + location + "&units=Metric&appid=" + System.getenv("APIKey"));
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
        return responseContent.toString();
    }
    private void ParseJSON() throws APIException {
        JSONObject jo;
        try {
            System.out.println("JSON RESPONSE IS: " + jsonResponse);
            jo = new JSONObject(jsonResponse);
        }
        catch (JSONException jex){
            throw new APIException(jex);
        }

        if (jo.has("cod") && !jo.isNull("cod") && !(jo.getInt("cod") == 200)){
            if(jo.has("message")){
                throw new APIException("ResponseCode: " + jo.getString("cod") + ", Message: " + jo.getString("message")  + ", JSON:" + jsonResponse );
            } else {
                throw new APIException("ResponseCode: " + jo.getString("cod") + ", JSON:" + jsonResponse );
            }
        }



//      coord
//          lat 	City geo location, latitude
//          lon 	City geo location, longitude
        if (jo.has("coord") && !jo.isNull("coord")){
            JSONObject joCoord = jo.getJSONObject("coord");
            CoordsLatitude = extractFloat("lat", joCoord);
            CoordsLongitude = extractFloat("lon", joCoord);
        } else {
            CoordsLongitude = Optional.empty();
            CoordsLatitude = Optional.empty();
        }
//      id 	    City identification
        CityId = extractInt("id", jo);
//      timezone    Shift in seconds from UTC
        Timezone = extractInt("timezone", jo);
//      name    City name
        Name = extractString("name", jo);
//      cod Internal parameter (probably success/error sign)
        COD = extractInt("cod", jo);


//    dt 	Data receiving time
//    unix, UTC

        DataReceivingTime = extractDate("dt", jo);

//    sys
//        message 	System parameter, do not use it
//        country 	Country code (GB, JP etc.)
//        sunrise 	Sunrise time 	unix, UTC
//        sunset 	Sunset time 	unix, UTC
        if (jo.has("sys") && !jo.isNull("sys")) {
            JSONObject joSys = jo.getJSONObject("sys");
            Country = extractString("country", joSys);
            SunriseTime = extractDate("sunrise", joSys);
            SunsetTime = extractDate("sunset", joSys);
        } else {
            Country = Optional.empty();
            SunriseTime = Optional.empty();
            SunsetTime = Optional.empty();
        }
//    main
//        temp 	Temperature -> Celsius
//        humidity 	Humidity -> %
//        temp_min 	Minimum temperature at the moment. This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally) -> Celsius
//        temp_max 	Maximum temperature at the moment. This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally) -> Celsius
//        pressure 	Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data) -> hPa
//        sea_level 	Atmospheric pressure on the sea level -> hPa
//        grnd_level 	Atmospheric pressure on the ground level -> hPa
        if (jo.has("main") && !jo.isNull("main")) {
            JSONObject joMain = jo.getJSONObject("main");
            Temperature = extractFloat("temp", joMain);
            Humidity = extractInt("humidity", joMain);
            TempMin = extractFloat("temp_min", joMain);
            TempMax = extractFloat("temp_max", joMain);
            Pressure = extractInt("pressure", joMain);
            PressureSeaLevel = extractInt("sea_level", joMain);
            PressureGroundLevel = extractInt("grnd_level", joMain);
        } else {
            Temperature = Optional.empty();
            Humidity = Optional.empty();
            TempMin = Optional.empty();
            TempMax = Optional.empty();
            Pressure = Optional.empty();
            PressureSeaLevel = Optional.empty();
            PressureGroundLevel = Optional.empty();
        }

//    wind
//        speed 	Wind speed 	meter/sec
//        deg 	Wind direction -> degrees (meteorological)
//        gust 	Wind gust -> meter/sec
        if (jo.has("wind") && !jo.isNull("wind")) {
            JSONObject joWind = jo.getJSONObject("wind");
            WindSpeed = extractFloat("speed", joWind);
            WindDirection = extractInt("deg", joWind);
            WindGust = extractFloat("gust", joWind);
        } else {
            WindSpeed = Optional.empty();
            WindDirection = Optional.empty();
            WindGust = Optional.empty();
        }

//    clouds
//        all 	Cloudiness 	% 	% 	%
        if (jo.has("clouds") && !jo.isNull("clouds")) {
            JSONObject joClouds = jo.getJSONObject("clouds");
            Cloudiness = extractInt("all", joClouds);
        } else {
            Cloudiness = Optional.empty();
        }

//    weather (more info Weather condition codes)
//        id 	Weather condition id
//        main 	Group of weather parameters (Rain, Snow, Extreme etc.) 	- 	- 	-
//        description 	Weather condition within the group 	- 	- 	-
//        icon 	Weather icon id
        if (jo.has("weather") && !jo.isNull("weather")) {
            JSONObject joWeather = jo.getJSONArray("weather").getJSONObject(0);
            WeatherId = extractInt("id", joWeather);
            WeatherGroup = extractString("main", joWeather);
            WeatherDescription = extractString("description", joWeather);
            WeatherIcon = extractString("icon", joWeather);
        } else {
            WeatherId = Optional.empty();
            WeatherGroup = Optional.empty();
            WeatherDescription = Optional.empty();
            WeatherIcon = Optional.empty();
        }
//    rain
//        1h 	Precipitation volume for last hour 	mm 	mm 	mm
//        3h 	Precipitation volume for last 3 hours 	mm 	mm 	mm
        if (jo.has("rain") && !jo.isNull("rain")) {
            JSONObject joRain = jo.getJSONObject("rain");
            Rain1h = extractInt("1h", joRain);
            Rain3h = extractInt("3h", joRain);
        } else {
            Rain1h = Optional.empty();
            Rain3h = Optional.empty();
        }
//    snow
//        1h 	Snow volume for last hour 	mm 	mm 	mm
//        3h 	Snow volume for last 3 hours 	mm 	mm 	mm
        if (jo.has("snow") && !jo.isNull("snow")) {
            JSONObject joSnow = jo.getJSONObject("snow");
            Snow1h = extractInt("1h", joSnow);
            Snow3h = extractInt("3h", joSnow);
        } else {
            Snow1h = Optional.empty();
            Snow3h = Optional.empty();
        }
    }

    private Optional<Integer> extractInt(String key, JSONObject jo) throws APIException {

        try {
            if (jo.has(key) && !jo.isNull(key)) {
                return Optional.of(jo.getInt(key));
            } else {
                return Optional.empty();
            }
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    private Optional<Float> extractFloat(String key, JSONObject jo) throws APIException {
        try {

            if (jo.has(key) && !jo.isNull(key)) {
                return Optional.of(jo.getFloat(key));
            } else {
                return Optional.empty();
            }
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    private Optional<String> extractString(String key, JSONObject jo) throws APIException {
        try {

            if (jo.has(key) && !jo.isNull(key)) {
                return Optional.of(jo.getString(key));
            } else {
                return Optional.empty();
            }

        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    private Optional<Date> extractDate(String key, JSONObject jo) throws APIException {
        try {
            if (jo.has(key) && !jo.isNull(key)) {
                return Optional.of(new Date(jo.getLong(key) * 1000));
            } else {
                return Optional.empty();
            }
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }



}
