/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.node;

import dnl.Params;
import dnl.Vehicle;
import dnl.link.Link;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * This class models a source (origin node): vehicles spawn at this location at predetermined rates.
 * 
 * @author Michael Levin
 */
public class Source extends Node
{
    
    public int SourceCounter = 0;
    public double newDemand;
    public List SourceVehicles; //create list instead of array since size needs to change
    
    /**
     * This stores the rate of demand as a map between final time and rate of demand.
     * For instance, the entry ([0, 20], 50) means that when the time is between 0 and 20s, 50veh/hr enter at this source.
     */
    private HashMap<Double[], Double> demand_rates;
    
    
    public Source(int id, double longitude, double latitude, double elevation)
    {
        super(id, longitude, latitude, elevation);
        
        demand_rates = new HashMap<>();
        
        SourceCounter = 0;
        
        this.SourceVehicles = new ArrayList(); //create list instead of array since size needs to change
    }
    
    /**
     * This is used to add demand to the source. 
     * It is typically called in ReadNetwork when constructing the network.
     */
    public void addDemand(double start_time, double end_time, double demand)
    {
        Double[] time = new Double[]{start_time, end_time};
        
        if(demand_rates.containsKey(time))
        {
            demand_rates.put(time, demand_rates.get(time) + demand);
        }
        else
        {
            demand_rates.put(time, demand);
        }
    }
       
    public void step()
    {
        /**
         * Here we need to add the specified amount of demand to the network.
         * First, calculate the rate at which vehicles enter during the next time step.
         * The time interval is [Params.time, Params.time + Params.dt).
         * If the demand applies for part of the time interval, scale the rate proportionally to the overlap.
         */
        
        double rate = 0.0;
        
        for(Double[] times : demand_rates.keySet())
        {
            
            double start_time = Math.max(Params.time, times[0]);
            double end_time = Math.min(Params.time + Params.dt, times[1]);

            if(end_time >= start_time)
            {
                rate += demand_rates.get(times) * (end_time - start_time) / Params.dt;
            }
        }
        
        // now add demand at the given rate
        this.newDemand = rate * Params.dt/3600.0;     
        
        // split it according to turning proportions
        for(Link ds : getOutgoing())
        {
            int CarNumb = (int) Math.round(newDemand * getTurningProp(null, ds));

            for(int i = 0; i < CarNumb; i++)
            {                
                SourceCounter += 1;
                Vehicle car = new Vehicle(SourceCounter);
                SourceVehicles.add(car); // thiz iz for a lizt
            }
            
            int addDemand = (int) Math.min((newDemand * getTurningProp(null, ds)),ds.getReceivingFlow());
            //ds.addFlow((int) (newDemand * getTurningProp(null, ds)));
            
            for(int i = 0; i < addDemand; i++)
            {               
                Vehicle MoveVehicle = (Vehicle) SourceVehicles.get(0);
                ds.addFlow(MoveVehicle);
                SourceVehicles.remove(0); //thiz iz for a lizt
            }
        }
    }
    
    public void update()
    {
        // nothing to do here
    }
        
    
    /**
     * This is actually useful - it stores which downstream link entering vehicles go to!
     * We need a dummy link to serve as the incoming link, since a source does not have incoming links.
     */
    Link dummy = new Link(-1, null, null, 0, 0, 0, 0)
    {
        public void step(){}
        public void update(){}
        public int getSendingFlow(){return 0;}
        public int getReceivingFlow(){return 0;}
        public void addFlow(Vehicle y){}
        public void removeFlow(int y){}
    };
    
    public void storeTurningProportion(Link i, Link j, double p)
    {
        super.storeTurningProportion(dummy, j, p);
    }
    
    public double getTurningProp(Link i, Link j)    
    {
        return super.getTurningProp(dummy, j);
    }
}