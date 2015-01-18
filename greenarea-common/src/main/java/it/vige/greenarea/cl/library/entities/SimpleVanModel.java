/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.library.entities;

/**
 * 
 * @author 00917308
 */
public class SimpleVanModel implements TruckModelInterface {
	private float grossCapacity;
	private int targetPayload;
	private static final float TOLERANCE = 0.1f;

	public SimpleVanModel(float grossCapacity, int targetPayload) {
		if (grossCapacity <= 0 || targetPayload <= 0)
			throw new IllegalArgumentException();
		this.grossCapacity = grossCapacity;
		this.targetPayload = targetPayload;
	}

	public double getLoadLevel(Double volume) {
		return (double) (100 * volume / grossCapacity);
	}

	@Override
	public double load(TruckLoadDescriptor tld, Freight p) {
		double eval = (double) (100 * (tld.getVolume() + p.getVolume()) / grossCapacity);
		if (eval > targetPayload)
			return -eval;
		tld.setVolume(tld.getVolume() + p.getVolume());
		return eval;
	}

	@Override
	public double unload(TruckLoadDescriptor tld, Freight p) {
		tld.setVolume(tld.getVolume() - p.getVolume());
		if (tld.getVolume() < TOLERANCE)
			tld.setVolume(0D);
		return getLoadLevel(tld.getVolume());
	}

	@Override
	public boolean canload(TruckLoadDescriptor tld, Freight p) {
		int eval = (int) (100 * (tld.getVolume() + p.getVolume()) / grossCapacity);
		return (eval <= targetPayload);
	}
}
