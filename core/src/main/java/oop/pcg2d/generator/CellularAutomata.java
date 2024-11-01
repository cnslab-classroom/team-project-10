package oop.pcg2d.generator;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import oop.pcg2d.utility.Pair;

public class CellularAutomata {
    private static final int EMPTY = 0;
    private static final int WALL = 1;

    private static final int LOOKED = 1; // use only in identifyRooms()
    private static final int NOT_LOOKED = 0; // use only in identifyRooms()

    private int[][] mapData;

    private final int MAPWIDTH;
    private final int MAPHEIGHT;
    private final double FILL_PROBABILITY;
    private final boolean IS_CONNECTED;
    
    private Vector<Vector<Pair>> rooms; // each room is a Vector containing every Pair of empty tile
                                        // not initialized until identifyAllRooms is called

    public CellularAutomata(int tileWidth, int tileHeight, double fillProb, boolean isConnected) {
        this.MAPWIDTH = tileWidth;
        this.MAPHEIGHT = tileHeight;
        this.FILL_PROBABILITY = fillProb;
        this.IS_CONNECTED = isConnected;
        mapData = new int[this.MAPHEIGHT][this.MAPWIDTH]; // all elements implicitly initialized to zero
    }

    public int[][] generate() {
        // generating algorithm goes here

        if (this.IS_CONNECTED) {
            rooms = identifyAllRooms();
            connectRooms();
        }

        return mapData;
    }

    private boolean isValidCoord(int x, int y) {
        return ( x >= 0 && x < this.MAPWIDTH && y >= 0 && y < this.MAPHEIGHT );
    }

    private void randomFill() {
        // FILL_PROBABILITY를 이용해 맵에 무작위 벽을 배치

    }

    private Vector<Vector<Pair>> identifyAllRooms() {
        Vector<Vector<Pair>> rooms = new Vector<Vector<Pair>>();

        // mapFlag to identify if tiles were visited
        int[][] mapFlag = new int[MAPHEIGHT][MAPWIDTH]; // all elements implicity initilized to NOT_LOOKED

        // checking all tiles in mapData
        for (int x = 0; x < MAPWIDTH; x++) {
            for (int y = 0; y < MAPHEIGHT; y++) {
                if (mapFlag[y][x] == NOT_LOOKED && mapData[y][x] == EMPTY) {
                    Vector<Pair> newRoom = identifySingleRoom(x, y);
                    rooms.add(newRoom);
                    
                    // set all tiles in newRoom in mapFlag to LOOKED
                    for (Pair p : newRoom) {
                        mapFlag[p.getY()][p.getX()] = LOOKED;
                    }
                }
            }
        }

        return rooms;
    }

    private Vector<Pair> identifySingleRoom(int startX, int startY) {
        // precondition: startX and startY is a valid tile coordinate representing an empty space in map

        Vector<Pair> room = new Vector<Pair>();
        // mapFlag to identify if tiles were visited
        int[][] mapFlag = new int[MAPHEIGHT][MAPWIDTH]; // all elements implicity initilized to NOT_LOOKED
        Queue<Pair> q = new LinkedList<Pair>();
        
        q.add(new Pair(startX, startY));
        mapFlag[startY][startX] = LOOKED;

        while (!q.isEmpty()) { // while q is not empty
            Pair tile = q.poll();
            room.add(tile);

            for (int x = tile.getX() - 1; x <= tile.getX() + 1; x++) {
                for (int y = tile.getY() - 1; y <= tile.getY() + 1; y++) {
                    if (isValidCoord(x, y) && (x == tile.getX() || y == tile.getY())) { // x,y 좌표가 올바른 좌표이고 tile의 동서남북 중 하나를 가리킬 때
                        if (mapFlag[y][x] == NOT_LOOKED && mapData[y][x] == EMPTY) { // tile has not been visited and is an empty tile
                            mapFlag[y][x] = LOOKED;
                            q.add(new Pair(x, y));
                        }
                    }
                }
            }
        }

        return room;
    }

    private void connectRooms() {

    }

    
    // used for testing identifyAllRooms method
    public void test(int[][] map) {
        this.mapData = map;
        rooms = identifyAllRooms(); 

        int x = 1;
        for (Vector<Pair> room : rooms) {
            System.out.println("Room #" + String.valueOf(x));
            for (Pair tile : room) {
                System.out.print("(" + String.valueOf(tile.getX()) + "," + String.valueOf(tile.getY()) + "),  ");
            }
            System.out.println();
            x++;
        }
    }
}
