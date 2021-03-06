/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.link;

import dnl.Params;
import dnl.Vehicle;
import dnl.node.Node;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



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
            cells[i].setCellId(i+1);
        }
        
        for (int i=0; i<CellNumber; i++) {
            if(i>0) {
                cells[i].setPrevCell(cells[i-1]);
                
            }    
            if(i<CellNumber-1) {
                cells[i].setNextCell(cells[i+1]);
            }
//            :)  :)   :)   :)  :)   :)   :)  :) :)  :)   :)   :)  :)   :)   :)  :)  :)  :)   :)   :)  :)   :)   :)  :)
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
            try {
                section.update();
            } catch (Exception ex) {
                Logger.getLogger(CTM.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public int getSendingFlow()
    {
        // fill this in
        return (int) cells[CellNumber-1].getSendingFlow();
    }
    
    public int getReceivingFlow()
    {
        // fill this in
        return (int) cells[0].getReceivingFlow();
        //:-)
    }
    
    public void addFlow(Vehicle y)
    {
        // fill this in
        cells[0].InFlow += 1;
        y.setVehicleLocation(cells[0]);
        cells[0].VehiclesInCell.add(y);
      //  System.out.println("Add flow    "+y+"   "+getId()+" "+Params.time);
        
    }
    
    public void removeFlow(int y)
    {
        // fill this in
        cells[CellNumber-1].OutFlow += 1;
        for(int i = 0; i < y; i++)
            {
                cells[CellNumber-1].VehiclesInCell.remove(0);
            }
        //System.out.println("Remove flow    "+y+"   "+getId()+" "+Params.time);
        
    }
}

