package fr.univtours.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Route {
	
	private int routeId;
	
	private Hotel firstNode;
	
	private Hotel LastNode;
	
	private int nbSiteVisite;
	
	private double parcouru;
	private double distance;
	
	private int Score;

	public Route(int routeId, Hotel firstNode, Hotel lastNode,  double distance) {
		this.routeId = routeId;
		this.firstNode = firstNode;
		this.LastNode = lastNode;
		this.nbSiteVisite = 0;
		this.distance = distance;
		this.parcouru = 0;
		this.Score =0;
	}

	public void addLastNode(Site lastNode) {



	}



}
