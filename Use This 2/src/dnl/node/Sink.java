/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.node;

import dnl.Params;
import dnl.link.Link;

/**
 * This class models behavior at a sink (at a destination node): all vehicles reaching the sink are removed from the network.
 * 
 * @author Michael Levin
 */
public class Sink extends Node
{
    
    private double exiting;
    
    public Sink(int id, double longitude, double latitude, double elevation)
    {
        super(id, longitude, latitude, elevation);
    }
    
    public void step()
    {
        exiting = 0;
        // all vehicles reaching the sink are removed.
        for(Link us : getIncoming())
        {

            //System.out.println(us.getSendingFlow());
            double y = us.getSendingFlow();
    //        System.out.println(Params.time + "," + y);
//            if(y>0) {
//                System.out.println(us.getSendingFlow());
//            }
            exiting += y;
         //   System.out.println(RoundedY);
            us.removeFlowSink((int) y);
        }
        //System.out.println(this.getNumExiting());
    }
    
    public void update()
    {
        // nothing to do here
    }
    
    public double getNumExiting()
    {
        return exiting;
    }
    
    
    public void storeTurningProportion(Link i, Link j, double p)
    {
        // removed due to uselessness
    }
    
    public double getTurningProp(Link i, Link j)    
    {
        // all vehicles exit when reaching the sink
        return 1;
    }
}