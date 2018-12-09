/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl.link;
import static dnl.Main.stringer;
import static dnl.Main.writer;
import static dnl.Main.stringer2;
import static dnl.Main.writer2;
import static dnl.Main.stringer3;
import static dnl.Main.writer3;
import dnl.Params;
import dnl.Vehicle;
import dnl.link.Link;
import dnl.node.Source;
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
    public int BlockedVeh = 0;
    
    public StringBuilder BSMOutput;
    public StringBuilder BSMOutputFiltered;
    public StringBuilder OccupancyOutput;
    
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
            
            OutFlow =  Math.floor(Math.min(this.getSendingFlow(), next.getReceivingFlow()));      
    //        System.out.print(next.getReceivingFlow());
        }
        if(prev!=null) {
            
            InFlow =  Math.floor(Math.min(prev.getSendingFlow(), this.getReceivingFlow()));
        }
        
//        if(this.CellId == 3) { // to add a bottleneck to cell X for t seconds
//            if(Params.time <= 275 && Params.time >= 245) {
//                OutFlow = 0;
        //        System.out.println("Time: " + Params.time + ", Size: " + this.VehiclesInCell.size() + "\n");
                //System.out.println("During this time, the outFlow is 0 for cell: "+ this.CellId);
//            }
//        }
        
//        
//        if(this.CellId == 4) { // to add a bottleneck to cell X-1 for t seconds
//            if(Params.time <= 275 && Params.time >= 245) {
//                InFlow = 0;
//         //       System.out.println("Time: " + Params.time + ", Size: " + this.VehiclesInCell.size() + "\n");
//                //System.out.println("During this time, the InFlow is 0 for cell: "+ this.CellId);
//            }
//        }        
//        
        if(prev!=null) {
            for(int i = 0; i < Math.floor(InFlow); i++)
            {       
                if(this.prev.VehiclesInCell.isEmpty()) {
                    break;
                }
                else {        
                    Vehicle MoveVehicle = (Vehicle) this.prev.VehiclesInCell.get(i);
                    this.IntercellVehicleInFlow.add(MoveVehicle);      
                }
            }
        }
        
            for(int i = 0; i < Math.floor(OutFlow); i++)
            {
                if(this.VehiclesInCell.isEmpty()) {
                    break;
                }
                else {  
                    Vehicle MoveVehicle = (Vehicle) this.VehiclesInCell.get(i);
                    this.IntercellVehicleOutFlow.add(MoveVehicle);
                }
            }
    }
    
    public void update() throws Exception
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
       
        
  //      if(Params.time == 276 && this.CellId == 4) {
    //        this.BlockedVeh = ((Vehicle) this.IntercellVehicleInFlow.get(0)).getVehId();
