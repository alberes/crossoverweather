package com.crossover.trial.weather;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.dao.AirportDataDAO;
import com.crossover.trial.weather.dao.AirportDataDAOImp;
import com.google.gson.Gson;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {
    public final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

    /** shared gson json to object factory */
    public final static Gson gson = new Gson();
    
    AirportDataDAO airportDataDAO = new AirportDataDAOImp();

    @Override
    public Response ping() {
        return Response.status(Response.Status.OK).entity("ready").build();
    }

    @Override
    @POST
    @Path("/weather/{iata}/{pointType}")
    public Response updateWeather(@PathParam("iata") String iataCode,
                                  @PathParam("pointType") String pointType,
                                  String datapointJson) {
    	
    	AirportData ad = airportDataDAO.findAirportData(iataCode);
    	if(ad == null){
    		LOGGER.log(Level.SEVERE, "Can not find Airport: " + "iata: " + iataCode + "Datapoint: " + datapointJson);
    		return Response.status(Response.Status.BAD_REQUEST).build();
    	}
    	//Try convert DataPoint String to Json
    	DataPoint dt = null;
    	try{
    		dt = gson.fromJson(datapointJson, DataPoint.class);
    		if(dt == null){
    			LOGGER.log(Level.SEVERE, "Can not convert Datapoint: " + "iata: " + iataCode + " Datapoint: " + datapointJson);
                return Response.status(Response.Status.BAD_REQUEST).build();
    		}
    	}catch(Exception e){
    		LOGGER.log(Level.SEVERE, "Can not convert Datapoint: " + "iata: " + iataCode + " Datapoint: " + datapointJson, e);
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
    	}
    	
        try {
            addDataPoint(iataCode, pointType, dt);
        } catch (WeatherException e) {
        	LOGGER.log(Level.SEVERE, iataCode + " Erro on service" , e);
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).build();
    }


    @Override
    public Response getAirports() {
        Set<String> retval = new HashSet<>();
        List<AirportData> airportData = airportDataDAO.getAllAirports();
        for (AirportData ad : airportData) {
            retval.add(ad.getIata());
        }
        return Response.status(Response.Status.OK).entity(retval).build();
    }


    @Override
    @GET
    @Path("/airport/{iata}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAirport(@PathParam("iata") String iata) {
        AirportData ad = airportDataDAO.findAirportData(iata);
        return Response.status(Response.Status.OK).entity(ad).build();
    }


    @Override
    @POST
    @Path("/airport/{iata}/{lat}/{long}")
    public Response addAirport(@PathParam("iata") String iata,
                               @PathParam("lat") String latString,
                               @PathParam("long") String longString) {
    	if(iata.length() != 3){
    		LOGGER.log(Level.SEVERE, iata + " invalid!");
    		return Response.status(Response.Status.BAD_REQUEST).build();
    	}
        addAirport(iata, Double.valueOf(latString), Double.valueOf(longString));
        return Response.status(Response.Status.OK).build();
    }


    @Override
    @DELETE
    @Path("/airport/{iata}")
    public Response deleteAirport(@PathParam("iata") String iata) {
    	airportDataDAO.deleteAirport(iata);
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response exit() {
        System.exit(0);
        return Response.noContent().build();
    }
    //
    // Internal support methods
    //

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dp a datapoint object holding pointType data
     *
     * @throws WeatherException if the update can not be completed
     */
    public void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {
    	AirportData ad = airportDataDAO.findAirportData(iataCode);
    	airportDataDAO.updateAtmosphericInformation(ad, pointType, dp);
    }

    /**
     * Add a new known airport to our list.
     *
     * @param iataCode 3 letter code
     * @param latitude in degrees
     * @param longitude in degrees
     *
     * @return the added airport
     **/
    public AirportData addAirport(String iataCode, double latitude, double longitude) {
        AirportData ad = new AirportData();
        ad.setIata(iataCode);
        ad.setLatitude(latitude);
        ad.setLongitude(longitude);
        
        airportDataDAO.addAirportData(ad);

        AtmosphericInformation ai = new AtmosphericInformation();
        airportDataDAO.addAtmosphericInformation(ad, ai);
        return ad;
    }
}
