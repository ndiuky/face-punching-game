package engine.render;

import engine.object.Scene;
import engine.util.AssetPool;
import engine.util.EnginePreferences;
import engine.util.Utils;
import engine.util.listener.KeyListener;
import engine.util.listener.MouseListener;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import scene.BoxingRingScene;
import scene.SceneSelectionScene;
import scene.TestingScene;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int width;

    private int height;

    private final String title;

    private long glfwWindow;

    private PickingTexture pickingTexture;

    public float r, g, b, a;

    private static Window window = null;

    private static Scene currentScene;

    private Window() {

        width = 1919;
        height = 1079;

        title = "Face punching game idk";
        r = 0.1f;
        b = 0.1f;
        g = 0.1f;
        a = 1;
    }

    public static void changeScene(int newScene) {
        System.out.println("Changing scene to " + newScene);

        currentScene = switch (newScene) {
            case 0 -> new TestingScene();
            case 1 -> new BoxingRingScene();
            case 2 -> new SceneSelectionScene();
            default -> {
                throw new RuntimeException("Unknown scene '" + newScene + "'");
            }
        };

        if (EnginePreferences.ENABLE_DATA_LOADING){
            currentScene.loadSceneData();
        }
        currentScene.init();
        currentScene.start();
    }

    public static Window get() {
        if (window == null) {
            window = new Window();
        }

        return window;
    }

    public void run() {
        init();
        loop();
        clear();
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // Fixing the bug, where glfwSetWindowSizeCallback method wasn't
        // called when the game is started
        glfwSetWindowSize(glfwWindow, width, height);
        try {
            width = Integer.parseInt(System.getenv("SCREEN_WIDTH"));
            height = Integer.parseInt(System.getenv("SCREEN_HEIGHT"));
        } catch (Exception e) {
            System.err.println("can't read system variables 'SCREEN_WIDTH' and/or 'SCREEN_HEIGHT', setting window size to default values: 1920x1080");
            width = 1920;
            height = 1080;
        }
        glfwSetWindowSize(glfwWindow, width, height);

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);
        glfwShowWindow(glfwWindow);
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        pickingTexture = new PickingTexture(width, height);

        glViewport(0, 0, width, height);

        //TODO: убрать хардкод
        Window.changeScene(2);
    }

    public void loop() {
        float beginTime = Utils.time();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("asset/shader/default.glsl");
        Shader pickingShader = AssetPool.getShader("asset/shader/pickingShader.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0, 0, width, height);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);

            DebugDraw.beginFrame();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                DebugDraw.draw();
                Renderer.bindShader(defaultShader);
//                Renderer.bindShader(pickingShader);
                currentScene.update(dt);
                currentScene.render();
            }

            glfwSwapBuffers(glfwWindow);
            MouseListener.reset();

            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    private void clear() {
        currentScene.saveSceneData();

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static float getTargetAspectRatio() {
        // TODO: 23.06.2024 убрать хардкод
        return 16f / 9f;
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }

    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    public static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    public static long getGlfwWindow() {
        return get().glfwWindow;
    }
}