/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl;

import dnl.link.Link;
import dnl.node.Node;
import dnl.node.Sink;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/**
 * This class represents and stores the entire network, including the sets of nodes and links.
 * Generally, this will be the go-to class for dealing with the network as a whole.
 * The easiest way to construct a new Network is to create an instance of ReadNetwork, then call createNetwork(String name) with the name of the network folder.
 * To calculate the state of the network at the next timestep, call nextTimestep().
 * There are also methods to get the sets of nodes and links, and the specific node or link with a given id.
 * 
 * @author Michael Levin
 */
public class Network 
{
    
    private List<Link> links;
    private List<Node> nodes;
    
    /**
     * Constructs the network with the given nodes and links.
     * In general, the easiest way to construct a Network will be to create a new ReadNetwork instance, then call createNetwork(String name) with the name of the network folder.
     */
    public Network(List<Node> nodes, List<Link> links)
    {
        this.nodes = nodes;
        this.links = links;
    }
    
    
    /** 
     * Returns the set of all nodes
     */
    public List<Node> getNodes()
    {
        return nodes;
    }
    
    /**
     * Returns the set of all links
     */
    public List<Link> getLinks()
    {
        return links;
    }
    
    
    /**
     * Finds the node with the given id. If none exists, returns null.
     */
    public Node getNode(int id)
    {
        for(Node n : nodes)
        {
            if(n.getId() == id)
            {
                return n;
            }
        }
        return null;
    }
    
    
    
    /**
     * Finds the link with the given id. If none exists, returns null.
     */
    public Link getLink(int id)
    {
        for(Link l : links)
        {
            if(l.getId() == id)
            {
                return l;
            }
        }
        return null;
    }
    
    
    /**
     * This method uses simulate() but records the exiting vehicles from each sink node for autograding.
     */
    public double autograde(File solutions) throws IOException
    {
        Scanner filein = new Scanner(solutions);
        
        List<Sink> solOrder = new ArrayList<>();
        
        filein.next();
        
        while(filein.hasNextInt())
        {
            solOrder.add((Sink)getNode(filein.nextInt()));
        }
        
        filein.nextLine();
        
        Params.time = 0;
        
        int count = 0;
        int correct = 0;
        
        try
        {
            while(Params.time <= Params.DURATION)
            {
                nextTimestep();
                
                if(filein.nextInt() != Params.time-Params.dt)
                {
                    throw new RuntimeException("Time mismatch");
                }

                for(Sink s : solOrder)
                {
                    double solution = filein.nextDouble();
                    double exiting = s.getNumExiting();

                    if(solution > 0.1 || exiting > 0.1)
                    {
                        if(Math.abs(solution - exiting) <= 0.01)
                        {
                            correct++;
                        }
                        count++;
                    }
                }
                
                
            }
        }
        catch(NoSuchElementException ex)
        {
            ex.printStackTrace(System.err);
        }
        
        filein.close();
        
        return ((double)correct)/count;
    }
    
    
    public void printAutograde(File output) throws IOException
    {
        PrintStream fileout = new PrintStream(new FileOutputStream(output), true);
        
        fileout.print("time");
        
        List<Sink> sinks = new ArrayList<>();
        
        for(Node n : nodes)
        {
            if(n instanceof Sink)
            {
                sinks.add((Sink)n);
            }
        }
        
        
        for(Sink n : sinks)
        {
            fileout.print("\t"+n.getId());
        }
        fileout.println("\t end");
        
        Params.time = 0;
        
        while(Params.time <= Params.DURATION)
        {
            nextTimestep();
            
            fileout.print(Params.time-Params.dt);
            
            for(Sink n : sinks)
            {
                fileout.print("\t"+n.getNumExiting());
            }
            fileout.println();
        }
        
        fileout.close();
    }
    
    /**
     * This method simulates traffic flow from the current time to Params.duration. 
     * It calls nextTimestep() for each time step.
     */
    public void simulate()
    {
        while(Params.time <= Params.DURATION)
        {
            nextTimestep();
        }
    }
    
    
    
    
    /**
     * This method calculates the state of the network at the next time step. 
     * First, it calls Link.step() and Node.step() on all nodes and links.
     * Then, it calls Link.update() and Node.update() on all nodes and links.
     * Nodes and links should implement step() and update() according to their specific traffic flow behavior.
     */
    public void nextTimestep()
    {
        for(Link l : links)
        {
            l.step();
        }
        
        for(Node n : nodes)
        {
            n.step();
        }
        
        for(Link l : links)
        {
            l.update();
        }
        
        for(Node n : nodes)
        {
            n.update();
        }
        
        
        // update the current time
        Params.time += Params.dt;
        
    }
    
    
    
    /**
     * Runs one-all time-dependent shortest path rooted at the specified origin node, with the given departure time.
     * This method sets the node shortest path labels.
     * To obtain the shortest path, it is necessary to call trace() with the desired origin and destination.
     */
    public void dijkstras(Node origin, int dep_time)
    {
        // initialize all node labels to infinite cost
        for(Node node : nodes)
        {
            node.pred = null;
            // here I'm using Integer.MAX_VALUE as a stand-in for infinity
            node.arr_time = Integer.MAX_VALUE;
        }
        origin.arr_time = dep_time;
        
        // the set of unsettled nodes
        Set<Node> Q = new HashSet<Node>();
        
        Q.add(origin);
        
        
        while(!Q.isEmpty())
        {
            // find node with minimum arrival time
            double best_arr_time = Integer.MAX_VALUE;
            Node u = null;
            
            for(Node node : Q)
            {
                if(node.arr_time < best_arr_time)
                {
                    best_arr_time = node.arr_time;
                    u = node;
                }
            }
            
            // remove u from Q, and search its successor nodes
            Q.remove(u);
            
            for(Link uv : u.getOutgoing())
            {
                // check if v can be reached faster from u
                Node v = uv.getDest();
                double v_arr_time = uv.getTT((int)u.arr_time) + u.arr_time;
                
                if(v_arr_time < v.arr_time)
                {
                    v.arr_time = v_arr_time;
                    Q.add(v);
                }
            }
        }
    }
    
    /**
     * After running dijkstras(), this method is used to find the shortest path between an origin and a destination.
     */
    public Path trace(Node origin, Node dest)
    {
        Node curr = dest;
        
        // the default trace, starting from the destination and moving to the origin, is going to create the list in reverse.
        // We will construct a path as a Link[] later.
        List<Link> reversed = new ArrayList<Link>();
        
        while(curr.pred != null)
        {
            reversed.add(curr.pred);
            curr = curr.pred.getSource();
        }
        
        // now construct the path in correct order
        Path output = new Path();
        
        for(int i = reversed.size()-1; i >= 0; i--)
        {
            output.add(reversed.get(i));
        }
        
        return output;
    }
}
