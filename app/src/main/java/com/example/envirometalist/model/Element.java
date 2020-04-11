package com.example.envirometalist.model;

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
