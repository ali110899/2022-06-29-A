package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	ItunesDAO dao;
	private List<Album> allAlbums;
	private SimpleDirectedWeightedGraph<Album, DefaultWeightedEdge> grafo;
	
	private List<Album> camminoMigliore;
	private int bestPunteggio;
	
	
	
	public Model() {
		
		this.allAlbums = new ArrayList<Album>();
		this.dao = new ItunesDAO();
		this.grafo = new  SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
	
	}
	
	public void creaGrafo(int num) {
		
		clearGraph();
		
		loadNodes(num);
		//aggiungo tutti i vertici creati
		Graphs.addAllVertices(this.grafo, this.allAlbums);
		
		//creo edge
		for(Album a1 : allAlbums) {
			for(Album a2 : allAlbums) {
				int peso = a1.getNumSongs()-a2.getNumSongs();
				//check se sono uguali ed elimino i doppioni
				//l'arco andrà da a2 ad a1
				if(peso>0) {
					//NB il vertice di source nel nostro caso è a2
					Graphs.addEdgeWithVertices(this.grafo, a2, a1, peso);
				}
			}
		}
		
		System.out.println(this.grafo.vertexSet().size());
		System.out.println(this.grafo.edgeSet().size());
		
	}
	
	private double getBilancio(Album album) {
		
		double bilancio=0; 
		//int bilancio=0; 
		
		//ci restituisce tutti gli archi entranti nell'album
		List<DefaultWeightedEdge> archiEntrantiAlbum = new ArrayList<> (this.grafo.incomingEdgesOf(album));
		List<DefaultWeightedEdge> archiUscentiAlbum = new ArrayList<> (this.grafo.outgoingEdgesOf(album));
		
		for(DefaultWeightedEdge edge : archiEntrantiAlbum) {
			bilancio = bilancio + this.grafo.getEdgeWeight(edge);
		}
		
		for(DefaultWeightedEdge edge : archiUscentiAlbum) {
			bilancio = bilancio - this.grafo.getEdgeWeight(edge);
		}
		
		return bilancio;
	}
	
	public List<Album> getAlbumTendina() {
		
		List<Album> listaAlbum = new ArrayList<Album>(this.grafo.vertexSet());
		//restituisce tutti i nodi ordinati per Titolo
		Collections.sort(listaAlbum);
		return listaAlbum;
	}
	
	private void clearGraph() {
		
		this.allAlbums = new ArrayList<Album>();
		this.grafo = new  SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
	}

	public void loadNodes(int num) {
		
		if(this.allAlbums.isEmpty()) {
			this.allAlbums = dao.getFilteredAlbums(num);
		}
	}
	
	public int getVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getEdge() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Bilancio> getAdiacenti(Album album) {
		
		//ci da tutti i nodi che vengono dopo
		List<Album> successori = Graphs.successorListOf(this.grafo, album);
		List<Bilancio> listaB = new ArrayList<>();
		
		for(Album a : successori) {
			Bilancio b = new Bilancio(a, (int)getBilancio(a));
			listaB.add(b);
		}
		
		Collections.sort(listaB);
		return listaB;
	}
		
	public List<Album> getPercorso(Album source, Album target, int x) {
		//Album a1-->source
		//Album a2-->target
		
		List<Album> parziale = new ArrayList<Album>();
		camminoMigliore= new ArrayList<Album>();
		bestPunteggio = 0;
		parziale.add(source);
		
		//Metodo ricorsivo
		ricorsione(parziale, target, x);
			
		return this.camminoMigliore;
		
	}

	private void ricorsione(List<Album> parziale, Album target, int x) {
		
		Album current = parziale.get(parziale.size()-1);
		
		//condizione uscita
		if(target.equals(current)) {
			//controllo se soluzione è miglore della best
			if(getScore(parziale)> this.bestPunteggio) {
				this.bestPunteggio = getScore(parziale);
				this.camminoMigliore = new ArrayList<Album>(parziale);
			}
			return;
		}
		//Continuo ad aggiungere elementi in parziale
		List<Album> lista = Graphs.successorListOf(this.grafo, current);
		
		for(Album a : lista) {
			//prendo l'arco tra current e nodo a e calcolo il peso
			if(this.grafo.getEdgeWeight(this.grafo.getEdge(current, a))>= x){
				parziale.add(a);
				ricorsione(parziale, target, x);
				//backtracking
				parziale.remove(a);
			}
		}
		
	}

	private int getScore(List<Album> parziale) {
		
		int score=0;
		//era gia nella lista
		Album source = parziale.get(0); 
		
		for(Album a : parziale) {
			if(getBilancio(a)>getBilancio(source) ) {
				score = score +1;
			}
		}
		
		return score;
	}
	
	
	
	
	
	
	
	
	
}
