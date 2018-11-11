/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.link;

import dnl.node.Node;

/**
 *
 * @author mlevin
 */
public class MN  extends Link
{
    // store the occupancy: that is the sending flow also.
    private double n;
    
    // this will be the change in occupancy at the next time step
    private double total_y;
    
    public MN(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
    {
        super(id, source, dest, length, ffspd, capacityPerLane, numLanes);
    }
    
    
    public void step()
    {
        // fill this in
    }
    
    public void update()
    {
         // addFlow() and removeFlow() updated total_y; now we need to propagate those changes to the occupancy
        n += total_y;
        
        // zero out total_y for the next time step
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
        // add y to the occupancy for the next time step
        total_y += y;
    }
    
    public void removeFlow(double y)
    {
        // remove y from the occupancy for the next time step
        total_y -= y;
    }
    
}
