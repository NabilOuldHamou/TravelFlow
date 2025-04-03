package fr.univtours.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter @Setter @ToString
public class Route {
	
	private int routeId;
	
	private Hotel firstNode;
	
	private Hotel LastNode;
	
	private int nbSiteVisite;
	
	private double parcouru;
	private double distance;
	
	private int Score;

	private ArrayList<Site> sites;

	public Route(int routeId, Hotel firstNode, Hotel lastNode,  double distance) {
		this.routeId = routeId;
		this.firstNode = firstNode;
		LastNode = lastNode;
		this.sites = new ArrayList<>();
		this.nbSiteVisite = 0;
		this.distance = distance;
		this.parcouru = 0;
		this.Score =0;
	}

	public void addSite(Site site) {
		site.setRouteId(this.routeId);
		this.sites.add(site);
		this.nbSiteVisite++;
		this.Score += site.getScore();
	}





}
