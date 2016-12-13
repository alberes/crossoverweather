package com.crossover.trial.weather.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.AtmosphericInformation;


/**
 * 
 * Hold data on synchronized map.
 * 
 * We can change for Bigdata or similar
 * 
 * @author alberes
 *
 */
public class CacheAWS {

	/** all known airports */
    public static Map<String, AirportData> airportData = new ConcurrentHashMap<>();
    
    /** atmospheric information for each airport, idx corresponds with airportData */
    public static Map<AirportData, AtmosphericInformation> atmosphericInformation = new ConcurrentHashMap<>();

    /**
     * Internal performance counter to better understand most requested information, this map can be improved but
     * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
     * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
     * performance metrics {@link #ping()}
     */
    public static Map<AirportData, Integer> requestFrequency = new HashMap<AirportData, Integer>();

    public static Map<Double, Integer> radiusFreq = new HashMap<Double, Integer>();

}
