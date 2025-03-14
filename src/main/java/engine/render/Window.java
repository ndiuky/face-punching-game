package engine.render;

import engine.event.KeyboardEventListener;
import engine.event.KeyboardEventProducer;
import engine.event.MouseEventProducer;
import engine.object.Scene;
import engine.util.AssetPool;
import engine.util.EnginePreferences;
import engine.util.Utils;
import engine.util.listener.KeyListener;
import engine.util.listener.MouseListener;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import scene.BoxingRingScene;
import scene.SceneSelectionScene;
import scene.TestingScene;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F11;
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
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL.createCapabilities;
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

public class Window implements KeyboardEventListener {

    private static GLFWVidMode vidMode;

    private TextRenderer textRenderer;

    private static long primaryMonitor;

    private static int width, height;

    private static float aspectRatio;

    private static boolean fullscreen;

    private static int windowPosX, windowPosY;

    private static String title;

    private static long glfwWindow;

    private static PickingTexture pickingTexture;

    public static float r, g, b, a;

    private static Scene currentScene;

    private static Window instance;

    public Window() {

        title = "Face punching game idk";
        fullscreen = false;
        r = 0.1f;
        b = 0.1f;
        g = 0.1f;
        a = 1;

    }

    public static void changeScene(int newScene) {
        if (currentScene != null) {
            currentScene.destroy();
            MouseEventProducer.clearListeners();
            KeyboardEventProducer.clearListeners();
        }

        KeyboardEventProducer.listenToKeyboardPress(GLFW_KEY_F11, instance);

        currentScene = switch (newScene) {
            case 0 -> new TestingScene();
            case 1 -> new BoxingRingScene();
            case 2 -> new SceneSelectionScene();
            default -> {
                throw new RuntimeException("Unknown scene '" + newScene + "'");
            }
        };

        if (EnginePreferences.ENABLE_DATA_LOADING) {
            currentScene.loadSceneData();
        }
        currentScene.init();
        currentScene.start();
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

        try (MemoryStack stack = MemoryStack.stackPush()) {

            primaryMonitor = GLFW.glfwGetPrimaryMonitor();
            vidMode = GLFW.glfwGetVideoMode(primaryMonitor);
            width = vidMode.width();
            height = vidMode.height();

            aspectRatio = (float) width / height;

            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        }

        //calling it with true parameter to fix issue, where camera scaling wasn't applying properly
        //TODO: запомнить последнюю позицию
        createWindow(true);
        createWindow(fullscreen);

        textRenderer = new TextRenderer();

        //TODO: убрать хардкод
        Window.changeScene(2);
    }

    private static void createWindow(boolean fullscreen) {
        if (glfwWindow == NULL) {
            glfwWindow = glfwCreateWindow(width, height, title, primaryMonitor, 0);
        }

        if (fullscreen) {
            int[] xpos = new int[1];
            int[] ypos = new int[1];
            GLFW.glfwGetWindowPos(glfwWindow, xpos, ypos);
            windowPosX = xpos[0];
            windowPosY = ypos[0];

            GLFW.glfwSetWindowMonitor(glfwWindow, primaryMonitor, 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate());
        } else {
            GLFW.glfwSetWindowMonitor(glfwWindow, 0, windowPosX, windowPosY, width / 2, height / 2, 0);
        }

        if (glfwWindow == NULL) {
            throw new RuntimeException("Exception while trying to create window");
        }

        glfwMakeContextCurrent(glfwWindow);

        if (!fullscreen) {
            GLFW.glfwSetWindowPos(
                    glfwWindow,
                    (vidMode.width() - width / 2) / 2,
                    (vidMode.height() - height / 2) / 2
            );
        }

        GLFW.glfwSwapInterval(1);

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            width = newWidth;
            height = newHeight;
        });

        glfwShowWindow(glfwWindow);
        createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        pickingTexture = new PickingTexture(width, height);

        glViewport(0, 0, width, height);
    }

    public void loop() {
        float beginTime = Utils.time();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("asset/shader/default.glsl");
        Shader pickingShader = AssetPool.getShader("asset/shader/pickingShader.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            textRenderer.init();
            textRenderer.drawText("Penis.", 400, 400);

//            glDisable(GL_BLEND);
//            pickingTexture.enableWriting();
//
//            glViewport(0, 0, width, height);
//            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//
//            Renderer.bindShader(pickingShader);
//            currentScene.render();
//
//            pickingTexture.disableWriting();
//            glDisable(GL_DEPTH_TEST);
//            glEnable(GL_BLEND);
//
//            DebugDraw.beginFrame();
//
//            glClearColor(r, g, b, a);
//            glClear(GL_COLOR_BUFFER_BIT);
//
//            if (dt >= 0) {
//                DebugDraw.draw();
//                Renderer.bindShader(defaultShader);
////                Renderer.bindShader(pickingShader);
//
//                currentScene.update(dt);
//                currentScene.render();
//            }

            glfwSwapBuffers(glfwWindow);
            MouseListener.reset();

            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

    }

    private void clear() {
        currentScene.saveSceneData();

        textRenderer.dispose();

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private static void toggleFullscreen() {
        fullscreen = !fullscreen;
        createWindow(fullscreen);
    }

    public static Window get() {
        if (instance == null) {
            instance = new Window();
        }

        return instance;
    }

    public static float getAspectRatio() {
        return aspectRatio;
    }

//    public static Vector2f getMiddleWindowPoint(){
//
//    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setWidth(int newWidth) {
        width = newWidth;
    }

    public static void setHeight(int newHeight) {
        height = newHeight;
    }

    public static long getGlfwWindow() {
        return glfwWindow;
    }

    @Override
    public void onKeyPress(int key) {
        if (key == GLFW_KEY_F11) {
            toggleFullscreen();
        }
    }

    @Override
    public void onKeyRelease(int key) {

    }
}