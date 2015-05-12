package com.example.parser.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public interface SimpleMouseListener extends MouseListener {

    default void mousePressed(MouseEvent e) {
        //NOOP
    }

    default void mouseReleased(MouseEvent e) {
        //NOOP
    }

    default void mouseEntered(MouseEvent e) {
        //NOOP
    }

    default void mouseExited(MouseEvent e) {
        //NOOP
    }
}
