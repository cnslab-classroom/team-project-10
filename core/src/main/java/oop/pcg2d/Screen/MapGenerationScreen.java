package oop.pcg2d.Screen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import oop.pcg2d.generator.CellularAutomata;
import oop.pcg2d.generator.RoomsAndMazes;
import oop.pcg2d.painter.Painter;
import oop.pcg2d.App;

// 마우스 입력으로 카메라 조정을 위한 import문
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
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
    // 재생성 버튼 변수
    private TextButton regenButton;
    // .txt 파일 저장 버튼 변수
    private TextButton saveTxtButton;
    // .png 파일 저장 버튼 변수
    private TextButton savePngButton;

    // 현재 시드 라벨 변수
    private Label seedLabel;

    // 카메라 변수
    private OrthographicCamera camera;
    private float dragStartX, dragStartY;

    // 드래그 및 줌 이동 속도 조절 상수 (필요에 따라 조정 가능)
    private static final float DRAG_SPEED = 1f;
    private static final float ZOOM_SPEED = 0.08f;

    // Back 버튼을 눌렀을 때, 이전 SelectMap 인스턴스를 저장하는 변수
    private SelectMap selectMapScreen;

    // 타일 테마 변수
    private String tileTheme;

    // png 저장 시 원하는 해상도 배율 설정
    final int scaleFactor = 64;

    // 생성자 (셀룰러 오토마타 알고리즘용)
    public MapGenerationScreen(App game, SelectMap selectMapScreen, int mapWidth, int mapHeight, long seed,
            double fillProb, boolean isConnected, String tileTheme) {
        super(game);
        this.selectMapScreen = selectMapScreen;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.seed = seed;
        this.algorithm = "Cellular Automata";
        this.fillProb = fillProb;
        this.isConnected = isConnected;
        this.tileTheme = tileTheme;
        init();
    }

    // 생성자 (Rooms and Mazes 알고리즘용)
    public MapGenerationScreen(App game, SelectMap selectMapScreen, int mapWidth, int mapHeight, long seed,
            int roomMinLen, int roomMaxLen,
            int roomGenAttempt, boolean removeDeadend, String tileTheme) {
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
        this.tileTheme = tileTheme;
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
        tileTextures = new Texture[6][];
        tileTextures[0] = new Texture[17];
        tileTextures[1] = new Texture[11];
        tileTextures[2] = new Texture[17];
        tileTextures[0][0] = new Texture(Gdx.files.internal("grass_left_top.png"));
        tileTextures[0][1] = new Texture(Gdx.files.internal("grass_mid_top.png"));
        tileTextures[0][2] = new Texture(Gdx.files.internal("grass_right_top.png"));
        tileTextures[0][3] = new Texture(Gdx.files.internal("grass_left_mid.png"));
        tileTextures[0][4] = new Texture(Gdx.files.internal("grass_mid_mid.png"));
        tileTextures[0][5] = new Texture(Gdx.files.internal("grass_right_mid.png"));
        tileTextures[0][6] = new Texture(Gdx.files.internal("grass_left_bot.png"));
        tileTextures[0][7] = new Texture(Gdx.files.internal("grass_mid_bot.png"));
        tileTextures[0][8] = new Texture(Gdx.files.internal("grass_right_bot.png"));
        tileTextures[0][9] = new Texture(Gdx.files.internal("grass_void.png"));
        tileTextures[0][10] = new Texture(Gdx.files.internal("grass_left.png"));
        tileTextures[0][11] = new Texture(Gdx.files.internal("grass_right.png"));
        tileTextures[0][12] = new Texture(Gdx.files.internal("grass_top.png"));
        tileTextures[0][13] = new Texture(Gdx.files.internal("grass_bot.png"));
        tileTextures[0][14] = new Texture(Gdx.files.internal("grass_center.png"));
        tileTextures[0][15] = new Texture(Gdx.files.internal("grass_left_right.png"));
        tileTextures[0][16] = new Texture(Gdx.files.internal("grass_top_bot.png"));

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

        tileTextures[2][0] = new Texture(Gdx.files.internal("lava_left_top.png"));
        tileTextures[2][1] = new Texture(Gdx.files.internal("lava_mid_top.png"));
        tileTextures[2][2] = new Texture(Gdx.files.internal("lava_right_top.png"));
        tileTextures[2][3] = new Texture(Gdx.files.internal("lava_left_mid.png"));
        tileTextures[2][4] = new Texture(Gdx.files.internal("lava_mid_mid.png"));
        tileTextures[2][5] = new Texture(Gdx.files.internal("lava_right_mid.png"));
        tileTextures[2][6] = new Texture(Gdx.files.internal("lava_left_bot.png"));
        tileTextures[2][7] = new Texture(Gdx.files.internal("lava_mid_bot.png"));
        tileTextures[2][8] = new Texture(Gdx.files.internal("lava_right_bot.png"));
        tileTextures[2][9] = new Texture(Gdx.files.internal("lava_void.png"));
        tileTextures[2][10] = new Texture(Gdx.files.internal("lava_left.png"));
        tileTextures[2][11] = new Texture(Gdx.files.internal("lava_right.png"));
        tileTextures[2][12] = new Texture(Gdx.files.internal("lava_top.png"));
        tileTextures[2][13] = new Texture(Gdx.files.internal("lava_bot.png"));
        tileTextures[2][14] = new Texture(Gdx.files.internal("lava_center.png"));
        tileTextures[2][15] = new Texture(Gdx.files.internal("lava_left_right.png"));
        tileTextures[2][16] = new Texture(Gdx.files.internal("lava_top_bot.png"));

        painter = new Painter(tileTheme);

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

    private void newRandomSeed() {
        this.seed = new Random().nextLong();
    }

    @Override
    public void show() {
        super.show();
        // 뒤로 가기 버튼 생성
        backButton = new TextButton("Back", skin);
        // 재생성 버튼 생성
        regenButton = new TextButton("Regenerate", skin);
        // 각 저장 버튼 생성
        saveTxtButton = new TextButton("Save As TXT", skin);
        savePngButton = new TextButton("Save As PNG", skin);
        // 현재 시드 라벨 생성
        seedLabel = new Label("Current Seed: " + this.seed, skin);

        // 버튼에 클릭 리스너 추가
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 기존의 SelectMap 화면으로 다시 돌아가기
                game.setScreen(selectMapScreen);
            }
        });
        regenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 기존 맵 생성 인자에 seed만 랜덤으로 바꿔 맵 재생성
                newRandomSeed();
                generateMap();
            }
        });

        // 각 저장 버튼에 클릭 이벤트 리스너 추가
        saveTxtButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveAsTxt();
            }
        });
        savePngButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // png 파일 저장 기능 구현 필요!
                saveAsPng();
            }
        });
        

        // 테이블을 여러 개로 나누어서 요소를 배치했습니다!
        // 상단 왼쪽 테이블 생성
        Table topLeftTable = new Table();
        topLeftTable.top().left().pad(10);
        topLeftTable.setFillParent(true);
        topLeftTable.add(seedLabel).left();

        // 상단 오른쪽 테이블 생성
        Table topRightTable = new Table();
        topRightTable.top().right().pad(10);
        topRightTable.setFillParent(true);
        topRightTable.add(regenButton).width(120).height(50).space(10);
        topRightTable.add(backButton).width(120).height(50).space(10);

        // 하단 오른쪽 테이블 생성
        Table bottomRightTable = new Table();
        bottomRightTable.bottom().right().pad(10);
        bottomRightTable.setFillParent(true);
        bottomRightTable.add(saveTxtButton).width(120).height(50).space(10);
        bottomRightTable.add(savePngButton).width(120).height(50).space(10);

        // 스테이지에 테이블 추가
        stage.addActor(topLeftTable);
        stage.addActor(topRightTable);
        stage.addActor(bottomRightTable);

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

        // 시드 라벨 업데이트
        seedLabel.setText("Current Seed: " + String.valueOf(this.seed));

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

    private void saveAsTxt() {
        // swing GUI를 OS 비주얼에 맞게 설정
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        }
        catch (Exception e) {
            Gdx.app.log(App.LOG, "Error: " + e.getMessage());
        }
        JFrame window = new JFrame();

        // 파일 선택 창 생성
        JFileChooser saveFileChooser = new JFileChooser();
        saveFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // 선택한 디렉토리에 새로운 파일 생성
        int result = saveFileChooser.showSaveDialog(window);
        if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedDirectory = saveFileChooser.getSelectedFile();
            String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmmss"));

            // 현재 시간을 이름으로 가진 새로운 .txt 파일 생성
            File saveFile = new File(selectedDirectory.getAbsolutePath() + File.separator + timeNow + ".txt");
            try {
                saveFile.createNewFile();
            }
            catch (Exception e) {
                Gdx.app.log(App.LOG, "Error: " + e.getMessage());
                JOptionPane.showMessageDialog(window, "Error: " + e.getMessage());
            }

            // 파일에 맵 데이터 저장
            try {
                FileWriter saveFileWriter = new FileWriter(saveFile);
                for (int y = 0; y < mapHeight; y++) {
                    for (int x = 0; x < mapWidth; x++) {
                        saveFileWriter.write(String.valueOf(mapData[y][x]));
                    }
                    saveFileWriter.write("\n");
                }
                saveFileWriter.close();
            }
            catch (IOException e) {
                Gdx.app.log(App.LOG, "Error: " + e.getMessage());
            }
        }
    }

    private void saveAsPng() {
        // Swing GUI를 OS 비주얼에 맞게 설정
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            Gdx.app.log(App.LOG, "Error setting Look and Feel: " + e.getMessage());
        }
    
        // 파일 선택 창 생성
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a location to save the PNG file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    
        // 선택한 디렉토리에 새로운 파일 생성
        // 현재 시간을 이름으로 가진 새로운 .png 파일 생성
        String defaultFileName = "map_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss")) + ".png";
        fileChooser.setSelectedFile(new File(defaultFileName));
    
        // 사용자 입력 처리
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String savePath = fileToSave.getAbsolutePath();
            // 파일 확장자 추가 (필요 시)
            if (!savePath.endsWith(".png")) {
                savePath += ".png";
            }

            // PNG 저장 작업
            int highResWidth = mapWidth * scaleFactor;
            int highResHeight = mapHeight * scaleFactor;
            FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, highResWidth, highResHeight, false);
    
            OrthographicCamera highResCamera = new OrthographicCamera();
            float tileSize = painter.getTileSize();
            float worldWidth = mapWidth * tileSize;
            float worldHeight = mapHeight * tileSize;
    
            highResCamera.setToOrtho(false, worldWidth, worldHeight);
            highResCamera.position.set(worldWidth / 2, worldHeight / 2, 0);
            highResCamera.update();
    
            frameBuffer.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
            batch.setProjectionMatrix(highResCamera.combined);
            batch.begin();
            painter.draw(mapData, batch, tileTextures, algorithm);
            batch.end();
    
            Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, highResWidth, highResHeight);
            frameBuffer.end();
    
            // Pixmap을 PNG로 저장
            FileHandle fileHandle = Gdx.files.absolute(savePath);
            PixmapIO.writePNG(fileHandle, pixmap);
    
            // 리소스 해제
            pixmap.dispose();
            frameBuffer.dispose();
        }
    }
}
