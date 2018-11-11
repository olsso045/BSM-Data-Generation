/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.link;

import dnl.Params;
import dnl.node.Node;



/**
 * This class propagates flow according to the cell transmission model.
 * 
 * @author 
 */
public class CTM extends Link
{
    /**
     * This is the array of cells used to model this link
     * This array has NOT been instantiated! You need to do that.
     */
    private Cell[] cells;
    private double CellSize;
    private int CellNumber;
    private double ffspd;
    private double length;
    public double shockwavespd;
    
    public CTM(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
    {
        super(id, source, dest, length, ffspd, capacityPerLane, numLanes);
        
        this.ffspd = ffspd;
        this.length = length;
        
        this.shockwavespd = this.ffspd;
        
        double qualifier = Math.max(this.ffspd, this.shockwavespd);
                
        CellSize = qualifier*Params.dt/3600;
        CellNumber = (int) Math.round(this.length/CellSize);
        
        cells = new Cell[CellNumber];
      
        for (int i=0; i<CellNumber; i++) {
            cells[i] = new Cell(this);
        }
        
        for (int i=0; i<CellNumber; i++) {
            if(i>0) {
                cells[i].setPrevCell(cells[i-1]);
                
            }    
            if(i<CellNumber-1) {
                cells[i].setNextCell(cells[i+1]);
            }
//            :)
        }
        
    }
    
    
    public void step()
    {
        // fill this in
        for(Cell section : cells){
            section.step();
        }
        
    }
    
    public void update()
    {
        // fill this in
        for(Cell section : cells) {
            section.update();
        }
    }
    
    public double getSendingFlow()
    {
        // fill this in
        return cells[CellNumber-1].getSendingFlow();
    }
    
    public double getReceivingFlow()
    {
        // fill this in
        return cells[0].getReceivingFlow();
        //:-)
    }
    
    public void addFlow(double y)
    {
        // fill this in
        
        cells[0].InFlow = y;
        System.out.println("Add flow    "+y+"   "+getId()+" "+Params.time);
        
    }
    
    public void removeFlow(double y)
    {
        // fill this in
        
        cells[CellNumber-1].OutFlow = y;
        System.out.println("Remove flow    "+y+"   "+getId()+" "+Params.time);
        
    }
}
