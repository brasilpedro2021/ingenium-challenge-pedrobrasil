package com.forex.statistics;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

@Service
public class ForexStatisticsService {

    private static final String FOREX_RECORDS_FILE_PATH = "json/euraud.json";
    private static final String INTEGER_KEY = "$numberInt";
    private static final String BID_KEY = "bid";
    private static final String ASK_KEY = "ask";
    
    public static void loadList() throws IOException, ParseException, JSONException {

        InputStream is = null;
		Scanner sc = null;
		Double minimumAsk = null;
		Double maximumAsk = null;
		Double averageAsk = null;
		Double medianAsk = null;
		Double minimumBid = null;
		Double maximumBid = null;
		Double averageBid = null;
		Double medianBid = null;
		Long volumeAsk = 0L;
		Long volumeBid = 0L;
		Long startTime = null;
		Integer dataNumber = 0;
		List<Double> valuesAsk = new ArrayList<Double>();
		List<Double> valuesBid = new ArrayList<Double>();
		
		try {
	        is = getInputStream();
		    sc = new Scanner(is, "UTF-8");
		    while (sc.hasNextLine()) {
		        String line = sc.nextLine();		        
		        JSONObject jo = new JSONObject(line);
		        Long currentTime = Long.parseLong(jo.getJSONObject("time").get("$numberLong").toString()) / (1000 * 60);
		        Double valueAsk = Double.parseDouble(jo.getJSONObject(ASK_KEY).get(INTEGER_KEY).toString()) / 10000;
		        Double valueBid = Double.parseDouble(jo.getJSONObject(BID_KEY).get(INTEGER_KEY).toString()) / 10000;
		        Long valueVolumeAsk = Long.parseLong(jo.getJSONObject("askVolume").get(INTEGER_KEY).toString());
		        Long valueVolumeBid = Long.parseLong(jo.getJSONObject("bidVolume").get(INTEGER_KEY).toString());
		        if (startTime != null && (currentTime - startTime) < 30) {
			        dataNumber++;
			        volumeAsk += valueVolumeAsk;
			        volumeBid += valueVolumeBid;
			        valuesAsk.add(valueAsk);
			        Collections.sort(valuesAsk);
			        valuesBid.add(valueBid);
			        Collections.sort(valuesBid);
		        	minimumAsk = Math.min(minimumAsk, valueAsk);
		        	maximumAsk = Math.max(maximumAsk, valueAsk);
		        	averageAsk = average(averageAsk, valueAsk, dataNumber);
		        	medianAsk = getMedian(valuesAsk);
		        	minimumBid = Math.min(minimumBid, valueBid);
		        	maximumBid = Math.max(maximumBid, valueBid);
		        	averageBid = average(averageBid, valueBid, dataNumber);
		        	medianBid = getMedian(valuesBid);
		        	
		        } else {
		        	if (startTime != null && (currentTime - startTime) > 30) {	 
				        System.out.println("Minimum Ask Value: " + minimumAsk);	        	
				        System.out.println("Maximum Ask Value: " + maximumAsk);	        	
				        System.out.println("Average Ask Value: " + averageAsk);	        	
				        System.out.println("Median Ask Value: " + minimumAsk);	        	
				        System.out.println("Total Ask Volume: " + volumeAsk);	        	
				        System.out.println("Minimum Bid Value: " + maximumBid);	        	
				        System.out.println("Maximum Bid Value: " + averageBid);	        	
				        System.out.println("Average Bid Value: " + medianBid);	        	
				        System.out.println("Median Bid Value: " + medianAsk);	        	
				        System.out.println("Total Bid Volume: " + volumeBid);
		        	}
		        	dataNumber = 1;
			        volumeAsk = valueVolumeAsk;
			        volumeBid = valueVolumeBid;
		        	startTime = currentTime;
		    		valuesAsk = new ArrayList<Double>();
		    		valuesAsk.add(valueAsk);
		    		valuesBid = new ArrayList<Double>();
		    		valuesBid.add(valueBid);
		        	minimumAsk = valueAsk;
		        	maximumAsk = valueAsk;
		        	averageAsk = valueAsk;
		        	medianAsk = valueAsk;
		        	minimumBid = valueBid;
		        	maximumBid = valueBid;
		        	averageBid = valueBid;
		        	medianBid = valueBid;

		        }

		    }
		    if (sc.ioException() != null) {
		        throw sc.ioException();
		    }
		} finally {
		    if (is != null) {
		        is.close();
		    }
		    if (sc != null) {
		        sc.close();
		    }
		}

    }
	
    private static Double getMedian(List<Double> values) {
		if (values == null || values.isEmpty()) {
			return null;
		}
		if (values.size() % 2 != 0) {
			return values.get((values.size() -1 ) / 2);
		}
		return (values.get((values.size()) / 2) + values.get((values.size()) / 2 - 1)) / 2;
	}

	private static Double average(Double averageAsk, double newValue, Integer averageWeight) {
		return (averageAsk * (averageWeight - 1) + newValue) / averageWeight;
	}

	private static InputStream getInputStream() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(FOREX_RECORDS_FILE_PATH);
        return resource.getInputStream();
    }
    
    
    
}
