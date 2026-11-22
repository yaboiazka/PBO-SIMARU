package com.simaru.util;

import java.io.IOException;
import java.io.UncheckedIOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Navigator {

    private static final String VIEW_PATH = "/com/simaru/view/";

    private static Stage stage;
    private static String currentView;

    private Navigator() {
    }

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    private static FXMLLoader loader(String name) {
        return new FXMLLoader(
            Navigator.class.getResource(VIEW_PATH + name + ".fxml")
        );
    }

    public static void show(
        String name,
        String title,
        double width,
        double height
    ) {
        try {

            // Simpan nama halaman yang sedang dibuka
            currentView = name;

            System.out.println("Halaman aktif: " + currentView);

            Parent root = loader(name).load();

            Scene scene = new Scene(root, width, height);

            // Memuat CSS SIMARU
            java.net.URL css = Navigator.class.getResource(
                "/com/simaru/css/status.css"
            );

            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            } else {
                System.err.println("File status.css tidak ditemukan!");
            }

            stage.setScene(scene);
            stage.setTitle(title);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {

            throw new UncheckedIOException(
                "Gagal memuat " + name + ".fxml", e
            );
        }
    }

    public static void loadInto(BorderPane pane, String name) {
        try {
            currentView = name;
            pane.setCenter(loader(name).load());

        } catch (IOException e) {

            e.printStackTrace();

            Alerts.error(
                "Gagal memuat halaman " + name + ": " + e.getMessage()
            );
        }
    }

    public static String getCurrentView() {
        return currentView;
    }

    public static void applyDialogStyle(javafx.scene.control.Dialog<?> dialog) {
        if (dialog == null || dialog.getDialogPane() == null) return;
        applyDialogPaneStyle(dialog.getDialogPane());
    }

    public static void applyDialogPaneStyle(javafx.scene.control.DialogPane dialogPane) {
        if (dialogPane == null) return;
        java.net.URL css = Navigator.class.getResource("/com/simaru/css/status.css");
        if (css != null && !dialogPane.getStylesheets().contains(css.toExternalForm())) {
            dialogPane.getStylesheets().add(css.toExternalForm());
        }
    }
}
