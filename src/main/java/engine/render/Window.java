package engine.render;

import engine.event.KeyboardEventListener;
import engine.event.KeyboardEventProducer;
import engine.event.MouseEventProducer;
import engine.object.Scene;
import engine.util.AssetPool;
import engine.util.Color;
import engine.util.EnginePreferences;
import engine.util.EngineUtils;
import engine.util.listener.KeyListener;
import engine.util.listener.MouseListener;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import scene.SceneSelectionScene;

import static engine.util.EnginePreferences.ENABLE_SMALLER_WINDOWED_SIZE;
import static engine.util.EnginePreferences.ENABLE_VSYNK;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F11;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements KeyboardEventListener {

    private static GLFWVidMode vidMode;

    private static long primaryMonitor;

    private static int width, height;

    private static int windowedWidth, windowedHeight;

    private static float aspectRatio;

    private static boolean fullscreen = EnginePreferences.FULLSCREEN_BY_DEFAULT;

    private static boolean isDebugMenuActive = EnginePreferences.IS_DEBUG_MENU_ACTIVE_BY_DEFAULT;

    private static int windowPosX, windowPosY;

    private static String title;

    private static long glfwWindow;

    private static PickingTexture pickingTexture;

    public static Color bgColor = Color.GREY1;

    private static Scene currentScene;

    private static Window instance;

    private static long currentFrame = 0;

    public Window() {
        title = "Face punching game idk";
    }

    public static void changeScene(Class<? extends Scene> sceneClass) {
        if (currentScene != null) {
            currentScene.destroy();
            MouseEventProducer.clearListeners();
            KeyboardEventProducer.clearListeners();
        }

        try {
            currentScene = sceneClass.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to create new instance of the scene from class " + sceneClass.getName() + ". Message: " + ex.getMessage());
        }

        //TODO: понятия не имею почему это ЗДЕСЬ
        KeyboardEventProducer.listenToKeyboardPress(GLFW_KEY_F11, instance);
        KeyboardEventProducer.listenToKeyboardPress(GLFW_KEY_F3, instance);

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
            throw new RuntimeException("Unable to initialize GLFW.");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {

            primaryMonitor = GLFW.glfwGetMonitors().get(EnginePreferences.CREATE_WINDOW_ON_MONITOR);

            try {
                vidMode = GLFW.glfwGetVideoMode(primaryMonitor);
            } catch (Exception e) {
                System.err.println("No monitor with ID=" + primaryMonitor);

                primaryMonitor = GLFW.glfwGetPrimaryMonitor();
                vidMode = GLFW.glfwGetVideoMode(primaryMonitor);
            }

            width = vidMode.width();
            height = vidMode.height();

            windowedWidth = width / 2 / (ENABLE_SMALLER_WINDOWED_SIZE ? 2 : 1);
            windowedHeight = height / 2 / (ENABLE_SMALLER_WINDOWED_SIZE ? 2 : 1);

            aspectRatio = (float) width / height;

            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        }

        createWindow(fullscreen);

        Window.changeScene(SceneSelectionScene.class);
    }

    private static void createWindow(boolean fullscreen) {
        if (glfwWindow == NULL) {
            if (fullscreen) {
                glfwWindow = glfwCreateWindow(width, height, title, primaryMonitor, 0);
            } else {
                glfwWindow = glfwCreateWindow(windowedWidth, windowedHeight, title, 0, 0);
            }

            windowPosX = (vidMode.height() - height / 2) / 2;
            windowPosY = (vidMode.width() - width / 2) / 2;
        }

        if (fullscreen) {
            int[] xPos = new int[1];
            int[] yPos = new int[1];
            GLFW.glfwGetWindowPos(glfwWindow, xPos, yPos);
            windowPosX = xPos[0];
            windowPosY = yPos[0];

            GLFW.glfwSetWindowMonitor(glfwWindow, primaryMonitor, 0, 0, width, height, vidMode.refreshRate());
        } else {
            GLFW.glfwSetWindowMonitor(glfwWindow, 0, windowPosX, windowPosY, windowedWidth, windowedHeight, 0);
        }

        if (glfwWindow == NULL) {
            throw new RuntimeException("Exception while trying to create window");
        }

        glfwMakeContextCurrent(glfwWindow);

        GLFW.glfwSwapInterval(ENABLE_VSYNK ? 1 : 0);

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        glfwShowWindow(glfwWindow);
        createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        pickingTexture = fullscreen
                ? new PickingTexture(width, height)
                : new PickingTexture(windowedWidth, windowedHeight);

        glViewport(0, 0,
                fullscreen ? width : windowedWidth,
                fullscreen ? height : windowedHeight);
    }

    public void loop() {
        float beginTime = EngineUtils.time();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShaderByName("default");
        Shader pickingShader = AssetPool.getShaderByName("pickingShader");

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

//            clearColor(Color.TRANSPARENT);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            renderSceneWithShader(pickingShader);

            pickingTexture.disableWriting();
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);

            DebugDraw.beginFrame();

//            clearColor(bgColor);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (dt >= 0) {
                currentScene.update(dt);
                renderSceneWithShader(defaultShader);
//                renderSceneWithShader(pickingShader);

                DebugDraw.draw();
            }

            glfwSwapBuffers(glfwWindow);
            MouseListener.reset();

            currentFrame++;

            endTime = EngineUtils.time();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    private void renderSceneWithShader(Shader shader) {
        Renderer.bindShader(shader);
        currentScene.render();
    }

    private void clear() {
        currentScene.saveSceneData();

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private static void toggleFullscreen() {
        fullscreen = !fullscreen;
        createWindow(fullscreen);
    }

    private void toggleDebugMenu() {
        isDebugMenuActive = !isDebugMenuActive;
        //TODO: реализовать
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

    //TODO: реализовать
//    public static Vector2f getMiddleWindowPoint(){
//
//    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    public static int getWidth() {
        return fullscreen ? width : windowedWidth;
    }

    public static int getHeight() {
        return fullscreen ? height : windowedHeight;
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

    public static long getCurrentFrame() {
        return currentFrame;
    }

    public static void clearColor(Color color) {
        glClearColor(color.r, color.g, color.b, color.a);
    }

    @Override
    public void onKeyPress(int key) {
        if (key == GLFW_KEY_F11) {
            toggleFullscreen();
        } else if (key == GLFW_KEY_F3) {
            toggleDebugMenu();
        }
    }

    @Override
    public void onKeyRelease(int key) {

    }
}