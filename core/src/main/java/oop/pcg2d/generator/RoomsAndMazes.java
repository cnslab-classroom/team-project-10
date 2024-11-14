package oop.pcg2d.generator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.List;

import oop.pcg2d.utility.Pair;
import oop.pcg2d.utility.Rectangle;

public class RoomsAndMazes {
    // 타일 타입: 
    private static final int EMPTY = 0; // 맵에서 빈 타일을 의미
    private static final int WALL = 1; // 맵에서 벽 타일을 의미

    private final int MAPWIDTH;
    private final int MAPHEIGHT;
    private final int ROOM_MIN_LEN; // 어떤 방의 변이 가질 수 있는 최소 길이
    private final int ROOM_MAX_LEN; // 어떤 방의 변이 가질 수 있는 최대 길이
    private final int ROOM_GEN_ATTEMPT; // 방 생성 시도 횟수
    private final boolean REMOVE_DEADEND;
    private int[][] mapData;
    private Vector<Rectangle> roomRects; // 각 방을 Rectangle 객체로 표현
    private Vector<Vector<Pair>> rooms; // 각 방은 타일의 좌표를 나타내는 Pair의 Vector이다
    // identifyAllRooms이 호출되기 전 까지는 초기화되지 않음

    private final Random rng; // 객체 내부에서의 모든 난수 생성은 이 rng를 사용할 것

    public RoomsAndMazes(long seed, int mapWidth, int mapHeight, int roomMinLen, int roomMaxLen, int roomGenAttempt, boolean removeDeadend){
        this.rng = new Random(seed);

        if (mapWidth % 2 == 0 || mapHeight % 2 == 0) {
            throw new IllegalArgumentException("mapWidth and mapHeight should both be odd numbers");
        }
        else {
            this.MAPWIDTH = mapWidth;
            this.MAPHEIGHT = mapHeight;
        }

        if (roomMinLen < 3) {
            throw new IllegalArgumentException("roomMinLen should be more than or equal to 3");
        }
        else {
            this.ROOM_MIN_LEN = roomMinLen;
        }

        if (roomMaxLen < roomMinLen) {
            throw new IllegalArgumentException("roomMaxLen should be more than or equal to roomMinLen");
        }
        else {
            this.ROOM_MAX_LEN = roomMaxLen;
        }

        if (roomGenAttempt < 1) {
            throw new IllegalArgumentException("roomGenAttempt should be more than or equal to 1");
        }
        else {
            this.ROOM_GEN_ATTEMPT = roomGenAttempt;
        }

        this.REMOVE_DEADEND = removeDeadend;

        // 모든 타일을 벽 타일로 초기화
        this.mapData = new int[this.MAPHEIGHT][this.MAPWIDTH];
        for (int[] row : this.mapData) {
            Arrays.fill(row, WALL);
        }

        roomRects = new Vector<>();
    }

    public int[][] generate() {
        // 1. 방 생성 시도 횟수 만큼 방을 랜덤으로 생성
        addRooms();

        // 2. 방을 제외한 곳을 미로로 채움
        for (int y = 1; y < this.MAPHEIGHT - 1; y += 2) {
            for (int x = 1; x < this.MAPWIDTH - 1; x += 2) {
                Pair currentPos = new Pair(x, y);
                if (getTile(currentPos) == WALL && numOrthogonalWall(currentPos) == 4) {
                    growMaze(new Pair(x, y));
                }
            }
        }

        // 3. 모든 공간을 서로 연결
        connectAllRooms();
        // 4. (선택적) 막다른 길 제거

        return this.mapData;
    }

    private void addRooms() {
        // 

        for (int i = 0; i < this.ROOM_GEN_ATTEMPT; i++) {
            // 맵을 벗어나지 않은 방 후보 생성
            // 이때 미로와 올바르게 정렬되려면 다음 조건을 충족해야 함:
            // 1. 방의 변의 길이는 ROOM_MIN_LEN과 ROOM_MAX_LEN 사이의 홀수 숫자로 생성됨
            // 2. newRoomX, newRoomY 또한 홀수 여야 함
            int newRoomWidth = rng.nextInt(this.ROOM_MIN_LEN/2, (this.ROOM_MAX_LEN-1)/2) * 2 + 1;
            int newRoomHeight = rng.nextInt(this.ROOM_MIN_LEN/2, (this.ROOM_MAX_LEN-1)/2) * 2 + 1;
            int newRoomX = rng.nextInt(0, (this.MAPWIDTH - newRoomWidth) / 2) * 2 + 1;
            int newRoomY = rng.nextInt(0, (this.MAPHEIGHT - newRoomHeight) / 2) * 2 + 1;
            Rectangle newRoomRect = new Rectangle(newRoomX, newRoomY, newRoomWidth, newRoomHeight);
            
            // 방 후보가 기존의 방들과 겹치는지 확인
            boolean isOverlap = false;
            for (Rectangle r : roomRects) {
                if (r.isOverlap(newRoomRect)) {
                    isOverlap = true;
                    break;
                }
            }

            // 안 겹치면 방을 맵에 추가
            if (!isOverlap) {
                carveRoom(newRoomRect);
                roomRects.add(newRoomRect);
            }
        }
    }

