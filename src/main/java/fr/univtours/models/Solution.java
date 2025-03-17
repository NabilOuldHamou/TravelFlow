package fr.univtours.models;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import fr.univtours.Instance;

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
		int jours = 0;
		Node N = Instance.getFirst();
		
		
		
		
		
		while(jours < Instance.getNbrDays()) {
			//Creation d'une route
			double bestRatio = Double.MAX_VALUE; 
			Node toGo = null;
			Route Route = new Route();
			this.routes.add(Route);
			while(! (toGo instanceof Hotel)) {
				double distance = Instance.getTravelDistances[jours];
				List<Node> reachable = reachableSite(N);
				
				for(int i = 0; i < reachable.size(); i++) {
					//Vérifier que le node peut atteindre un hotel
					Node test = reachable.get(i);
					if( hotelInSight(test, distance)) {
						double ratio = getRatio(N, test);
						if( bestRatio < ratio) {
							toGo = test;
							bestRatio = ratio;
						}
					}
					
					
				}
				
				if(toGo != null) {
					distance -= Instance.getDistance()[N.getId()][toG.getId()];
					//Ajouter togo a la route
					this.routes.add(toGo);
					N=toGo;
				}
			}
			
			
			jours++;
		}
		
		
	}

	
	private double getRatio(Node n1, Node n2) {
		// TODO Auto-generated method stub
		double distance = Instance.getDistances()[n1.getId()][n2.getId];
		
		double score = n2.getScore();
		
		return score / distance;
	}


	/*
	 * Vérifie que le site peut rejoindre un hotel
	 */
	public boolean hotelInSight(Node N, double distance) {
		for(Node Nhotel : nodes) {
			
			if(Nhotel instanceof Hotel) {
				if( Instance.GetDistances()[Nhotel.getId()][N.getId()] < distanceRestante )
					return true;
			}
		}
		
		return false;
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
	
	
	/*
	 * Vérifie que l'ont peut atteindre la hotel final a partir de l'hotel actuel 
	 */
	public boolean routeValidator(int routeID) {
		
		return distance[routes.get(routeID).getLast().getID][instance.getLast().getId()] < DistTotalRestante;
	}

}
