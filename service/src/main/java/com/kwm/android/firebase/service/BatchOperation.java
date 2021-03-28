package com.kwm.android.firebase.service;

import java.util.Map;

public class BatchOperation {
    private Type type;
    private String[] path;
    private Map<String, Object> data;

    public BatchOperation() {
    }

    public BatchOperation delete(String[] path){
        type = Type.DELETE;
        this.path = path;
        return this;
    }
    public BatchOperation update(String[] path, Map<String, Object> data){
        type = Type.UPDATE;
        this.path = path;
        this.data = data;
        return this;
    }
    public BatchOperation create(String[] path, Map<String, Object> data){
        type = Type.CREATE;
        this.path = path;
        this.data = data;
        return this;
    }
    public String[] getPath() {
        return path;
    }

    public Type getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public enum Type {
        UPDATE,
        CREATE,
        DELETE
    }
}
