// 테스트 목적용 파일

package oop.pcg2d;

import java.util.Random;

import oop.pcg2d.generator.CellularAutomata;

public class Test {
    public static void main(String[] args) {
        CellularAutomata gen = new CellularAutomata(100, 100, 0.5, false, 10);
        gen.generate();
        gen.testRender();
    }
}