package fr.univtours.models;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class Solution {
	
	
	private List<Route> routes;
	
	private List<Node> nodes;
	
	
	/*
	 * Fontion k plus grand ratio:
	 *  Evalue le ratio sur la valeur / distance
	 *  
	 */
	public void kpgr() {
		
		//Tant que l'hotel final n'a pas été atteint
		boolean fin = true;
		Node N = Instance.first;
		while(fin) {
			fin = false;
			List<Node> reachable = reachableSite(N);
			
			for(int i = 0; i < reachable.size(); i++) {
				//Vérifier que le node peut atteindre un hotel
				//Choisir le node qui donne le plus grand ratio
				
			}
			
			if(intanceOf(N) == Site)
				fin = true;
			
		}
	}
	
	/*
	 * Retourne la liste de node ateignable dans la journée qui n'ont pas été visité
	 * Doit contenir une instance hotel
	 */
	public ArrayList<Node> reachableSite(Node N){
		
		//Distance restante
		double dist;
		
		//Distance total restante
		double totalDist;
		
		//l'hotel de fin doit se trouver à une distance < totalDist
		//Sinon dumb la route
		int jour = 0;
		
		
	}
	
	public boolean routeValidator(int routeID) {
		
		return distance[routes.get(routeID).getLast().getID][instance.getLast().getId()];
	}

}
