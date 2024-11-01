// 테스트 목적용 파일

package oop.pcg2d;

import java.util.Random;

import oop.pcg2d.generator.CellularAutomata;

public class Test {
    public static void main(String[] args) {
        int[][] map = {
            {0,1,1,1,1,1},
            {1,1,1,1,1,1},
            {1,1,1,1,1,1},
            {1,1,1,1,1,1},
            {1,1,1,1,1,0},
        };
        long seed = new Random().nextLong();
        CellularAutomata gen = new CellularAutomata(6, 5, 0, false, seed);
        gen.test(map);
        gen.testRender();

    }
}
