package com.example.armychess;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class distribution extends DataSupport {
    private List<String> store=new ArrayList<>();

    public List<String> getStore() {
        return store;
    }
    public void setStore(List<String> store) {
        this.store = store;
    }
}
