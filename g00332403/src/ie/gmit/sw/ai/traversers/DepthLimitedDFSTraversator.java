package ie.gmit.sw.ai.traversers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import ie.gmit.sw.ai.node.Node;

public class DepthLimitedDFSTraversator implements Traversator{
	private Node[][] maze;
	private int limit;
	private boolean keepRunning = true;
	//private long time = System.currentTimeMillis();
	private int visitCount = 0;

    private Node goal;
    private Set<Node> isVisited = null;
    private LinkedList<Node> pathToGoal = null;
	
	public DepthLimitedDFSTraversator(int limit, Node goal){
		this.limit = limit;
		this.goal = goal;
	}
	
	public void traverse(Node[][] maze, Node node) {
		this.maze = maze;
		pathToGoal = new LinkedList<>();
		isVisited = new HashSet<>();
		
		if(dfs(node, 1) == true){
		    pathToGoal.addFirst(node);
        }
		
		//System.out.println("Search with limit " + limit);
		isVisited = null;
	}
	
	private boolean dfs(Node node, int depth){
		if (!keepRunning || depth > limit) return false;
		
		isVisited.add(node);
		//node.setVisited(true);	
		visitCount++;
		
//		if (node.isGoalNode()){
//	        time = System.currentTimeMillis() - time; //Stop the clock
//	        TraversatorStats.printStats(node, time, visitCount);
//	        keepRunning = false;
//			return false;
//		}
		
		if (node.equals(goal)){

		    pathToGoal.addFirst(node);

	        keepRunning = false;

			return true;
		}
		
//		try { //Simulate processing each expanded node
//			Thread.sleep(50);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
		Node[] children = node.adjacentNodes(maze);
		for (int i = 0; i < children.length; i++) {
//			if (children[i] != null && !children[i].isVisited()){
			if (children[i] != null && !isVisited.contains(children[i])){
				//children[i].setParent(node);
				//dfs(children[i], depth + 1);
				if(dfs(children[i], depth + 1) == true) {
					pathToGoal.addFirst(node);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Node getNextNode() {
		// TODO Auto-generated method stub
		if(pathToGoal.size() > 0){

	        return pathToGoal.getFirst();
        }
        else
        {
	        return null;
        }
	}
}