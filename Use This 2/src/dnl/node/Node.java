/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.node;

import dnl.link.Link;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This represents a node in the network.
 * This is an abstract class: key methods are not implemented, and must be implemented in subclasses. 
 * These depend on the specific intersection model.
 * 
 * @author Michael Levin
 */
public abstract class Node implements Comparable<Node>
{
    // id used to reference the node
    private int id;
    
    /**
     * Stores turning proportions between links. 
     * Turning proportions specify the route assignment.
     */
    private Map<Link, Map<Link, Double>> turning_prop;
    
    /**
     * Stores sets of incoming and outgoing links
     */
    private List<Link> incoming, outgoing;
    
    
    /**
     * Elevation, in ft
     */
    private double elevation;
    
    // latitude and longitude
    private double latitude, longitude;
    
    /**
     * Constructs the node with the given id, latitude, longitude, and elevation (in ft).
     * The incoming and outgoing sets of links will be updated as links are constructed.
     * 
     */
    public Node(int id, double longitude, double latitude, double elevation)
    {
        // store node parameters
        this.id = id;
        this.elevation = elevation;
        this.latitude = latitude;
        this.longitude = longitude;
        
        
        // construct incoming and outgoing sets. They will be populated as links are constructed.
        incoming = new ArrayList<>();
        outgoing = new ArrayList<>();
        
        turning_prop = new HashMap<>();
    }
    
    
    /**
     * Returns the elevation (in ft)
     */
    public double getElevation()
    {
        return elevation;
    }
    
    /**
     * Returns the latitude.
     */
    public double getLatitude()
    {
        return latitude;
    }
    
    /**
     * Returns the longitude.
     */
    public double getLongitude()
    {
        return longitude;
    }
    
    /**
     * Used when constructing the link: adjusts the turning proportion from link i to link j.
     * Turning proportions must add to 1 for all incoming links.
     */
    public void storeTurningProportion(Link i, Link j, double p)
    {
        if(!turning_prop.containsKey(i))
        {
            turning_prop.put(i, new HashMap<>());
        }
        turning_prop.get(i).put(j, p);
    }
    
    
    /**
     * Returns the proportion of flow leaving link i turning onto link j.
     * This returns a value between 0 and 1.
     */
    public double getTurningProp(Link i, Link j)    
    {
        if(turning_prop.containsKey(i) && turning_prop.get(i).containsKey(j))
        {
            return turning_prop.get(i).get(j);
        }
        else
        {
            return 0.0;
        }
    }
    
    /**
     * Returns the set of incoming links
     */
    public List<Link> getIncoming()
    {
        return incoming;
    }
    
    /**
     * Returns the set of outgoing links
     */
    public List<Link> getOutgoing()
    {
        return outgoing;
    }
    
    /**
     * Adds the passed link to the incoming or outgoing sets of links, as appropriate
     */
    public void addLink(Link l)
    {
        if(l.getSource() == this)
        {
            outgoing.add(l);
        }
        else if(l.getDest() == this)
        {
            incoming.add(l);
        }
    }
    
    /**
     * Returns the id for this node
     */
    public int getId()
    {
        return id;
    }
    
    /**
     * This is called every time step. 
     * For the Node class, step() should move flow between incoming and outgoing links.
     */
    public abstract void step();
    
    /**
     * This is called every time step, after step() has been called for all nodes and links.
     * It can be used to finish any updating work that could not occur during step().
     * DO NOT use this to update the internal state of links. This is used to update the state of this node.
     */
    public abstract void update();
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Used to sort nodes by id.
     */
    public int compareTo(Node rhs)
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
    
    
    // used by shortest path
    public Link pred;
    public double arr_time;
}
