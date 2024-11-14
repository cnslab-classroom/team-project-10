// 테스트 목적용 파일

package oop.pcg2d;

import oop.pcg2d.generator.CellularAutomata;
import oop.pcg2d.generator.RoomsAndMazes;

public class Test {
    public static void main(String[] args) {
        // CellularAutomata gen = new CellularAutomata(100, 100, 0.5, false, 155);
        RoomsAndMazes gen = new RoomsAndMazes(10, 33, 33, 3, 9, 200, false);

        gen.generate();
        gen.testRender();
        gen.testRoom_jinwook();
    }
}