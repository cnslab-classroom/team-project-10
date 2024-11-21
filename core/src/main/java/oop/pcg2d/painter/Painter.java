package oop.pcg2d.painter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

public class Painter {
    // MapGenerationScreen에서 사용할 타일 크기
    // 타일 크기 변수
    private int tileSize = 16;
    // 타일 사이즈 getter
    public int getTileSize() {
        return tileSize;
    }
//1
    public void draw(int[][] x, SpriteBatch batch, Texture[][] image, String algorithm) {   // 입력받은 2차원 배열에 따라 맵 출력, image는 타일 이미지
        int s = tileSize;    // 타일 길이 (변수로 변경했어요)
        int asset_n = 2;    // 타일 이미지 번호


        if (algorithm.equals("Cellular Automata") || algorithm.equals("Rooms and Mazes")) {
            for (int i = x.length - 1; i >= 0; i--) {
                for (int j = 0; j < x[0].length; j++) {
                    if (x[i][j] == 1) {
                        if (is_top(x, i, j)) {
                            if (is_bot(x, i, j)) {
                                if (is_left(x, i, j)) {
                                    if (is_right(x, i, j)) { // 상, 하, 좌, 우 블록 존재할때
                                        batch.draw(image[asset_n][4], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 상, 하, 좌 블록 존재할때
                                        batch.draw(image[asset_n][5], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                } else {
                                    if (is_right(x, i, j)) { // 상, 하, 우 블록 존재할때
                                        batch.draw(image[asset_n][3], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 상, 하
                                        batch.draw(image[asset_n][15], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                }
                            } else {
                                if (is_left(x, i, j)) {
                                    if (is_right(x, i, j)) { // 상, 좌, 우
                                        batch.draw(image[asset_n][7], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 상, 좌
                                        batch.draw(image[asset_n][8], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                } else {
                                    if (is_right(x, i, j)) { // 상, 우
                                        batch.draw(image[asset_n][6], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 상
                                        batch.draw(image[asset_n][13], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                }
                            }
                        } else {
                            if (is_bot(x, i, j)) {
                                if (is_left(x, i, j)) {
                                    if (is_right(x, i, j)) { // 하, 좌, 우
                                        batch.draw(image[asset_n][1], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 하, 좌
                                        batch.draw(image[asset_n][2], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                } else {
                                    if (is_right(x, i, j)) { // 하, 우
                                        batch.draw(image[asset_n][0], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 하
                                        batch.draw(image[asset_n][12], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                }
                            } else {
                                if (is_left(x, i, j)) {
                                    if (is_right(x, i, j)) { // 좌, 우
                                        batch.draw(image[asset_n][16], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 좌
                                        batch.draw(image[asset_n][11], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                } else {
                                    if (is_right(x, i, j)) { // 우
                                        batch.draw(image[asset_n][10], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 주변에 블록이 없을 때
                                        batch.draw(image[asset_n][14], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        batch.draw(image[asset_n][9], j * s, (x.length - i - 1) * s, s, s);
                    }
                }
            }
        }

        /*if (algorithm.equals("Rooms and Mazes")) {
            for (int i = x.length - 1; i >= 0; i--) {
                for (int j = 0; j < x[0].length; j++) {
                    if (x[i][j] == 1) {
                        if (i == x.length - 1) {
                            if (j == 0) {
                                batch.draw(image[asset_n][10], j * s, (x.length - i - 1) * s, s, s);
                                continue;
                            } else {
                                batch.draw(image[asset_n][8], j * s, (x.length - i - 1) * s, s, s);
                                continue;
                            }
                        }
                        if (is_top(x, i, j)) {
                            if (is_bot(x, i, j)) {
                                if (is_left(x, i, j)) {
                                    if (is_right(x, i, j)) { // 상, 하, 좌, 우 블록 존재할때
                                        batch.draw(image[asset_n][4], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 상, 하, 좌 블록 존재할때
                                        batch.draw(image[asset_n][2], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                } else {
                                    if (is_right(x, i, j)) { // 상, 하, 우 블록 존재할때
                                        batch.draw(image[asset_n][3], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 상, 하
                                        batch.draw(image[asset_n][5], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                }
                            } else {
                                if (is_left(x, i, j)) {
                                    if (is_right(x, i, j)) { // 상, 좌, 우
                                        batch.draw(image[asset_n][7], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 상, 좌
                                        batch.draw(image[asset_n][7], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                } else {
                                    if (is_right(x, i, j)) { // 상, 우
                                        batch.draw(image[asset_n][6], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 상
                                        batch.draw(image[asset_n][7], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                }
                            }
                        } else {
                            if (is_bot(x, i, j)) {
                                if (is_left(x, i, j)) {
                                    if (is_right(x, i, j)) { // 하, 좌, 우
                                        batch.draw(image[asset_n][4], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 하, 좌
                                        batch.draw(image[asset_n][2], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                } else {
                                    if (is_right(x, i, j)) { // 하, 우
                                        batch.draw(image[asset_n][0], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 하
                                        batch.draw(image[asset_n][2], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                }
                            } else {
                                if (is_left(x, i, j)) {
                                    if (is_right(x, i, j)) { // 좌, 우
                                        batch.draw(image[asset_n][1], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 좌
                                        batch.draw(image[asset_n][2], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                } else {
                                    if (is_right(x, i, j)) { // 우
                                        batch.draw(image[asset_n][0], j * s, (x.length - i - 1) * s, s, s);
                                    } else { // 주변에 블록이 없을 때
                                        batch.draw(image[asset_n][1], j * s, (x.length - i - 1) * s, s, s);
                                    }
                                }
                            }
                        }
                    } else if (x[i][j] == 0) {
                        batch.draw(image[asset_n][9], j * s, (x.length - i - 1) * s, s, s);
                    }
                }
            }
        } */
    }

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
}
