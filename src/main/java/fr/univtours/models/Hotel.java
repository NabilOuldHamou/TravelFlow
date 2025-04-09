package fr.univtours.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Hotel extends Node {

    private HotelType hotelType;

    private int nbVisit = 0;

    public Hotel(HotelType hotelType, int id, double x, double y, double score) {
        super(id, x, y, score, null, null);
        this.hotelType = hotelType;
        if(this.hotelType != hotelType.INTERMEDIATE ){
            nbVisit++;
        }
    }
}
