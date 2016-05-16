package edu.sfsu.napkin;

public class Wine {

    private int vintage;
    private String name;
    private String id;
    private String winery;
    private String varietal;
    private String type;
    private String imageUrl;
    private String description;

    public Wine(int vintage, String name, String id, String winery, String varietal, String type, String imageUrl) {
        this.vintage = vintage;
        this.name = name;
        this.id = id;
        this.winery = winery;
        this.varietal = varietal;
        this.type = type;
        this.imageUrl = imageUrl;
        description = "";
    }

    public boolean setDescription(String d) {
        description = d;
        return description.equals(d);
    }

    public int getVintage() {
        return vintage;
    }
    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
    public String getWinery() {
        return winery;
    }
    public String getVarietal() {
        return varietal;
    }
    public String getType() {
        return type;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getDescription() {
        return description;
    }
}
