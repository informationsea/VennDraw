/*
    Venn Draw : Draw Venn Diagram
    Copyright (C) 2016 Yasunobu OKAMURA All Rights Reserved

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package info.informationsea.venn;

import de.codecentric.centerdevice.MenuToolkit;
import info.informationsea.venn.fx.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;

@Slf4j
public class  VennDraw extends Application {

    public static java.awt.Font mplus;

    public static void main(String... args) {

        try {
            mplus = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, VennDraw.class.getResourceAsStream("fx/mplus-1p-regular.ttf")).deriveFont(12.0f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        if (args.length == 0) {
            Font.loadFont(VennDraw.class.getResourceAsStream("fx/mplus-1p-regular.ttf"), 12);

            launch(args); // Start GUI
        } else {
            // TODO: Implement command line interface
            System.out.println("VennDraw " + VersionResolver.getVersion());
            System.out.println("Command line interface is not implemented");
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fx/MainWindow.fxml"));

        loader.load();
        Scene scene = new Scene((Parent) loader.getRoot());
        scene.getStylesheets().add(getClass().getResource("fx/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Venn Draw");


        MainWindowController controller = loader.getController();
        controller.setMainStage(primaryStage);

        primaryStage.setOnCloseRequest(event1 -> {
            controller.onClose(null);
            event1.consume();
        });


        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            log.info("os x menu");
            MenuToolkit menuToolkit = MenuToolkit.toolkit();
            Menu appMenu = menuToolkit.createDefaultApplicationMenu("VennDraw");

            appMenu.getItems().get(appMenu.getItems().size()-1).setOnAction(event -> MainWindowController.getMainWindowControllers().get(0).onQuit(event));
            MenuItem aboutMenu = new MenuItem("About VennDraw");
            aboutMenu.setOnAction(event -> MainWindowController.getMainWindowControllers().get(0).onAbout(event));
            appMenu.getItems().add(0, aboutMenu);
            appMenu.getItems().add(1, new SeparatorMenuItem());

            menuToolkit.setApplicationMenu(appMenu);
        }

        primaryStage.show();
    }
}
