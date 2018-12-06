/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;


/**
 * This is the Main class. This class contains the entry point for running the project, method main(String[]). 
 * 
 * 
 * @author Michael Levin
 */
public class Main {

    
    public static BufferedWriter writer;
    public static StringBuilder stringer;
    
    public static BufferedWriter writer2;
    public static StringBuilder stringer2;
    
    public static BufferedWriter writer3;
    public static StringBuilder stringer3;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        
        ReadNetwork read = new ReadNetwork();
        Network network = read.createNetwork("CTM1");
        
        String CTMVehicleOutput = "D:\\BSM\\CTMVehicleOutputAll.csv";
        writer = new BufferedWriter(new FileWriter(CTMVehicleOutput));
        stringer = new StringBuilder();
        
        stringer.append("Vehicle ID,Time,X-Coord,Y-Coord,Velocity,Acceleration\n");
        
        writer.write(stringer.toString());     
        
        String CTMCellOutput = "D:\\BSM\\CTMCellOutput.csv";
        writer2 = new BufferedWriter(new FileWriter(CTMCellOutput));
        stringer2 = new StringBuilder();
        
        stringer2.append("Time, Cell #, Inflow, Prev Occ, Outflow (vph), Occupancy (# vehs)\n");
        //Inflow (vph), Previous Occupancy (# vehs), Density (veh/mi)
        writer2.write(stringer2.toString());
        
        String CTMVehicleOutputPartial = "D:\\BSM\\CTMVehicleOutputFiltered.csv";
        writer3 = new BufferedWriter(new FileWriter(CTMVehicleOutputPartial));
        stringer3 = new StringBuilder();
        
        stringer3.append("Vehicle ID,Time,X-Coord,Y-Coord,Velocity,Acceleration\n");
        
        writer3.write(stringer3.toString());     
  
        network.simulate();
        
        writer.close();
        
        writer2.close();
        
        writer3.close();
    }
    
}
