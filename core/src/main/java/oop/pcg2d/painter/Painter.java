package oop.pcg2d.painter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

public class Painter {

    private boolean is_left(int[][] x, int i, int j) {      // 왼쪽에 블록이 있는지 확인
        if (j == 0) {return false;}
        return x[i][j-1] == 1;
    }

    private boolean is_right(int[][] x, int i, int j) {     // 오른쪽에 블록이 있는지 확인
        if (j == x[0].length - 1) {return false;}
        return x[i][j+1] == 1;
    }

    private boolean is_top(int[][] x, int i, int j) {       // 위에 블록이 있는지 확인
        if (i == 0) {return true;}
        return x[i-1][j] == 1;
    }

    private boolean is_bot(int[][] x, int i, int j) {       // 아래에 블록이 있는지 확인
        if (i == x.length - 1) {return false;}
        return x[i+1][j] == 1;
    }

    public void draw(int[][] x, SpriteBatch batch, Texture[] image) {    // 입력받은 2차원 배열에 따라 맵 출력, image는 타일 이미지
        int s = 16;    // 타일 길이

        for(int i = x.length - 1; i >= 0; i--) {
            for(int j = 0; j < x[0].length; j++) {
                if(x[i][j] == 1) {
                    if (is_top(x, i, j)) {
                        if (is_bot(x, i, j)) {
                            if (is_left(x, i, j)) {
                                if (is_right(x, i, j)) {     // 상, 하, 좌, 우 블록 존재할때
                                    batch.draw(image[4], j * s, (x.length - i - 1) * s, s, s);
                                } else {        // 상, 하, 좌 블록 존재할때
                                    batch.draw(image[5], j * s, (x.length - i - 1) * s, s, s);
                                }
                            } else {
                                if (is_right(x, i, j)) {  // 상, 하, 우 블록 존재할때
                                    batch.draw(image[3], j * s, (x.length - i - 1) * s, s, s);
                                } else {   // 상, 하
                                    batch.draw(image[5], j * s, (x.length - i - 1) * s, s, s);
                                }
                            }
                        } else {
                            if (is_left(x, i, j)) {
                                if (is_right(x, i, j)) {      // 상, 좌, 우
                                    batch.draw(image[7], j * s, (x.length - i - 1) * s, s, s);
                                } else {        // 상, 좌
                                    batch.draw(image[8], j * s, (x.length - i - 1) * s, s, s);
                                }
                            } else {
                                if (is_right(x, i, j)) {        // 상, 우
                                    batch.draw(image[6], j * s, (x.length - i - 1) * s, s, s);
                                } else {       // 상
                                    batch.draw(image[7], j * s, (x.length - i - 1) * s, s, s);
                                }
                            }
                        }
                    }
                    else {
                        if (is_bot(x, i, j)) {
                            if (is_left(x, i, j)) {
                                if(is_right(x, i, j)) {     // 하, 좌, 우
                                    batch.draw(image[1], j * s, (x.length - i - 1) * s, s, s);
                                } else {        // 하, 좌
                                    batch.draw(image[2], j * s, (x.length - i - 1) * s, s, s);
                                }
                            }
                            else {
                                if(is_right(x, i, j)) {     // 하, 우
                                    batch.draw(image[0], j * s, (x.length - i - 1) * s, s, s);
                                } else {        // 하
                                    batch.draw(image[1], j * s, (x.length - i - 1) * s, s, s);
                                }
                            }
                        }
                        else {
                            if (is_left(x, i, j)) {
                                if (is_right(x, i, j)) {        // 좌, 우
                                    batch.draw(image[1], j * s, (x.length - i - 1) * s, s, s);
                                } else {        // 좌
                                    batch.draw(image[2], j * s, (x.length - i - 1) * s, s, s);
                                }
                            } else {
                                if (is_right(x, i, j)) {        // 우
                                    batch.draw(image[0], j * s, (x.length - i - 1) * s, s, s);
                                } else {        // 주변에 블록이 없을 때
                                    batch.draw(image[1], j * s, (x.length - i - 1) * s, s, s);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
