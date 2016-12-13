package com.crossover.trial.weather.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crossover.trial.weather.AirportData;
import com.crossover.trial.weather.AtmosphericInformation;
import com.crossover.trial.weather.DataPoint;
import com.crossover.trial.weather.DataPointType;
import com.crossover.trial.weather.util.CacheAWS;

public class AirportDataDAOImp implements AirportDataDAO{

    
	@Override
	public AirportData findAirportData(String iataCode) {
		return CacheAWS.airportData.get(iataCode);
	}

	@Override
	public List<AirportData> getAllAirports() {
		List<AirportData> list = new ArrayList<>();		
		list.addAll(CacheAWS.airportData.values());
		return list;
	}

	@Override
	public int getAirportDataIdx(String iataCode) {
        AirportData ad = findAirportData(iataCode);
        return getAllAirports().indexOf(ad);
	}

	@Override
	public void addAirportData(AirportData airportData) {
		CacheAWS.airportData.put(airportData.getIata(), airportData);
	}

	@Override
	public void updateAtmosphericInformation(AirportData ad, String pointType, DataPoint dp) {
		AtmosphericInformation ai = CacheAWS.atmosphericInformation.get(ad);
		if(ai == null){
			ai = new AtmosphericInformation();
			CacheAWS.atmosphericInformation.put(ad, ai);
		}

        if (pointType.equalsIgnoreCase(DataPointType.WIND.name())) {
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }else if (pointType.equalsIgnoreCase(DataPointType.TEMPERATURE.name())) {
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }else if (pointType.equalsIgnoreCase(DataPointType.HUMIDTY.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }else if (pointType.equalsIgnoreCase(DataPointType.PRESSURE.name())) {
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }else if (pointType.equalsIgnoreCase(DataPointType.CLOUDCOVER.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }else if (pointType.equalsIgnoreCase(DataPointType.PRECIPITATION.name())) {
            if (dp.getMean() >=0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }
        
        throw new IllegalStateException("couldn't update atmospheric data");

	}

	@Override
	public void deleteAirport(String iataCode) {		
		AirportData ad = findAirportData(iataCode);
		if(ad != null){
			CacheAWS.atmosphericInformation.remove(ad);
			CacheAWS.airportData.remove(ad.getIata());
		}
	}

	@Override
	public List<AtmosphericInformation> getAllAtmosphericInformation() {
		List<AtmosphericInformation> list = new ArrayList<>();
		list.addAll(CacheAWS.atmosphericInformation.values());
		return list;
	}

	@Override
	public void addAtmosphericInformation(AirportData airportData, AtmosphericInformation atmosphericInformation) {
		CacheAWS.atmosphericInformation.put(airportData, atmosphericInformation);		
	}

	@Override
	public Map<String, Object> getPerformance() {
        Map<String, Object> retval = new HashMap<>();

        int datasize = 0;
        for (AtmosphericInformation ai : CacheAWS.atmosphericInformation.values()) {
            // we only count recent readings
            if (ai.getCloudCover() != null
                || ai.getHumidity() != null
                || ai.getPressure() != null
                || ai.getPrecipitation() != null
                || ai.getTemperature() != null
                || ai.getWind() != null) {
                // updated in the last day
                if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
                    datasize++;
                }
            }
        }
        retval.put("datasize", datasize);
        
        Map<String, Double> freq = new HashMap<>();
        
        int count = 0;        
        for(Integer i : CacheAWS.requestFrequency.values()){
        	count += i;
        }
        
        if(count > 0){
        	// fraction of queries
	        for (AirportData data : getAllAirports()) {
	            double frac = (double)CacheAWS.requestFrequency.getOrDefault(data, 0) / count;
	            freq.put(data.getIata(), frac);
	        }
        }
        retval.put("iata_freq", freq);
        
        int m = CacheAWS.radiusFreq.keySet().stream()
                .max(Double::compare)
                .orElse(1000.0).intValue() + 1;

        int[] hist = new int[m];
        for (Map.Entry<Double, Integer> e : CacheAWS.radiusFreq.entrySet()) {
            int i = e.getKey().intValue();
            hist[i] += e.getValue();
        }
        retval.put("radius_freq", hist);


		return retval;
	}

	
	
}
