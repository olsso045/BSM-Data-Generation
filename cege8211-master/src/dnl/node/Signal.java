/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.node;

import dnl.Params;
import dnl.link.Link;
import java.util.*;

/**
 * This class is used to model a traffic signal. 
 * There can be any number of incoming and outgoing links.
 * During construction, addPhase() and setOffset() will be called to store signal cycle information.
 * addPhase() adds a phase to the signal cycle; setOffset() sets the start time of the cycle (relative to 0).
 * 
 * @author 
 */
public class Signal extends Node
{
    
    private Map<Integer, Double> time_red;
    private Map<Integer, Double> time_yellow;
    private Map<Integer, Double> time_green;
    private Map<Integer, Double> phase_length;
    private Map<Integer, List<Link[]>> turns;
      
    private double offset;
        
    public Signal(int id, double longitude, double latitude, double elevation)
    {
        super(id, longitude, latitude, elevation);
        
        time_red = new HashMap<Integer, Double>();
        time_yellow = new HashMap<Integer, Double>();
        time_green = new HashMap<Integer, Double>();
        phase_length = new HashMap<Integer, Double>();
        turns = new HashMap<Integer, List<Link[]>>();
        
    }
    
    public void step()
    {
        double[] GreenProportion = getGreenProportion();
        Map<Link, Double> AddFlow = new HashMap<Link, Double>();
        Map<Link, Double> RemoveFlow = new HashMap<Link, Double>();
        
        if (GreenProportion[1] + GreenProportion[3] > 0) {
        
            for (int i=0; i<2; i++) {
            
                int sequence = (int) GreenProportion[i*2];
                double Proportion = GreenProportion[2*i+1];
            
                if (Proportion > 0.0) {
                
                    List<Link[]> Movements = turns.get(sequence);
                    
                    double phi = 1.0;
                
                    for (Link[] turning : Movements) {
                    
                        Link origin = turning[0];
                        Link destination = turning[1];
                        
                        double ReceivingFlow = Math.min(destination.getReceivingFlow(), destination.getCapacity()*Proportion*Params.dt/3600);
                        
                        double SendingFlow = Math.min(origin.getSendingFlow(), origin.getCapacity()*Proportion*Params.dt/3600);
                        
                        phi = Math.min(phi, ReceivingFlow/(SendingFlow*this.getTurningProp(origin, destination)));              
                    
                    }
                
                    for (Link[] turning : Movements) {
                    
                        Link origin = turning[0];
                        Link destination = turning[1];
                        
                        double ReceivingFlow = Math.min(destination.getReceivingFlow(), destination.getCapacity()*Proportion*Params.dt/3600);
                        
                        double SendingFlow = Math.min(origin.getSendingFlow(), origin.getCapacity()*Proportion*Params.dt/3600);
                        
                        if (AddFlow.containsKey(destination)) {
                        
                            AddFlow.put(destination, SendingFlow*phi*this.getTurningProp(origin, destination)+AddFlow.get(destination));
                      
                        }
                        
                        else {
                        
                            AddFlow.put(destination, SendingFlow*phi*this.getTurningProp(origin, destination));
                        
                        }
                    
                        
                        if (RemoveFlow.containsKey(origin)) {
                        
                            RemoveFlow.put(origin, SendingFlow*phi*this.getTurningProp(origin, destination)+RemoveFlow.get(origin));
                      
                        }
                        
                        else {
                        
                            RemoveFlow.put(origin, SendingFlow*phi*this.getTurningProp(origin, destination));
                            
                        }
                    }

                }
               
            }
        for (Link downstream: AddFlow.keySet()) {
            downstream.addFlow(AddFlow.get(downstream));
        }
        
        for (Link upstream: RemoveFlow.keySet()) {
            upstream.removeFlow(RemoveFlow.get(upstream));
        }     
    }
    }    
    
    public void update()
    {
        // fill this in 
    }
    
    /**
     * Returns the length of the cycle, in s.
     */
    public double getCycleLength()
    {
                double cycle_length = 0.0;
        
        for(int sequence : phase_length.keySet()) {
            cycle_length += phase_length.get(sequence);
            
        }
                 
        return cycle_length;
    }
    
    
    
