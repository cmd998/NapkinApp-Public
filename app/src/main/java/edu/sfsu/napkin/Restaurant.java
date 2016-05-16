package edu.sfsu.napkin;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Restaurant class that stores all the restaurants
 * @author Christopher Dea (wrote line 14-88)
 * @since 11/14/2015.
 */
public class Restaurant {
    private String name;
    private JSONArray address;
    private String city;
    private String stateCode;
    private String postalCode;
    private String phone;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private String ratingImgUrlLarge;
    private double distance;

    /**
     * Gets all the info from the parse JSON and stores them here
     * @author Christopher Dea
     * @since 11/14/2015.
     */
    public Restaurant(String name, JSONArray address, String city, String stateCode, String postalCode,
                      double latitude, double longitude, String phone, String imageUrl, String ratingImgUrlLarge, double distance) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.stateCode = stateCode;
        this.postalCode = postalCode;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.ratingImgUrlLarge = ratingImgUrlLarge;
        this.distance = distance;
    }

    /**
     * Bunch of getters method to get particular info of the restaurant
     * @author Christopher Dea wrote line 52 - 95)
     * @since 11/14/2015.
     */
    public String getName() { return name; }
    public JSONArray getDisplayAddress() {
        return address;
    }

    public String getDisplayAddressPretty() {
        StringBuilder prettyAddress = new StringBuilder();
        for (int i = 0; i < address.length(); i++) {
            try {
                prettyAddress.append(address.get(i)).append("\n");
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        return prettyAddress.toString();
    }
    public String getCity(){
        return city;
    }
    public String getStateCode(){
        return stateCode;
    }
    public String getPostalCode(){
        return postalCode;
    }
    public String getPhone(){
        return phone;
    }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getImageUrl() { return imageUrl; }
    public String getRatingImgUrlLarge() {return ratingImgUrlLarge; }
    public double getDistance(){return distance; }
    public String getDistanceMiles(){
        double distance = getDistance();
        String distanceText;

        distance = (distance/ 1609.344);
        distanceText = String.format( "%.2f", distance);
        distanceText = String.format("%s mi", distanceText);

        return distanceText;
    }
}
