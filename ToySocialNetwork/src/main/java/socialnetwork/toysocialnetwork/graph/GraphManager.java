package socialnetwork.toysocialnetwork.graph;

import socialnetwork.toysocialnetwork.domain.Friendship;
import socialnetwork.toysocialnetwork.domain.User;

import java.util.*;

/**
 * An enum storing the colors nodes can take during a full DFS algorithm.
 */
enum Color {
    White, Grey, Black
}

class IntegerWrapper{
    public IntegerWrapper() {    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    int value;
}

/**
 * A utility class meant to deal with any graph-related issue or algorithm.
 */
public class GraphManager {
    /**
     * A static method meant for converting two iterable containers of Users and Friendships respectively into this class' graph format.
     * @param usersIterable The iterable that goes over the repository of users.
     * @param friendsIterable The iterable that goes over the repository of friendships.
     * @return The graph containing users as nodes and friendships as edges as an adjacency list represented on a Hashmap.
     */
    private static Map<Node, List<Node>> constructGraph(Iterable<User> usersIterable, Iterable<Friendship> friendsIterable) {
        ArrayList<Long> idList = new ArrayList<>();
        HashMap<Node, List<Node>> nodesMap = new HashMap<>();
        HashMap<Long, Node> IDToNodeMapper = new HashMap<>();

        int idIndex;

        for (User u : usersIterable) {
            idIndex = idList.indexOf(u.getId());
            if (idIndex == -1) {
                idList.add(u.getId());

            }
        }

        for (Long id : idList) {
            Node node = new Node(id);
            nodesMap.put(node, new ArrayList<>());
            IDToNodeMapper.put(id, node);
        }

        for (Friendship f : friendsIterable) {
            Long ID1 = f.getU1ID();
            Long ID2 = f.getU2ID();
            Node node1 = IDToNodeMapper.get(ID1);
            Node node2 = IDToNodeMapper.get(ID2);

            nodesMap.get(node1).add(node2);
            nodesMap.get(node2).add(node1);
        }

        return nodesMap;
    }

    /**
     * Runs the wrapper over the DFS algorithm.
     * @param graph The graph to run the algorithm on. Given in this class' standard format.
     * @return The number of connected components found by DFS.
     */
    private int DFS_Start(Map<Node, List<Node>> graph) {
        int componentsCounter = 0;
        for (Map.Entry<Node, List<Node>> entry : graph.entrySet()) {
            if (entry.getKey().getColor() == Color.White) {
                componentsCounter++;
                DFS(graph, entry.getKey(), componentsCounter);
            }
        }

        return componentsCounter;
    }

    /**
     * Performs the recursive DFS procedure.
     * @param graph The graph to run the algorithm on. Given in this class' standard format.
     * @param n The node that should be processed in that iteration.
     * @param componentNum The index of this node's connected component.
     */
    private void DFS(Map<Node, List<Node>> graph, Node n, int componentNum) {
        n.setColor(Color.Black);
        n.setComponentNum(componentNum);

        for (Node otherNode : graph.get(n)) {
            if (otherNode.getColor() == Color.White)
                DFS(graph, otherNode, componentNum);
        }
    }

