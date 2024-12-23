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
    private static final int SURVIVAL_THRESHOLD = 4;      // 어떤 타일이 벽 타일이고, 주변 8칸 중에 이만큼 이상 벽 타일이 있으면 이 타일은 벽 타일을 유지
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
        fillBoundary();
        this.mapData = randomFill();
        for (int i = 0; i < SMOOTH_ITERATION_NUM; i++) {
            this.mapData = smoothing();
        }
        if (this.IS_CONNECTED) {
            rooms = identifyAllRooms();
            fillTinyRooms();
            connectAllRooms();
        }
        return mapData;
    }

    private int[][] copyMap() {
        int[][] result = new int[this.MAPHEIGHT][this.MAPWIDTH];
        for (int y = 0; y < MAPHEIGHT; y++) {
            for (int x = 0; x < MAPWIDTH; x++) {
                result[y][x] = this.mapData[y][x];
            }
        }
        return result;
    }

    private boolean isValidCoord(int x, int y) {
        return (x >= 0 && x < this.MAPWIDTH && y >= 0 && y < this.MAPHEIGHT);
    }

    private void fillBoundary() {
        // 맵의 가장자리를 벽으로 채움
        Arrays.fill(this.mapData[0], WALL); // 위쪽 가장자리
        Arrays.fill(this.mapData[MAPHEIGHT-1], WALL); // 아래쪽 가장자리
        for (int y = 1; y < MAPHEIGHT; y++) {
            this.mapData[y][0] = WALL; // 왼쪽 가장자리
            this.mapData[y][MAPWIDTH-1] = WALL; // 오른쪽 가장자리
        }
    }

    private int[][] randomFill() {
        int[][] newMapData = copyMap();
        // 모든 맵의 타일을 순차적으로 탐색해 FILL_PROBABILITY 확률 만큼 벽을 배치 (맵 가장 자리는 )
        for (int y = 1; y <= MAPHEIGHT - 2; y++) {
            for (int x = 1; x <= MAPWIDTH - 2; x++) {
                if (rng.nextDouble() <= this.FILL_PROBABILITY)
                    newMapData[y][x] = WALL;
            }
        }
        return newMapData;
    }

    private int[][] smoothing() {
        // 맵의 모든 타일을 차례대로 순회해 cellular automata 규칙을 적용 시킨 새로운 맵을 생성하고 반환
        int[][] newMapData = copyMap();
    
        for (int y = 1; y <= MAPHEIGHT - 2; y++) {
            for (int x = 1; x <= MAPWIDTH - 2; x++) {
                int wallCount = countAdjacentWalls(x, y); // 주변 벽의 개수를 셈
                if (this.mapData[y][x] == EMPTY && wallCount >= BIRTH_THRESHOLD) newMapData[y][x] = WALL;
                else if (this.mapData[y][x] == WALL && wallCount >= SURVIVAL_THRESHOLD) newMapData[y][x] = WALL;
                else newMapData[y][x] = EMPTY;
            }
        }
        
        return newMapData;
    }
    private int countAdjacentWalls(int x, int y) {
        int count = 0;
        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if ((i != x || j != y) && isValidCoord(i, j)) {
                    if (mapData[j][i] == WALL) // 주변 타일이 벽인 경우
                        count++;
                }
            }
        }
        
        return count; // 주변 벽 타일 개수 반환
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

    private void fillTinyRooms() {
        int minimumSize = Math.round(MAPWIDTH * MAPHEIGHT * 0.025f);
        for (Vector<Pair> room : rooms) {
            if (room.size() < minimumSize) {
                for (Pair cell : room) this.mapData[cell.getY()][cell.getY()] = WALL;
            }
        }
    }

    private void connectAllRooms() {
        while (this.rooms.size() > 1) { // 방이 2개 이상일 때,
            int room1, room2;
            do {
                room1 = rng.nextInt(0, this.rooms.size());
                room2 = rng.nextInt(0, this.rooms.size());
            } while (room1 != room2);
            connectTwoRooms(0, 1); // 방을 그 다음 인덱스의 방과 연결
            this.rooms = identifyAllRooms();
        }
    }

    private void connectTwoRooms(int index1, int index2) {
        // 두개의 방에서 각각 임의의 타일을 선택하고 가중치가 부여된 random walk를 사용
        // 목표 지점으로 향하는 방향에 대해 더 많은 가중치를 부여
        final int LARGE_WEIGHT = 2; // 큰 가중치, 이 숫자만큼 해당 방향의 Direction을 movePool에 추가
        final int SMALL_WEIGHT = 1; // 작은 가중치, 이 숫자만큼 해당 방향의 Direction을 movePool에 추가

        enum Direction {
            NORTH(0, -1),
            SOUTH(0, 1),
            EAST(1, 0),
            WEST(-1, 0);

            public final int X_OFFSET;
            public final int Y_OFFSET;

            Direction(int xOffset, int yOffset) {
                this.X_OFFSET = xOffset;
                this.Y_OFFSET = yOffset;
            }
        }

        LinkedList<String> movePool = new LinkedList<>();

        Vector<Pair> startRoom = this.rooms.get(index1);
        Vector<Pair> endRoom = this.rooms.get(index2);
        Pair cursor = new Pair(startRoom.get(rng.nextInt(0, startRoom.size())));
        Pair destination = new Pair(endRoom.get(rng.nextInt(0, endRoom.size())));

        while (!cursor.equals(destination)) {
            movePool.clear();
            // X축 가중치 계산
            if (cursor.getX() > 0) { // cursor 위치가 서쪽 끝이 아닌 경우
                for (int i = 0; i < SMALL_WEIGHT; i++) movePool.add("WEST");
            }
            if (cursor.getX() < (this.MAPWIDTH - 1)) { // cursor 위치가 동쪽 끝이 아닌 경우
                for (int i = 0; i < SMALL_WEIGHT; i++) movePool.add("EAST");
            }
            if (cursor.getX() < destination.getX()) { // cursor 위치가 목표의 왼쪽인 경우
                for (int i = 0; i < LARGE_WEIGHT; i++) movePool.add("EAST");
            }
            else if (cursor.getX() > destination.getX()) { // cursor 위치가 목표의 오른쪽인 경우
                for (int i = 0; i < LARGE_WEIGHT; i++) movePool.add("WEST");
            }
            // Y축 가중치 계산
            if (cursor.getY() > 0) { // cursor 위치가 북쪽 끝이 아닌 경우
                for (int i = 0; i < SMALL_WEIGHT; i++) movePool.add("NORTH");
            }
            if (cursor.getY() < (this.MAPHEIGHT - 1)) { // cursor 위치가 남쪽 끝이 아닌 경우
                for (int i = 0; i < SMALL_WEIGHT; i++) movePool.add("SOUTH");
            }
            if (cursor.getY() < destination.getY()) { // cursor 위치가 목표의 위쪽인 경우
                for (int i = 0; i < LARGE_WEIGHT; i++) movePool.add("SOUTH");
            }
            else if (cursor.getY() > destination.getY()) { // cursor 위치가 목표의 아래쪽인 경우
                for (int i = 0; i < LARGE_WEIGHT; i++) movePool.add("NORTH");
            }

            int randIndex = rng.nextInt(movePool.size());
            Direction move = Direction.valueOf(movePool.get(randIndex));
            cursor.setX(cursor.getX() + move.X_OFFSET);
            cursor.setY(cursor.getY() + move.Y_OFFSET);
            mapData[cursor.getY()][cursor.getX()] = EMPTY; // cursor의 위치를 빈 타일로 지정
        }
    }

    public void test(int[][] map) {
        this.mapData = map;
        rooms = identifyAllRooms();
        connectAllRooms();

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
        // 빈 타일은 ".", 벽 타일은 "#"로 표현
        System.out.println();
        for (int y = 0; y < MAPHEIGHT; y++) {
            for (int x = 0; x < MAPWIDTH; x++) {
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
}