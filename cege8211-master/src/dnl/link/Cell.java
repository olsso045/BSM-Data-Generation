/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.link;
import dnl.Params;
        
/**
 * This class represents a single cell in the cell transmission model.
 * It should be constructed as needed by class CTM.
 * 
 * @author 
 */
public class Cell 
{
    /**
     * This is the CTM link that this cell belongs to.
     * Having a reference is useful...
     */
    private CTM link;
    private double Q;
    private double N;
    private double en = 0.0;
    public double InFlow = 0.0;
    public double OutFlow = 0.0;
        
    
    
    
    /**
     * Having references to the previous and next cell is probably useful for propagating flow.
     * These are initially null! You have to set these references in class CTM AFTER all the cells have been constructed.
     * Use setNextCell(), setPrevCell() to update these references.
     */
    private Cell prev, next;
    
    /**
     * Constructs a Cell as part of the given CTM link.
     * You may want to add additional parameters here.
     */
    
    public Cell(CTM link)
    {
        this.link = link;
        this.Q = this.link.getCapacity()*Params.dt/3600;
        this.N = this.link.getNumLanes()*Params.JAM_DENSITY*this.link.getFFSpeed()*Params.dt/3600;
        //:-)
        //System.out.println("Máximo número de carros es    "+N);        
    }
    
    /**
     * Update the reference to the next (downstream) cell.
     */
    public void setNextCell(Cell next)
    {
        this.next = next;
    }
    
    /**
     * Update the reference to the previous (upstream) cell.
     */
    public void setPrevCell(Cell prev)
    {
        this.prev = prev;
    }
    
    
    
    public void step()
    {
        // fill this in
        
        if(next!=null) {
            OutFlow = Math.min(this.getSendingFlow(), next.getReceivingFlow());        
        }
        if(prev!=null) {
            InFlow = Math.min(prev.getSendingFlow(), this.getReceivingFlow());
        }
        //System.out.println("Número de carros    "+en);    
        
    }
    
    public void update()
    {
        // fill this in
        
        en = en + InFlow - OutFlow;
        
        InFlow = 0;
        OutFlow = 0;
        
                
        
    }
    
    public double getSendingFlow()
    {
        // fill this in
        
        return Math.min(Q, en);
    }
    
    public double getReceivingFlow()
    {
        // fill this in
        
        return Math.min(Q, Math.min(link.getFFSpeed(),link.shockwavespd)/Math.max(link.getFFSpeed(),link.shockwavespd)*(N-en));
        
    }
}
