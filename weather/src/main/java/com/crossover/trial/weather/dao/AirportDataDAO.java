package com.crossover.trial.weather.dao;

import java.util.List;
import java.util.Map;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.AtmosphericInformation;
import com.crossover.trial.weather.DataPoint;

public interface AirportDataDAO {
	
    /**
     * Given an iataCode find the airport data
     *
     * @param iataCode as a string
     * @return airport data or null if not found
     */
    public AirportData findAirportData(String iataCode);

    /**
     * Given an iataCode find the airport data
     *
     * @param iataCode as a string
     * @return airport data or null if not found
     */
	public int getAirportDataIdx(String iataCode);
	
	/**
	 * Add AirportData
	 * 
	 * @param airportData
	 */
	public void addAirportData(AirportData airportData);

	/**
	 * Return all Airports
	 * 
	 * @return List<AirportData>
	 */
    public List<AirportData> getAllAirports();
    
    /**
     * Update AtmosphericInformation
     * 
     * @param ad
     * @param pointType
     * @param dp
     */
    public void updateAtmosphericInformation(AirportData ad, String pointType, DataPoint dp);
    
    /**
     * Delete AirportData
     * @param iataCode
     */
    public void deleteAirport(String iataCode);

    /**
     * 
     * Return all AtmosphericInformation
     * 
     * @return List<AtmosphericInformation>
     */
    public List<AtmosphericInformation> getAllAtmosphericInformation();
    
    /**
     * 
     * Add AtmosphericInformation on Map
     * 
     * @param airportData
     * @param atmosphericInformation
     */
    public void addAtmosphericInformation(AirportData airportData, AtmosphericInformation atmosphericInformation);
    
    /**
     * Create a Map with data of access
     * 
     * @return Map<String, Object>
     */
    public Map<String, Object> getPerformance();

}
