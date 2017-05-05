package ua.room414;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class Main extends JFrame {
    private static SimpleUniverse universe;
    private static Scene scene;
    private static Map<String, Shape3D> nameMap;
    private static BranchGroup root;
    static Canvas3D canvas;

    private static TransformGroup wholeFly;
    private static Transform3D transform3D;

    private Main() throws IOException {
        configureWindow();
        configureCanvas();
        configureUniverse();
        addModelToUniverse();
        setPlaneElementsList();
        addAppearance();
        addImageBackground();
        addLightToUniverse();
        addOtherLight();
        ChangeViewAngle();
        root.compile();
        universe.addBranchGraph(root);
    }

    private void configureWindow() {
        setTitle("Dragon");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void configureCanvas() {
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setDoubleBufferEnable(true);
        getContentPane().add(canvas, BorderLayout.CENTER);
    }

    private void configureUniverse() {
        root = new BranchGroup();
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
    }

    private void addModelToUniverse() throws IOException {
        scene = getSceneFromFile("eagle.obj");
        root = scene.getSceneGroup();
    }

    private void addLightToUniverse() {
        Bounds bounds = new BoundingSphere();
        Color3f color = new Color3f(65 / 255f, 30 / 255f, 25 / 255f);
        Vector3f lightdirection = new Vector3f(-1f, -1f, -1f);
        DirectionalLight dirlight = new DirectionalLight(color, lightdirection);
        dirlight.setInfluencingBounds(bounds);
        root.addChild(dirlight);
    }

    private void printModelElementsList(Map<String, Shape3D> nameMap) {
        for (String name : nameMap.keySet()) {
            System.out.printf("Name: %s\n", name);
        }
    }

    private void setPlaneElementsList() {
        nameMap = scene.getNamedObjects();
        //Print elements of model:
        printModelElementsList(nameMap);
        wholeFly = new TransformGroup();
        transform3D = new Transform3D();
        transform3D.setScale(new Vector3d(1.3, 1.3, 1.3));
        wholeFly.setTransform(transform3D);

        for (String key : nameMap.keySet()) {
            root.removeChild(nameMap.get(key));
        }

        wholeFly.addChild(nameMap.get("eg2_body"));
        wholeFly.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        root.addChild(wholeFly);
    }

    private Texture getTexture(String path) {
        TextureLoader textureLoader = new TextureLoader(path, "LUMINANCE", canvas);
        Texture texture = textureLoader.getTexture();
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));
        return texture;
    }

    private Material getMaterial() {
        Material material = new Material();
        material.setAmbientColor(new Color3f(139 / 255f, 69 / 255f, 19 / 255f));
        material.setDiffuseColor(new Color3f(139 / 255f, 69 / 255f, 19 / 255f));
        material.setSpecularColor(new Color3f(139 / 255f, 69 / 255f, 19 / 255f));
        material.setShininess(0.8f);
        material.setLightingEnable(true);
        return material;
    }

    private void addAppearance() {
        Appearance bodyApp = new Appearance();
        bodyApp.setTexture(getTexture("body.jpg"));
        TextureAttributes texAttr2 = new TextureAttributes();
        texAttr2.setTextureMode(TextureAttributes.MODULATE);
        bodyApp.setTextureAttributes(texAttr2);
        bodyApp.setMaterial(getMaterial());
        Shape3D body = nameMap.get("eg2_body");
        body.setAppearance(bodyApp);
    }

    private void addImageBackground() {
        TextureLoader t = new TextureLoader("snow-mountain-8.jpg", canvas);
        Background background = new Background(t.getImage());
        background.setImageScaleMode(Background.SCALE_FIT_ALL);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        background.setApplicationBounds(bounds);
        root.addChild(background);
    }

    private void ChangeViewAngle() {
        ViewingPlatform vp = universe.getViewingPlatform();
        TransformGroup vpGroup = vp.getMultiTransformGroup().getTransformGroup(0);
        Transform3D vpTranslation = new Transform3D();
        Vector3f translationVector = new Vector3f(0.0F, 1.2F, 8F);
        vpTranslation.setTranslation(translationVector);
        vpGroup.setTransform(vpTranslation);
    }

    private void addOtherLight() {
        Color3f directionalLightColor = new Color3f(Color.BLACK);
        Color3f ambientLightColor = new Color3f(Color.WHITE);
        Vector3f lightDirection = new Vector3f(-1F, -1F, -1F);
        AmbientLight ambientLight = new AmbientLight(ambientLightColor);
        DirectionalLight directionalLight = new DirectionalLight(directionalLightColor, lightDirection);
        Bounds influenceRegion = new BoundingSphere();
        ambientLight.setInfluencingBounds(influenceRegion);
        directionalLight.setInfluencingBounds(influenceRegion);
        root.addChild(ambientLight);
        root.addChild(directionalLight);
    }

    private static Scene getSceneFromFile(String location) throws IOException {
        ObjectFile file = new ObjectFile(ObjectFile.RESIZE);
        file.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
        return file.load(new FileReader(location));
    }

    public static void main(String[] args) {
        try {
            Main window = new Main();
            Animation planeMovement = new Animation(wholeFly, transform3D, window);
            window.addKeyListener(planeMovement);
            window.setVisible(true);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
