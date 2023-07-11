package it.polito.tdp.itunes.model;

import java.util.Objects;

public class Bilancio implements Comparable<Bilancio>{

	private Album a;
	private Integer bilancio;
	
	public Bilancio(Album a, Integer bilancio) {
		super();
		this.a = a;
		this.bilancio = bilancio;
	}

	@Override
	public String toString() {
		return a + ", bilancio= " + bilancio;
	}

	public Album getA() {
		return a;
	}

	public Integer getBilancio() {
		return bilancio;
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, bilancio);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bilancio other = (Bilancio) obj;
		return Objects.equals(a, other.a) && bilancio == other.bilancio;
	}
	
	public int compareTo(Bilancio b) {
		return b.getBilancio().compareTo(bilancio);
	}
	
}
