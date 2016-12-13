package com.crossover.trial.weather;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 *
 * TODO: Implement the Airport Loader
 * 
 * @author code test administrator
 */
public class AirportLoader {

    /** end point for read queries */
    private WebTarget query;

    /** end point to supply updates */
    private WebTarget collect;

    public AirportLoader() {
        Client client = ClientBuilder.newClient();
        query = client.target("http://localhost:9090/query");
        collect = client.target("http://localhost:9090/collect");
    }

    public void upload(InputStream airportDataStream) throws IOException{
    	
    	Response response = collect.path("/ping").request().get();
    	if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            System.out.println("Service AWS at " + collect.getUri() + "is unavailable.");
            return;
    	}
    	
    	
    	
        BufferedReader reader = new BufferedReader(new InputStreamReader(airportDataStream));
        String l = null;
        String[] data = null;
        while ((l = reader.readLine()) != null) {
        	//System.out.println(l);
        	data = l.split(",");
        	//System.out.println(data[4] +"," +  data[6] + "," + data[7]);
        	response = collect.path("/airport/" + data[4].replaceAll("\"", "") + "/" + data[6] + "/" + data[7]).
        			request().post(Entity.entity("", "application/json"));
        	System.out.println(data[4] + "," +  data[6] + "," + data[7] + ": "+ (response.getStatus() == Response.Status.OK.getStatusCode()));
        	System.out.println("http://localhost:9090/collect" + "/airport/" + data[4].replaceAll("\"", "") + "/" + data[6] + "/" + data[7]);
        }
    }

    public static void main(String args[]) throws IOException{
        File airportDataFile = new File(args[0]);
        if (!airportDataFile.exists() || airportDataFile.length() == 0) {
            System.err.println(airportDataFile + " is not a valid input");
            System.exit(1);
        }

        AirportLoader al = new AirportLoader();
        al.upload(new FileInputStream(airportDataFile));
        System.exit(0);
    }
}
