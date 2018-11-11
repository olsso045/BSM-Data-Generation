/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.node;

import dnl.link.Link;
import java.util.*;
import java.lang.Math;

/**
 * This class represents a diverge: where 1 upstream link branches out into 2 or more downstream links.
 * 
 * @author 
 */
public class UnblockedDiverge extends Node
{
    public UnblockedDiverge(int id, double longitude, double latitude, double elevation)
    {
        super(id, longitude, latitude, elevation);
    }
    
    public void step()
    {
        // move flow from upstream link to downstream link as permitted by sending and receiving flows.
        // if there is more than 1 upstream or downstream link, throw an error - this is not the intersection control you're looking for.
        if(getIncoming().size() != 1 || getOutgoing().size() == 1)
        {
            throw new RuntimeException("Wrong number of incoming or outgoing links: \nIncoming: "+getIncoming().size()+" Outgoing: "+getOutgoing().size());
        }
        // upstream link
        Link us = getIncoming().get(0);
        // downstream link
        List<Link> ds = getOutgoing();
        
        double y = 0.0;
        
        ArrayList<Double> flows = new ArrayList<>();       
        
        for (Link path : ds)
        {
            double outflow = Math.min(path.getReceivingFlow(),getTurningProp(us, path)*us.getSendingFlow());
            path.addFlow(outflow);  
            y += outflow;
            
     
            System.out.println("Outgoing Flow " + path + " " + outflow);
        }
        
        
        
        us.removeFlow(y);
        
        System.out.println("Incoming Flow " + us + " " + y);
    }
    
    public void update()
    {
        // fill this in 
    }
}
