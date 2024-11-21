package oop.pcg2d.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import oop.pcg2d.App;

/*
Screen2D는 핵심적으로 세 가지 클래스가 있음.
Actor, Stage, Group, Screen이 있음.
Actor는 화면에 그려지는 모든 객체를 나타내며, Stage는 Actor를 관리하고 화면에 그리는 역할을 함.
Screen은 게임의 화면을 나타내며, 여러 개의 Stage를 가질 수 있음.
Group은 Actor의 서브클래스로, 여러 개의 Actor를 하나의 그룹으로 묶을 수 있음.

이 클래스는 랜덤 시드 맵 구현을 위해 사용자로부터 입력을 받아 게임을 시작하는 메인 메뉴 화면을 나타냄.
*/

// AbstractScreen 클래스는 Screen 인터페이스를 구현하고, 게임의 화면을 나타내는 클래스들의 부모 클래스 역할을 함.
public abstract class AbstractScreen implements Screen {
    protected final App game; // Game 클래스 인스턴스
    protected Stage stage; // Actor를 관리하고 화면에 그리는 역할을 하는 객체
    protected Skin skin; // UI 스타일을 정의하는 객체

    // 게임 인스턴스를 받아서 초기화하는 생성자
    public AbstractScreen(App game) {
        this.game = game;

        // ScreenViewport는 화면 크기에 맞춰 스테이지의 크기를 조정하는 뷰포트
        this.stage = new Stage(new ScreenViewport());

        // uiskin.json 파일을 읽어와서 스킨을 생성
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    }

    protected String getName(){
        return getClass().getSimpleName();
    }
    
    @Override
    // 각 화면이 렌더링될 때 호출되는 메서드
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // 화면을 지움
        stage.act(delta); // 스테이지의 액터들을 업데이트
        stage.draw(); // 스테이지에 등록된 액터들을 그림
    }
    
    @Override
    public void resize(int width, int height) {
        Gdx.app.log(App.LOG, "Resizing screen: " + getName() + " to: " + width + " x " + height);

        // 뷰포트 크기를 화면 크기에 맞춰 업데이트
        stage.getViewport().update(width, height, true);
    }

    @Override
    // 화면이 보여질 때 호출되는 메서드
    public void show() {
        Gdx.app.log(App.LOG, "Showing screen: " + getName());
        Gdx.input.setInputProcessor(stage); // 입력 처리를 현재 스테이지로 설정하여 Actor들이 입력을 받을 수 있도록 함
    }

    @Override
    public void pause() {
        Gdx.app.log(App.LOG, "Pausing screen: " + getName());
        Gdx.input.setInputProcessor(null); // 입력 처리 해제
    }

    @Override
    public void resume() {
        Gdx.app.log(App.LOG, "Resuming screen: " + getName());
    }

    @Override
    public void dispose() {
        Gdx.app.log(App.LOG, "Disposing screen: " + getName());
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void hide() {
        Gdx.app.log(App.LOG, "Hiding screen: " + getName());
    }
}
