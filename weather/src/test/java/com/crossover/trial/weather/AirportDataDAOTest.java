package com.crossover.trial.weather;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.crossover.trial.weather.dao.AirportDataDAO;
import com.crossover.trial.weather.dao.AirportDataDAOImp;
import com.crossover.trial.weather.util.CacheAWS;

public class AirportDataDAOTest {

	AirportDataDAO airportDataDAO;
	
    @Before
    public void setUp() throws Exception {
    	airportDataDAO = new AirportDataDAOImp();
    	AirportData airportData = new AirportData();
    	airportData.setIata("EGLL");
    	airportData.setLatitude(51.4775);
    	airportData.setLongitude(-0.461389);
    	airportDataDAO.addAirportData(airportData);
    }

	@Test
	public void testFindAirportData() {
		AirportData airportData = airportDataDAO.findAirportData("EGLL");
		Assert.assertNotNull("Found Airport", airportData);
		Assert.assertEquals("AirportData EGLL", "EGLL", airportData.getIata());
	}

	@Test
	public void testGetAllAirports() {
		List<AirportData> list = airportDataDAO.getAllAirports();
		Assert.assertTrue("Airports ", !list.isEmpty());
	}

	@Test
	public void testAddAirportData() {
		AirportData airportData = new AirportData();
    	airportData.setIata("MMU");
    	airportData.setLatitude(40.79935);
    	airportData.setLongitude(-74.4148747);
    	airportDataDAO.addAirportData(airportData);
    	
    	AirportData airportDataOther = airportDataDAO.findAirportData("MMU");
    	Assert.assertTrue("Airport MMU", airportData.equals(airportDataOther));
	}

	@Test
	public void testUpdateAtmosphericInformation() {
		AirportData airportData = airportDataDAO.findAirportData("MMU");
		DataPoint dp = new DataPoint(1, 2, 3, 4, 5);
		
		airportDataDAO.updateAtmosphericInformation(airportData, DataPointType.WIND.name(), dp);
		AtmosphericInformation ai = CacheAWS.atmosphericInformation.get(airportData);
		Assert.assertNotNull("AtmosphericInformation found", ai);
	}

	@Test
	public void testGetAllAtmosphericInformation() {
		List<AtmosphericInformation> list = airportDataDAO.getAllAtmosphericInformation();
		Assert.assertTrue("AtmosphericInformation ", !list.isEmpty());
	}



	@Test
	public void testAddAtmosphericInformation() {
    	AirportData airportData = new AirportData();
    	airportData.setIata("JFK");
    	airportData.setLatitude(40.639751);
    	airportData.setLongitude(-73.778925);
    	airportDataDAO.addAirportData(airportData);

    	airportDataDAO.addAirportData(airportData);
    	
		AtmosphericInformation atmosphericInformation = new AtmosphericInformation();
		atmosphericInformation.setCloudCover(new DataPoint(1, 2, 3, 4, 5));
		atmosphericInformation.setHumidity(new DataPoint(6, 7, 8, 9, 10));
		atmosphericInformation.setPrecipitation(new DataPoint(11, 12, 13, 14, 15));
		atmosphericInformation.setPressure(new DataPoint(16, 17, 18, 19, 20));
		atmosphericInformation.setTemperature(new DataPoint(21, 800, 23, 24, 25));
		airportDataDAO.addAtmosphericInformation(airportData, atmosphericInformation);
		
		AtmosphericInformation ai = CacheAWS.atmosphericInformation.get(airportData);
		Assert.assertNotNull("AtmosphericInformation found", ai);
	}

	@Test
	public void testDeleteAirport() {
		airportDataDAO.deleteAirport("MMU");
		AirportData airportData = airportDataDAO.findAirportData("MMU");
		Assert.assertNull("AirportData was removed", airportData);
	}
}
