package com.crossover.trial.weather;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.dao.AirportDataDAO;
import com.crossover.trial.weather.dao.AirportDataDAOImp;
import com.crossover.trial.weather.util.CacheAWS;
import com.crossover.trial.weather.util.CalculateAWS;
import com.google.gson.Gson;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

    public final static Logger LOGGER = Logger.getLogger(RestWeatherQueryEndpoint.class.getName());

    /** shared gson json to object factory */
    public static final Gson gson = new Gson();
    
    private CalculateAWS calculate = new CalculateAWS();
    
    private AirportDataDAO airportDataDAO = new AirportDataDAOImp();

    static {
        init();
    }
    /**
     * Retrieve service health including total size of valid data points and request frequency information.
     *
     * @return health stats for the service as a string
     */
    @Override
    public String ping() {
    	Map<String, Object> retval = airportDataDAO.getPerformance();

        return gson.toJson(retval);
    }

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport information and
     * return a list of matching atmosphere information.
     *
     * @param iata the iataCode
     * @param radiusString the radius in km
     *
     * @return a list of atmospheric information
     */
    @Override
    public Response weather(String iata, String radiusString) {
    	List<AtmosphericInformation> retval = new ArrayList<>();
    	
        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
        
        if(radius < 0){
        	radius = 0;
        }
        
        AirportData ad = airportDataDAO.findAirportData(iata);
        
        if(ad == null){
        	LOGGER.log(Level.SEVERE, "Can not find Airport: " + "iata: " + iata);
        	return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        updateRequestFrequency(ad, radius);

    	if (radius == 0) {
        	addAtmosphericInformation(CacheAWS.atmosphericInformation.get(ad), retval);
        } else {
            List<AirportData> airportData = airportDataDAO.getAllAirports();
            for (int i=0;i< airportData.size(); i++){
            	AirportData a = airportData.get(i);
            	double distance = calculate.calculateDistance(ad, a);
                if (distance <= radius){
                    AtmosphericInformation ai = CacheAWS.atmosphericInformation.get(a);
                    addAtmosphericInformation(ai, retval);
                }
            }
        }
        return Response.status(Response.Status.OK).entity(retval).build();
    }


    /**
     * Records information about how often requests are made
     *
     * @param airportData an AirportData
     * @param radius query radius
     */
    public void updateRequestFrequency(AirportData airportData, Double radius) {
        CacheAWS.requestFrequency.put(airportData, CacheAWS.requestFrequency.getOrDefault(airportData, 0) + 1);
        CacheAWS.radiusFreq.put(radius, CacheAWS.radiusFreq.getOrDefault(radius, 0) + 1);
    }

    /**
     * A dummy init method that loads hard coded data
     */
    protected static void init() {
    	CacheAWS.airportData.clear();
    	CacheAWS.atmosphericInformation.clear();
    	CacheAWS.requestFrequency.clear();

    	WeatherCollectorEndpoint weatherCollectorEndpoint = new RestWeatherCollectorEndpoint();
    	weatherCollectorEndpoint.addAirport("BOS", "42.364347", "-71.005181");
    	weatherCollectorEndpoint.addAirport("EWR", "40.6925", "-74.168667");
    	weatherCollectorEndpoint.addAirport("JFK", "40.639751", "-73.778925");
    	weatherCollectorEndpoint.addAirport("LGA", "40.777245", "-73.872608");
    	weatherCollectorEndpoint.addAirport("MMU", "40.79935", "-74.4148747");
    }
    
    private void addAtmosphericInformation(AtmosphericInformation ai, List<AtmosphericInformation> retval){
    	if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPrecipitation() != null
                || ai.getPressure() != null || ai.getTemperature() != null || ai.getWind() != null){
	         retval.add(ai);
	     }
    }

}
