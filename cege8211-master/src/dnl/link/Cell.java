/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.link;
import static dnl.Main.stringer;
import static dnl.Main.writer;
import dnl.Params;
import dnl.Vehicle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
        
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
    private int CellId;
    private double Q;
    private double N;
    private double en = 0.0;
    public double InFlow = 0.0;
    public double OutFlow = 0.0;
    
    public StringBuilder BSMOutput;
    
    public List VehiclesInCell;
    public List IntercellVehicleInFlow; //vehicles that will move to next cell (used so that cells can reference previous cell's list of vehicles
    public List IntercellVehicleOutFlow; //vehicles that will move to next cell (used so that cells can reference previous cell's list of vehicles    
    
    
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
        
        this.VehiclesInCell = new ArrayList();
        this.IntercellVehicleInFlow = new ArrayList();
        this.IntercellVehicleOutFlow = new ArrayList();
        //      
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
    
    public void setCellId(int id)
    {
        this.CellId = id;
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
        
        if(prev!=null) {
            for(int i = 0; i < Math.floor(InFlow); i++)
        {
                Vehicle MoveVehicle = (Vehicle) this.prev.VehiclesInCell.get(0);
                this.IntercellVehicleInFlow.add(MoveVehicle);       
        }
        }
        
        for(int i = 0; i < Math.floor(OutFlow); i++)
        {
            Vehicle MoveVehicle = (Vehicle) this.VehiclesInCell.get(0);
            this.IntercellVehicleOutFlow.add(MoveVehicle);
        }
        
    }
    
    public void update() throws IOException
    {
        // fill this in
        
        for(int i = 0; i < IntercellVehicleInFlow.size(); i++)
        {
            Vehicle MoveVehicle = (Vehicle) this.IntercellVehicleInFlow.get(i);
            MoveVehicle.setVehicleLocation(this);
            this.VehiclesInCell.add(MoveVehicle); 
        }
        
        for(int i = 0; i < IntercellVehicleOutFlow.size(); i++)
        {
            Vehicle MoveVehicle = (Vehicle) this.IntercellVehicleOutFlow.get(i);
            this.VehiclesInCell.remove(MoveVehicle);
        }
        
        this.IntercellVehicleInFlow = new ArrayList();
        this.IntercellVehicleOutFlow = new ArrayList();    
        
        en = en + InFlow - OutFlow;
        
        int StoredOutFlow = (int) Math.floor(OutFlow);
        InFlow = 0;
        OutFlow = 0;
        

        if(this.VehiclesInCell!=null) {
            BSMOutput = new StringBuilder();
            for (Object car : this.VehiclesInCell) {
                int VehId = ((Vehicle)car).getVehId();
                int TimeStep = Params.time;
                double CellSize = this.link.getFFSpeed()*Params.dt/3600;
                double CurrentCell = (this.CellId)*CellSize-CellSize/2; // this is x-coord
                double YCoord = 0;
                double Speed = (StoredOutFlow*3600/Params.dt)/(this.VehiclesInCell.size()/CellSize);
                String Acceleration = "n/a";              
                
                BSMOutput.append(Integer.toString(VehId));
                BSMOutput.append(",");
                BSMOutput.append(Integer.toString(TimeStep));
                BSMOutput.append(",");
                BSMOutput.append(Double.toString(CurrentCell));
                BSMOutput.append(",");
                BSMOutput.append(Double.toString(YCoord));
                BSMOutput.append(",");
                BSMOutput.append(Double.toString(Speed));
                BSMOutput.append(",");
                BSMOutput.append(Acceleration);
                BSMOutput.append("\n");
        
                writer.write(BSMOutput.toString());  
            }
        }
        
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
