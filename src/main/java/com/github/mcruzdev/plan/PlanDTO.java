package com.github.mcruzdev.plan;

public class PlanDTO {

    private String name;
    private String status;
    private String description;

    public Plan convert() {
        return new Plan(name, status, description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
