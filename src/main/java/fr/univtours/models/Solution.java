package fr.univtours.models;

import java.util.ArrayList;
import java.util.List;
import fr.univtours.models.Hotel;
import fr.univtours.models.HotelType;
import fr.univtours.models.Node;
import fr.univtours.models.Site;
import fr.univtours.utils.Pair;
import lombok.Getter;
import lombok.Setter;

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


	ArrayList<Hotel> hotels = new ArrayList<>();

	public void kpgr() {
		int jours = 0;
		Route route = this.routes.get(jours);

		while (jours < this.routes.size()) {
			Node currentNode = (Node) route.getFirstNode();
			Node toGo = currentNode;
			double parcouru = 0;

			do {
				double bestRatio = 0;
				List<Node> reachable = reachableSite(currentNode, route.getDistance() - parcouru);
				int min = -1;
				this.hotels.clear();

				for (int i = 0; i < reachable.size(); i++) {
					Node test = reachable.get(i);
					if (test instanceof Hotel ) {
						Hotel hotel = (Hotel) test;
						if (min == -1) {
							this.hotels.add(hotel);
							min = hotel.getNbVisit();
						} else {
							if (hotel.getNbVisit() < min) {
								this.hotels.clear();
								this.hotels.add(hotel);
								min = hotel.getNbVisit();
							} else if (hotel.getNbVisit() == min) {
								this.hotels.add(hotel);
							}
						}
					}else{
						break;
					}
				}

				for (int i = 0; i < reachable.size(); i++) {
					Node test = reachable.get(i);
					if (route.getLastNode() == null && test != currentNode) {
						if (!visited(test)
								&& LastHotelInSight(route.getDistance() - parcouru - this.instance.getDistances()[currentNode.getId()][test.getId()], test)
								&& hotelInSight(test, route.getDistance() - parcouru - this.instance.getDistances()[currentNode.getId()][test.getId()])) {
							double ratio = getRatio(currentNode, test);
							if (this.hotels.contains(test) || currentNode == toGo) {
								toGo = test;
							} else {
								if (bestRatio < ratio) {
									toGo = test;
									bestRatio = ratio;
								}
							}
						}
					} else {
						// Dernière Route
						if (LastHotelInSight(route.getDistance() - parcouru - this.instance.getDistances()[currentNode.getId()][test.getId()], test)
								&& !visited(test)) {
							double ratio = getRatio(currentNode, test);
							if (bestRatio < ratio) {
								toGo = test;
								bestRatio = ratio;
							}
						}
					}
				}

				if (toGo != null && toGo != currentNode) {
					parcouru += this.instance.getDistances()[currentNode.getId()][toGo.getId()];
					if (toGo instanceof Hotel) {
						route.setLastNode((Hotel) toGo);
						route.setParcouru(parcouru);
					} else if (toGo instanceof Site) {
						Site site = (Site) toGo;
						site.setRouteId(route.getRouteId());
						route.addSite(site);
					}
					currentNode = toGo;
				} else {
					// Gérer le cas où aucun site n'est atteignable
					break;
				}
			} while (!(toGo instanceof Hotel));

			jours++;
			if (jours < this.routes.size()) {
				route = this.routes.get(jours);
				route.setFirstNode((Hotel) toGo);
			}
		}
		//linkSite();
	}

	private boolean LastHotelInSight(double dist , Node test) {
		if(test instanceof Hotel) {
			if(((Hotel) test).getHotelType() == HotelType.END) {
				return true;
			}
			double sumDist = 0;
			double distToEnd= this.instance.getDistances()[this.instance.getLast().getId()][test.getId()];
			for(Route r : this.routes) {

				if(r.getFirstNode() == null ) {
					sumDist += r.getDistance();
				}



			}
			return sumDist >= this.instance.getDistances()[this.instance.getLast().getId()][test.getId()];
		}
		return dist >= this.instance.getDistances()[this.instance.getLast().getId()][test.getId()];
	}



	private boolean visited(Node test) {
		if ((test instanceof Site) ) {
			return ((Site) test).getRouteId() >= 0;
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
	 * Vérifie que le site peut rejoindre un hotel pas visitée
	 */
	public boolean hotelInSight(Node N, double distance) {
		if(N instanceof Hotel) {
			return true;
		}else{
			for(Node Nhotel : Snodes) {

				if(this.hotels.contains(Nhotel)) {
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




	//Print les routes
	public void printRoutes() {
		int score = 0;
		for(Route route : routes) {
			System.out.println(route);
			score += route.getScore();
		}
		System.out.println("Score: " + score);
	}

	public List<Pair<Hotel, Double>> hotelScores(double rayon) {
		List<Pair<Hotel, Double>> hotelScores = new ArrayList<>();
		List<Site> sites = new ArrayList<>();
		List<Hotel> intermediateHotels = new ArrayList<>();
		Snodes.forEach(n -> {
			if (n instanceof Hotel && ((Hotel) n).getHotelType().equals(HotelType.INTERMEDIATE)) {
				intermediateHotels.add((Hotel) n);
			} else if (n instanceof Site) {
				sites.add((Site) n);
			}
		});

		for (Hotel hotel : intermediateHotels) {
			List<Site> possibleSites = new ArrayList<>();
			double sumDistance = 0;
			for (Site site : sites) {
				// TODO refaire avec les identifiants
				double distance = Math.sqrt(Math.pow(hotel.getX() - site.getX(), 2) + Math.pow(hotel.getY() - site.getY(), 2));
				sumDistance += distance;

				if (distance <= rayon) {
					possibleSites.add(site);
				}
			}

			double sumScore = possibleSites.stream().mapToDouble(Site::getScore).sum();
			double ratio = (sumScore / sumDistance) * 100;

			hotelScores.add(new Pair<>(hotel, ratio / possibleSites.size()));
		}

		return hotelScores;
	}

	public void checkSolution(){
		boolean check = true;
		for(Route route : routes) {
			if(!checkRoute(route)) {
				System.out.println("Route " + route.getRouteId() + " is not valid");
				check = false;
			}
		}
		if(!checkSite()) {
			System.out.println("Some sites are not valid");
			check = false;
		}
		if(!check) {
			System.out.println("Some routes are not valid");
			return;
		}
		System.out.println("All routes are valid");

	}

	public void linkSite(){
		for(Route route : routes) {
			for(int i=0; i < route.getSites().size() ; i++){
				Site site = route.getSites().get(i);
				if( i-1 >= 0) {
					site.setPrev(route.getSites().get(i - 1));
				}
				if( i+1 < route.getSites().size()) {
					site.setNext(route.getSites().get(i + 1));
				}
			}
		}
	}

	public boolean checkSite(){
		for(Node node : Snodes) {
			if(node instanceof Site) {
				int routeId = ((Site) node).getRouteId();
				if (routeId != -1) {
					if(! this.routes.get(routeId).getSites().contains(node)) {
						System.out.println("Site " + node.getId() + " is not in the right route");
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean checkRoute(Route route) {
		if(route.getFirstNode() == null || route.getLastNode() == null) {
			System.out.println("Hotel not set");
			return false;
		}
		if(route.getNbSiteVisite() == 0) {
			System.out.println("No site visited");
			return false;
		}
		if(route.getParcouru() > route.getDistance()) {
			System.out.println("Distance parcouru is greater than distance");
			return false;
		}
		int value = 0;
		for(Site site : route.getSites()) {
			value+= site.getScore();
		}
		if(value != route.getScore()) {
			System.out.println("Score is not equal to the sum of the sites");
			return false;
		}

		return true;
	}

}