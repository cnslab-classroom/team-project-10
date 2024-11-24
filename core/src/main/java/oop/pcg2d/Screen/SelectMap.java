package oop.pcg2d.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import oop.pcg2d.App;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.Random;

public class SelectMap extends AbstractScreen {

    // 알고리즘 선택 버튼 관련 변수들
    private String[] algorithms = { "Cellular Automata", "Rooms and Mazes" };
    private int algorithmIndex = 0;
    private TextButton algorithmButton;

    // 공통 파라미터
    private TextField widthTextField;
    private TextField heightTextField;
    private TextField seedTextField;
    private Label widthLabel;
    private Label heightLabel;
    private Label seedOptionalLabel;

    // 타일 테마 선택
    private String[] tileThemes = { "Grass", "Stone", "Lava" };
    private ButtonGroup<CheckBox> tileThemeGroup;

    // 알고리즘별 파라미터
    private Slider fillProbSlider;
    private TextButton connectRoomsButton;
    private boolean isConnected = true; // 기본값을 true로 설정

    private TextField roomMinLenTextField;
    private TextField roomMaxLenTextField;
    private Slider roomGenAttemptSlider;
    private TextButton removeDeadendButton;
    private boolean removeDeadend = false; // 기본값 설정

    // 알고리즘별 파라미터를 담는 테이블
    private Table algorithmParamsTable;

    // 배경 이미지 텍스처
    private Image background;

    // 생성자
    public SelectMap(App game) {
        super(game);
        initUI();
    }

