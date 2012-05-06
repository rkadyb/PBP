package com.pbp;

public class Timer {
	
	private Long start = 0L;
	private Long stop = 0L;
	
	public void start() {
		this.start = System.currentTimeMillis();
	}
	
	public void stop() {
		this.stop = System.currentTimeMillis();
	}
	
	public long getTime() {
		return (this.stop - this.start);
	}
}
