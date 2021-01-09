package com.es.sample.demo.score.status;

public enum Rater {

    REPORTER("평론가"),
    AUDIENCE("관객");

    private String desc;

    Rater(String desc) {
        this.desc = desc;
    }

}
