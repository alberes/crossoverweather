package com.crossover.trial.weather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class LocalServiceEndpointTest {

    private WeatherQueryEndpoint weatherQueryEndpoint;

    private WeatherCollectorEndpoint weatherCollectorEndpoint;

    private Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        weatherQueryEndpoint = new RestWeatherQueryEndpoint();

        weatherCollectorEndpoint = new RestWeatherCollectorEndpoint();

        gson = new Gson();
        
		AirportData airportData = new AirportData();
    	airportData.setIata("EWR");
    	airportData.setLatitude(40.6925);
    	airportData.setLongitude(-74.168667);
    	
		weatherCollectorEndpoint.addAirport(airportData.getIata(), String.valueOf(airportData.getLatitude()),String.valueOf(airportData.getLongitude()));
		
		airportData = new AirportData();
    	airportData.setIata("LHR");
    	airportData.setLatitude(51.4775);
    	airportData.setLongitude(-0.461389);
    	
		weatherCollectorEndpoint.addAirport(airportData.getIata(), String.valueOf(airportData.getLatitude()),String.valueOf(airportData.getLongitude()));
    }

	@Test
	public void testAddDataPoint() {
		AirportData airportData = new AirportData();
    	airportData.setIata("STN");
    	airportData.setLatitude(51.885);
    	airportData.setLongitude(0.235);
    	
		weatherCollectorEndpoint.addAirport(airportData.getIata(), String.valueOf(airportData.getLatitude()),String.valueOf(airportData.getLongitude()));
		AirportData airportDataOther = (AirportData)weatherCollectorEndpoint.getAirport("STN").getEntity();
		Assert.assertEquals("AirportData STN", airportData, airportDataOther);
	}

	@Test
	public void testAddDataPoint2() {
		AirportData airportData = new AirportData();
    	airportData.setIata("FFFF");
    	airportData.setLatitude(51.885);
    	airportData.setLongitude(0.235);
    	
    	Response response = weatherCollectorEndpoint.addAirport(airportData.getIata(), String.valueOf(airportData.getLatitude()),String.valueOf(airportData.getLongitude()));
		Assert.assertEquals("IATA not valid", 400, response.getStatus());
	}
	
	@Test
	public void testGetAirports() {
		Set<String> list = (Set<String>) weatherCollectorEndpoint.getAirports().getEntity();
		Assert.assertTrue("Airports ", !list.isEmpty());
	}
	
	@Test
	public void testGetAirport() {
		AirportData airportData = (AirportData) weatherCollectorEndpoint.getAirport("EWR").getEntity();
		Assert.assertNotNull("Found Airport", airportData);
		Assert.assertEquals("AirportData EWR", "EWR", airportData.getIata());
	}

	@Test
	public void testUpdateWeather() {
		DataPoint dp = new DataPoint.Builder()
                .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
		Response response = weatherCollectorEndpoint.updateWeather("EWR", DataPointType.WIND.name(), gson.toJson(dp));
		Assert.assertEquals("Updated weather", 200, response.getStatus());
	}

	@Test
	public void testDeleteAirport() {
		Response response = weatherCollectorEndpoint.deleteAirport("EWR");
		Assert.assertEquals("Deleted AieportData", 200, response.getStatus());
		
		AirportData airportData = (AirportData) weatherCollectorEndpoint.getAirport("EWR").getEntity();
		Assert.assertNull("Not found Airport", airportData);

	}
	
	@Test
	public void tesWeather(){
		DataPoint windDp = new DataPoint.Builder()
                .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
        weatherCollectorEndpoint.updateWeather("LHR", "wind", gson.toJson(windDp));
        weatherQueryEndpoint.weather("LHR", "0").getEntity();

        DataPoint cloudCoverDp = new DataPoint.Builder()
                .withCount(4).withFirst(10).withMedian(60).withLast(100).withMean(50).build();
        weatherCollectorEndpoint.updateWeather("LHR", "cloudcover", gson.toJson(cloudCoverDp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) weatherQueryEndpoint.weather("LHR", "0").getEntity();
        assertEquals(ais.get(0).getWind(), windDp);
        assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
	}
	
}
