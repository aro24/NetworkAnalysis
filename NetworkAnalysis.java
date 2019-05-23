import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;


public class NetworkAnalysis {
	static Scanner s = new Scanner(System.in);
	static FlowNetwork graph;
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner s = new Scanner(System.in);
		File f = new File(args[0]);
		Scanner sF = new Scanner(f);
		int verticies=sF.nextInt();
		sF.nextLine();
		graph= new FlowNetwork(verticies);
		while(sF.hasNextLine()){
			boolean isCopper;
			int spd;
			String fIn=sF.nextLine();
			String [] edge=fIn.split("\\s+");
			int vertStart=Integer.parseInt(edge[0]);
			int vertEnd = Integer.parseInt(edge[1]);
			if(edge[2].equals("optical")){
				isCopper=false;
			}
			else{
				isCopper=true;
			}
			int bandW=Integer.parseInt(edge[3]);
			int len=Integer.parseInt(edge[4]);
			FlowEdge n = new FlowEdge(vertStart, vertEnd, bandW, len, isCopper);
			graph.addEdge(n);
			FlowEdge nb = new FlowEdge(vertEnd, vertStart, bandW, len, isCopper);
			graph.addEdge(nb);
		}
		while(true){
			System.out.println("\n-------------\nMENU\n"
					+ "1. Find the lowest latency path \n2. Check if copper-only connected \n"
					+ "3. Check max data transfer between vertices \n4. Find the lowest average latency spanning tree \n"
					+ "5. Check vertex for articulation point \n6. Exit");
			int menu=s.nextInt();
			
			if(menu==1){
				lowestLatencyPath();
			}
			else if(menu==2){
				copperConnected();
			}
			else if(menu==3){
				maxBandwidth();
			}
			else if(menu==4){
				spanningTree();
			}
			else if(menu==5){
				findArticulationPoint();
			}
			else if(menu==6){
				break;
			}
			else{
				System.out.println("Invalid input. Please use a valid number.");
			}
		}
	}

	private static void findArticulationPoint() {
		// TODO Auto-generated method stub
		boolean found=false;
	    for(int i=0; i<graph.V(); i++)
	    {
	      for(int j=i+1; j<graph.V(); j++)
	      {
	          CC articulation = new CC(graph, i, j);
	          if(articulation.count()!=1)
	          {
	        	found=true;
	            System.out.println("Removing vertices " + i + " " + j + " will cause a graph failure");
	          }
	      }
	    }
	    if(!found){
	    	System.out.println("No articulation points.");
	    }
	}

	private static void spanningTree() {
		// TODO Auto-generated method stub
	    PrimMST mst = new PrimMST(graph);
	    System.out.println("The lowest average spanning tree contains:");
	    Iterator<FlowEdge> itr = mst.edges().iterator();
	    while(itr.hasNext())
	    {
	      System.out.println(itr.next());
	    }
	}

	private static void maxBandwidth() {
		// TODO Auto-generated method stub
		System.out.println("Enter 1st vertex");
		int vert1=s.nextInt();
		System.out.println("Enter 2nd vertex");
		int vert2=s.nextInt();
		FordFulkerson maxB= new FordFulkerson(graph, vert1, vert2);
		System.out.println("The max bandwidth is: " + maxB.value());
		Iterator<FlowEdge> itr = graph.edges().iterator();
		while(itr.hasNext()){
			FlowEdge fix=itr.next();
			fix.flowToZero();
		}
		
	}

	private static void copperConnected() {
		// TODO Auto-generated method stub
		FlowNetwork graphCpy= new FlowNetwork(graph.V());
		Iterator<FlowEdge> itr =graph.edges().iterator();
		while(itr.hasNext()){
			FlowEdge comp=itr.next();
			if(comp.isCopper()){
				graphCpy.addEdge(comp);
			}
		}
		CC copperOnly=new CC(graphCpy);
		if(copperOnly.count()>1){
			System.out.println("The graph is not copper only connected.");
		}
		else{
			System.out.println("The graph is copper only connected.");
		}
	}

	private static void lowestLatencyPath() {
		// TODO Auto-generated method stub
		System.out.println("Enter 1st vertex");
		int vert1=s.nextInt();
		System.out.println("Enter 2nd vertex");
		int vert2=s.nextInt();
		findLowestLatencyPath(vert1, vert2);
	}


	private static void findLowestLatencyPath(int vert1, int vert2) {
		DijkstraAllPairsSP tree= new DijkstraAllPairsSP(graph);
            if (tree.hasPath(vert1,vert2)||tree.hasPath(vert2, vert1)) {
                System.out.println(vert1+" to "+ vert2+ " \nLatency:"+tree.dist(vert1, vert2));
                System.out.println();
            }
            else {
                System.out.printf("%d to %d         no path\n", vert1, vert2);
            }
            Iterable<FlowEdge> path=tree.path(vert1, vert2);
            Iterator<FlowEdge> itr=path.iterator();

            double bandwidth=Double.POSITIVE_INFINITY;
            while(itr.hasNext()){
            	FlowEdge e=itr.next();
            	System.out.println(e);
            	if(e.capacity()<bandwidth){
            		bandwidth=e.capacity();
            	}
            }
            System.out.println("Bandwidth of path is: "+bandwidth);
		// TODO Auto-generated method stub

	}

}