    private int getTile(Pair pos) {
        return mapData[pos.getY()][pos.getX()];
    }

    private void carvePos(Pair pos) {
        this.mapData[pos.getY()][pos.getX()] = EMPTY;
    }

    private void carveRoom(Rectangle room) {
        for (Pair p : room.getAllPoints()) {
            carvePos(p);
        }
    }

    private boolean isInBound(Pair pos) {
        if (pos.getX() > 0                   &&
            pos.getX() < this.MAPWIDTH - 1   &&
            pos.getY() > 0                   &&
            pos.getY() < this.MAPHEIGHT - 1) return true;
        else return false;
    }

    private int numOrthogonalWall(Pair pos) {
        // pos에 있는 타일의 동,서,남,북에 위치한 타일 중 벽 타일의 개수를 반환
        int count = 0;
        if (getTile(pos.getNorth()) == WALL) count++; // 남
        if (getTile(pos.getSouth()) == WALL) count++; // 북
        if (getTile(pos.getEast()) == WALL) count++; // 동
        if (getTile(pos.getWest()) == WALL) count++; // 서
        return count;
    }

    // private int numDiagonalWall(Pair pos) {
    //     // pos에 있는 타일의 남동,남서,북서,북동에 위치한 타일 중 벽 타일의 개수를 반환
    //     int count = 0;
    //     if (getTile(pos.getNE()) == WALL) count++; // 남동
    //     if (getTile(pos.getNW()) == WALL) count++; // 남서
    //     if (getTile(pos.getSE()) == WALL) count++; // 북동
    //     if (getTile(pos.getSW()) == WALL) count++; // 북서
    //     return count;
    // }

    private void growMaze(Pair startPosition) {
        Vector<Pair> todo = new Vector<>();
        todo.add(startPosition);
        carvePos(startPosition);

        while (!todo.isEmpty()) {
            Pair current = todo.getLast();
            
            // current 타일에서 동서남북에 있는 타일 중 미로를 올바르게 확장할 수 있는 후보 칸을 candidate에 추가
            // 후보 칸은 기존의 미로랑 방과 연결되면 안됨
            Vector<Pair> candidateList = new Vector<>();
            Pair candidate = current.getNorth();
            if (getTile(candidate) == WALL && isInBound(candidate) && numOrthogonalWall(candidate) >= 3) {
                candidateList.add(candidate);
            }
            candidate = current.getSouth();
            if (getTile(candidate) == WALL && isInBound(candidate) && numOrthogonalWall(candidate) >= 3 ) {
                candidateList.add(candidate);
            }
            candidate = current.getEast();
            if (getTile(candidate) == WALL && isInBound(candidate) && numOrthogonalWall(candidate) >= 3) {
                candidateList.add(candidate);
            }
            candidate = current.getWest();
            if (getTile(candidate) == WALL && isInBound(candidate) && numOrthogonalWall(candidate) >= 3) {
                candidateList.add(candidate);
            }

            if (!candidateList.isEmpty()) {
                int randIndex = rng.nextInt(candidateList.size());
                Pair chosen = candidateList.get(randIndex);
                carvePos(chosen);
                // 미로가 각 방들과 올바르게 정렬되기 위해 한번 더 그 방향으로 길을 확장
                chosen = new Pair(chosen.getX() + (chosen.getX() - current.getX()), chosen.getY() + (chosen.getY() - current.getY()));
                carvePos(chosen);
                todo.add(chosen);
            }
            // 후보가 하나도 없으면 current는 막다른 곳이기 때문에 todo에서 삭제
            else {
                current = todo.removeLast();
            }
        }
    }

    public void testRender() {
        // 현재 this.mapData를 콘솔창에 출력
        // 빈 타일은 ".", 벽 타일은 "#"로 표현
        System.out.println();
        for (int y = 0; y < this.MAPHEIGHT; y++) {
            for (int x = 0; x < this.MAPWIDTH; x++) {
                switch (this.mapData[y][x]) {
                    case EMPTY:
                        System.out.print(".");
                        break;
                    case WALL:
                        System.out.print("#");
                        break;
                }
            }
            System.out.println();
        }
    }


    
       // 3. 모든 공간을 서로 연결
    private void connectAllRooms() {
        rooms = identifyAllRooms();
        while (rooms.size() > 1) {
            boolean connected = false;
            for (int i = 1; i<rooms.size();i++) {
                if (areRoomsAdjacent(rooms.get(0), rooms.get(i))) {
                    connectRooms(rooms.get(0), rooms.get(i));
                        rooms = identifyAllRooms();
                        connected = true;
                        break;
                }
            }
            if (!connected) {
                System.out.println("Failed to connect all rooms.");
                break;
            }
        }
    }



