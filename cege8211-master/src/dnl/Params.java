/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl;

/**
 * This class stores important global parameters that are useful across the entire project.
 * 
 * @author Michael Levin
 */
public class Params 
{
    // This is the time step, in seconds
    public static int dt = 6;
    
    // This is the current time in the network. It is updated when Network.newTimestep() is called.
    public static int time = 0;
    
    
    // this is the end time of the simulation, in s. The maximum number of time steps is DURATION/dt.
    public static int DURATION = 7200;
    
    // jam density in veh/mi: assume 20ft/vehicle
    public static double VEHICLE_LENGTH = 20;
    public static double JAM_DENSITY = 5280.0/VEHICLE_LENGTH;
}
