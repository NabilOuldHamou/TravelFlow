package fr.univtours.models.solutions;

import java.util.ArrayList;
import java.util.List;

import fr.univtours.models.*;
import fr.univtours.utils.Pair;
import lombok.Getter;

import fr.univtours.Instance;
import lombok.ToString;

@ToString @Getter
public class Solution {

	private Instance instance ;
	private List<Route> routes = new ArrayList<>();

	private List<Node> Snodes = new ArrayList<>();

	public Solution(Instance instance) {
		this.instance = instance;
		//instanciation des routes
		int j = instance.getNbrDays() -1;

		this.routes.add(new Route(0, instance.getFirst(), null, instance.getTravelDistances()[0]));
		for( int i=1; i  < j; i++){
			this.routes.add(new Route(i,null, null, instance.getTravelDistances()[i]));

		}
		this.routes.add(new Route(j,null,  instance.getLast(), instance.getTravelDistances()[j]));

		//Instanciation des Noeuds
		for(int i = 0; i<instance.getNodes().length; i++){
			this.Snodes.add(instance.getNodes() [i]);
		}



	}


	/*
	 * Fontion k plus grand ratio:
	 *  Evalue le ratio sur la valeur / distance
	 *
	 */
	public void kpgr() {

		//Tant que l'hotel final n'a pas été atteint
		int jours = 0;

		Route Route = this.routes.get(jours);
		while(jours < this.routes.size() ) {




			Node currentNode = (Node) Route.getFirstNode();
			Node toGo = null;
			//while(currentNode != this.instance.getLast() && jours < this.routes.size()) {}

			double Parcouru = 0;
			do {
				double bestRatio = -1;
				List<Node> reachable = reachableSite(currentNode, Route.getDistance() - Parcouru);

				for(int i = 0; i < reachable.size(); i++) {
					//Vérifier que le node peut atteindre un hotel
					Node test = reachable.get(i);
					if(Route.getLastNode() == null) {
						if (hotelInSight(test,
								Route.getDistance() - Parcouru - this.instance.getDistances()[currentNode.getId()][test.getId()])
								&& !visited(test)) {
							double ratio = getRatio(currentNode, test);
							if (bestRatio < ratio) {
								toGo = test;
								bestRatio = ratio;
							}
						}
					}else{
						//Dernière Route
						if (LastHotelInSight(Route.getDistance()
								- Parcouru
								- this.instance.getDistances()[currentNode.getId()][test.getId()], test)
								&& !visited(test)) {
							double ratio = getRatio(currentNode, test);
							if (bestRatio < ratio) {
								toGo = test;
								bestRatio = ratio;
							}
						}

					}


				}

				if(toGo != null ) {
					Parcouru += this.instance.getDistances()[currentNode.getId()][toGo.getId()];
					if(toGo instanceof Hotel) {
						Route.setLastNode((Hotel) toGo);
						Route.setParcouru(Parcouru);
					}else{
						if(toGo instanceof Site) {
							Site site = (Site) toGo;
							site.setRouteId(Route.getRouteId());
							Route.addSite(site);
						}

					}
					currentNode=toGo;
				}else{
					//Gérer le cas où aucun site n'est atteignable
				}
			}while(! (toGo instanceof Hotel));


			jours++;
			if(jours < this.routes.size()) {
				Route = this.routes.get(jours);
				Route.setFirstNode((Hotel) toGo);
			}

		}
	}

	private boolean LastHotelInSight(double dist , Node test) {
		if(test instanceof Hotel && ((Hotel) test).getHotelType() != HotelType.END)
			return false;
		return dist >= this.instance.getDistances()[this.instance.getLast().getId()][test.getId()];
	}

	private boolean visited(Node test) {
		if ((test instanceof Site) && (((Site) test).getRouteId() >= 0)) {
			return true;
		}
		return false;
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
		if(N instanceof Hotel) {
			return true;
		}else{
			for(Node Nhotel : Snodes) {

				if(Nhotel instanceof Hotel) {
					if( this.instance.getDistances()[Nhotel.getId()][N.getId()] < distance )
						return true;
				}
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

		for(Node Place: Snodes){
			if( Place != N && this.instance.getDistances()[Place.getId()][N.getId()] < distance){
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

	//Print les routes
	public void printRoutes() {
		int score = 0;
		for(Route route : routes) {
			System.out.println(route);
			score += route.getScore();
		}
		System.out.println("Score: " + score);
	}

}