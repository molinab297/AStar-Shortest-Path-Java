package com.molinab;

import java.util.*;

public class AStar {
    private static final int DIAGONAL_COST = 14;
    private static final int V_H_COST = 10;
    //Blocked cells are just null Cell values in grid
    private static PriorityQueue<Cell> openList;
    private static boolean closedList[][];
    private static int startX, startY, endX, endY, numRows, numColumns;
    private static Cell [][] graph = new Cell[numRows][numColumns];
    
    
    // Represents an individual node on a 2d graph
    private static class Cell{  
        int heuristicCost = 0; //Heuristic cost
        int finalCost = 0; // G+H
        int xPos, yPos;
        Cell parent; 
        
        Cell(int x, int y){
            this.xPos = x;
            this.yPos = y; 
        }
        
        @Override
        public String toString(){
            return "["+this.xPos+", "+this.yPos+"]";
        }
    }
   
    // currently using Manhattan method although likely to change
    private static int CalcHeuristicCost(int x, int y)
    {
        return Math.abs(x-endX)+Math.abs(y-endY);
    }
            
    public static void setBarrier(int x, int y)
    { 
        graph[x][y] = null;
    }
    public static void setStartCell(int x, int y){
        startX = x;
        startY = y;
    }
    public static void setEndCell(int x, int y){
        endX = x;
        endY = y; 
    }
    
    private static void checkAndUpdateCost(Cell current, Cell neighbor, int cost){
        // If neighbor cell is a barrier or is already on the closed list, return
        if(neighbor == null || closedList[neighbor.xPos][neighbor.yPos])
            return;
        
        int finalCost = neighbor.heuristicCost+cost;
        
        boolean inOpenList = openList.contains(neighbor);
        
        // If the neighbor cell is not in the open list or we have found a shorter path to the cell
        if(!inOpenList || finalCost<neighbor.finalCost)
        {
            neighbor.finalCost = finalCost;
            neighbor.parent = current;
            if(!inOpenList)
                openList.add(neighbor);
        }
    }
    

    public static void RunAStar(){ 
        
        //add the start location to open list.
        openList.add(graph[startX][startY]);
        
        Cell current;
        
        while(true){ 
           
            // peek and remove element from priority queue
            current = openList.poll();
      
            // move current node to visited list
            closedList[current.xPos][current.yPos]=true; 

            // if the current Node is the Destination Node, we have found the path
            if(current.equals(graph[endX][endY])){
                return; 
            } 

            // Temp cell to hold neighbor of current cell
            Cell neighbor;
            
            if(current.xPos-1>=0)
            {
                neighbor = graph[current.xPos-1][current.yPos];
                checkAndUpdateCost(current, neighbor, current.finalCost+V_H_COST); 

                if(current.yPos-1>=0){                      
                    neighbor = graph[current.xPos-1][current.yPos-1];
                    checkAndUpdateCost(current, neighbor, current.finalCost+DIAGONAL_COST); 
                }

                if(current.yPos+1<graph[0].length){
                    neighbor = graph[current.xPos-1][current.yPos+1];
                    checkAndUpdateCost(current, neighbor, current.finalCost+DIAGONAL_COST); 
                }
            } 

            if(current.yPos-1>=0){
                neighbor = graph[current.xPos][current.yPos-1];
                checkAndUpdateCost(current, neighbor, current.finalCost+V_H_COST); 
            }

            if(current.yPos+1<graph[0].length){
                neighbor = graph[current.xPos][current.yPos+1];
                checkAndUpdateCost(current, neighbor, current.finalCost+V_H_COST); 
            }

            if(current.xPos+1<graph.length){
                neighbor = graph[current.xPos+1][current.yPos];
                checkAndUpdateCost(current, neighbor, current.finalCost+V_H_COST); 

                if(current.yPos-1>=0){
                    neighbor = graph[current.xPos+1][current.yPos-1];
                    checkAndUpdateCost(current, neighbor, current.finalCost+DIAGONAL_COST); 
                }
                
                if(current.yPos+1<graph[0].length){
                   neighbor = graph[current.xPos+1][current.yPos+1];
                    checkAndUpdateCost(current, neighbor, current.finalCost+DIAGONAL_COST); 
                }  
            }
        } 
    }
    
    /*
    Parameters :
    x, y = Graph dimensions
    startX, startY = start location's x and y coordinates
    endX, endY = end location's x and y coordinates
    int[][] blockedCoordinates = array containing barrier coordinates
    */
    public static void FindPath(int x, int y, int startX, int startY, int endX, 
            int endY, int[][] blockedCoordinates){
           
           numRows = x;
           numColumns = y;
           graph = new Cell[x][y];
           closedList = new boolean[x][y];
           openList = new PriorityQueue<>((Object o1, Object o2) ->{
                Cell c1 = (Cell)o1;
                Cell c2 = (Cell)o2;
                return c1.finalCost<c2.finalCost? -1: 
                        c1.finalCost>c2.finalCost?1:0;
            });
           
 
           
           //Set Start Location & End Location
           setStartCell(startX, startY);
           setEndCell(endX, endY); 
           
           // Initialize and calculate H cost for each cell in graph
           for(int i=0;i<x;++i){
              for(int j=0;j<y;++j){
                  graph[i][j] = new Cell(i, j);
                  graph[i][j].heuristicCost = CalcHeuristicCost(i, j);
              }
           }
           graph[startX][startX].finalCost = 0;
           
           /*
             Set blocked cells. Simply set the cell values to null
             for blocked cells.
           */
           for(int i=0;i<blockedCoordinates.length;++i){
               setBarrier(blockedCoordinates[i][0], blockedCoordinates[i][1]);
           }
           
           //Display initial map
           System.out.println("Grid: ");
            for(int i=0;i<x;++i){
                for(int j=0;j<y;++j){
                   if(i==startX && j==startY)
                       System.out.print("SO  "); //Source
                   else if(i==endX && j==endY)
                       System.out.print("DE  ");  //Destination
                   else if(graph[i][j]!=null)
                       System.out.printf("%-3d ", 0);
                   else
                       System.out.print("BL  "); 
                }
                System.out.println();
            } 
            System.out.println();
           
           RunAStar(); 
           
           // print out map with F scores
           System.out.println("\nScores for cells: ");
           for(int i=0;i<x;++i){
               for(int j=0;j<x;++j){
                   if(graph[i][j]!=null)System.out.printf("%-3d ", graph[i][j].finalCost);
                   else System.out.print("BL  ");
               }
               System.out.println();
           }
           System.out.println();
            
           // Print out path
           if(closedList[endX][endY]){
               //Trace back the path 
                System.out.println("Path: ");
                Cell current = graph[endX][endY];
                System.out.print(current);
                while(current.parent!=null){
                    System.out.print(" -> "+current.parent);
                    current = current.parent;
                } 
                System.out.println();
           }else System.out.println("No possible path");
    }
     
    public static void main(String[] args){   
        
        AStar.FindPath(5, 5, 0, 0, 2, 3, new int[][]{{0,4},{2,2},{3,1},{3,3}}); 
        
    }
}