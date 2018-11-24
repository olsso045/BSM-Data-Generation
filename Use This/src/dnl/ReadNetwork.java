/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl;

import dnl.link.CTM;
import dnl.link.Link;
import dnl.node.Node;
import dnl.node.Sink;
import dnl.node.Source;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * 
 * @author Michael Levin
 */
public class ReadNetwork 
{
    /**
     * These are Node types. They are not case-sensitive.
     * They correspond to the type field in the nodes.txt input file.
     * They are used to identify the Node subclass that is to be constructed.
     */
    public static final String SOURCE = "source";
    public static final String SINK = "sink";
    
    /**
     * These are Link types. They are not case-sensitive.
     * They are used in the type field in the links.txt input file.
     */
    public static final String CTM = "CTM";  
      
    private Map<Integer, Node> nodesmap;
    private Map<Integer, Link> linksmap;
    
    /**
     * Constructs a new ReadNetwork
     */
    public ReadNetwork()
    {
        
    }
    
    /**
     * Reads in the network with the given directory.
     * For instance, if given the name "2-link", it will read in the network in the "networks/2-link" folder.
     */
    public Network createNetwork(String name) throws IOException
    {
        nodesmap = new TreeMap<>();
        linksmap = new TreeMap<>();
        
        
        readNodes(new File("networks/"+name+"/nodes.txt"));
        readLinks(new File("networks/"+name+"/links.txt"));
        
        
        readTurningProportions(new File("networks/"+name+"/turning_proportions.txt"));
        readDemand(new File("networks/"+name+"/demand.txt"));
        
        
        List<Node> nodes = new ArrayList<>();
        List<Link> links = new ArrayList<>();
        
        
        for(int i : nodesmap.keySet())
        {
            nodes.add(nodesmap.get(i));
        }
        
        for(int i : linksmap.keySet())
        {
            links.add(linksmap.get(i));
        }
        
        nodesmap = null;
        linksmap = null;
        
        return new Network(nodes, links);
    }
    
    /**
     * Constructs a node with the given parameters.
     * Elevation is in ft.
     */
    public Node createNode(int id, String type, double longitude, double latitude, double elevation)
    {
        if(type.equalsIgnoreCase(SOURCE))
        {
            return new Source(id, longitude, latitude, elevation);
        }
        else if(type.equalsIgnoreCase(SINK))
        {
            return new Sink(id, longitude, latitude, elevation);
        }
        else
        {
            throw new RuntimeException("Node type not recognized: "+type);
        }
    }
    
    
    /**
     * Constructs a link with the given parameters.
     * Capacity is in veh/hr.
     * Free flow speed is in mi/hr.
     * Length is in mi.
     */
    public Link createLink(int id, String type, Node source, Node dest, double length, double ffspeed, double capacity, int numLanes)
    {
        if(type.equalsIgnoreCase(CTM))
        {
            return new CTM(id, source, dest, length, ffspeed, capacity, numLanes);
        }
        else
        {
            throw new RuntimeException("Link type ont recognized: "+type);
        }
    }
    
    /**
     * Reads all nodes from the specified file.
     * Header data is ignored.
     */
    public void readNodes(File file) throws IOException
    {
        Scanner filein = new Scanner(file);
        
        while(!filein.hasNextInt() && filein.hasNextLine())
        {
            filein.nextLine();
        }
        
        while(filein.hasNextInt())
        {
            int id = filein.nextInt();
            String type = filein.next();
            double longitude = filein.nextDouble();
            double latitude = filein.nextDouble();
            double elevation = filein.nextDouble();
            
            Node node = createNode(id, type, longitude, latitude, elevation);
            
            nodesmap.put(node.getId(), node);
            
            
            if(filein.hasNextLine())
            {
                filein.nextLine();
            }
        }
        filein.close();
    }
    
    
    /**
     * Reads all links from the specified file.
     * Header data is ignored.
     */
    public void readLinks(File file) throws IOException
    {
        Scanner filein = new Scanner(file);
        
        while(!filein.hasNextInt() && filein.hasNextLine())
        {
            filein.nextLine();
        }
        
        while(filein.hasNextInt())
        {
            int id = filein.nextInt();
            String type = filein.next();
            int source_id = filein.nextInt();
            int dest_id = filein.nextInt();
            double length = filein.nextDouble();
            double ffspd = filein.nextDouble();
            double capacityPerLane = filein.nextDouble();
            int numLanes = filein.nextInt();
            
            Node source = nodesmap.get(source_id);
            Node dest = nodesmap.get(dest_id);
            
            if(source == null)
            {
                throw new RuntimeException("Source node not found: "+source_id);
            }
            
            if(dest == null)
            {
                throw new RuntimeException("Dest node not found: "+dest_id);
            }
            
            Link link = createLink(id, type, source, dest, length, ffspd, capacityPerLane, numLanes);
            
            linksmap.put(link.getId(), link);
            
            
            if(filein.hasNextLine())
            {
                filein.nextLine();
            }
        }
        filein.close();
    }

    /**
     * Reads the demand (incoming vehicles) from the given file
     */
    public void readDemand(File file) throws IOException
    {
        Scanner filein = new Scanner(file);
        
        while(!filein.hasNextInt() && filein.hasNextLine())
        {
            filein.nextLine();
        }
        
        while(filein.hasNextInt())
        {
            int node_id = filein.nextInt();
            double start_time = filein.nextDouble();
            double end_time = filein.nextDouble();
            double rate = filein.nextDouble();
            
            Node node = nodesmap.get(node_id);
            
            if(node == null)
            {
                throw new RuntimeException("Cannot find node "+node_id);
            }
            
            if(!(node instanceof Source))
            {
                throw new RuntimeException("Attempting to add demand to non-source node: "+node_id);
            }
            
            ((Source)node).addDemand(start_time, end_time, rate);
        }
    }
    
    /** 
     * Reads the turning proportions from the given file.
     */
    public void readTurningProportions(File file) throws IOException
    {
        Scanner filein = new Scanner(file);
        
        while(!filein.hasNextInt() && filein.hasNextLine())
        {
            filein.nextLine();
        }
        
        while(filein.hasNextInt())
        {
            int upstream_id = filein.nextInt();
            int downstream_id = filein.nextInt();
            double proportion = filein.nextDouble();
            
            Link upstream = linksmap.get(upstream_id);
            Link downstream = linksmap.get(downstream_id);
            
            if(upstream == null && downstream == null)
            {
                throw new RuntimeException("Cannot find link pair: upstream: "+upstream_id+" downstream: "+downstream_id);
            }
            
            if(downstream != null)
            {
                downstream.getSource().storeTurningProportion(upstream, downstream, proportion);
            }
            else
            {
                upstream.getDest().storeTurningProportion(upstream, downstream, proportion);
            }
            
            if(filein.hasNextLine())
            {
                filein.nextLine();
            }
        }
        filein.close();
    }
    
    /**
     * This method reads the signal cycle data for all nodes.
     * Note that signal cycle data will only be stored for nodes that are actual signals. Otherwise, it will be ignored.
     * This method reads two files: first, it looks at signals.txt to calculate the offset. Then, it looks at phases.txt to read signal phases
     */
  
}
