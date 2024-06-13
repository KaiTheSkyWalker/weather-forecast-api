package org.example.weatherprojectspringboot.api.controller;

import org.example.weatherprojectspringboot.api.service.weatherService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@RestController
@RequestMapping("/api/weather")
public class weatherController {

    private final weatherService weatherService;

    @Autowired // we want to use the methods in this class
    public weatherController(weatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{cityName}")
    public ResponseEntity<String> getWeatherByCity(@PathVariable String cityName) throws IOException, ParseException {
            return weatherService.getWeather(cityName, null);
    }

    @GetMapping("/{cityName}/{date}")
    public ResponseEntity<String> getWeatherByCityAndDate(@PathVariable String cityName, @PathVariable String date) throws IOException, ParseException {
        return weatherService.getWeather(cityName, date);
    }
}