    /**
     * This updates the offset of the signal cycle (in s). For instance, if the offset is 21s, then phase 1 starts at t=21s. 
     * Note that the signal cycle repeats, so there will always be a phase active at t=0. Depending on the offset, it may not be the first phase!
     */
    public void setOffset(double offset)
    {
        this.offset = offset;
    }
    
    /**
     * This adds the specified phase to this signal. Recall that a signal cycle consists of a list of phases.
     * Sequence is the order that this phase occurs in a fixed cycle, starting from 1.
     * Green, yellow, and all-red time are in seconds. 
     * Protected_turns is a list of pairs of links - one incoming, one outgoing.
     * For instance, if (i,j) is in protected_turns, then this phase allows movement from i to j
     */
    public void addPhase(int sequence, double green, double yellow, double all_red, List<Link[]> protected_turns)
    {
        time_red.put(sequence, all_red);
        time_yellow.put(sequence, yellow);
        time_green.put(sequence, green);
        phase_length.put(sequence, all_red + yellow + green);
        turns.put(sequence, protected_turns);
         
    }
  
    
    public double[] getGreenProportion() 
        {
        
        double[] GreenProportion = new double[4];
        double CycleLength = getCycleLength();
        
        double Relative1 = (Params.time + this.offset)% CycleLength;
        double Relative2 = (Params.time + this.offset + Params.dt)% CycleLength;
              
        int Relative1Phase = 0; 
        int Relative2Phase = 0;
        double Relative1PhaseStart = 0.0;
        double Relative1PhaseEnd = 0.0;
        double Relative2PhaseStart = 0.0;
        double Relative2PhaseEnd = 0.0;
        
        double TimeMarker = 0.0;
        
        for(int sequence : phase_length.keySet())
        {
            if (Relative1 > TimeMarker & Relative1 <= (TimeMarker + phase_length.get(sequence))) {
                Relative1Phase = sequence;
                Relative1PhaseStart = Relative1 - TimeMarker;
                Relative1PhaseEnd =  TimeMarker + phase_length.get(sequence) - Relative1;
            }            
            
            if (Relative2 > TimeMarker & Relative2 <= (TimeMarker + phase_length.get(sequence))) {
                Relative2Phase = sequence;
                Relative2PhaseStart = Relative2 - TimeMarker;
                Relative2PhaseEnd =  TimeMarker + phase_length.get(sequence) - Relative2;
            }

            TimeMarker += phase_length.get(sequence);
        
     }
        
        
        if (Relative1Phase == Relative2Phase) {
            
           if (Relative2PhaseStart < time_yellow.get(Relative2Phase)*0.5 + time_green.get(Relative2Phase)) 
           {
               GreenProportion[0] = Relative2Phase;
               GreenProportion[1] = 1;
               GreenProportion[2] = 0;
               GreenProportion[3] = 0;
           }
           
           else 
           {
           
               GreenProportion[0] = Relative2Phase;
               GreenProportion[1] = (time_yellow.get(Relative2Phase)*0.5 + time_green.get(Relative2Phase) - Relative1PhaseStart)/Params.dt; 
               GreenProportion[2] = 0;
               GreenProportion[3] = 0;
           }           
        }
        
        else {
        
              if (time_yellow.get(Relative1Phase)*0.5 + time_red.get(Relative1Phase) <= Relative1PhaseEnd) {
              
                  GreenProportion[0] = Relative1Phase;
                  GreenProportion[1] = (time_yellow.get(Relative1Phase)*0.5 + time_green.get(Relative1Phase) - Relative1PhaseStart)/Params.dt; 
                  
              }
                  
              else
              {
              
                  GreenProportion[0] = Relative1Phase;
                  GreenProportion[1] = 0.0;
                  
              }
              
              //System.out.println(Relative2Phase);
              
              if (Relative2PhaseStart <= time_yellow.get(Relative2Phase)*0.5 + time_green.get(Relative2Phase)) 
              {
              
                  GreenProportion[2] = Relative2Phase;
                  GreenProportion[3] = Relative2PhaseStart/Params.dt;
              
              }
              else
              {
              
                  GreenProportion[2] = Relative2Phase;
                  GreenProportion[3] = Math.min(time_yellow.get(Relative2Phase)*0.5 + time_green.get(Relative2Phase),1)/Params.dt; 
              
              }
        
        }
        //System.out.println(time_yellow);    
        return GreenProportion;
        
    }
            
            
}