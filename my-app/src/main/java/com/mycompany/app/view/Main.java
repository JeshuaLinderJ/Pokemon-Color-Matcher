package com.mycompany.app.view;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PokemonImageViewer viewer = new PokemonImageViewer();
            viewer.setVisible(true);
        });
    }
}