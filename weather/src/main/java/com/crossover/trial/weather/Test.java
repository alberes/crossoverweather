package com.crossover.trial.weather;

import com.google.gson.Gson;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		DataPoint datapointJson = new DataPoint(10, 15, 20, 25, 30);
		String json = gson.toJson(datapointJson);
		System.out.println(json);
	}

}
