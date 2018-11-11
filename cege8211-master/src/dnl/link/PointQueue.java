/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.link;

import dnl.node.Node;

import java.util.*;

import dnl.Params;

/**
 * This class propagates flow according to the point queue model (no spatial constraints).
 * 
 * @author 
 */
public class PointQueue extends Link
{
    // store the occupancy: that is the sending flow also.
    public double n;
    
    public LinkedList<Double> occupancy = new LinkedList<>(); 
    
    // this will be the change in occupancy at the next time step
    private double total_y;
    
    public PointQueue(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
    {
        super(id, source, dest, length, ffspd, capacityPerLane, numLanes);
        for (int i=1; i<=getFFTime()/Params.dt-1; i++) {
            occupancy.add(0.0);
    }
    }
    
    public void step()
    {
        // fill this in
    }
    
    public void update()
    {
        n=n+total_y+occupancy.get(0);
        occupancy.removeFirst();
        total_y = 0;
            }
    
    public double getSendingFlow()
    {
        return Math.min(n, getCapacity()/600);
    }
    
    public double getReceivingFlow()
    {
        return getCapacity()/600;
    }
    
    public void addFlow(double y)
    {
        occupancy.add(y);
        //System.out.println("Add flow    "+y+"   "+getId()+" "+Params.time);
        //System.out.println(occupancy);
    }
    
    public void removeFlow(double y)
    {
        total_y -= y;
        //System.out.println("Remove flow    "+y+"   "+getId()+" "+Params.time);
    }
}
