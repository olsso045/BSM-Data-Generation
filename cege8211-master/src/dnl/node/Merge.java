/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.node;

import dnl.link.Link;
import java.util.List;

/**
 * This class represents a merge: where 2 or more upstream links combine into 1 downstream link.
 * 
 * @author 
 */
public class Merge extends Node
{
    public Merge(int id, double longitude, double latitude, double elevation)
    {
        super(id, longitude, latitude, elevation);
    }
    
    public void step()
    {
        // move flow from upstream link to downstream link as permitted by sending and receiving flows.
        // if there is more than 1 upstream or downstream link, throw an error - this is not the intersection control you're looking for.
        if(getIncoming().size() == 1 || getOutgoing().size() != 1)
        {
            throw new RuntimeException("Wrong number of incoming or outgoing links: \nIncoming: "+getIncoming().size()+" Outgoing: "+getOutgoing().size());
        }
        // upstream link
        List<Link> us = getIncoming();
        // downstream link
        Link ds = getOutgoing().get(0);
        
        double total_sending_flow = 0.0;
        
        double total_capacity = 0.0;
        
        for (Link path : us)
        {
            total_sending_flow += path.getSendingFlow();
            total_capacity += path.getCapacity();
        }
        
        if(total_sending_flow <= ds.getReceivingFlow())
        {
            for(Link path : us)
            {
                path.removeFlow(path.getSendingFlow());
                    }
            
            ds.addFlow(total_sending_flow);
            
        }
        else
        {
            double unused_space_ds = ds.getReceivingFlow();
            double leftover_us = total_capacity;
            
            
            for(Link path : us)
            {
                if(path.getSendingFlow() <= path.getCapacity()*ds.getReceivingFlow()/total_capacity)
                {
                    path.removeFlow(path.getSendingFlow());
                    leftover_us -= path.getCapacity();
                    unused_space_ds -= path.getSendingFlow();
                    }
            }
            
            for(Link path : us)
            {
                if(path.getSendingFlow() > path.getCapacity()*ds.getReceivingFlow()/total_capacity)
                {
                    path.removeFlow(unused_space_ds*path.getCapacity()/leftover_us);
                }
            }
            
            ds.addFlow(ds.getReceivingFlow());
                       
           
        }
    }
    
   
    public void update()
    {
        // fill this in 
    }
}
