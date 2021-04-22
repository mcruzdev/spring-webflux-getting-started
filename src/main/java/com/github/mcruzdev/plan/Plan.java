package com.github.mcruzdev.plan;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import java.beans.Transient;

public class Plan implements Persistable<String> {

    @Id
    private String id;
    private String name;
    private String status;
    private String description;

    protected Plan() {
    }

    public Plan(String name, String status, String description) {
        this.name = name;
        this.status = status;
        this.description = description;
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.id == null;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
