import java.util.*;

class main{
   public static void main(String[] args){
      Graph graph = new Graph();
      
      Vertex Source;
      Vertex Dest;
      int index;
      Scanner in = new Scanner (System.in);
      graph.graphBuilder();// builds the graph
      int cost;
      if(graph.getSize() > 0){ //checks to make sure something was input into the graph
      
         //this loop gets a source city and a destination city, it then will build 
         //the adjacency list and output the results
         
        while(true){//forever loop to continue finding shortest paths

            RRAdjList AdjList = new RRAdjList(graph);//clears the list each time, this is done for new sources to be input
            System.out.println("Please enter source city: ");
            Source = graph.finder(in.nextLine());//finds the source in the graph
            if (AdjList.findPosition(Source) == -1)//if the source is not in the list then this sets up a condition to reattempt input
                  Source = null;
            if (Source != null){//checks to ensure something was input
               AdjList.dijkstra(Source);//performs the algorithim
               System.out.println("Please enter destination city: ");
               Dest = graph.finder(in.nextLine());
               if (AdjList.findPosition(Dest) != -1){//if the dest is not in the list then this sets up a condition to reattempt input
                  cost = AdjList.dvFind(AdjList.findPosition(Dest));
                  if(cost < 1000000000){//controls for unreachable nodes
                     System.out.println("The cheapest route costs: " + cost);
                     System.out.println("The cheapest route from destination to source is (not including source): ");
                     index = AdjList.findPosition(Dest);//used to access the path
                     while (AdjList.pvFind(index) != null){//this loop traverses the path used
                     
                        System.out.println("" + Dest);
                        Dest = AdjList.pvFind(index);
                        index = AdjList.findPosition(Dest);
                     }
                  }
                  
                  else
                     System.out.println("The destination is impossible to reach");//if dv is infinite then output this
               }
            }   
         }
         
      }
   }
}   

//this class builds the graph (edges and nodes)
//it's only here for personal organizational purposes
class Graph{
   private ArrayList<Vertex> graph = new ArrayList<>();
   private int size;
   
   public void graphBuilder(){
      Vertex curVert;
      boolean flag = true;
      String cityName;
      Scanner in = new Scanner(System.in);
      while (flag){
         System.out.println("Please enter all cities (type 'x' when done)");
         cityName = in.nextLine();
         if (cityName.equals("x")){
            flag = false;
         }
         else 
            graph.add(new Vertex(cityName));           
      }
      Edge temp;
      flag = true;
      int cost;
      for(int i = 0; i < graph.size(); i++){
         while (flag){
            
            System.out.println("Please enter an adjacent city to " + graph.get(i) + " and then it's cost (type 'x' when done)");
            cityName = in.nextLine();
            if (cityName.equals("x"))
               flag = false;
            else{
               System.out.println("Enter cost of travel: ");
               cost = in.nextInt();
               in.nextLine();
               temp = new Edge(finder(cityName), cost);
               if (temp.skip())
                  graph.get(i).addEdge(temp);
            }
         }
         flag = true;
      }
      size = graph.size();
   }
   
   public int getSize(){
      return size;
   }
   
   public Vertex[] getArray(){
      return graph.toArray(new Vertex[size]);

   }
   
   public Vertex finder(String s){//prevents duplicate vertices and finds vertices
      for (int i = 0; i < graph.size(); i++)
         if (graph.get(i).toString().equals(s))
            return graph.get(i);
      System.out.println("City not found plese enter a new city");
      return null;
   }
}


class Vertex{
   private String name;//holds the city name
   private ArrayList<Edge> adjacents = new ArrayList<>();//may need to be array list
   
   public Vertex(){
      name = null;
      adjacents = null;
   }
   
   public Vertex (String n){
      name = n;
   }
   
   public void addEdge (Edge e){
      adjacents.add(e);
   }
   
   public ArrayList<Edge> getEdge(){
      return adjacents;
   }
   
