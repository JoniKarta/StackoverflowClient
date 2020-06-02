package com.example.envirometalist.model;

import java.util.Date;
import java.util.Map;

public class Action {
    private String type;
    private String actionId;
    private ElementId element;
    private Date createdTimestamp;
    private Invoker invokedBy;
    private Map<String, Object> actionAttributes;

    public Action() {
    }

    public Action(String type, String actionId, ElementId element, Date createdTimestamp, Invoker invokedBy, Map<String, Object> actionAttributes) {
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

    public ElementId getElement() {
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
