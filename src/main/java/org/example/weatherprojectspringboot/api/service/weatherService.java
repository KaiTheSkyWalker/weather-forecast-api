package org.example.weatherprojectspringboot.api.service;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.example.weatherprojectspringboot.api.model.CityModel;
import org.example.weatherprojectspringboot.api.model.ForecastModel;
import org.example.weatherprojectspringboot.api.model.DateModel;


import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import java.util.Date;


/* implemented into the hashmap to access it */
@Service // to be injected
public class weatherService {
    static final Map<String, String> cityCodeMap = new HashMap<>();

    static {
//        JSONParser parser = new JSONParser();
//
//        try {
//            // Path to the JSON file
//            FileReader reader = new FileReader("/Users/leeowen/Projects/weather-project-springboot/package.json");
//
//            // Parse the JSON file
//            JSONObject jsonObject = (JSONObject) parser.parse(reader);
//
//            // Get the array for "城市代码"
//            JSONArray cityCodes = (JSONArray) jsonObject.get("城市代码");
//
//
//            for (Object object : cityCodes) {
//                JSONObject province = (JSONObject) object;
//
//                JSONArray cities = (JSONArray) province.get("市");
//                for (Object c : cities) {
//                    JSONObject city = (JSONObject) c;
//                    String cityName = (String) city.get("市名");
//                    String cityCode = (String) city.get("编码");
//                    cityCodeMap.put(cityName, cityCode);
//                }
//            }
//            reader.close();
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//        }
//    }
        File file = new File("/Users/leeowen/Projects/weather-project-springboot/package.json");
        String fileContent = null; // Specify the encoding if necessary
        try {
            fileContent = FileUtils.readFileToString(file, "UTF-8"); // try to read file in this format
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // fastJSON
        JSONObject jsonObject = JSON.parseObject(fileContent); // parse file into an object
        CityModel cityModel = JSON.toJavaObject(jsonObject, CityModel.class); // map to the object (jsonObject, model)

        List<CityModel.CityProvinceModel> cityProvinceModels = cityModel.get城市代码(); // get the list of CityProvinceModel objects

        if (cityProvinceModels != null) { // not empty
            for (CityModel.CityProvinceModel cityProvinceModel : cityProvinceModels) { // for 每个省
                List<CityModel.CityProvinceModel.CityPostalCode> cityPostalCodes = cityProvinceModel.get市(); // 每个省取出市
                for (CityModel.CityProvinceModel.CityPostalCode cityPostalCode : cityPostalCodes) { // 每个市取出每个市名和编码
                    cityCodeMap.put(cityPostalCode.get市名(), cityPostalCode.get编码());
                }
            }
        }
    }

    // get the cityCode from the hashmap
    public static String getCityCode(String cityName) {
        return cityCodeMap.get(cityName);
    }

    public ResponseEntity<String> getWeather(String cityName, String date) throws IOException {
        String cityCode = weatherService.getCityCode(cityName); // get the cityCode from the map
        if (cityCode == null) {
            return new ResponseEntity<>("城市不存在！", HttpStatus.NOT_FOUND);
        }

        List<ForecastModel> forecasts = new ArrayList<>(); // store all the date into a list

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet("http://t.weather.itboy.net/api/weather/city/" + cityCode);

            // execute the http request
            try (CloseableHttpResponse response = httpclient.execute(httpget)) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                // alibaba fastJSON
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(result);
                com.alibaba.fastjson.JSONArray forecastArray = jsonObject.getJSONObject("data").getJSONArray("forecast");
                EntityUtils.consume(entity);

//                    System.out.println(forecastArray);
//                    List<Forecast> forecasts = new ArrayList<>();

                int count = 0;
                if (date == null) {
                    for (Object forecastObject : forecastArray) { // loop through the JSONArray to get each JSONObject
                        if (count < 7) {
                            com.alibaba.fastjson.JSONObject forecastJson = (com.alibaba.fastjson.JSONObject) forecastObject;

                            // Map JSON object to Forecast model
                            // alibaba
                            // map the forecastJson to java Object to the ForecastModel class

                            ForecastModel forecast = forecastJson.toJavaObject(ForecastModel.class);
                            // Check if the forecast date matches the provided date
                            forecasts.add(forecast);
                            count++;
                        } else {
                            break;
                        }
                    }
                } else if (validateDate(date) && calculateDaysDifference(date + "T00:00:00Z")) {
                    for (Object forecastObject : forecastArray) {
                        com.alibaba.fastjson.JSONObject forecastJson = (com.alibaba.fastjson.JSONObject) forecastObject;
                        // Map JSON object to Forecast model
                        ForecastModel forecast = forecastJson.toJavaObject(ForecastModel.class);
                        if (date.equals(forecast.getYmd())) {
                            forecasts.add(forecast);
                        }
                    }
                } else {
                    return new ResponseEntity<>("ERROR", HttpStatus.NOT_FOUND);
                }
            }
        }
        return new ResponseEntity<>(forecasts.toString(), HttpStatus.OK);
    }

    public boolean validateDate(String date) {
        DateModel correctDateModel = new DateModel();
        SimpleDateFormat sdf = new SimpleDateFormat(correctDateModel.getDate());
        sdf.setLenient(false);

        try {
            Date inputDate = sdf.parse(date);
            int month = inputDate.getMonth();
            int day = inputDate.getDate();

            if (month < 0 || month > 11) {
                return false;
            } else if (day < 1 || day > 31) {
                return false;
            } else if (month == 1 && (day < 1 || day > 28 + (isLeapYear(inputDate.getYear()) ? 1 : 0))) {
                return false;
            }
        } catch (java.text.ParseException e) {
            return false;
        }
        return true;
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public boolean calculateDaysDifference(String date) {
        Instant currentDate = Instant.now();
        Instant inputDate = Instant.parse(date);

        Duration duration = Duration.between(currentDate, inputDate);
        long daysDifference = duration.toDays();

        return (daysDifference >= 0 && daysDifference < 14);
    }
}

