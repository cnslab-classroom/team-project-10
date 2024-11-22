package oop.pcg2d;

import oop.pcg2d.Screen.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;

// Game 클래스는 여러 화면을 관리하고 게임 루프를 실행하는 클래스
public class App extends Game {
    // 로그 기록
    public static String LOG = App.class.getSimpleName();

    private FPSLogger fpsLogger; // fps 로그

    public static boolean DEV_MODE = true; // 개발 모드

    private MainMenuScreen mainMenuScreen; // 메인 메뉴 화면 인스턴스


    private static final int TARGET_FPS = 60; // 원하는 FPS
    private static final float TARGET_FRAME_TIME = 1.0f / TARGET_FPS; // 프레임당 시간
    private float accumulator = 0;

    @Override
    // 게임이 시작될 때 호출되는 메서드
    public void create() {
        Gdx.app.log(LOG, "Creating game on " + Gdx.app.getType());
        this.fpsLogger = new FPSLogger();
        
        // 메인메뉴 화면을 생성함. Main클래스 인스턴스를 전달하여 생성
        this.mainMenuScreen = new MainMenuScreen(this);

        // 현재 화면을 메인메뉴 화면으로 설정함
        this.setScreen(this.mainMenuScreen);



    }

    @Override
    // 화면 크기가 변경될 때 호출되는 메서드
    public void resize(int width, int height) {

        // 현재 활성화된 화면의 resize 메서드 호출
        super.resize(width, height);

        Gdx.app.log(LOG, "Resizing game to: " + width + " x " + height);
    }

    @Override
    // 매 프레임마다 호출되는 메서드
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime(); // 실제 지난 시간
        accumulator += deltaTime;
    
        if (accumulator >= TARGET_FRAME_TIME) {
            accumulator -= TARGET_FRAME_TIME;
    
            // 현재 활성화된 화면의 render 메서드 호출
            super.render();
    
            // 개발 모드일 때 fps 로그 출력
            if (DEV_MODE) {
                this.fpsLogger.log();
            }
        } else {
            // 남은 시간 동안 대기
            try {
                Thread.sleep((long)((TARGET_FRAME_TIME - accumulator) * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void pause() {
        super.pause();
        Gdx.app.log(LOG, "Pausing game");
    }

    @Override
    public void resume() {
        super.resume();
        Gdx.app.log(LOG, "Resuming game");
    }

    @Override
    public void dispose() {
        super.dispose();
        Gdx.app.log(LOG, "Disposing game");
        this.mainMenuScreen.dispose();
    }
    
}
