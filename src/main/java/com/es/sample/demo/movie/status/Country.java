package com.es.sample.demo.movie.status;

public enum Country {

    USA("미국"),
    KO("한국");

    private String desc;

    Country(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
