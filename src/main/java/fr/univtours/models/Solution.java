package fr.univtours.models;

import java.util.ArrayList;
import java.util.List;
import fr.univtours.models.Hotel;
import fr.univtours.models.HotelType;
import fr.univtours.models.Node;
import fr.univtours.models.Site;
import lombok.Getter;
import lombok.Setter;

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

		Route Route = this.routes.get(jours);
		while(jours < this.routes.size()) {


			double bestRatio = Double.MAX_VALUE; 

			Node currentNode = (Node) Route.getFirstNode();
			Node toGo = null;
			//while(currentNode != this.instance.getLast() && jours < this.routes.size()) {}
			do {
				double Parcouru = 0;
				List<Node> reachable = reachableSite(toGo, Route.getDistance() - Parcouru);
				
				for(int i = 0; i < reachable.size(); i++) {
					//Vérifier que le node peut atteindre un hotel
					Node test = reachable.get(i);
					if( hotelInSight(test,
							Route.getDistance() - Parcouru - this.instance.getDistances() [currentNode.getId()] [test.getId()])
									&& visited(test)) {
						double ratio = getRatio(currentNode, test);
						if( bestRatio > ratio) {
							toGo = test;
							bestRatio = ratio;
						}
					}
					
					
				}
				
				if(toGo != null ) {
					Parcouru += this.instance.getDistances()[currentNode.getId()][toGo.getId()];
					if(toGo instanceof Hotel) {
						Route.setLastNode((Hotel) toGo);
						Route.setParcouru(Parcouru);
					}else{
						Route.addSite((Site) toGo);
					}
					currentNode=toGo;
				}else{
					//Gérer le cas où aucun site n'est ateignable
				}
			}while(! (toGo instanceof Hotel));
			
			
			jours++;
			if(jours < this.routes.size()) {
				Route = this.routes.get(jours);
				Route.setFirstNode((Hotel) toGo);
			}

		}
		
		
	}

	private boolean visited(Node test) {
		if ((test instanceof Site) && (((Site) test).getRouteId() != 0)) {
			return false;
		}
		return true;
	}


	private double getRatio(Node n1, Node n2) {
		// TODO Auto-generated method stub
		double distance = this.instance.getDistances()[n1.getId()][n2.getId()];
		
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
	public ArrayList<Node> reachableSite(Node N, double distance){

		ArrayList<Node> reachableSites = new ArrayList<>();

		for(Node Place: nodes){
			if(this.instance.getDistances()[Place.getId()][N.getId()] < distance){
				reachableSites.add(Place);
			}
		}

		return reachableSites;
	}
	
	
	/*
	 * Vérifie que l'ont peut atteindre la hotel final a partir de l'hotel actuel 
	 */
	public boolean routeValidator(int routeID) {
		return true;
		//return this.instance.getDistances()[routes.get(routeID).getLast().getID][instance.getLast().getId()] < DistTotalRestante;
	}

}
