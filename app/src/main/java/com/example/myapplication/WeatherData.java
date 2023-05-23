package com.example.myapplication;


public class WeatherData {
    private String location;
    private double temperature;
    private double humidity;
    private double pressure;
    private String description;

    public WeatherData(String location, double temperature, double humidity, double pressure, String description) {
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.description = description;
    }

    public WeatherData() {
        this.location = "";
        this.temperature = 0;
        this.humidity = 0;
        this.pressure =0 ;
        this.description = "";
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
