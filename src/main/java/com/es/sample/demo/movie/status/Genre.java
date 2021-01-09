package com.es.sample.demo.movie.status;

public enum Genre {

    ACTION("액션"),
    ADVENTURE("모험"),
    SF("SF"),
    THRILLER("스릴러"),
    DRAMA("드라마"),
    COMEDY("코미디"),
    FANTASY("판타지"),
    CRIME("범죄"),
    MUSICAL("뮤직컬"),
    MELLOW("멜로"),
    ROMANCE("로멘스"),
    WAR("전쟁"),
    NORI("느와르")
    ;


    private String desc;

    Genre(String desc) {
        this.desc = desc;
    }
}