//            System.out.println(BlockedVeh.getVehId());
   //         }
        
        this.IntercellVehicleInFlow = new ArrayList();
        this.IntercellVehicleOutFlow = new ArrayList();
        
        int StoredEn = (int) Math.round(en);
                
        en = en + InFlow - OutFlow;
        
        int StoredOutFlow = (int) Math.floor(OutFlow);
        int StoredInFlow = (int) Math.floor(InFlow);

        InFlow = 0;
        OutFlow = 0;    
        
        OccupancyOutput = new StringBuilder();
        int TimeStep = Params.time;
        int CellNumber = this.CellId;
        int LinkNumber = this.link.getId();
        double CellSize = this.link.getFFSpeed()*Params.dt/3600;
        double Occupancy = Math.round(en);
        double CellDensity = (Occupancy/CellSize);
        
        OccupancyOutput.append(TimeStep);
        OccupancyOutput.append(",");
        OccupancyOutput.append(LinkNumber);
        OccupancyOutput.append(",");
        OccupancyOutput.append(CellNumber);
        OccupancyOutput.append(",");
        OccupancyOutput.append(StoredInFlow);
        OccupancyOutput.append(",");
        OccupancyOutput.append(StoredEn);
        OccupancyOutput.append(",");
        OccupancyOutput.append(StoredOutFlow);
        OccupancyOutput.append(",");
        OccupancyOutput.append(Occupancy);
    //    OccupancyOutput.append(",");
  //      OccupancyOutput.append(CellDensity);
        OccupancyOutput.append("\n");
        
        writer2.write(OccupancyOutput.toString()); 
        

        if(this.VehiclesInCell!=null) {

            for (Object car : this.VehiclesInCell) {
                BSMOutput = new StringBuilder();
                BSMOutputFiltered = new StringBuilder();
//                if(Params.time == 6){
       //             System.out.println(this.VehiclesInCell.get(0) + "," + this.CellId);
//                }
                int VehId = ((Vehicle)car).getVehId();
                //System.out.println("Time: "+Params.time + " Vehicle: "+VehId+ " Cell: " + this.CellId);
                if(VehId == 1) {
                    if(next!=null) {
                        StoredOutFlow = Math.min((int)this.VehiclesInCell.size(),(int)this.next.getReceivingFlow());
                        this.OutFlow = StoredOutFlow;
                    }
                    else{
                        StoredOutFlow = (int)this.VehiclesInCell.size();
                        this.OutFlow = StoredOutFlow;
                        }
                    }
                double CurrentCell = 0;
                if(this.link.getId() == 23) {
                    CurrentCell = (this.CellId)*CellSize-CellSize/2 + this.link.getLength();
                }
                else {
                    CurrentCell = (this.CellId)*CellSize-CellSize/2; // this is x-coord        
                            }
                
                
                double YCoord = 0;
                double Speed = (StoredOutFlow*3600/Params.dt)/(this.VehiclesInCell.size()/CellSize);
//                if(Params.time >= 276) {
//                    if(VehId == 77 || VehId == 78) {
//                        Speed = (StoredInFlow*3600/Params.dt)/(this.VehiclesInCell.size()/CellSize);
//                        }
//                }
                String Acceleration = "n/a";
                                                
                BSMOutput.append(VehId);
                BSMOutput.append(",");
                BSMOutput.append(TimeStep);
                BSMOutput.append(",");
                BSMOutput.append(CurrentCell);
                BSMOutput.append(",");
                BSMOutput.append(YCoord);
                BSMOutput.append(",");
                BSMOutput.append(Speed);
                BSMOutput.append(",");
                BSMOutput.append(Acceleration);
                BSMOutput.append("\n");
        
                writer.write(BSMOutput.toString()); 
                
                int RandomizedId = ((Vehicle)car).getVehRandomizedId();
                double PenetrationRate = 65.0; // enter percentage as a whole number not a decimal
                        
                if(RandomizedId <= PenetrationRate) {
                    BSMOutputFiltered.append(VehId);
                    BSMOutputFiltered.append(",");
                    BSMOutputFiltered.append(TimeStep);
                    BSMOutputFiltered.append(",");
                    BSMOutputFiltered.append(CurrentCell);
                    BSMOutputFiltered.append(",");
                    BSMOutputFiltered.append(YCoord);
                    BSMOutputFiltered.append(",");
                    BSMOutputFiltered.append(Speed);
                    BSMOutputFiltered.append(",");
                    BSMOutputFiltered.append(Acceleration);
                    BSMOutputFiltered.append("\n");
                    
                    writer3.write(BSMOutputFiltered.toString());
                }
            }
        }    
    }
    
    public double getSendingFlow()
    {
        return Math.min(Q, this.VehiclesInCell.size());
    }
    
    public double getReceivingFlow()
    {
        int LinkIDE = this.link.getId();
        //System.out.println(Math.min(Q, Math.min(link.getFFSpeed(),link.shockwavespd)/Math.max(link.getFFSpeed(),link.shockwavespd)*(N-this.VehiclesInCell.size())));
   //     if(LinkIDE == 23) {
        
   //         System.out.println(Math.min(Q, Math.min(link.getFFSpeed(),link.shockwavespd)/Math.max(link.getFFSpeed(),link.shockwavespd)*(N-this.VehiclesInCell.size())));
    //    }
        return Math.round(Math.min(Q, Math.min(link.getFFSpeed(),link.shockwavespd)/Math.max(link.getFFSpeed(),link.shockwavespd)*(N-this.VehiclesInCell.size())));  
    }
}