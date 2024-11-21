package oop.pcg2d.Screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import oop.pcg2d.App;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;

public class MainMenuScreen extends AbstractScreen {

    // 텍스처 클래스는 이미지 파일을 GPU 메모리에 로드하고 관리하는 클래스
    private Texture backgroundTexture;

    private Image background; // Actor의 한 종류로, 이미지를 화면에 그리는 역할 (Actor의 서브클래스)
    // 텍스처를 사용하여 이미지를 생성하고, 스테이지에 추가함

    public MainMenuScreen(App game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        // 배경 이미지 로드
        backgroundTexture = new Texture("ui/KWUbackground.png");
        background = new Image(backgroundTexture); // 이미지 객체 생성
        background.setFillParent(true); // 이미지가 스테이지 전체를 채우도록 설정
        stage.addActor(background); // 스테이지에 배경 이미지 추가

        // UI 요소들을 담을 테이블 생성
        Table table = new Table();
        table.setFillParent(true); // 테이블이 스테이지 전체를 채우도록 설정
        table.align(Align.center); // 테이블 내 요소들을 중앙 정렬

        stage.addActor(table); // 스테이지에 테이블 추가
        // 배경 이미지보다 나중에 추가되므로, 배경 이미지 위에 테이블의 요소들이 그려짐

        // 프로그램 제목 레이블 생성
        Label titleLabel = new Label("MapGenerator with Java", skin);
        titleLabel.setFontScale(2); // 글자 크기
        titleLabel.setColor(Color.GOLD); // 글자 색상

        // 제목을 테이블에 추가
        table.add(titleLabel).colspan(2).pad(10);
        table.row();

        // 버튼 생성
        TextButton button1 = new TextButton("Start", skin);
        button1.setColor(Color.WHITE);
        TextButton button2 = new TextButton("Settings", skin);
        button2.setColor(Color.WHITE);
        TextButton button3 = new TextButton("Exit", skin);
        button3.setColor(Color.WHITE);

        // 버튼을 테이블에 추가
        table.add(button1).width(200).height(50).pad(10);
        table.row(); // 다음 행으로 이동
        table.add(button2).width(200).height(50).pad(10);
        table.row();
        table.add(button3).width(200).height(50).pad(10);

        // "Start" 버튼에 클릭 이벤트 리스너 추가
        button1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SelectMap(game));
            }
        });

        // "Exit" 버튼에 클릭 이벤트 리스너 추가
        button3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit(); // 애플리케이션 종료
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        backgroundTexture.dispose();
    }

}
