package com.exam.API;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;

public class WeatherAPI {
//    public static void main(String[] args) throws IOException, ParseException {
//    	getAPI("60", "127");
//    }
	
	static public ArrayList<String> category;
	static public ArrayList<String> value;
    
    public static void getAPI(String lat, String lon) throws IOException, ParseException {
    	// 오늘 날짜 받아오기 
    	Date date = new Date();
    	LocalTime now = LocalTime.now();
    	int hour = now.getHour() - 1;
    	String bHour = Integer.toString(hour) + "40";
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//    	System.out.println(sdf.format(date));
    	
    	if (hour < 10) {
    		bHour = null;
    		bHour = "0" + Integer.toString(hour) + "40";
    	}
    	
    	// 초단기실황조회.
    	String apiURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";
    	String serviceKey = "tjD5lYUAzevWJitnYN0uROhvyqlGc4NcgUCPWef%2F%2FfI3sAEhPEb%2BzxSzms8ocoznYQ9UsAAGaUXQlvlcVk%2Fksw%3D%3D";
    	String nx = lat;  // 위도 
    	String ny = lon; // 경도 
    	String baseDate = sdf.format(date);  // 조회하고 싶은 날짜
    	String baseTime = bHour;   // 조회하고 싶은 시간 
    	String type = "json";  // 타입 
    	String numberOfRows = "1000";  // 한 페이지 결과 수
    	
//    	System.out.println(baseTime);
    	
    	StringBuilder urlBuilder = new StringBuilder(apiURL);
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode(type, "UTF-8")); 
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); 
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); 
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); 
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numberOfRows", "UTF-8") + "=" + URLEncoder.encode(numberOfRows, "UTF-8")); 
        
        /*
         * Get 방식으로 전송해서 파라미터 받아오기
         */        
        URL url = new URL(urlBuilder.toString());
        System.out.println(url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String data = sb.toString();
        System.out.println(data);
        System.out.println("-----------------------------");
        
        // Json parser를 만들어 만들어진 문자열 데이터를 객체화.
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject)parser.parse(data);
        
        // response 키를 가지고 데이터를 파싱
        JSONObject parse_response = (JSONObject)obj.get("response");
        
        // response로부터 body 찾기
        JSONObject parse_body = (JSONObject)parse_response.get("body");
        
        // body로부터 items 찾기
        JSONObject parse_items = (JSONObject)parse_body.get("items");
        JSONArray parse_item = (JSONArray)parse_items.get("item");
        
        category = findCategory(parse_item);
        value = findValue(parse_item);
        
//        for (int i = 0; i < category.size(); i++) {
//        	System.out.println(category.get(i) + " : " + value.get(i));
//        }
        
        System.out.println("-----------------------");
        
        for (int i = 0; i < parse_item.size(); i++) {
        	System.out.println(parse_item);
        	System.out.println(parse_item.get(i));
        }
        
        
//        T1H	기온	℃
//        RN1	1시간 강수량	mm
//        UUU	동서바람성분	m/s
//        VVV	남북바람성분	m/s
//        REH	습도	%
//        PTY	강수형태	코드값
//        VEC	풍향	deg
//        WSD	풍속	m/s
    }
//    
    public static ArrayList<String> findCategory(JSONArray parse_item) {
    	ArrayList categoryList = new ArrayList();
    	String getCategory = null;
    	
    	try {
    	 for (int i = 0; i <parse_item.size(); i++) {
    		 System.out.println(parse_item.size());
         	JSONObject parse_test = (JSONObject)parse_item.get(i);
         	
         	getCategory = (String)parse_test.get("category");
         	categoryList.add(i, getCategory);  
    		}
    	}catch (NullPointerException e) {
    		 e.printStackTrace();
    		 System.out.println("에ㄹ~");
    	 }
        
    	System.out.println("category : " + categoryList);
    	return categoryList;
    }
    
    public static ArrayList<String> findValue(JSONArray parse_item) {
    	ArrayList valueList = new ArrayList();
    	String value = null;
    	
    	try {
    		for (int i = 0; i < parse_item.size(); i++) {
    			JSONObject parse_test = (JSONObject)parse_item.get(i);
    			value = (String)parse_test.get("obsrValue");
    			valueList.add(i, value);
    			value = null;
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	System.out.println("value " + valueList);
    	return valueList;
    }
}
