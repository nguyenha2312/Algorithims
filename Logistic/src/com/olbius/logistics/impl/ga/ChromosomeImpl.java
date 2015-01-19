package com.olbius.logistics.impl.ga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.olbius.algorithms.core.Graph;
import com.olbius.algorithms.core.Node;
import com.olbius.algorithms.core.bnb.BnB;
import com.olbius.algorithms.core.bnb.Column;
import com.olbius.algorithms.core.bnb.ColumnImpl;
import com.olbius.algorithms.core.bnb.Matrix;
import com.olbius.algorithms.core.bnb.MatrixImpl;
import com.olbius.algorithms.core.bnb.OptimizationProblem;
import com.olbius.algorithms.core.bnb.OptimizationProblemImpl;
import com.olbius.algorithms.core.bnb.Row;
import com.olbius.algorithms.core.bnb.RowImpl;
import com.olbius.alogorithms.core.ga.Chromosome;
import com.olbius.alogorithms.core.ga.Gene;
import com.olbius.logistics.Customer;

public class ChromosomeImpl implements Chromosome{

	private static List<Gene> genesGobal;
	
	private Graph graph;
	
	private List<Gene> genes;
	
	private int maxW;
	
	private int value;
	
	private Node node;
	
	public ChromosomeImpl() {
		genes = new ArrayList<Gene>();
		
		List<Integer> list = new ArrayList<Integer>();
		
		for(int i = 0; i < getGenesGobal().size(); i++) {
			list.add(i);
		}
		
		int r;
		int index;

		while(!list.isEmpty()) {
			r = (int)(list.size() * OperationsImpl.random.nextDouble());
			if(r != list.size()) {
				index = list.get(r);
				list.remove(r);
				this.genes.add(getGenesGobal().get(index));
			}
		}
		
	}
	
	public ChromosomeImpl(List<Gene> genes) {
		this.genes = genes;
	}
	
	@Override
	public Gene getGene(int index) {
		return genes.get(index);
	}

	@Override
	public void addGene(Gene gene) {
		genes.add(gene);
	}

	@Override
	public String toString() {
		return genes.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		Chromosome c = (Chromosome) obj;
		if(c.getSize() == this.getSize() && !super.equals(obj)) {
			for(int i = 0; i < c.getSize(); i++) {
				if(!c.getGene(i).equals(this.getGene(i))) {
					return false;
				}
			}
			return true;
		}
		return super.equals(obj);
	}

	@Override
	public int getSize() {
		return genes.size();
	}
	
	public static List<Gene> getGenesGobal() {
		if(genesGobal == null) {
			genesGobal = new ArrayList<Gene>();
		}
		return genesGobal;
	}
	
	public void setMaxW(int maxW) {
		this.maxW = maxW;
	}

	@Override
	public Object getValue() {
		return this.value;
	}
	
	public void calcValue() {
		Map<Integer, List<Gene>> map = new HashMap<Integer, List<Gene>>();
		int i = 0;
		int index = 0;
		int sum = 0;
		while(i < genes.size()) {
			if(map.get(index) == null) {
				map.put(index, new ArrayList<Gene>());
			}
			sum += (int)((Customer)genes.get(i).getInfo()).getRequest().getWeight();
			if(sum > maxW) {
				sum = 0;
				index++;
			} else {
				map.get(index).add(genes.get(i));
				i++;
			}
		}
		for(int j : map.keySet()) {
			value += calcValue(map.get(j));
		}
	}
	
	public int calcValue(List<Gene> list) {
		
		Map<Integer, Row> rows = new HashMap<Integer, Row>();
		Map<Integer, Column> columns = new HashMap<Integer, Column>();
		
		Matrix matrix = new MatrixImpl();
		
		for(Gene gene1 : list) {
			Node i = ((Customer)gene1.getInfo()).getNode();
			Row row = rows.get(i.getName());
			if(row == null) {
				row = new RowImpl((int) i.getName());
				rows.put((Integer) i.getName(), row);
				((MatrixImpl) matrix).getRows().add(row);
			}
			for(Gene gene2 : list) {
				Node j = ((Customer)gene2.getInfo()).getNode();
				Column column = columns.get(j.getName());
				if(column == null) {
					column = new ColumnImpl((int) j.getName());
					columns.put((Integer) j.getName(), column);
					((MatrixImpl) matrix).getColumns().add(column);
				}
				if(!gene1.equals(gene2)) {
					((MatrixImpl) matrix).initValue(row, column, graph.getWeight(i, j));
				} else {
					((MatrixImpl) matrix).initValue(row, column, Integer.MAX_VALUE);
				}
			}
		}
		
		Row row = rows.get(node.getName());
		if(row == null) {
			row = new RowImpl((int) node.getName());
			rows.put((Integer) node.getName(), row);
			((MatrixImpl) matrix).getRows().add(row);
		}
		
		for(Gene gene2 : list) {
			Node j = ((Customer)gene2.getInfo()).getNode();
			Column column = columns.get(j.getName());
			if(column == null) {
				column = new ColumnImpl((int) j.getName());
				columns.put((Integer) j.getName(), column);
				((MatrixImpl) matrix).getColumns().add(column);
			}
			if(!node.equals(j)) {
				((MatrixImpl) matrix).initValue(row, column, graph.getWeight(node, j));
			} else {
				((MatrixImpl) matrix).initValue(row, column, Integer.MAX_VALUE);
			}
		}
		
		Column column = columns.get(node.getName());
		if(column == null) {
			column = new ColumnImpl((int) node.getName());
			columns.put((Integer) node.getName(), column);
			((MatrixImpl) matrix).getColumns().add(column);
		}
		
		((MatrixImpl) matrix).initValue(row, column, Integer.MAX_VALUE);
		
		OptimizationProblem problem = new OptimizationProblemImpl(matrix);
		
		BnB bnB = new BnB(problem);
		
		problem = bnB.solve();
		
		return (int) problem.getValue();
		
	}
	
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
	public int getMaxW() {
		return maxW;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
	
	/*public void swap(int pointOne, int pointTwo) {
		Gene geneOne = genes.get(pointOne);
		
		Gene geneTwo = genes.get(pointTwo);
		
		genes.remove(pointOne);
		genes.add(pointOne, geneTwo);
		genes.remove(pointTwo);
		genes.add(pointTwo, geneOne);
	}*/
}
