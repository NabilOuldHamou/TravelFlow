package fr.univtours.models;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import fr.univtours.Instance;

public class Solution {


	public Solution(Instance instance) {
		this.instance = instance;
		//instanciation des routes
		int i =0;
		this.routes.add(new Route(0, instance.getFirst(), null, instance.getTravelDistances()[0]));
		for(i = 1; i < instance.getNbrDays() - 1; i++){
			this.routes.add(new Route(i,null, null, instance.getTravelDistances()[i]));
		}
		i++;
		this.routes.add(new Route(i,null,  instance.getLast(), instance.getTravelDistances()[i]));

		//Instanciation des Noeuds
		for(int j = 0; j<instance.getNodes().length; j++){
			this.nodes.add((Node) instance.getNodes() [j]);
		}



	}

	private Instance instance ;
	private List<Route> routes = new ArrayList<>();
	
	private List<Node> nodes ;
	
	
	/*
	 * Fontion k plus grand ratio:
	 *  Evalue le ratio sur la valeur / distance
	 *  
	 */
	public void kpgr() {
		
		//Tant que l'hotel final n'a pas été atteint
		int jours = 0;
		Hotel HFirst = this.instance.getFirst();
		

		
		
		
		while(jours < this.instance.getNbrDays()) {
			//Creation d'une route
			double bestRatio = Double.MAX_VALUE; 

			Route Route = this.routes.get(jours);
			Node currentNode = (Node) Route.getFirstNode();
			Node toGo = null;
			while(currentNode != this.instance.getLast() && jours < this.routes.size()) {}
			do {
				double Parcouru = 0;
				List<Node> reachable = reachableSite(toGo);
				
				for(int i = 0; i < reachable.size(); i++) {
					//Vérifier que le node peut atteindre un hotel
					Node test = reachable.get(i);
					if( hotelInSight(test, Route.getDistance() - Parcouru - this.instance.getDistances() [currentNode.getId()] [test.getId()] )) {
						double ratio = getRatio(currentNode, test);
						if( bestRatio < ratio) {
							toGo = test;
							bestRatio = ratio;
						}
					}
					
					
				}
				
				if(toGo != null) {
					Parcouru += this.instance.getDistances()[currentNode.getId()][toGo.getId()];
					//Ajouter togo a la route
					Route.addLastNode(toGo);
					currentNode=toGo;
				}
			}while(! (toGo instanceof Hotel));
			
			
			jours++;
		}
		
		
	}

	
	private double getRatio(Node n1, Node n2) {
		// TODO Auto-generated method stub
		double distance = Instance.getDistances()[n1.get][n2.getId];
		
		double score = n2.getScore();
		
		return score / distance;
	}


	/*
	 * Vérifie que le site peut rejoindre un hotel
	 */
	public boolean hotelInSight(Node N, double distance) {
		for(Node Nhotel : nodes) {
			
			if(Nhotel instanceof Hotel) {
				if( this.instance.getDistances()[Nhotel.getId()][N.getId()] < distance )
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