    public void test_jinwook() {
        System.out.println();
        for (int y = 0; y < this.MAPHEIGHT; y++) {
            for (int x = 0; x < this.MAPWIDTH; x++) {
                switch (this.mapData[y][x]) {
                    case EMPTY:
                        System.out.print(".");
                        break;
                    case WALL:
                        System.out.print("#");
                        break;
                }
            }
            System.out.println();
        }
    }

    public void testRoom_jinwook() {

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

    // 두 방이 서로 인접한지 확인
    int index = 0;
    public boolean areRoomsAdjacent(Vector<Pair> room1, Vector<Pair> room2) {
        for (Pair coord1 : room1) {
            int x1 = coord1.getX();
            int y1 = coord1.getY();
    
            for (Pair coord2 : room2) {
                int x2 = coord2.getX();
                int y2 = coord2.getY();
    
                // Check if coord2 is one tile away from coord1
                if ((Math.abs(x1 - x2) == 2 && y1 == y2) || (Math.abs(y1 - y2) == 2 && x1 == x2))
                    return true;
            }
        }
        return false;
    }

    // 인접한 방 연결
    public void connectRooms(Vector<Pair> room1, Vector<Pair> room2) {
        Vector<Pair> adjacentCoordinates = new Vector<>();
        
        for (Pair coord1 : room1) {
            int x1 = coord1.getX();
            int y1 = coord1.getY();
    
            for (Pair coord2 : room2) {
                int x2 = coord2.getX();
                int y2 = coord2.getY();
    
                // Check if coord2 is one tile away from coord1
                if ((Math.abs(x1 - x2) == 2 && y1 == y2)) {
                    adjacentCoordinates.add(new Pair((x1+x2)/2, y1));
                }
                else if ((Math.abs(y1 - y2) == 2 && x1 == x2)) {
                    adjacentCoordinates.add(new Pair(x1, (y1+y2)/2));
                }
                    
            }
        }

        if (!adjacentCoordinates.isEmpty()) {
            Pair changeWall = adjacentCoordinates.get(rng.nextInt(adjacentCoordinates.size()));
            mapData[changeWall.getY()][changeWall.getX()] = EMPTY;
            System.out.println(changeWall.getX() + ", " + changeWall.getY() + " 정상화 성공");
        }
        else
            System.out.println(" 정상화 실패");
        
    }


    private Vector<Vector<Pair>> identifyAllRooms() {
        Vector<Vector<Pair>> rooms = new Vector<Vector<Pair>>();
        // visited는 맵의 해당 좌표를 탐색한 적이 있는지 기록하기 위한 배열 맵과 1대1 대응하는 같은 크기의 배열
        boolean[][] visited = new boolean[MAPHEIGHT][MAPWIDTH]; // 모든 원소는 묵시적으로 false로 초기화

        // checking all tiles in mapData
        for (int x = 0; x < MAPWIDTH; x++) {
            for (int y = 0; y < MAPHEIGHT; y++) {


                if (!visited[y][x] && mapData[y][x] == EMPTY) {
                    Vector<Pair> newRoom = identifySingleRoom(x, y);
                    rooms.add(newRoom);
                    
                    // set all tiles in newRoom in mapFlag to LOOKED
                    for (Pair p : newRoom) {
                        visited[p.getY()][p.getX()] = true;
                    }
                }
            }
        }
        return rooms;
    }
    private Vector<Pair> identifySingleRoom(int startX, int startY) {
        // precondition: startX, startY는 맵에서 어떤 빈 타일의 x, y 좌표
        Vector<Pair> room = new Vector<Pair>();
        // visited는 맵의 해당 좌표를 탐색한 적이 있는지 기록하기 위한 배열 맵과 1대1 대응하는 같은 크기의 배열
        boolean[][] visited = new boolean[MAPHEIGHT][MAPWIDTH]; // 모든 원소는 묵시적으로 false로 초기화
        Queue<Pair> q = new LinkedList<Pair>();
        
        q.add(new Pair(startX, startY));
        visited[startY][startX] = true;

        while (!q.isEmpty()) {
            Pair tile = q.poll();
            room.add(tile);

            for (int x = tile.getX() - 1; x <= tile.getX() + 1; x++) {
                for (int y = tile.getY() - 1; y <= tile.getY() + 1; y++) {
                    if (isValidCoord(x, y) && (x == tile.getX() || y == tile.getY())) { // x,y 좌표가 올바른 좌표이고 tile의 동서남북 중 하나를 가리킬 때
                        if (!visited[y][x] && mapData[y][x] == EMPTY) { // tile has not been visited and is an empty tile
                            visited[y][x] = true;
                            q.add(new Pair(x, y));
                        }
                    }
                }
            }
        }
        return room;
    }
    private boolean isValidCoord(int x, int y) {
        return (x >= 0 && x < this.MAPWIDTH && y >= 0 && y < this.MAPHEIGHT);
    }

        // 4. (선택적) 막다른 길 제거



}