package org.rtosss.batcherapp.model;

public final class TickStats implements Comparable<TickStats> {
	private Integer tick;
	private String handle;
	private Integer capacity;
	
	public TickStats(Integer tick, String handle, Integer capacity) {
		this.tick = tick;
		this.handle = handle;
		this.capacity = capacity;
	}
	
	public Integer getTick() {
		return tick;
	}

	public String getHandle() {
		return handle;
	}

	public Integer getCapacity() {
		return capacity;
	}

	@Override
	public int compareTo(TickStats o) {
		if(o.tick == null) {
			return 1;
		}
		if(tick == null) {
			return -1;
		}
		if(tick > o.tick) {
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
		result = prime * result + ((tick == null) ? 0 : tick.hashCode());
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
		if (tick == null) {
			if (other.tick != null)
				return false;
		} else if (!tick.equals(other.tick))
			return false;
		return true;
	}
}
