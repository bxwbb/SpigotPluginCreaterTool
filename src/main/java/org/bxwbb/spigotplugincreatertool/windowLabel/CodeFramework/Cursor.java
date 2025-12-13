package org.bxwbb.spigotplugincreatertool.windowLabel.CodeFramework;

import javafx.application.Platform;
import javafx.scene.shape.Rectangle;

public class Cursor {

    private int posX;
    private int posY;
    protected Rectangle cursor;
    private boolean blink;
    private final CodeFramework codeFramework;
    private Runnable blinkRunnable;

    public Cursor(CodeFramework codeFramework) {
        this.codeFramework = codeFramework;
        cursor = new Rectangle(0, 0, 3, CodeFramework.codeFont.getSize());
        cursor.setFill(CodeFramework.CURSOR_COLOR);
        cursor.setVisible(false);
    }

    public CodeFramework getCodeFramework() {
        return codeFramework;
    }

    public int getPosX() {
        return posX;
    }

    protected void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    protected void setPosY(int posY) {
        this.posY = posY;
    }

    public Rectangle getCursor() {
        return cursor;
    }

    public void setCursor(Rectangle cursor) {
        this.cursor = cursor;
    }

    public boolean isBlink() {
        return blink;
    }

    public void setBlink(boolean blink) {
        this.blink = blink;
        cursor.setVisible(true);
        if (blink) {
            blinkRunnable = new BlinkRunnable();
        } else {
            ((BlinkRunnable) blinkRunnable).blink = false;
        }
    }

    class BlinkRunnable implements Runnable {

        public boolean blink;

        @Override
        public void run() {
            while (true) {
                Platform.runLater(() -> cursor.setVisible(!cursor.isVisible()));
                if (!blink) {
                    Platform.runLater(() -> cursor.setVisible(true));
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

}
