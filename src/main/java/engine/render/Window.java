package engine.render;

import engine.event.KeyboardEventListener;
import engine.event.KeyboardEventProducer;
import engine.event.MouseEventProducer;
import engine.util.AssetPool;
import engine.util.Color;
import engine.util.EnginePreferences;
import engine.util.listener.KeyListener;
import engine.util.listener.MouseListener;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import registry.ShaderType;
import scene.ForestScene;
import scene.Scene;
import util.Utils;

import static engine.util.EnginePreferences.ENABLE_SMALLER_WINDOWED_SIZE;
import static engine.util.EnginePreferences.ENABLE_VSYNK;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F11;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F5;
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
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
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
import static util.Utils.printf;

public class Window implements KeyboardEventListener {

    private static GLFWVidMode vidMode;

    private static long primaryMonitor;

    public static int width, height;

    public static float aspectRatio;

    private static boolean fullscreen = EnginePreferences.FULLSCREEN_BY_DEFAULT;

    private static boolean isDebugMenuActive = EnginePreferences.IS_DEBUG_MENU_ACTIVE_BY_DEFAULT;

    private static int windowPosX, windowPosY;

    private static String title;

    public static long glfwWindow;

    private static PickingTexture pickingTexture;

    public static Scene currentScene;

    private static Window instance;

    public static long currentFrame = 0;

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
        KeyboardEventProducer.listenToKeyboardPress(GLFW_KEY_F5, instance);

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

            PointerBuffer monitorBuffer = GLFW.glfwGetMonitors();

            primaryMonitor = monitorBuffer.get(Math.clamp(EnginePreferences.CREATE_WINDOW_ON_MONITOR, 0, monitorBuffer.capacity() - 1));

            try {
                vidMode = GLFW.glfwGetVideoMode(primaryMonitor);
            } catch (Exception e) {
                System.err.println("No monitor with ID=" + primaryMonitor);

                primaryMonitor = GLFW.glfwGetPrimaryMonitor();
                vidMode = GLFW.glfwGetVideoMode(primaryMonitor);
            }

            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        }

        createWindow(fullscreen);

        changeScene(ForestScene.class);
    }

    private static void createWindow(boolean fullscreen) {
        long usedMonitor;

        if (fullscreen) {
            width = vidMode.width();
            height = vidMode.height();

            usedMonitor = primaryMonitor;
        } else {
            width = vidMode.width() / 2 / (ENABLE_SMALLER_WINDOWED_SIZE ? 2 : 1);
            height = vidMode.height() / 2 / (ENABLE_SMALLER_WINDOWED_SIZE ? 2 : 1);

            usedMonitor = 0;
        }

        aspectRatio = (float) width / height;

        if (glfwWindow == NULL) {
            if (fullscreen) {
                glfwWindow = glfwCreateWindow(vidMode.width(), vidMode.height(), title, usedMonitor, 0);
            } else {
                glfwWindow = glfwCreateWindow(vidMode.width() / 2 / (ENABLE_SMALLER_WINDOWED_SIZE ? 2 : 1), vidMode.height() / 2 / (ENABLE_SMALLER_WINDOWED_SIZE ? 2 : 1), title, 0, 0);
            }

            // TODO: падазрительные переменные
            windowPosX = (vidMode.width() - height / 2) / 2;
            windowPosY = (vidMode.height() - width / 2) / 2;

            PointerBuffer monitorBuffer = GLFW.glfwGetMonitors();

            int xOffset = 0;
            // TODO: тут раньше был usedMonitor вместо primaryMonitor
            for (int i = 0; i < Math.clamp(EnginePreferences.CREATE_WINDOW_ON_MONITOR, 0, primaryMonitor); i++) {
                xOffset += GLFW.glfwGetVideoMode(monitorBuffer.get(i)).width();
            }

            windowPosX = vidMode.width() / 2 + xOffset;
            windowPosY = (int) (vidMode.height() / 2.2f);
        } else {
            currentScene.camera.updateProjectionSize();
        }

        if (fullscreen) {
            int[] xPos = new int[1];
            int[] yPos = new int[1];
            GLFW.glfwGetWindowPos(glfwWindow, xPos, yPos);
            windowPosX = xPos[0];
            windowPosY = yPos[0];

            GLFW.glfwSetWindowMonitor(glfwWindow, usedMonitor, 0, 0, width, height, vidMode.refreshRate());
        } else {
            GLFW.glfwSetWindowMonitor(glfwWindow, NULL, windowPosX, windowPosY, width, height, GLFW_DONT_CARE);
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
        glfwSetWindowSizeCallback(glfwWindow, Window::windowResizeCallback);
        glfwSetWindowPosCallback(glfwWindow, Window::windowPosCallback);

        glfwShowWindow(glfwWindow);
        createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        pickingTexture = new PickingTexture(Window.width, Window.height);

        glViewport(0, 0, width, height);
    }

    private static void windowPosCallback(long window, int x, int y) {
        printf("Window pos callback! Old x:%, old y:%, new x:%, new y:%", windowPosX, windowPosY, x, y);
        windowPosX = x;
        windowPosY = y;
    }

    // TODO: мб перетащить
    private static void windowResizeCallback(long window, int newWidth, int newHeight) {
        printf("Window resize callback! Old width:%, old height:%, new width:%, new height:%", width, height, newWidth, newHeight);
        width = newWidth;
        height = newHeight;
    }

    public void loop() {
        float beginTime = Utils.time();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader(ShaderType.DEFAULT);
        Shader uiShader = AssetPool.getShader(ShaderType.UI);
        Shader pickingShader = AssetPool.getShader(ShaderType.PICKING);
        Shader fontShader = AssetPool.getShader(ShaderType.FONT);

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            renderSceneWithShader(pickingShader);

            pickingTexture.disableWriting();
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);

            DebugDraw.beginFrame();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            if (dt >= 0) {
                // TODO: нужно разделить обновление графики от обновления реального состояния объектов. Это должно увеличить производительность В РАЗЫ
                currentScene.update(dt);
                renderSceneWithShader(defaultShader);
                renderSceneWithShader(fontShader);
                renderSceneWithShader(uiShader);

                DebugDraw.draw();
            }

            glfwSwapBuffers(glfwWindow);
            MouseListener.reset();

            currentFrame++;

            endTime = Utils.time();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    private void renderSceneWithShader(Shader shader) {
        Renderer.currentShader = shader;
        currentScene.render();
    }

    private void clear() {
        if (EnginePreferences.ENABLE_DATA_SAVING) {
            currentScene.saveSceneData();
        }

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private static void toggleFullscreen() {
        fullscreen = !fullscreen;
        createWindow(fullscreen);
    }

    //TODO: implement
    private void toggleDebugMenu() {
        isDebugMenuActive = !isDebugMenuActive;
    }

    public static Window get() {
        if (instance == null) {
            instance = new Window();
        }

        return instance;
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
        } else if (key == GLFW_KEY_F5) {
            changeScene(ForestScene.class);
        }
    }

    @Override
    public void onKeyRelease(int key) {
    }
}