package com.example.envirometalist.model;

import java.util.Date;
import java.util.Map;

public class Element {

    private String elementId; // In the ElementBoundary it was String
    private String type;
    private String name;
    private boolean active;
    private Date createdTimestamp;
    private Creator createdBy; // createdBy was a Creator class
    private Location location;
    private Map<String, Object> elementAttribute;
    public Element() {

    }

    public Element(String elementId, String type, String name, boolean active, Date createdTimestamp, Creator createdBy, Location location, Map<String, Object> elementAttribute) {
        this.elementId = elementId;
        this.type = type;
        this.name = name;
        this.active = active;
        this.createdTimestamp = createdTimestamp;
        this.createdBy = createdBy;
        this.location = location;
        this.elementAttribute = elementAttribute;
    }

    public String getElementId() {
        return elementId;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean getActive() {
        return active;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public Creator getCreatedBy() {
        return createdBy;
    }

    public Location getLocation() {
        return location;
    }

    public Map<String, Object> getElementAttribute() {
        return elementAttribute;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public void setCreatedBy(Creator createdBy) {
        this.createdBy = createdBy;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setElementAttribute(Map<String, Object> elementAttribute) {
        this.elementAttribute = elementAttribute;
    }

    @Override
    public String toString() {
        return "Element{" +
                "elementId='" + elementId + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy=" + createdBy +
                ", location=" + location +
                ", elementAttribute=" + elementAttribute +
                '}';
    }
}

