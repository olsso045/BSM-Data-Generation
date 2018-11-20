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
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        //System.out.println(Autograde.gradeNetwork("unblockedDiverge1"));
        //System.out.println(Autograde.gradeHW(new File("HW6.txt")));
        
        ReadNetwork read = new ReadNetwork();
        Network network = read.createNetwork("CTM1");
        
        String CTMOutput = "C:\\Users\\chen4416.AD\\Desktop\\BSM-Data-Generation-master\\OUTPUT\\CTMOutput.csv";
        writer = new BufferedWriter(new FileWriter(CTMOutput));
        stringer = new StringBuilder();
        
        stringer.append("Vehicle ID,Time,X-Coord,Y-Coord,Velocity,Acceleration\n");
        
        writer.write(stringer.toString());       
        
        network.simulate();
        
        writer.close();
    }
    
}