   public int getLowestCost(){//retrieves teh lowest cost edge
      int lowest = 0;
      int i;
      if(adjacents.get(0) != null)
         lowest = adjacents.get(0).getCost();
      for(i = 1; i < adjacents.size();i++)
         if(lowest > adjacents.get(i).getCost())
            lowest = adjacents.get(i).getCost();
      return i;
   }
   
   public int numberEdges(){//retrieves the number of adjacent nodes
      return adjacents.size();
   }
   
   public String toString(){
      return name;
   }
}

class Edge{
   private int cost;
   private Vertex nextVert;
   
   public Edge(Vertex v, int c){
      cost = c;
      nextVert = v;
   }
   
   public int getCost(){//retrieves edge's cost
      return cost;
   }
   
   public Vertex getVert(){//retrieves the vertex that the edge points to
      return nextVert;
   }
   
   public boolean skip(){
      if (nextVert == null)
         return  false;
      else
         return true;
   }
}

class RRAdjList {

   //the following arrays are the adjacency list

   private Vertex[] vertices;
   private boolean[] status;
   private int[] dv;
   private Vertex[] pv;
   
   public RRAdjList(Graph g){//this will build the initial state of the adjacency list
      vertices = g.getArray();
      status = new boolean[g.getSize()];
      for(int i = 0; i < status.length; i++){
         status[i] = false;
      }
      dv = new int[g.getSize()];
      for(int i = 0; i < dv.length; i++){
         dv[i] = 1000000000;
      }
      pv = new Vertex[g.getSize()];
      for(int i = 0; i < pv.length; i++){
         pv[i] = null;
      }
   }
   
   public int dvFind(int i){//returns an indexes dv value
      return dv[i];
   }
   
   public Vertex pvFind(int i){//returns an indexes previous node
      return pv[i];
   }
   
   public void dijkstra(Vertex s){//the dijkstra algorithim
     
     int indexV = findPosition(s);//finds the position that the source holds in the adjacency list
     int indexW = 0;//by default, the first edge
     dv[indexV] = 0;//sets the source dv to 0
     int smallest = findPosition(s);
     if(vertices[indexV].getEdge().size() == 0)//checks to make sure there is an edge in the node
         return;
     for(int i = 0; i < vertices.length; i++){//traverses the entire list if need be
         indexV = smallest;//uses the index that currently has the shortest path
         status[indexV] = true;//sets the list entry's status too visited
         if(!vertices[indexV].getEdge().isEmpty()){//ensures that the node being checked is not empty
            smallest = findPosition(vertices[indexV].getEdge().get(0).getVert());//sets the smallest index to the first edge of the current vertex being checked
            for(int k = 0; k < vertices[indexV].numberEdges(); k++){//will traverse all edges
               indexW = findPosition(vertices[indexV].getEdge().get(k).getVert());//this gets the index of the vertex where the edge (the one being looked at) is pointing
               if(status[indexW] != true){//if the edge has not been looked at then...
                  if(dv[indexV] + vertices[indexV].getEdge().get(k).getCost() < dv[indexW]){//and if the dw is greater than dv + cost of connecting edge, then...
                     dv[indexW] = dv[indexV] + vertices[indexV].getEdge().get(k).getCost();//then change dw to dv + cost of the edge we are looking at
                     pv[indexW] = vertices[indexV];//set the previous node to the vertex at indexV
                     if (dv[indexW] < dv[smallest])//if dw is smaller than the current smallest dv then...
                        smallest = findPosition(vertices[indexW]);//set the smallest index to the index of indexW
                  }
               }
            }
         }
     }
   }
   
   public int findPosition(Vertex v){//this finds an index of a vertex int the adjacency list
      for(int i = 0; i < vertices.length; i++){
         if(vertices[i] == v)
            return i;  //if foudn return the index of the item in the adjacency list
      }
      return -1;//if not found return -1
   }
}