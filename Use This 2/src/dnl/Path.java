/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl;

import java.util.ArrayList;
import dnl.link.Link;

/**
 * 
 * @author Michael Levin
 */
public class Path extends ArrayList<Link>
{
    
    // returns the free flow travel time of this path in s
    public double getFFTime()
    {
        double output = 0.0;
        for(Link link : this)
        {
            output += link.getFFTime();
        }
        return output;
    }
    
    // returns the total length of this path in mi
    public double getLength()
    {
        double output = 0.0;
        for(Link link : this)
        {
            output += link.getLength();
        }
        return output;
    }
}
