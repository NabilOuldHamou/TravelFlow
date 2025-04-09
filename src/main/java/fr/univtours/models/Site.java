package fr.univtours.models;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Site extends Node {

	
	private int routeId = -1;
	
    public Site(int id, double x, double y, double score) {
        super(id, x, y, score, null, null);
    }

}
