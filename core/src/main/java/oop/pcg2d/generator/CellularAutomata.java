package oop.pcg2d.generator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

import oop.pcg2d.utility.Pair;

public class CellularAutomata {
    // cellular automata 규칙:
    private static final int BIRTH_THRESHOLD = 5;      // 어떤 타일이 빈 타일이고, 주변 8칸 중에 이만큼 이상 벽 타일이 있으면 이 타일은 벽 타일로 바뀜
    private static final int SURVIVAL_THRESHOLD = 4;   // 어떤 타일이 벽 타일이고, 주변 8칸 중에 이만큼 이상 벽 타일이 없으면 이 타일은 빈 타일로 바뀜
    private static final int SMOOTH_ITERATION_NUM = 5; // smoothing 메서드를 호출하는 횟수 (횟수가 많을수록 맵 지형이 매끄러워짐)

    // 타일 타입: 
    private static final int EMPTY = 0; // 맵에서 빈 타일을 의미
    private static final int WALL = 1; // 맵에서 벽 타일을 의미

    private int[][] mapData;

    private final int MAPWIDTH;
    private final int MAPHEIGHT;
    private final double FILL_PROBABILITY;
    private final boolean IS_CONNECTED;

    private final Random rng; // 객체 내부에서의 모든 난수 생성은 이 rng를 사용할 것
    private Vector<Vector<Pair>> rooms; // 각 방은 타일의 좌표를 나타내는 Pair의 Vector이다
                                        // identifyAllRooms이 호출되기 전 까지는 초기화되지 않음

    public CellularAutomata(int tileWidth, int tileHeight, double fillProb, boolean isConnected, long seed) throws IllegalArgumentException {
        this.MAPWIDTH = tileWidth;
        this.MAPHEIGHT = tileHeight;
        if (fillProb < 0.0 || fillProb > 1.0) 
            throw new IllegalArgumentException("Invalid Argument: fillProb must be between 0.0 and 1.0 (0.0 and 1.0 included)");
        this.FILL_PROBABILITY = fillProb;
        this.IS_CONNECTED = isConnected;

        this.rng = new Random(seed);
        mapData = new int[this.MAPHEIGHT][this.MAPWIDTH]; // 모든 원소는 묵시적으로 0으로 초기화 (즉, 맵의 모든 타일이 빈 타일)
    }

    public int[][] generate() {
        this.mapData = randomFill();
        for (int i = 0; i < SMOOTH_ITERATION_NUM; i++) {
            this.mapData = smoothing();
        }
        if (this.IS_CONNECTED) {
            rooms = identifyAllRooms();
            connectAllRooms();
        }
        return mapData;
    }

    private boolean isValidCoord(int x, int y) {
        return ( x >= 0 && x < this.MAPWIDTH && y >= 0 && y < this.MAPHEIGHT );
    }

    private int[][] randomFill() {
        // FILL_PROBABILITY를 이용해 맵에 무작위 벽을 배치한 맵을 생성하고 반환
        int[][] newMapData = new int[this.MAPHEIGHT][this.MAPWIDTH];
        // TODO: 메서드 구현하기
        // ...
        return newMapData;
    }

    private int[][] smoothing() {
        // 맵의 모든 타일을 차례대로 순회해 cellular automata 규칙을 적용 시킨 새로운 맵을 생성하고 반환
        int[][] newMapData = new int[this.MAPHEIGHT][this.MAPWIDTH];
        // TODO: 메서드 구현하기
        // ...
        return newMapData;
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

    private void connectAllRooms() {
        // this.rooms의 모든 방이 연결될 수 있도록 길을 뚫는다
        // this.rooms에서 마지막 방을 제외한 모든 방을 그 다음 차례의 방과 연결한다
        // this.rooms의 마지막 방의 첫번째 방과 연결한다

        for (int i = 0; i < this.rooms.size(); i++) {
            if (i == this.rooms.size() - 1) {
                connectTwoRooms(i, 0); // 마지막 인덱스의 방을 첫 인덱스의 방과 연결
            }
            else {
                connectTwoRooms(i, i + 1); // 방을 그 다음 인덱스의 방과 연결
            }
        }
    }

    private void connectTwoRooms(int index1, int index2) {
        // 두개의 방에서 각각 임의의 타일을 선택하고 가중치가 부여된 random walk를 사용
        // 목표 지점으로 향하는 방향에 대해 더 많은 가중치를 부여
        final int LARGE_WEIGHT = 4;
        final int SMALL_WEIGHT = 1;

        Pair[] directions = {
            new Pair(-1, 0), // 서
            new Pair(1, 0), // 동
            new Pair(0, -1), // 북
            new Pair(-0, 1) // 남
        };
        int[] dirWeight = new int[4];
        int randomNumber = 0;

        Vector<Pair> startRoom = this.rooms.get(index1);
        Vector<Pair> endRoom = this.rooms.get(index2);

        Pair cursor = new Pair(startRoom.get(rng.nextInt(0, startRoom.size())));
        Pair destination = new Pair(endRoom.get(rng.nextInt(0, endRoom.size())));

        while (!cursor.equals(destination)) {
            Arrays.fill(dirWeight, SMALL_WEIGHT);
            if (cursor.getX() < destination.getX()) dirWeight[1] = LARGE_WEIGHT; // 동쪽에 가중치 부여
            else if (cursor.getX() > destination.getX()) dirWeight[0] = LARGE_WEIGHT; // 서쪽에 가중치 부여
            if (cursor.getY() > destination.getY()) dirWeight[2] = LARGE_WEIGHT; // 북쪽에 가중치 부여
            else if (cursor.getY() < destination.getY()) dirWeight[3] = LARGE_WEIGHT; // 남쪽에 가중치 부여
            
            int maxNum = 0;
            int temp = 0;
            for (int i : dirWeight) maxNum+=dirWeight[i];
            randomNumber = rng.nextInt(0, maxNum + 1);
            for (int i = 0; i < 4; i++) {
                temp += dirWeight[i];
                if (randomNumber >= temp) {
                    if (!isValidCoord(cursor.getX() + directions[i].getX(), cursor.getY() + directions[i].getY())) {
                        break; // 유효하지 방향이기 때문에 다시 추첨
                    }
                    else {
                        cursor.setX(cursor.getX() + directions[i].getX());
                        cursor.setY(cursor.getY() + directions[i].getY());
                        mapData[cursor.getY()][cursor.getX()] = EMPTY; // cursor의 위치를 빈 타일로 지정
                        break;
                    }
                    
                }
            }
        }
    }

    public void test(int[][] map) {
    // identifyAllRooms 메소드 테스트 용도
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

    public void testRender() {
        // 현재 this.mapData를 콘솔창에 출력
        // 빈 타일은 " ", 벽 타일은 "█"로 표햔
        System.out.println();
        for (int y = 0; y < MAPHEIGHT; y++) {
            for (int x = 0; x < MAPWIDTH; x++) {
                switch (this.mapData[y][x]) {
                    case EMPTY:
                        System.out.print(" ");
                        break;
                    case WALL:
                        System.out.print("█");
                        break;
                }
            }
            System.out.println();
        }
    }
}
