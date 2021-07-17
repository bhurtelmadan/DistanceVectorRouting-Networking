//Madan Bhurtel
//1001752499

package app;

import java.util.ArrayList;
import java.util.HashMap;


/*
 * each route node class
 */
public class Node {
    private final int nodeId;
    private final NodeTextRegion nodeRegion;
    private final RouterSimulator sim;
    private final int[] costs = new int[RouterSimulator.NUM_NODES];
    private final int[] routes = new int[RouterSimulator.NUM_NODES];
    private final int[][] nextVectors = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];
    private ArrayList<HashMap<Integer,Integer>> nextNodes=new ArrayList<HashMap<Integer,Integer>>();

    // --------------------------------------------------
    public Node(final int ID, final RouterSimulator sim) {
        nodeId = ID;
        this.sim = sim;
        nodeRegion = new NodeTextRegion("  Output Region for Router #" + String.valueOf(ID + 1) + "  ", ID + 1);
        
    }

    /*
     * set the distance vector table for route node
     */
    public void setCost(final int[] costs, final boolean bChange) {

        System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);

        for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {
            routes[i] = i;
            if (costs[i] != RouterSimulator.INFINITY && costs[i] != 0) {
                HashMap<Integer,Integer> m = new HashMap<>();
                m.put(i,costs[i]);
                nextNodes.add(m);
            }
            for (int j = 0; j < RouterSimulator.NUM_NODES; j++) {
                if (i == nodeId) {
                    nextVectors[i][j] = costs[j];                   
                } else if (!bChange)
                    nextVectors[i][j] = RouterSimulator.INFINITY;
            }
        }
        if (bChange) {
            if (calculateCost()) {
                System.arraycopy(this.costs, 0, nextVectors[nodeId], 0, RouterSimulator.NUM_NODES);
                sendDataToOther(false);
            }

        } else {
            nodeRegion.initArea();
            sendDataToOther(true);
        }

    }

    /*
     * send route's distance vector change to neighbor routes
     */
    private void sendDataToOther(boolean bInit) {
        for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {
            if (i != nodeId) {
                if (costs[i] != RouterSimulator.INFINITY || bInit) {
                    final int[] vectorCost = new int[RouterSimulator.NUM_NODES];
                    System.arraycopy(costs, 0, vectorCost, 0, RouterSimulator.NUM_NODES);
                    final RouterData dtRouter = new RouterData(nodeId, i, vectorCost);
                    sendUpdate(dtRouter);
                }
            }
        }
    }

    /*
     * recalculate distance vector
     */
    private boolean calculateCost() {
        int cost = 0;  
        boolean bUpdated = false;
        for(int i = 0; i < RouterSimulator.NUM_NODES; i++) {            
            for(int dest = 0; dest < RouterSimulator.NUM_NODES; dest++) {
                cost = nextVectors[i][dest];
                if(i != nodeId) 
                    cost += nextVectors[nodeId][i];
               
                if(cost < costs[dest]) {
                    costs[dest] = cost; 
                    routes[dest] = i;
                    bUpdated = true;
                }
            }
        }

        return bUpdated; 
    }

    /*
     * process for neighbor's distance vector change receive.
     */
    public void receiveUpdate(final RouterData pkt) {
        System.arraycopy(pkt.cost, 0, nextVectors[pkt.sourceid], 0, RouterSimulator.NUM_NODES);

        final boolean bUpdated = calculateCost();
        if (bUpdated) {
            System.arraycopy(this.costs, 0, nextVectors[nodeId], 0, RouterSimulator.NUM_NODES);
            sendDataToOther(false);
        }
    }

    /*
     * send to neighbor
     */
    private void sendUpdate(final RouterData pkt) {
        sim.addRouterEvent(pkt);
    }

    /*
     * print its GUI TextArea.
     */
    public void printNodeRegion() {
        nodeRegion.print("Current state for router " + nodeId + " at Cycle " + sim.getCycle() + " \n\n");

        nodeRegion.print("Distancetable: \n");
        nodeRegion.print("\n");
        for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {
            nodeRegion.print("\t" + String.valueOf(i + 1));
        }

        nodeRegion.print("\n");

        for (int i = 0; i < 12 * (1 + RouterSimulator.NUM_NODES); i++) {
            nodeRegion.print("-");
        }
        nodeRegion.print("\n");

        for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {
            // if(i != myID) {
            nodeRegion.print(String.valueOf(i + 1));
            for (int j = 0; j < RouterSimulator.NUM_NODES; j++) {
                nodeRegion.print("\t" + nextVectors[i][j]);
            }
            nodeRegion.print("\n");
            // }
        }

        nodeRegion.print("\n");

        nodeRegion.println("Our distance vector and routes:");
        nodeRegion.print("\n");
        
        nodeRegion.print("destination");
        for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {
            nodeRegion.print("\t" + String.valueOf(i + 1));
        }

        nodeRegion.print("\n");

        for (int i = 0; i < 12 * (1 + RouterSimulator.NUM_NODES); i++) {
            nodeRegion.print("-");
        }
        nodeRegion.print("\n");
        nodeRegion.print("Link");
        for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {
            if(i==nodeId){
                nodeRegion.print("\t" + String.valueOf(i + 1));
            }else{
                nodeRegion.print("\t" + String.valueOf(getNextRoute(i) + 1));
            }
            
        }

        nodeRegion.print("\n");
        nodeRegion.print("cost");
        for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {
            nodeRegion.print("\t" + costs[i]);
        }

        nodeRegion.print("\n");
        nodeRegion.print("\n");

    }

    /*
     * update link cost and send to neighbors
     */
    public void updateLinkCost(final int dest, final int newcost) {
        nextVectors[nodeId][dest] = newcost;

        final boolean dirty = calculateCost();
        if(dirty) {
            sendDataToOther(false);
        }
    }
    private int  getNextRoute(int dest) {
        int linkCost=RouterSimulator.INFINITY;
        int r=0;
        for(int i=0;i<nextNodes.size();i++){
            HashMap<Integer,Integer> n=nextNodes.get(i);
            for(int j=0;j<RouterSimulator.NUM_NODES;j++){
                if(n.containsKey(j)){
                    int cost = n.get(j)+nextVectors[j][dest];
                    if(linkCost>cost){
                        r=j;
                        linkCost=cost;
                    }
                }
            }
        }
        return r;
    }
}
