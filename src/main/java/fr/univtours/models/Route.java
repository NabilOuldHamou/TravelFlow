package fr.univtours.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter @ToString
public class Route {
	
	private int routeId;
	
	private Hotel firstNode; // Correspond au premier hotel de la route
	private Hotel LastNode; // Correspond au dernier hotel de la route
	private int nbSiteVisite;
	private double parcouru;
	private double distance;
	private double score;
	private List<Site> sites;

	public Route(Route route) {
		this.routeId = route.getRouteId();
		this.firstNode = route.getFirstNode();
		this.LastNode = route.getLastNode();
		this.nbSiteVisite = route.getNbSiteVisite();
		this.parcouru = route.getParcouru();
		this.distance = route.getDistance();
		this.score = route.getScore();
		this.sites = route.getSites();
	}

	public Route(int routeId, Hotel firstNode, Hotel lastNode,  double distance) {
		this.routeId = routeId;
		this.firstNode = firstNode;
		LastNode = lastNode;
		this.sites = new ArrayList<>();
		this.nbSiteVisite = 0;
		this.distance = distance;
		this.parcouru = 0;
		this.score =0;
	}

	public void addSite(Site site) {
		this.sites.add(site);
		this.nbSiteVisite++;
		this.score += site.getScore();
	}

	public Site getLastSide() {
		return this.sites.getLast();
	}

	public void addParcouru(double parcouru) {
		this.parcouru += parcouru;
	}

	public static List<Route> deepCopyRoutes(List<Route> originalRoutes) {
		return originalRoutes.stream()
				.map(Route::new) // calls copy constructor
				.collect(Collectors.toList());
	}

}
