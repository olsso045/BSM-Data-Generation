/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.link;

import dnl.Vehicle;
import dnl.node.Node;

/**
 * This represents a link in the network.
 * This is an abstract class: key methods are not implemented, and must be implemented in subclasses. 
 * These depend on the specific flow model.
 * 
 * @author Michael Levin
 */
public abstract class Link implements Comparable<Link>
{
    // id used to reference the link
    private int id;
    
    // stores the upstream and downstream nodes
    private Node source, dest;
    
    // capacity per lane in veh/hr
    private double capacityPerLane;
    
    // number of lanes
    private int numLanes;
    
    // link length in miles
    private double length;
    
    // free flow speed in mi/hr
    private double ffspd; 
    
    
    /**
     * Constructs a new link with the given parameters.
     * Generally links will be constructed in ReadNetwork.
     * 
     * Jam density is global and found in Params.java.
     * Backwards wave speed may or may not be calculated based on these parameters - depends on the flow model.
     * Length is in mi, ffspd in mi/hr, capacityPerLane in veh/hr
     */
    
    public Link(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
    {
        // store link parameters
        this.id = id;
        this.source = source;
        this.dest = dest;
        this.capacityPerLane = capacityPerLane;
        this.ffspd = ffspd;
        this.length = length;
        this.numLanes = numLanes;
        
        // update incoming/outgoing sets of links in the Node class
        if(source != null)
        {
            source.addLink(this);
        }
        if(dest != null)
        {
            dest.addLink(this);
        }
    }
    
    /**
     * Returns the free flow speed in mi/hr
     */
    public double getFFSpeed()
    {
        return ffspd;
    }
    
    /**
     * Returns the free flow travel time in s.
     */
    public double getFFTime()
    {
        return getLength() / getFFSpeed() * 3600.0;
    }
    
    /**
     * Returns the travel time when entering at the given time (in s).
     * NOTE: this is not filled in! Currently just returns the free flow time.
     */
    public double getTT(int enter_time)
    {
        return getFFTime();
    }
    
    
    /**
     * Returns the average grade (change in elevation) in percent vertical change in ft per horizontal distance
     */
    public double getAvgGrade()
    {
        return (dest.getElevation() - source.getElevation()) / (getLength() * 5280);
    }
    
    /**
     * Returns the length in mi
     */
    public double getLength()
    {
        return length;
    }
    
    /**
     * Returns the capacity per lane in veh/hr
     */
    public double getCapacityPerLane()
    {
        return capacityPerLane;
    }
    
    /**
     * Returns the total capacity in veh/hr
     */
    public double getCapacity()
    {
        return capacityPerLane * numLanes;
    }
    
    /**
     * Returns the number of lanes
     */
    public double getNumLanes()
    {
        return numLanes;
    }
    
    

    /**
     * Returns the upstream node for this link
     */
    public Node getSource()
    {
        return source;
    }
    
    /**
     * Returns the downstream node for this link
     */
    public Node getDest()
    {
        return dest;
    }
    
    
    /**
     * Returns the id for this link
     */
    public int getId()
    {
        return id;
    }
    
    /**
     * This is called every time step. 
     * For the Link class, step() should propagate flow along the link.
     */
    public abstract void step();
    
    /**
     * This is called every time step, after step() has been called for all nodes and links.
     * It can be used to finish any updating work that could not occur during step()
     */
    public abstract void update();
    
    /**
     * Adds the given flow (in veh, for a single time step) to the upstream end of the link.
     * This method is called when vehicles enter the link!
     * This is usually called by the Node step() method.
     */
    public abstract void addFlow(Vehicle y);
    
    
    
    /**
     * Removes the given flow (in veh, for a single time step) from the downstream end of the link.
     * This method is called when vehicles exit the link! 
     * This is usually called by the Node step() method.
     * @param y 
     */
    public abstract void removeFlow(int y);
    
    /**
     * Returns the maximum flow that could exit the link in the next time step (in veh)
     */
    public abstract int getSendingFlow();
    
    
    /**
     * Returns the maximum flow that could enter the link in the next time step (in veh)
     */
    public abstract int getReceivingFlow();
    
    
    /**
     * Used to sort links by id.
     */
    public int compareTo(Link rhs)
    {
        return id - rhs.id;
    }
    
    
    /**
     * Used for hashing. You can ignore it.
     */
    public int hashCode()
    {
        return id;
    }
    
    /**
     * Returns the id of the link
     */
    public String toString()
    {
        return ""+id;
    }
}
