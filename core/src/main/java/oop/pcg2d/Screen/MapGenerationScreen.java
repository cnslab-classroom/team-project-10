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

// 마우스 입력으로 카메라 조정을 위한 import문
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.MathUtils;

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

    // 카메라 변수
    private OrthographicCamera camera;
    private float dragStartX, dragStartY;

    // 드래그 및 줌 이동 속도 조절 상수 (필요에 따라 조정 가능)
    private static final float DRAG_SPEED = 1f;
    private static final float ZOOM_SPEED = 0.08f;

    // Back 버튼을 눌렀을 때, 이전 SelectMap 인스턴스를 저장하는 변수
    private SelectMap selectMapScreen;

    // 생성자 (셀룰러 오토마타 알고리즘용)
    public MapGenerationScreen(App game, SelectMap selectMapScreen, int mapWidth, int mapHeight, long seed,
            double fillProb, boolean isConnected) {
        super(game);
        this.selectMapScreen = selectMapScreen;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.seed = seed;
        this.algorithm = "Cellular Automata";
        this.fillProb = fillProb;
        this.isConnected = isConnected;
        init();
    }

    // 생성자 (Rooms and Mazes 알고리즘용)
    public MapGenerationScreen(App game, SelectMap selectMapScreen, int mapWidth, int mapHeight, long seed,
            int roomMinLen, int roomMaxLen,
            int roomGenAttempt, boolean removeDeadend) {
        super(game);
        this.selectMapScreen = selectMapScreen;
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

        // 화면의 크기를 가져와서 카메라의 뷰포트 크기로 설정
        float w = Gdx.graphics.getWidth(); // 화면의 너비
        float h = Gdx.graphics.getHeight(); // 화면의 높이
        camera = new OrthographicCamera();

        // 카메라의 크기를 화면의 크기로 설정
        camera.setToOrtho(false, w, h);

        // 타일 텍스처 로드
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

        // 카메라의 초기 위치를 맵 중앙으로 설정
        // 카메라의 위치를 맵의 중앙으로 설정
        float tileSize = painter.getTileSize();
        float mapWidth = mapData[0].length * tileSize;
        float mapHeight = mapData.length * tileSize;
        camera.position.set(mapWidth / 2, mapHeight / 2, 0);
        camera.update();
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

        // 뒤로 가기 버튼 생성
        backButton = new TextButton("Back", skin);

        // 버튼에 클릭 리스너 추가
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 기존의 SelectMap 화면으로 다시 돌아가기
                game.setScreen(selectMapScreen);
                dispose(); // 현재 화면 자원 해제
            }
        });

        // 버튼을 스테이지에 추가
        Table table = new Table();
        table.setFillParent(true);
        table.top().right().pad(10); // 버튼 위치 우측으로 변경
        table.add(backButton).width(100).height(50);

        stage.addActor(table);

        // 입력 프로세서 설정
        InputMultiplexer multiplexer = new InputMultiplexer();

        // 스테이지의 입력 프로세서 추가
        multiplexer.addProcessor(stage);

        // 마우스 입력 프로세서 추가
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                dragStartX = screenX;
                dragStartY = screenY;
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                float deltaX = dragStartX - screenX;
                float deltaY = screenY - dragStartY;

                // 줌 레벨에 따라 이동량을 보정 (줌 레벨 곱하기)
                camera.translate(deltaX * camera.zoom * DRAG_SPEED, deltaY * camera.zoom);
                clampCamera();
                camera.update();

                dragStartX = screenX;
                dragStartY = screenY;
                return true;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                // 줌 속도 변경
                float zoomAmount = camera.zoom * ZOOM_SPEED * amountY;
                camera.zoom += zoomAmount;

                // 맵 크기와 카메라 뷰포트 크기를 이용하여 최대 줌 레벨 계산
                float tileSize = painter.getTileSize();
                float mapWidth = mapData[0].length * tileSize;
                float mapHeight = mapData.length * tileSize;

                float viewportWidth = camera.viewportWidth;
                float viewportHeight = camera.viewportHeight;

                // 줌 아웃도 드래그와 마찬가지로 패딩을 줘서 맵의 끝까지 가지 않도록 함
                float paddingX = viewportWidth / 4;
                float paddingY = viewportHeight / 4;

                // 최대 줌 레벨 계산 시 패딩을 고려하도록 함
                float zoomX = (mapWidth + 2 * paddingX) / viewportWidth;
                float zoomY = (mapHeight + 2 * paddingY) / viewportHeight;

                // 최대 줌 레벨은 맵을 화면에 맞추는 데 필요한 최소 값
                float maxZoom = Math.max(zoomX, zoomY);

                // 최소 줌 레벨 설정 (필요에 따라 조정 가능)
                float minZoom = 0.5f;

                // 줌 레벨을 최소 및 최대 줌 레벨 사이로 클램핑
                camera.zoom = MathUtils.clamp(camera.zoom, minZoom, maxZoom);
                camera.update();

                return true;
            }
        });
        // 입력 프로세서 설정
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        // 화면을 지움
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 카메라 업데이트
        camera.update();

        // 배치 카메라 설정
        batch.setProjectionMatrix(camera.combined);

        // 맵 렌더링 코드 유지
        batch.begin();
        painter.draw(mapData, batch, tileTextures, algorithm);
        batch.end();

        // 스테이지를 업데이트하고 그리기
        stage.act(delta);
        stage.draw();
    }

    // 카메라 위치를 맵 내부로 제한하는 메서드임
    private void clampCamera() {
        float cameraX = camera.position.x;
        float cameraY = camera.position.y;

        float viewportWidth = camera.viewportWidth * camera.zoom;
        float viewportHeight = camera.viewportHeight * camera.zoom;

        float tileSize = painter.getTileSize();
        float mapWidth = mapData[0].length * tileSize;
        float mapHeight = mapData.length * tileSize;

        // 여유 범위
        float paddingX = viewportWidth / 4;
        float paddingY = viewportHeight / 4;

        float minX = -paddingX + viewportWidth / 2;
        float maxX = mapWidth + paddingX - viewportWidth / 2;
        float minY = -paddingY + viewportHeight / 2;
        float maxY = mapHeight + paddingY - viewportHeight / 2;

        if (cameraX < minX)
            cameraX = minX;
        if (cameraX > maxX)
            cameraX = maxX;

        if (cameraY < minY)
            cameraY = minY;
        if (cameraY > maxY)
            cameraY = maxY;

        camera.position.set(cameraX, cameraY, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        super.dispose();
        // 리소스 해제
        batch.dispose();
        for (Texture[] texture : tileTextures) {
            for (Texture t : texture) {
                t.dispose();
            }
        }
    }
}
