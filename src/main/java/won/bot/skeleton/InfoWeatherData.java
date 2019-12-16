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
