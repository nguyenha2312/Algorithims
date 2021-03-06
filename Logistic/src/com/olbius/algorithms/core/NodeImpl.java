package com.olbius.algorithms.core;

public class NodeImpl implements Node{

	private int index;
	
	public NodeImpl(int name) {
		this.index = name;
	}
	
	@Override
	public int compareTo(Node o) {
		if(o.equals(this)) {
			return 0;
		}
		return this.index - (Integer)o.getName();
	}

	@Override
	public Object getInfo() {
		return null;
	}

	@Override
	public Object getInfo(int index) {
		return null;
	}

	@Override
	public Object getName() {
		return index;
	}

	@Override
	public String toString() {
		return Integer.toString(index);
	}
}
