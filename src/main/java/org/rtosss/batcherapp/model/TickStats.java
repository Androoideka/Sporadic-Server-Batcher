package org.rtosss.batcherapp.model;

public final class TickStats implements Comparable<TickStats> {
	private int tick;
	private String handle;
	private int capacity;
	private boolean marker;
	
	public TickStats(int tick, String handle, int capacity, boolean marker) {
		this.tick = tick;
		this.handle = handle;
		this.capacity = capacity;
		this.marker = marker;
	}
	
	public int getTick() {
		return tick;
	}

	public String getHandle() {
		return handle;
	}

	public int getCapacity() {
		return capacity;
	}
	
	public boolean isMarker() {
		return marker;
	}

	@Override
	public int compareTo(TickStats o) {
		if(o == null || tick > o.tick) {
			return 1;
		}
		if(tick < o.tick) {
			return -1;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + tick;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TickStats other = (TickStats) obj;
		if (tick != other.tick)
			return false;
		return true;
	}
}
