/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.node;

import dnl.Vehicle;
import dnl.link.Link;

/**
 * This Node type simply connects two links, possibly with different capacities.
 * The behavior is to move vehicles given by min(sending flow, receiving flow).
 * 
 * @author Michael Levin
 */
public class Series extends Node
{
    public Series(int id, double longitude, double latitude, double elevation)
    {
        super(id, longitude, latitude, elevation);
    }
    
    
    public void step()
    {
        // move flow from upstream link to downstream link as permitted by sending and receiving flows.
        // if there is more than 1 upstream or downstream link, throw an error - this is not the intersection control you're looking for.
        if(getIncoming().size() != 1 || getOutgoing().size() != 1)
        {
            throw new RuntimeException("Wrong number of incoming or outgoing links: \nIncoming: "+getIncoming().size()+" Outgoing: "+getOutgoing().size());
        }
        
        // upstream link
        Link us = getIncoming().get(0);
        Link ds = getOutgoing().get(0);
        
        int y = (Math.min(us.getSendingFlow(), ds.getReceivingFlow()));
        
                System.out.println(us.getSendingFlow()+" "+ ds.getReceivingFlow());

     
    }
    
    public void update()
    {
               Link us = getIncoming().get(0);
        Link ds = getOutgoing().get(0);
        
        int y = (Math.min(us.getSendingFlow(), ds.getReceivingFlow()));
        
            //   System.out.println(us.getSendingFlow()+" "+ ds.getReceivingFlow());

       us.removeFlow(y);
        ds.addFlowSeries(y); // nothing to do here
    }
}