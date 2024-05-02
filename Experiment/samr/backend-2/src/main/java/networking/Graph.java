package networking;

import java.util.*;
import java.util.function.*;


/** Graph<V> represents a generic graph with vertices of type V and includes the most crucial of graph io operators. */
public interface Graph<V> {

	/** Counting */
	public int edgesN();
	public int verticesN();
	public int degree(V vertex);
	/** Checks */
	public boolean has(V vertex);
	public boolean has(V src, V... dests);
	/** Assigning */
	public void add(V vertex, V... out_connections);
	public void remove(V vertex);
	public void remove(V src, V... dests);
	/** Iteration */
	public Iterable<V> vertices();
	public Iterable<V> connections(V source);

	public static <V> void traverseDepth(Graph<V> graph, Consumer<V> on_discovery, Consumer<V> on_finish) {

		if(on_discovery == null) on_discovery = (V v)->{};
		if(on_finish == null) on_finish = (V v)->{};

		final HashSet<V> found = new HashSet<V>();
		final Deque<V> frontier = new ArrayDeque<V>();
		final V start = graph.vertices().iterator().next();
		found.add(start);
		frontier.addFirst(start);
		on_discovery.accept(start);
		while(!frontier.isEmpty()) {
			final V v = frontier.removeFirst();
			for(V dest : graph.connections(v)) {
				if(found.add(dest)) {
					on_discovery.accept(dest);
					frontier.addLast(dest);
				}
			}
			on_finish.accept(v);
		}
		
	}
	public static <V> void traverseBreadth(Graph<V> graph,
		BiConsumer<V, V> on_discovery,
		Consumer<V> on_finish,
		Consumer<V> on_new_component,
		BiConsumer<V, V> on_cycle
	) {

		if(on_discovery == null) on_discovery = (V p, V v)->{};
		if(on_finish == null) on_finish = (V v)->{};
		if(on_new_component == null) on_new_component = (V v)->{};
		if(on_cycle == null) on_cycle = (V p, V v)->{};

		final HashSet<V> found = new HashSet<V>();
		final Deque<V> traversal = new ArrayDeque<V>();
		for(V vertex : graph.vertices()) {
			if(!found.contains(vertex)) {	// only start a new tree for newly found nodes

				found.add(vertex);
				traversal.push(vertex);		// add the first item to the stack

				on_discovery.accept(null, vertex);
				on_new_component.accept(vertex);

				while(!traversal.isEmpty()) {	// when the stack is empty, we have processed everything in this tree
					final V v = traversal.peek();	// next node to search from
					boolean none = true;
					for(V u : graph.connections(v)) {	// for each connection of v
						if(found.add(u)) {				// only add if not new...
							none = false;				// if new, push to stack
							traversal.push(u);
							on_discovery.accept(v, u);	// push predecessor and node
						} else {
							on_cycle.accept(v, u);	// not necessarily - need to ensure this vertex has not finished yet
						}
					}
					if(none) {
						traversal.pop();	// nothing new was added, we pop the top (which is still v)
						on_finish.accept(v);
					}
				}

			}
		}

	}




	/** IMPLEMENTATIONS */

	/** A simple directed graph -- list<V --> list<V>> internal structure (acutally Map<V, Set<V>>) */
	public static class DirectedGraph<V> implements Graph<V> {

		protected HashMap<V, HashSet<V>> graph = new HashMap<V, HashSet<V>>();
		protected int n_edges = 0;

		public DirectedGraph() {}


		@Override
		public int edgesN() {
			return this.n_edges;
		}
		@Override
		public int verticesN() {
			return this.graph.size();	// this.graph.keySet().size(); ???
		}
		@Override
		public int degree(V vertex) {
			if(this.has(vertex)) {
				return this.graph.get(vertex).size();
			}
			return -1;
		}

		@Override
		public boolean has(V vertex) {
			return this.graph.containsKey(vertex);
		}
		@Override
		public boolean has(V src, V... dests) {
			if(this.has(src)) {
				final HashSet<V> connections = this.graph.get(src);
				for(V dest : dests) {
					if(!connections.contains(dest)) {
						return false;
					}
				}
			}
			return false;
		}

		@Override
		public void add(V vertex, V... out_connections) {
			if(!this.has(vertex)) {
				this.graph.put(vertex, new HashSet<V>());
			}
			if(out_connections != null) {
				final HashSet<V> connections = this.graph.get(vertex);
				for(V dest : out_connections) {
					if(!this.has(dest)) {
						this.add(dest, (V[])null);
					}
					if(connections.add(dest)) {
						this.n_edges++;
					}
				}
			}
		}
		@Override
		public void remove(V vertex) {
			if(this.has(vertex)) {
				this.n_edges -= this.graph.get(vertex).size();
				this.graph.remove(vertex);
				for(V v : this.graph.keySet()) {
					if(this.graph.get(v).remove(vertex)) {
						this.n_edges--;
					}	// remove connections going to the vertex
				}
			}
		}
		@Override
		public void remove(V src, V... dests) {
			if(this.has(src)) {
				final HashSet<V> connections = this.graph.get(src);
				for(V dest : dests) {
					if(connections.remove(dest)) {
						this.n_edges--;
					}
				}
			}
		}

		@Override
		public Iterable<V> vertices() {
			return this.graph.keySet();
		}
		@Override
		public Iterable<V> connections(V src) {
			if(this.has(src)) {
				return this.graph.get(src);
			}
			return null;
		}

	}





	// public static void main(String... args) {

	// 	Graph<String> graph = new DGraph<String>();
	// 	graph.add("hello", "bye", "world", "chubs", "people");
	// 	graph.add("bye", "hello", "chubs");
	// 	graph.add("chubs", "squishies", "friends", "bubs");
	// 	System.out.println(graph.connections("hello"));
	// 	System.out.println(graph.vertices());

	// 	Graph.DFTraversal(graph,
	// 		(String p, String s)->{System.out.printf("%s --> Discovered: %s\n", p, s);},
	// 		(String s)->{System.out.printf("Completed: %s\n", s);},
	// 		(String s)->{System.out.printf("New component starting: %s\n", s);},
	// 		(String p, String s)->{System.out.printf("Found cycle edge: %s --> %s\n", p, s);}
	// 	);

	// }

}