    private void initUI() {

        // 스킨에서 'dirt' 드로어블 가져오기
        Drawable dirtDrawable = skin.getDrawable("dirt");

        // 'dirt' 드로어블을 TiledDrawable로 변환
        TiledDrawable backgroundTiledDrawable = new TiledDrawable((TextureRegionDrawable) dirtDrawable);

        // TiledDrawable로 Image 생성
        background = new Image(backgroundTiledDrawable);
        background.setFillParent(true);
        stage.addActor(background);

        // 메인 테이블 생성 및 설정
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center().pad(10); // 전체 패딩 추가
        stage.addActor(mainTable);

        // 화면 제목 추가
        Label titleLabel = new Label("Create New Map", skin, "bold");
        mainTable.add(titleLabel).colspan(2).center().padBottom(20);
        mainTable.row();

        // 타일 테마 선택 부분
        Label tileThemeLabel = new Label("<Select a Tile Theme>", skin);
        tileThemeGroup = new ButtonGroup<>();
        tileThemeGroup.setMaxCheckCount(1);
        tileThemeGroup.setMinCheckCount(1);
        tileThemeGroup.setUncheckLast(true);

        // 타일 테마 체크박스 스타일 (갑옷 아이콘)생성
        CheckBox.CheckBoxStyle tileThemeCheckBoxStyle = new CheckBox.CheckBoxStyle();
        tileThemeCheckBoxStyle.checkboxOn = skin.getDrawable("armor");
        tileThemeCheckBoxStyle.checkboxOff = skin.getDrawable("armor-bg");
        tileThemeCheckBoxStyle.font = skin.getFont("font");

        // (갑옷 아이콘) 드로어블 크기 조정
        float drawableScale = 2.5f; // 크기 배율 조절
        tileThemeCheckBoxStyle.checkboxOn.setMinWidth(tileThemeCheckBoxStyle.checkboxOn.getMinWidth() * drawableScale);
        tileThemeCheckBoxStyle.checkboxOn
                .setMinHeight(tileThemeCheckBoxStyle.checkboxOn.getMinHeight() * drawableScale);
        tileThemeCheckBoxStyle.checkboxOff
                .setMinWidth(tileThemeCheckBoxStyle.checkboxOff.getMinWidth() * drawableScale);
        tileThemeCheckBoxStyle.checkboxOff
                .setMinHeight(tileThemeCheckBoxStyle.checkboxOff.getMinHeight() * drawableScale);

        Table tileThemeTable = new Table();
        for (String theme : tileThemes) {
            CheckBox checkBox = new CheckBox(theme, tileThemeCheckBoxStyle);
            tileThemeGroup.add(checkBox);
            tileThemeTable.add(checkBox).left().padRight(10);
        }
        tileThemeGroup.setChecked("Grass");

        // 타일 테마 선택 추가
        mainTable.add(tileThemeLabel).center().colspan(2).padBottom(10);
        mainTable.row();
        mainTable.add(tileThemeTable).colspan(2).center().padBottom(20);
        mainTable.row();

        // 알고리즘 선택 버튼
        algorithmButton = new TextButton("Algorithm: " + algorithms[algorithmIndex], skin);
        mainTable.add(algorithmButton).colspan(2).center().padBottom(20);
        mainTable.row();

        // 공통 파라미터 입력 필드
        widthLabel = new Label("Map Width Size:", skin);
        widthTextField = new TextField("", skin);

        heightLabel = new Label("Map Height Size:", skin);
        heightTextField = new TextField("", skin);

        // 너비와 높이 입력 필드를 한 행에 배치
        Table sizeTable = new Table();
        sizeTable.add(widthLabel).left().padRight(5);
        sizeTable.add(widthTextField).width(100).padRight(20);
        sizeTable.add(heightLabel).left().padRight(5);
        sizeTable.add(heightTextField).width(100);
        mainTable.add(sizeTable).colspan(2).center().padBottom(20);
        mainTable.row();

        // 랜덤 시드 입력 필드
        Label seedLabel = new Label("Enter Seed Value:", skin);
        seedTextField = new TextField("", skin);
        seedOptionalLabel = new Label("Seed is Optional", skin);
        seedOptionalLabel.setColor(Color.GRAY); // 회색 글씨 설정

        Table seedTable = new Table();
        seedTable.add(seedLabel).left().padRight(5);
        seedTable.add(seedTextField).width(200).padRight(10);
        seedTable.add(seedOptionalLabel).left();
        mainTable.add(seedTable).colspan(2).center().padBottom(20);
        mainTable.row();

        // 알고리즘별 파라미터 테이블
        algorithmParamsTable = new Table();
        updateAlgorithmParamsTable(algorithms[algorithmIndex]);
        mainTable.add(algorithmParamsTable).colspan(2).fillX().padBottom(20);
        mainTable.row();

        // 버튼들의 크기를 동일하게 설정
        float buttonWidth = algorithmButton.getWidth();
        float buttonHeight = algorithmButton.getHeight();

        // 확인 및 취소 버튼 생성
        TextButton confirmButton = new TextButton("Create", skin);
        TextButton backButton = new TextButton("Back", skin);

        // 버튼들의 크기 설정
        confirmButton.setSize(buttonWidth, buttonHeight);
        backButton.setSize(buttonWidth, buttonHeight);

        // 버튼들을 한 행에 배치
        Table buttonTable = new Table();
        buttonTable.add(confirmButton).width(buttonWidth).height(buttonHeight).padRight(20);
        buttonTable.add(backButton).width(buttonWidth).height(buttonHeight);
        mainTable.add(buttonTable).bottom().center();
        mainTable.row();

        // 이벤트 리스너 설정
        setListeners(confirmButton, backButton);
    }

