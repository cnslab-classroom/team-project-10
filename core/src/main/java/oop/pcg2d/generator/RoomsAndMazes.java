package oop.pcg2d.generator;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

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


}
