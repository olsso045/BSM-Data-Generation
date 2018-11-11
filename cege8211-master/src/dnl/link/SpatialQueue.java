/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.link;

import dnl.node.Node;

import dnl.Params;

import java.lang.Math;

/**
 * This class propagates flow according to the spatial queue model, including capacity constraints and a density constraint per link.
 * 
 * @author 
 */
public class SpatialQueue extends PointQueue
{
    public SpatialQueue(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
    {
        super(id, source, dest, length, ffspd, capacityPerLane, numLanes);
    }
    
    public double getReceivingFlow()
    {
        double summation = 0.0;
            for (Double section : occupancy)
            {
                summation += section;
            }
                
        return Math.min(getCapacity()/600, Params.JAM_DENSITY*getLength()-(n+summation));
    }
}
