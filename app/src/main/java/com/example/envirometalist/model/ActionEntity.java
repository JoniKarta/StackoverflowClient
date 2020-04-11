package com.example.envirometalist.model;

import java.util.Date;
import java.util.Map;

public class ActionEntity {
    private String type;
    private String actionId;
    private Element element;
    private Date createdTimestamp;
    private Invoker invokedBy;
    private Map<String, Object> actionAttributes;

    public ActionEntity() {
    }

    public ActionEntity(String type, String actionId, Element element, Date createdTimestamp, Invoker invokedBy, Map<String, Object> actionAttributes) {
        this.type = type;
        this.actionId = actionId;
        this.element = element;
        this.createdTimestamp = createdTimestamp;
        this.invokedBy = invokedBy;
        this.actionAttributes = actionAttributes;
    }

    public String getType() {
        return type;
    }

    public String getActionId() {
        return actionId;
    }

    public Element getElement() {
        return element;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public Invoker getInvokedBy() {
        return invokedBy;
    }

    public Map<String, Object> getActionAttributes() {
        return actionAttributes;
    }
}
