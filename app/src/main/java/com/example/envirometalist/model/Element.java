package com.example.envirometalist.model;

import java.util.Date;

public class Element {

    private Long elementId; // In the ElementBoundary it was String
    private String type;
    private String name;
    private boolean active;
    private Date createdTimestamp;
    private String createdBy; // createdBy was a Creator class
    private double lat; // lat/lang were Location class
    private double lang;
    private String elementAttribute; // In the ElementBoundary it was map

    public Long getElementId() {
        return elementId;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public boolean isActive() {
        return active;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public String getElementAttribute() {
        return elementAttribute;
    }

    public void setElementAttribute(String elementAttribute) {
        this.elementAttribute = elementAttribute;
    }

}

/*
public class Element {
    private String elementId;

    public Element() {
    }

    public Element(String elementId) {
        super();
        this.elementId = elementId;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    @Override
    public String toString() {
        return "Element [elementId=" + elementId + "]";
    }

}
*/
