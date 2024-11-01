// 테스트 목적용 파일

package oop.pcg2d;

import oop.pcg2d.generator.CellularAutomata;

public class Test {
    public static void main(String[] args) {
        int[][] map = {
            {0,0,1,0,0,0},
            {0,1,0,0,0,0},
            {1,0,0,0,0,0},
            {0,0,0,0,1,1},
            {0,0,0,0,1,0},
        };
        new CellularAutomata(6, 5, 0, false, 0L).test(map);
    }
}
