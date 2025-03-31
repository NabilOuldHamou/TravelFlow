package fr.univtours.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.univtours.Instance;
import fr.univtours.utils.Pair;

public class Solution {


	private List<Route> routes;

	private List<Node> nodes;
	private Instance instance;

	private double distanceRestante;

	public Solution(Instance instance) {
		this.instance = instance;
		this.routes = new ArrayList<>();
		this.nodes = Arrays.asList(instance.getNodes());
	}

	/*
	 * Fontion k plus grand ratio:
	 *  Evalue le ratio sur la valeur / distance
	 *
	 */
	public void kpgr() {

		//Tant que l'hotel final n'a pas été atteint
		int jours = 0;
		Node N = nodes.getFirst();

		while(jours < instance.getNbrDays()) {
			//Creation d'une route
			double bestRatio = Double.MAX_VALUE;
			Node toGo = null;
			Route Route = new Route();
			this.routes.add(Route);
			while(! (toGo instanceof Hotel)) {
				double distance = instance.getTravelDistances()[jours];
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
					distance -= instance.getDistances()[N.getId()][toGo.getId()];
					//Ajouter togo a la route
					// TODO this.routes.add(toGo);
					N=toGo;
				}
			}


			jours++;
		}


	}


	private double getRatio(Node n1, Node n2) {
		// TODO Auto-generated method stub
		double distance = instance.getDistances()[n1.getId()][n2.getId()];

		double score = n2.getScore();

		return score / distance;
	}


	/*
	 * Vérifie que le site peut rejoindre un hotel
	 */
	public boolean hotelInSight(Node N, double distance) {
		for(Node Nhotel : nodes) {

			if(Nhotel instanceof Hotel) {
				if( instance.getDistances()[Nhotel.getId()][N.getId()] < distanceRestante )
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

		return null;

	}


	/*
	 * Vérifie que l'ont peut atteindre la hotel final a partir de l'hotel actuel
	 */
	public boolean routeValidator(int routeID) {
		// TODO
		//return instance.getDistances()[routes.get(routeID).getLast().getID][instance.getLast().getId()] < DistTotalRestante;
		return false;
	}

	public List<Pair<Hotel, Double>> hotelScores(double rayon) {
		List<Pair<Hotel, Double>> hotelScores = new ArrayList<>();
		List<Site> sites = new ArrayList<>();
		List<Hotel> intermediateHotels = new ArrayList<>();
		nodes.forEach(n -> {
			if (n instanceof Hotel && ((Hotel)n).getHotelType().equals(HotelType.INTERMEDIATE)) {
				intermediateHotels.add((Hotel)n);
			} else if (n instanceof Site) {
				sites.add((Site)n);
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

}