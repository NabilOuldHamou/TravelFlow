package fr.univtours.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Hotel extends Node {

    private HotelType hotelType;

    public Hotel(HotelType hotelType, double x, double y, double score) {
        super(x, y, score);
        this.hotelType = hotelType;
    }
}
