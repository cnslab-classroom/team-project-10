package oop.pcg2d.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import oop.pcg2d.App;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class InformationScreen extends AbstractScreen {

    private Texture backgroundTexture;
    private Image background;

    public InformationScreen(App game) {
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

        // 메인 테이블 생성
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center().pad(20);
        stage.addActor(mainTable);

        // 제목 레이블 생성
        Label titleLabel = new Label("Information", skin, "bold");
        mainTable.add(titleLabel).padBottom(20);
        mainTable.row();

        // 정보 텍스트 생성 (예시 문장)
        String infoText = "This program was created by Kwangwoon University students.\n\n"
                + "The purpose of the program is to learn Java in an object-oriented programming course...\n\n"
                + "Creators:\n"
                + "- Jeonhyeongyu\n"
                + "- Kimjunsik\n"
                + "- Kimjinwook\n"
                + "- Moongijoo\n\n"
                + "Special Features:\n"
                + "- Randomized map generation\n"
                + "- Supports various themes\n"
                + "- User-friendly interface\n\n"
                + "Thank you.";

        // 정보 레이블 생성
        Label infoLabel = new Label(infoText, skin, "dim");
        // 정보 레이블 배경 설정
        infoLabel.getStyle().background = skin.getDrawable("scroll-bg");
        infoLabel.setWrap(true); // 텍스트 줄바꿈 허용
        infoLabel.setAlignment(Align.topLeft);

        // ScrollPane 스타일 적용
        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScroll = skin.getDrawable("scroll-vertical"); // 스킨에 맞는 드로어블 사용
        scrollStyle.vScrollKnob = skin.getDrawable("scroll-knob-vertical");

        // ScrollPane 생성
        ScrollPane scrollPane = new ScrollPane(infoLabel, skin);
        scrollPane.setStyle(scrollStyle);
        scrollPane.setFadeScrollBars(false); // 스크롤바 항상 표시
        scrollPane.setScrollingDisabled(true, false); // 가로 스크롤 비활성화, 세로 스크롤 활성화
        scrollPane.setScrollbarsOnTop(true);

        // ScrollPane을 담을 테이블 생성
        Table scrollTable = new Table();
        scrollTable.add(scrollPane).width(600).height(400); // 원하는 크기로 설정
        mainTable.add(scrollTable).padBottom(20);
        mainTable.row();

        // "Back" 버튼 생성
        TextButton backButton = new TextButton("Back", skin);
        mainTable.add(backButton).width(600).height(65);

        // "Back" 버튼에 클릭 리스너 추가
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
        Gdx.input.setInputProcessor(stage); // 입력 프로세서 설정
    }

    @Override
    public void dispose() {
        super.dispose();
        backgroundTexture.dispose();
    }
}