    private void setListeners(TextButton confirmButton, TextButton backButton) {
        // 알고리즘 선택 버튼 클릭 시
        algorithmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 알고리즘 인덱스 변경
                algorithmIndex = (algorithmIndex + 1) % algorithms.length;
                // 버튼 텍스트 업데이트
                algorithmButton.setText("Algorithm: " + algorithms[algorithmIndex]);
                // 알고리즘별 파라미터 테이블 업데이트
                updateAlgorithmParamsTable(algorithms[algorithmIndex]);
                // 맵 사이즈 라벨 업데이트
                updateMapSizeLabels(algorithms[algorithmIndex]);
            }
        });

        // 확인 버튼 클릭 시 동작 정의
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    // 공통 파라미터 가져오기
                    int mapWidth = Integer.parseInt(widthTextField.getText());
                    int mapHeight = Integer.parseInt(heightTextField.getText());
                    long seed = seedTextField.getText().isEmpty() ? new Random().nextLong()
                            : Long.parseLong(seedTextField.getText());
                    String selectedAlgorithm = algorithms[algorithmIndex];

                    // 타일 테마 선택
                    String selectedTileTheme = tileThemeGroup.getChecked().getText().toString();

                    // Rooms and Mazes 알고리즘의 경우 맵 크기가 홀수여야 함
                    if (selectedAlgorithm.equals("Rooms and Mazes")) {
                        if (mapWidth % 2 == 0 || mapHeight % 2 == 0) {
                            // 오류 메시지 표시
                            showErrorDialog("Map size must be odd numbers for Rooms and Mazes.");
                            return;
                        }
                    }

                    // 알고리즘별 파라미터 가져오기
                    if (selectedAlgorithm.equals("Cellular Automata")) {
                        double fillProb = fillProbSlider.getValue();

                        // 맵 생성 화면으로 이동
                        game.setScreen(new MapGenerationScreen(game, SelectMap.this, mapWidth, mapHeight, seed,
                                fillProb, isConnected, selectedTileTheme));
                    } else if (selectedAlgorithm.equals("Rooms and Mazes")) {
                        int roomMinLen = Integer.parseInt(roomMinLenTextField.getText());
                        int roomMaxLen = Integer.parseInt(roomMaxLenTextField.getText());
                        int roomGenAttempt = (int)(roomGenAttemptSlider.getValue());
                
                        // 방 최소/최대 길이가 홀수여야 함
                        if (roomMinLen % 2 == 0 || roomMaxLen % 2 == 0) {
                            // 오류 메시지 표시
                            showErrorDialog("Room min and max lengths must be odd numbers.");
                            return;
                        }

                        // 맵 생성 화면으로 이동
                        game.setScreen(new MapGenerationScreen(game, SelectMap.this, mapWidth, mapHeight, seed,
                                roomMinLen, roomMaxLen, roomGenAttempt, removeDeadend, selectedTileTheme));
                    }
                } catch (NumberFormatException e) {
                    // 숫자 형식이 잘못된 경우 오류 메시지 표시
                    showErrorDialog("Please enter valid numeric values.");
                }
            }
        });

        // 뒤로가기 버튼 클릭 시 메인 메뉴로 돌아감
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    @Override
    public void show() {
        super.show();

        // 입력 프로세서 설정
        Gdx.input.setInputProcessor(stage);
    }

    private void updateAlgorithmParamsTable(String algorithm) {
        algorithmParamsTable.clear(); // 기존의 파라미터 필드 제거
        algorithmParamsTable.defaults().expandX().fillX().space(5);

        if (algorithm.equals("Cellular Automata")) {
            // Cellular Automata 파라미터 생성

            // 버튼과 슬라이더의 크기 설정
            float buttonWidth = algorithmButton.getWidth();
            float buttonHeight = algorithmButton.getHeight();

            fillProbSlider = new Slider(0.4f, 0.6f, 0.01f, false, skin);
            fillProbSlider.setValue(0.45f);
            fillProbSlider.setSize(buttonWidth, buttonHeight);

            // 슬라이더 내부에 표시할 레이블 생성
            final Label fillProbLabel = new Label("Fill Probability", skin);
            fillProbLabel.setSize(buttonWidth, buttonHeight);
            fillProbLabel.setAlignment(Align.center);
            fillProbLabel.setTouchable(Touchable.disabled); // 레이블이 입력을 받지 않도록 설정

            // 슬라이더와 레이블을 포함하는 테이블 생성
            Table sliderTable = new Table();
            sliderTable.addActor(fillProbSlider);
            sliderTable.addActor(fillProbLabel);
            sliderTable.setSize(buttonWidth, buttonHeight);

            // 슬라이더에 입력 리스너 추가
            fillProbSlider.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    updateFillProbLabel();
                    return true;
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    updateFillProbLabel();
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    fillProbLabel.setText("Fill Probability");
                }

                private void updateFillProbLabel() {
                    fillProbLabel.setText(String.format("Fill Probability : %.2f", fillProbSlider.getValue()));
                }
            });

            // Connect Rooms 버튼 생성
            connectRoomsButton = new TextButton("Connect Rooms: On", skin);
            connectRoomsButton.setSize(buttonWidth, buttonHeight);
            connectRoomsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    isConnected = !isConnected;
                    connectRoomsButton.setText("Connect Rooms: " + (isConnected ? "On" : "Off"));
                }
            });

            // 파라미터 테이블에 슬라이더와 버튼 추가
            algorithmParamsTable.add(sliderTable).width(buttonWidth).height(buttonHeight).left().padRight(20);
            algorithmParamsTable.add(connectRoomsButton).width(buttonWidth).height(buttonHeight).right();
            algorithmParamsTable.row();

        } else if (algorithm.equals("Rooms and Mazes")) {
            // Rooms and Mazes 파라미터 생성
            // 방 최소 및 최대 길이 입력 필드 생성
            Label roomMinLenLabel = new Label("Room Min Length (Odd):", skin);
            roomMinLenTextField = new TextField("5", skin);

            Label roomMaxLenLabel = new Label("Room Max Length (Odd):", skin);
            roomMaxLenTextField = new TextField("11", skin);

            // 방 최소 최대 길이 파라미터 테이블에 추가
            algorithmParamsTable.add(roomMinLenLabel).left();
            algorithmParamsTable.add(roomMinLenTextField).right();
            algorithmParamsTable.row();

            algorithmParamsTable.add(roomMaxLenLabel).left();
            algorithmParamsTable.add(roomMaxLenTextField).right();
            algorithmParamsTable.row();
            // End of 방 최소 최대 길이 파라미터

            // 버튼과 슬라이더의 크기 설정
            float buttonWidth = algorithmButton.getWidth();
            float buttonHeight = algorithmButton.getHeight();

            // Start of 방 생성 시도 슬라이더 생성
            // Room Generation Attempts 슬라이더 생성
            roomGenAttemptSlider = new Slider(1, 100, 1, false, skin);
            roomGenAttemptSlider.setValue(50); // 기본값 설정
            roomGenAttemptSlider.setSize(buttonWidth, buttonHeight);

            // 슬라이더 내부에 표시할 레이블 생성
            final Label roomGenAttemptLabel = new Label("Room Generation Attempts", skin);
            roomGenAttemptLabel.setSize(buttonWidth, buttonHeight);
            roomGenAttemptLabel.setAlignment(Align.center);
            roomGenAttemptLabel.setTouchable(Touchable.disabled);

            // 슬라이더와 레이블을 포함하는 테이블 생성
            Table sliderTable = new Table();
            sliderTable.addActor(roomGenAttemptSlider);
            sliderTable.addActor(roomGenAttemptLabel);
            sliderTable.setSize(buttonWidth, buttonHeight);

            // 슬라이더에 입력 리스너 추가
            roomGenAttemptSlider.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    updateRoomGenAttemptLabel();
                    return true;
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    updateRoomGenAttemptLabel();
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    roomGenAttemptLabel.setText("Room Generation Attempts");
                }

                private void updateRoomGenAttemptLabel() {
                    roomGenAttemptLabel.setText(String.format("Attempts: %d", (int) roomGenAttemptSlider.getValue()));
                }
            }); // End of 방 생성 시도 슬라이더 리스너

            // Remove Dead Ends 버튼 생성
            removeDeadendButton = new TextButton("Remove Dead Ends: Off", skin);
            removeDeadendButton.setSize(buttonWidth, buttonHeight);
            removeDeadendButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    removeDeadend = !removeDeadend;
                    removeDeadendButton.setText("Remove Dead Ends: " + (removeDeadend ? "On" : "Off"));
                }
            });

            // 파라미터 테이블에 슬라이더와 버튼 추가
            algorithmParamsTable.add(sliderTable).width(buttonWidth).height(buttonHeight).left().padRight(20);
            algorithmParamsTable.add(removeDeadendButton).width(buttonWidth).height(buttonHeight).right();
            algorithmParamsTable.row();

        }
    }

    // 맵 사이즈 라벨을 업데이트하는 메서드 추가
    private void updateMapSizeLabels(String algorithm) {
        if (algorithm.equals("Rooms and Mazes")) {
            widthLabel.setText("Map Width Size (Odd):");
            heightLabel.setText("Map Height Size (Odd):");
        } else {
            widthLabel.setText("Map Width Size:");
            heightLabel.setText("Map Height Size:");
        }
    }

    // 오류 메시지를 표시하는 메서드
    private void showErrorDialog(String message) {
        Dialog dialog = new Dialog("", skin);
        dialog.text(message);
        dialog.setColor(Color.RED);
        dialog.button("OK");
        dialog.show(stage);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
