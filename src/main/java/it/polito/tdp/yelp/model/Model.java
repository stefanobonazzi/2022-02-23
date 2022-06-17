package it.polito.tdp.yelp.model;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	private Graph<Review, DefaultWeightedEdge> graph;
	private List<Review> vertices;
	private List<Review> result;
	private int maxArchi;
	
	public Model() {
		this.dao = new YelpDao();
	}

	public List<String> getAllCities() {
		return this.dao.getAllCities();
	}

	public List<Business> getBusinesses(String citta) {
		return this.dao.getBusinesses(citta);
	}

	public List<Review> creaGrafo(Business b) {
		this.graph = new SimpleDirectedWeightedGraph<Review, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.vertices = this.dao.getReviews(b);
		
		Graphs.addAllVertices(this.graph, this.vertices);
		
		for(Review r1: this.vertices) {
			for(Review r2: this.vertices) {
				if(!r1.equals(r2) && r1.getDate().isBefore(r2.getDate())) {
					DefaultWeightedEdge edge = this.graph.getEdge(r1, r2);
					
					if(edge == null) {
						double weight = ChronoUnit.DAYS.between(r1.getDate(), r2.getDate());
						if(weight > 0) {
							edge = this.graph.addEdge(r1, r2);
							this.graph.setEdgeWeight(edge, weight);
						}
					}
				}
			}
		}
		
		this.result = new ArrayList<Review>();
		this.maxArchi = 0;
		
		for(Review r: this.vertices) {
			int i = this.graph.outgoingEdgesOf(r).size();
			if(i > maxArchi) {
				result = new ArrayList<Review>();
				result.add(r);
				maxArchi = i;
			} else if(i == maxArchi) {
				result.add(r);
			}
		}
		
		return this.result;
	}

	public int getMaxArchi() {
		return maxArchi;
	}
	
	public String getGraphParameters() {
		return "Grafo creato: "+this.graph.vertexSet().size()+" vertici, "+this.graph.edgeSet().size()+" archi\n\n";
	}
	
	private List<Review> longest;
	private double daysApart;
	
	public List<Review> trovaMiglioramento() {
		this.longest = new ArrayList<Review>();
		this.daysApart = 0;
		
		List<Review> parziale = new ArrayList<Review>();
		this.ricorsiva(parziale, false, 0);
		return this.longest;
	}
	
	public void ricorsiva(List<Review> parziale, boolean noMore, int lvl) {
		if(noMore) {
			if(parziale.size() > this.longest.size()) {
				this.longest = new ArrayList<Review>(parziale);
				this.daysApart = ChronoUnit.DAYS.between(parziale.get(0).getDate(), parziale.get(parziale.size()-1).getDate());
			}
		} else {
			if(lvl == 0) {
				for(Review r: this.vertices) {
					parziale.add(r);
					this.ricorsiva(parziale, noMore, lvl+1);
					parziale.remove(r);
				}
			} else {
				Review last = parziale.get(parziale.size()-1);
				double s1 = last.getStars();
				Set<DefaultWeightedEdge> edges = this.graph.outgoingEdgesOf(last);
				if(edges.isEmpty()) {
					this.ricorsiva(parziale, true, lvl+1);
				} else {
					for(DefaultWeightedEdge e: edges) {
						Review r = Graphs.getOppositeVertex(this.graph, e, last);
						double s2 = r.getStars();
						boolean found = false;
						if(!parziale.contains(r) && s1 <= s2) {
							found = true;
							parziale.add(r);
							this.ricorsiva(parziale, noMore, lvl+1);
							parziale.remove(r);
						}
						
						if(!found) {
							this.ricorsiva(parziale, true, lvl+1);
						}
					}
				}
			}
		}
	}

	public double getDaysApart() {
		return daysApart;
	}
	
}
