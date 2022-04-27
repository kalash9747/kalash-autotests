package models;

import annotations.Column;

public class Cars {
    private String id;
    private String mark;
    private String model;
    private String price;
    @Column(name = "engine_type_id")
    private String engineTypeId;
    @Column(name = "person_id")
    private String personId;

    public Cars() {
    }

    public String getId() {
        return id;
    }

    public String getMark() {
        return mark;
    }

    public String getModel() {
        return model;
    }

    public String getPrice() {
        return price;
    }

    public String getEngineTypeId() {
        return engineTypeId;
    }

    public String getPersonId() {
        return personId;
    }

    @Override
    public String toString() {
        return "Cars{" +
                "id='" + id + '\'' +
                ", mark='" + mark + '\'' +
                ", model='" + model + '\'' +
                ", price='" + price + '\'' +
                ", engineTypeId='" + engineTypeId + '\'' +
                ", personId='" + personId + '\'' +
                '}';
    }
}
