package ua.room414;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Animation implements ActionListener, KeyListener {
    private Button go;
    private TransformGroup wholePlane;
    private Transform3D translateTransform;
    private Transform3D rotateTransformX;
    private Transform3D rotateTransformY;
    private Transform3D rotateTransformZ;
    private float sign = 1.0f;
    private float zoom = 1.8f;
    private float xLocation = 0.0f;
    private float yLocation = 0.0f;
    private float zLocation = 0.0f;
    private int moveType = 1;
    private Timer timer;

    Animation(TransformGroup wholePlane, Transform3D trans, JFrame frame) {
        go = new Button("Fly!");
        this.wholePlane = wholePlane;
        this.translateTransform = trans;
        rotateTransformX = new Transform3D();
        rotateTransformY = new Transform3D();
        rotateTransformZ = new Transform3D();
        Main.canvas.addKeyListener(this);
        timer = new Timer(100, this);
        Panel p = new Panel();
        p.add(go);
        frame.add("South", p);
        go.addActionListener(this);
        go.addKeyListener(this);
    }

    private void initialPlaneState() {
        xLocation = 0.0f;
        yLocation = 0.0f;
        zLocation = 0.0f;
        zoom = 0.5f;
        moveType = 1;
        sign = 1.0f;
        rotateTransformY.rotY(-Math.PI / 2.8);
        translateTransform.mul(rotateTransformY);
        if (timer.isRunning()) {
            timer.stop();
        }
        go.setLabel("Fly!");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // start timer when button is pressed
        if (e.getSource() == go) {
            if (!timer.isRunning()) {
                timer.start();
                go.setLabel("Stop");
            } else {
                timer.stop();
                go.setLabel("Fly!");
            }
        } else {
            move(moveType);
            translateTransform.setScale(new Vector3d(zoom, zoom, zoom));
            translateTransform.setTranslation(new Vector3f(xLocation, yLocation, zLocation));
            wholePlane.setTransform(translateTransform);
        }
    }

    private void move(int mType) {
        if (mType == 1) {
            xLocation += sign * 0.1 * Math.random();
            yLocation += sign * 0.1 * Math.random();
            sign *= -1;
        }
        if (mType == 2) {
            if (zoom < 0.05) {
                initialPlaneState();
            } else {
                rotateTransformY.rotY(-11 / 4f * Math.PI);
                xLocation -= 0.04;
                yLocation -= 0.04;
                zLocation -= 0.05;
                zoom -= 0.05;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //Invoked when a key has been pressed.
        if (e.getKeyChar() == 's') {
            yLocation = yLocation - .07f;
        }
        if (e.getKeyChar() == 'a') {
            xLocation = xLocation - .07f;
        }
        if (e.getKeyChar() == 'w') {
            yLocation = yLocation + .07f;
        }
        if (e.getKeyChar() == 'd') {
            xLocation = xLocation + .07f;
        }
        if (e.getKeyChar() == 'r') {
            rotateTransformY.rotY(Math.PI);
            translateTransform.mul(rotateTransformY);
        }
        if (e.getKeyChar() == '1') {
            rotateTransformX.rotX(Math.PI / 6);
            translateTransform.mul(rotateTransformX);
        }
        if (e.getKeyChar() == '2') {
            rotateTransformY.rotY(Math.PI / 6);
            translateTransform.mul(rotateTransformY);
        }
        if (e.getKeyChar() == '3') {
            rotateTransformZ.rotZ(Math.PI / 6);
            translateTransform.mul(rotateTransformZ);
        }
        if (e.getKeyChar() == '0') {
            rotateTransformY.rotY(Math.PI / 2.8);
            translateTransform.mul(rotateTransformY);
            moveType = 2;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}