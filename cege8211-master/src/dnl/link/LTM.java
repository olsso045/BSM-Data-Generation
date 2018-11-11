/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.link;

import dnl.Params;
import dnl.node.Node;
import java.util.LinkedList;

/**
 * This class propagates flow according to the link transmission model.
 * 
 * @author 
 */
public class LTM extends Link
{
    
        private LinkedList<Double> CumulativeBeginning = new LinkedList<>(); 
        private LinkedList<Double> CumulativeEnd = new LinkedList<>(); 
        private double length;
        private double ffspd;
        private double shockwavespd;
    
        private double AddFleaux;
        private double RemoveFleaux;
        
        
               
    public LTM(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
    {
        super(id, source, dest, length, ffspd, capacityPerLane, numLanes);
        this.length = length;
        this.ffspd = ffspd;
        
        this.shockwavespd = 2*this.ffspd;
        for (int i=0; i<= Math.ceil(getFFTime()/Params.dt)-1; i++) {
            CumulativeBeginning.add(0.0);
    }
        for (int i=0; i<= Math.ceil((this.length/this.shockwavespd)/Params.dt)-1; i++) {
            CumulativeEnd.add(0.0);
    }
        
    }
    
    
    public void step()
    {
        // fill this in
        double NewBeginning;
        NewBeginning = CumulativeBeginning.getLast() + AddFleaux;
        double NewEnd;
        NewEnd = CumulativeEnd.getLast() + RemoveFleaux;
        
        CumulativeBeginning.add(NewBeginning);
        CumulativeEnd.add(NewEnd);
        
       
        CumulativeBeginning.removeFirst();
        CumulativeEnd.removeFirst();     
        
        //System.out.println("Add flow    "+AddFleaux+"   "+getId()+" "+Params.time);
        //System.out.println("Remove flow    "+RemoveFleaux+"   "+getId()+" "+Params.time);    
        
        AddFleaux = 0;
        RemoveFleaux = 0;
           
            }
    
    public void update()
    {
        // fill this in
    }
    
    public double getSendingFlow()
    {
        // fill this in
        return Math.min(CumulativeBeginning.get(0)-CumulativeEnd.getLast(), getCapacity()*Params.dt/3600);
    }
    
    public double getReceivingFlow()
    {
        // fill this in
        return Math.min(Params.JAM_DENSITY*this.length+CumulativeEnd.get(0)-CumulativeBeginning.getLast(), getCapacity()*Params.dt/3600);
    }
    
    public void addFlow(double y)
    {
        // fill this in
        this.AddFleaux = y;
    }
    
    public void removeFlow(double y)
    {
        // fill this in
        this.RemoveFleaux = y;
    }
}