    /**
     * Finds the number of connected components that a graph has.
     * @param usersIterable An Iterable over the repository of users (the nodes of the graph).
     * @param friendsIterable An Iterable over the repository of friendships (the edges of the graph).
     * @return The number of connected components of the given graph.
     */
    public int countConnectedComponents(Iterable<User> usersIterable, Iterable<Friendship> friendsIterable) {
        Map<Node, List<Node>> convertedGraph = constructGraph(usersIterable, friendsIterable);
        return DFS_Start(convertedGraph);
    }
    /*
    private void Dijkstra(Map<Node, List<Node>> graph, Node source) {
        for (Map.Entry<Node, List<Node>> entry : graph.entrySet()) {
            entry.getKey().setDistance(Node.MAX_DISTANCE);
            entry.getKey().setPrevious(null);
        }
        source.setDistance(0);
        PriorityQueue<Node> Q = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.getDistance() - o2.getDistance();
            }
        });

        for (Map.Entry<Node, List<Node>> entry : graph.entrySet())
        {
            Q.add(entry.getKey());
        }

        while(!Q.isEmpty())
        {
            Node u = Q.peek();
            Q.remove(u);

            for(Node v: graph.get(u))
            {
                int altDist = u.getDistance() + 1;
                if(altDist < v.getDistance())
                {
                    v.setDistance(altDist);
                    v.setPrevious(u);
                }
            }
        }
    }

    public List<Node> getLongestPathComponent(Iterable<User> usersIterable, Iterable<Friendship> friendsIterable) {
        Map<Node, List<Node>> convertedGraph = constructGraph(usersIterable, friendsIterable);

        DFS_Start(convertedGraph);

        Node maxDistanceNode = null;
        int overallMaxDist = 0;
        for (Map.Entry<Node, List<Node>> entry : convertedGraph.entrySet()){
            Dijkstra(convertedGraph, entry.getKey());

            int maxDist = 0;
            int nodeDist;
            for(Map.Entry<Node, List<Node>> entry2: convertedGraph.entrySet())
            {
                nodeDist = entry2.getKey().getDistance();
                if(nodeDist > maxDist && nodeDist != Node.MAX_DISTANCE)
                    maxDist = nodeDist;
            }
            if(maxDist > overallMaxDist)
            {
                overallMaxDist = maxDist;
                maxDistanceNode = entry.getKey();
            }
        }

        if(maxDistanceNode == null)
        {
            System.out.println("Antisociali rau astia... also, adauga exceptie aici");
            return null;
        }

        List<Node> longestPathComponent = new ArrayList<>();

        for(Map.Entry<Node, List<Node>> entry : convertedGraph.entrySet())
        {
            if(entry.getKey().getComponentNum() == maxDistanceNode.getComponentNum())
            {
                longestPathComponent.add(entry.getKey());
            }
        }

        return longestPathComponent;
        //System.out.println(maxDistanceNode + " " + overallMaxDist);
    }*/

    /**
     * Checks if the current path found in the graph is valid
     * @param graph The graph the path is part of
     * @param solution The path found so far
     * @return true, if the path is valid. false, if it is not.
     */
    private boolean LongestPathIsValid(Map<Node, List<Node>> graph, List<Node> solution)
    {
        if(new HashSet<>(solution).size() != solution.size())
            return false;


        for(int i = 0; i < solution.size() - 1; i++)
        {
            Node n = solution.get(i);
            Node n2 = solution.get(i + 1);
            if(!graph.get(n).contains(n2))
                return false;
        }
        return true;
    }

    /**
     * Checks if the current path found in a graph is the longest one
     * @param solution The path found so far
     * @param maxFoundSize The length of the longest path found so far
     * @param conCompIndex The connected component that the path is part of.
     */
    private void LongestPathAnalyzeSolution(List<Node> solution, IntegerWrapper maxFoundSize, IntegerWrapper conCompIndex)
    {
        if((solution.size() - 1) > maxFoundSize.getValue()){
            maxFoundSize.setValue(solution.size() - 1);
            conCompIndex.setValue(solution.get(0).getComponentNum());
        }
    }

    /**
     * Performs backtracking on a graph to find the longest path.
     * @param graph  The graph to check.
     * @param solution The path found so far.
     * @param maxFoundSize The length of the longest path found so far.
     * @param conCompIndex The connected component that the path is part of.
     */
    private void LongestPathBacktracking(Map<Node, List<Node>> graph, List<Node> solution, IntegerWrapper maxFoundSize, IntegerWrapper conCompIndex)
    {
        for(Node node: graph.keySet())
        {
            solution.add(node);

            if(LongestPathIsValid(graph, solution))
            {
                LongestPathAnalyzeSolution(solution, maxFoundSize, conCompIndex);

                LongestPathBacktracking(graph, solution, maxFoundSize, conCompIndex);
            }

            solution.remove(solution.size() - 1);
        }
    }

    /**
     * Coordinates the search for the component with the longest path.
     * @param usersIterable An iterable going over the repository of users.
     * @param friendsIterable An iterable going over the repository of friendships.
     * @return The list of users making up the most sociable component.
     */
    public List<Long> getLongestPathComponent(Iterable<User> usersIterable, Iterable<Friendship> friendsIterable)
    {
        Map<Node, List<Node>> convertedGraph = constructGraph(usersIterable, friendsIterable);
        List<Node> solutionList = new ArrayList<>();
        DFS_Start(convertedGraph);

        IntegerWrapper maxFoundSize = new IntegerWrapper(), conCompIndex = new IntegerWrapper();
        LongestPathBacktracking(convertedGraph, solutionList, maxFoundSize, conCompIndex);
        List<Long> mostSociableComponent = new ArrayList<>();

        for(Node n: convertedGraph.keySet())
            if(n.getComponentNum() == conCompIndex.getValue())
                mostSociableComponent.add(n.getId());

        return mostSociableComponent;
    }
    
}
