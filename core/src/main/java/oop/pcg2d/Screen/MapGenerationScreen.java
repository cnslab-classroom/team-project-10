package oop.pcg2d.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import oop.pcg2d.generator.CellularAutomata;
import oop.pcg2d.generator.RoomsAndMazes;
import oop.pcg2d.painter.Painter;
import oop.pcg2d.App;

public class MapGenerationScreen extends AbstractScreen {

    private int mapWidth;
    private int mapHeight;
    private long seed;
    private String algorithm;

    // 셀룰러 오토마타 파라미터
    private double fillProb;
    private boolean isConnected;

    // Rooms and Mazes 파라미터
    private int roomMinLen;
    private int roomMaxLen;
    private int roomGenAttempt;
    private boolean removeDeadend;

    private int[][] mapData; // 생성된 맵 데이터

    private SpriteBatch batch;
    private Texture[][] tileTextures;
    private Painter painter;

    // 뒤로가기 버튼 변수
    private TextButton backButton;

    // 생성자 (셀룰러 오토마타 알고리즘용)
    public MapGenerationScreen(App game, int mapWidth, int mapHeight, long seed, double fillProb, boolean isConnected) {
        super(game);
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.seed = seed;
        this.algorithm = "Cellular Automata";
        this.fillProb = fillProb;
        this.isConnected = isConnected;

        init();
    }

    // 생성자 (Rooms and Mazes 알고리즘용)
    public MapGenerationScreen(App game, int mapWidth, int mapHeight, long seed, int roomMinLen, int roomMaxLen,
            int roomGenAttempt, boolean removeDeadend) {
        super(game);
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.seed = seed;
        this.algorithm = "Rooms and Mazes";
        this.roomMinLen = roomMinLen;
        this.roomMaxLen = roomMaxLen;
        this.roomGenAttempt = roomGenAttempt;
        this.removeDeadend = removeDeadend;

        init();
    }

    private void init() {
        batch = new SpriteBatch();

        // 타일 텍스처 로드 (팀원들과 동일한 경로와 이름으로 설정)
        tileTextures = new Texture[2][];
        tileTextures[0] = new Texture[9];
        tileTextures[1] = new Texture[11];
        tileTextures[0][0] = new Texture(Gdx.files.internal("grass_left_top.png"));
        tileTextures[0][1] = new Texture(Gdx.files.internal("grass_mid_top.png"));
        tileTextures[0][2] = new Texture(Gdx.files.internal("grass_right_top.png"));
        tileTextures[0][3] = new Texture(Gdx.files.internal("grass_left_mid.png"));
        tileTextures[0][4] = new Texture(Gdx.files.internal("grass_mid_mid.png"));
        tileTextures[0][5] = new Texture(Gdx.files.internal("grass_right_mid.png"));
        tileTextures[0][6] = new Texture(Gdx.files.internal("grass_left_bot.png"));
        tileTextures[0][7] = new Texture(Gdx.files.internal("grass_mid_bot.png"));
        tileTextures[0][8] = new Texture(Gdx.files.internal("grass_right_bot.png"));
        
        tileTextures[1][0] = new Texture(Gdx.files.internal("stone_left_top.png"));
        tileTextures[1][1] = new Texture(Gdx.files.internal("stone_mid_top.png"));
        tileTextures[1][2] = new Texture(Gdx.files.internal("stone_right_top.png"));
        tileTextures[1][3] = new Texture(Gdx.files.internal("stone_right_mid.png"));
        tileTextures[1][4] = new Texture(Gdx.files.internal("stone_mid_mid.png"));
        tileTextures[1][5] = new Texture(Gdx.files.internal("stone_right_mid.png"));
        tileTextures[1][6] = new Texture(Gdx.files.internal("stone_left_bot.png"));
        tileTextures[1][7] = new Texture(Gdx.files.internal("stone_mid_top.png"));
        tileTextures[1][8] = new Texture(Gdx.files.internal("stone_mid_bot.png"));
        tileTextures[1][9] = new Texture(Gdx.files.internal("stone_void.png"));
        tileTextures[1][10] = new Texture(Gdx.files.internal("stone_front_left.png"));

        painter = new Painter();

        // 맵 생성
        generateMap();
    }

    private void generateMap() {
        if (algorithm.equals("Cellular Automata")) {
            CellularAutomata ca = new CellularAutomata(mapWidth, mapHeight, fillProb, isConnected, seed);
            mapData = ca.generate();
        } else if (algorithm.equals("Rooms and Mazes")) {
            RoomsAndMazes rm = new RoomsAndMazes(seed, mapWidth, mapHeight, roomMinLen, roomMaxLen, roomGenAttempt,
                    removeDeadend);
            mapData = rm.generate();
        }
    }

    @Override
    public void show() {
        super.show();

        // 기존 코드 유지...

        // 뒤로 가기 버튼 생성
        backButton = new TextButton("Back", skin);

        // 버튼에 클릭 리스너 추가
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // SelectMap 화면으로 돌아가기
                game.setScreen(new SelectMap(game));
                dispose(); // 현재 화면 자원 해제
            }
        });

        // 버튼을 스테이지에 추가
        Table table = new Table();
        table.setFillParent(true);
        table.top().left().pad(10);
        table.add(backButton).width(100).height(50);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // 화면을 지움
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 맵 렌더링 코드 유지
        batch.begin();
        painter.draw(mapData, batch, tileTextures, algorithm);
        batch.end();

        // 스테이지를 업데이트하고 그리기
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        // 리소스 해제
        batch.dispose();
        for(Texture[] texture : tileTextures) {
            for(Texture t : texture) {
                t.dispose();
            }
        }
    }
}
