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

package info.informationsea.venn.fx;

import info.informationsea.venn.*;
import info.informationsea.venn.graphics.PointConverter;
import info.informationsea.venn.graphics.VennDrawGraphics2D;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MainWindowController implements Initializable {

    @Getter
    private Stage mainStage;

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem quitMenu;

    @FXML
    private SplitPane splitter;

    @FXML
    private SplitPane splitter2;

    @FXML
    private AnchorPane imageAnchorPane;

    @FXML
    private ImageView resultImageView;

    @FXML
    private Accordion groupList;

    @FXML
    private TableView<Map.Entry<Set<String>, Set<String>>> combinationTable;

    @FXML
    private TableColumn<Map.Entry<Set<String>, Set<String>>, String> tableCombinationColumn;

    @FXML
    private TableColumn<Map.Entry<Set<String>, Set<String>>, Integer> tableNumberOfItems;

    @FXML
    private TableColumn<Map.Entry<Set<String>, Set<String>>, String> tableItems;

    private List<GroupViewController> groupViewControllerList = new ArrayList<>();

    private CombinationSolver<String, String> combinationSolver = null;
    private List<VennFigureParameters.Attribute<String>> keyList = null;
    private VennFigure<String> currentVennFigure = null;
    private PointConverter pointConverter = null;
    private boolean modified = false;

    @Getter
    private static List<MainWindowController> mainWindowControllers = new ArrayList<>();

    private static final int IMAGE_MARGIN = 20;
    private static final int IMAGE_HIDPI = 2;

    public void setMainStage(Stage stage) {
        mainStage = stage;
        mainWindowControllers.add(this);
    }

    @FXML
    void onAddGroup(ActionEvent event) {
        addGroup();
        refreshCombination();
    }

    @FXML
    void onExportClicked(ActionEvent event) {
        if (combinationSolver == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PowerPoint Slide", "*.pptx"),
                new FileChooser.ExtensionFilter("PNG image", "*.png"),
                new FileChooser.ExtensionFilter("SVG image", "*.svg"),
                new FileChooser.ExtensionFilter("PDF image", "*.pdf")
        );
        fileChooser.setTitle("Export as image");
        File file = fileChooser.showSaveDialog(mainStage);
        if (file == null) return;

        VennFigureParameters<String> parameters = new VennFigureParameters<String>(combinationSolver, keyList);
        boolean hasOpacity = keyList.stream().filter(it -> {
            Color color = VennDrawGraphics2D.decodeColor(it.getColorCode());
            switch (color.getAlpha()) {
                case 0xff:
                case 0x00:
                    return false;
                default:
                    return true;
            }
        }).count() > 0;

        try {
            if (file.getName().endsWith(".pptx")) {
                if (hasOpacity) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ellipses will filled with opacity colors", ButtonType.OK);
                    alert.setHeaderText("Transparency is not supported in PowerPoint Export");
                    alert.showAndWait();
                }
                VennExporter.exportAsPowerPoint(parameters, file);
            } else if (file.getName().endsWith(".png")) {
                VennExporter.exportAsPNG(parameters, file, 800, 10);
            } else if (file.getName().endsWith(".svg")) {
                VennExporter.exportAsSVG(parameters, file, new Dimension(800, 800));
            } else if (file.getName().endsWith(".pdf")) {
                if (hasOpacity) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Ellipses will filled with opacity colors", ButtonType.OK);
                    alert.setHeaderText("Transparency is not supported in PDF Export");
                    alert.showAndWait();
                }
                VennExporter.exportAsPDF(parameters, file);
            } else {
                throw new UnsupportedOperationException("" + file.getName() + " is not supported filetype");
            }

        } catch (IOException ioe) {
            log.info("Failed to export {}", ioe);
        }
    }

    private Tooltip imageTooltip = new Tooltip();

    @FXML
    void onMouseMovedOnImage(MouseEvent event) {
        VennFigure.Point converted = pointConverter.convert(
                new VennFigure.Point(event.getX()*IMAGE_HIDPI - IMAGE_MARGIN/2, event.getY()*IMAGE_HIDPI - IMAGE_MARGIN/2));

        List<VennFigure.Oval<String>> ovalList = currentVennFigure.ovalsAtPoint(converted);
        if (ovalList.size() > 0) {
            Set<String> groups = new HashSet<>();
            for (VennFigure.Oval<String> oval : ovalList) groups.add(oval.getUserData());

            String tipString = String.join(", ", groups) + "\n" +
                    String.join(", ", combinationSolver.getCombinationResult().get(groups));
            if (tipString.length() > 100)
                tipString = tipString.substring(0, 100) + "...";

            imageTooltip.setText(tipString);
            imageTooltip.show(mainStage, event.getScreenX() + 20, event.getScreenY() + 20);
        } else {
            imageTooltip.hide();
        }
    }

    @FXML
    void onMouseExitedOnImage(MouseEvent event) {
        imageTooltip.hide();
    }

    @FXML
    void onSaveDataset(ActionEvent event) {
        if (combinationSolver == null) return;

        boolean allEnabled = true;
        for (GroupViewController controller : groupViewControllerList) {
            if (!controller.isEnabled()) allEnabled = false;
        }
        if (!allEnabled) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Disabled groups will not be saved.", ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();
            if (!result.isPresent() || !result.get().equals(ButtonType.OK)) return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel", "*.xlsx"),
                new FileChooser.ExtensionFilter("Excel Classical Format", "*.xls"),
                new FileChooser.ExtensionFilter("CSV", "*.csv")
        );
        fileChooser.setTitle("Save dataset");
        File file = fileChooser.showSaveDialog(mainStage);
        if (file == null) return;

        try {
            CombinationImporterExporter.export(file, combinationSolver, keyList);
            modified = false;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getLocalizedMessage(), ButtonType.OK);
            alert.setHeaderText("Failed to save dataset");
            alert.showAndWait();
            e.printStackTrace();
        }
    }


    @FXML
    void onLoadDataset(ActionEvent event) {
        if (!confirmDiscard()) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Files", "*.csv", "*.xlsx", "*.xls")
        );
        fileChooser.setTitle("Load dataset");
        File file = fileChooser.showOpenDialog(mainStage);
        if (file == null) return;

        try {
            CombinationSolver<String, String> loaded = CombinationImporterExporter.importCombination(file);
            loadDataset(loaded.getValues());
            modified = false;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getLocalizedMessage(), ButtonType.OK);
            alert.setHeaderText("Failed to load dataset");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    void onLoadSampleFour(ActionEvent event) {
        if (!confirmDiscard()) return;
        loadDataset(SampleCombinationGenerator.fourCombinationValues);
        modified = false;
    }

    @FXML
    void onLoadSampleThree(ActionEvent event) {
        if (!confirmDiscard()) return;
        loadDataset(SampleCombinationGenerator.threeCombinationValues);
        modified = false;
    }

    @FXML
    void onLoadSampleTwo(ActionEvent event) {
        if (!confirmDiscard()) return;
        loadDataset(SampleCombinationGenerator.twoCombinationValues);
        modified = false;
    }

    public void loadDataset(Map<String, Set<String>> value) {
        groupViewControllerList.clear();
        groupList.getPanes().clear();

        for (Map.Entry<String, Set<String>> one : value.entrySet()) {
            GroupViewController controller = addGroup();
            controller.setName(one.getKey());
            controller.setElements(one.getValue());
        }
    }

    @FXML
    public void onClose(ActionEvent event) {
        if (!confirmDiscard()) return;
        mainStage.close();
        mainWindowControllers.remove(this);
    }

    @FXML
    public void onQuit(ActionEvent event) {
        List<MainWindowController> removeList = new ArrayList<>();

        for (MainWindowController controller : mainWindowControllers) {
            if (!controller.confirmDiscard()) {
                mainWindowControllers.removeAll(removeList);
                return;
            }
            controller.mainStage.close();
            removeList.add(controller);
        }

        mainWindowControllers.removeAll(removeList);
    }

    @FXML
    void onNew(ActionEvent event) {
        try {
            Stage primaryStage = new Stage();
            VennDraw drawer = new VennDraw();
            drawer.start(primaryStage);
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }


    @FXML
    public void onAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About VennDraw");
        alert.setHeaderText("VennDraw " + VersionResolver.getVersion());
        alert.setContentText("Version: " + VersionResolver.getVersion() + "\n" +
                "Git Commit: " + VersionResolver.getGitCommit().substring(0, 5) + "\n" +
                "Build date: " + VersionResolver.getBuildDate() + "\n\n" +
                "Copyright (C) 2016  Yasunobu OKAMURA\n" +
                "    This program comes with ABSOLUTELY NO WARRANTY\n" +
                "    This is free software, and you are welcome to redistribute it under GPL3 or later\n\n" +
                "Open Source Softwares:\n" +
                "TableIO : Copyright (C) 2015 Yasunobu OKAMURA All Rights Reserved\n" +
                "OpenCSV : Copyright (C) OpenCSV development team\n" +
                "Apache Batik: Copyright (C) Apache XML Graphics Project\n" +
                "Apache Commons: Copyright (C) The Apache Software Foundation.\n" +
                "Apache POI: Copyright (C) The Apache Software Foundation.\n" +
                "Apache PDFBox: Copyright (C) The Apache Software Foundation.\n" +
                "Xalan: Copyright (C) The Apache Software Foundation.\n" +
                "Slf4j: Copyright (c) 2004-2013 QOS.ch All rights reserved.\n" +
                "NSMenuFX: Copyright (c) 2015, codecentric AG All Rights Reserved.\n" +
                "args4j: Copyright (C) 2003-2016 Kohsuke Kawaguchi\n" +
                "M+ FONT: Copyright (C) 2015 Kouji Morishita\n"+
                "sRGB Color Space Profile.icm: Copyright (c) 1998 Hewlett-Packard Company");

        javafx.scene.image.Image image = new Image(getClass().getResourceAsStream("icon-128.png"));
        ImageView imageView = new ImageView(image);
        alert.setGraphic(imageView);
        alert.showAndWait();
    }

    @FXML
    public void onGithub(ActionEvent event) {
        try {
            Desktop.getDesktop().browse(URI.create("https://github.com/informationsea/VennDraw"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuBar.setUseSystemMenuBar(true);

        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            quitMenu.setVisible(false);
        }


        refreshVennFigure();

        tableCombinationColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(String.join(", ", param.getValue().getKey())));

        tableNumberOfItems.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().size()));

        tableItems.setCellValueFactory(param -> new ReadOnlyStringWrapper(String.join(", ", param.getValue().getValue())));


        splitter.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> {
            refreshVennFigure();
        });
        splitter2.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> {
            refreshVennFigure();
        });
        splitter2.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> {
            refreshVennFigure();
        });

        addGroup().setElements(Arrays.asList("A", "B", "C"));
        addGroup().setElements(Arrays.asList("B", "C", "D"));
        groupViewControllerList.get(0).getTitledPane().setExpanded(true);
        modified = false;
    }

    public GroupViewController addGroup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("group.fxml"));
            TitledPane pane = loader.load();
            groupList.getPanes().add(pane);
            pane.setExpanded(true);

            GroupViewController controller = loader.getController();
            controller.setMainWindowController(this);
            controller.setName("group " + (groupViewControllerList.size() + 1));

            groupViewControllerList.add(controller);
            refreshGroupState();
            return controller;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeGroup(GroupViewController controller) {
        if (groupViewControllerList.size() > 2) {
            groupList.getPanes().remove(controller.getTitledPane());
            groupViewControllerList.remove(controller);
            refreshCombination();
            refreshGroupState();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Cannot remove the selected group");
            alert.setContentText("Two or more groups are required");
            alert.showAndWait();
        }
    }

    public void moveUpGroup(GroupViewController controller) {
        int index = groupViewControllerList.indexOf(controller);
        groupViewControllerList.remove(index);
        groupViewControllerList.add(index - 1, controller);
        groupList.getPanes().remove(index);
        groupList.getPanes().add(index - 1, controller.getTitledPane());
        controller.getTitledPane().setExpanded(true);
        refreshGroupState();
        refreshCombination();
    }

    public void moveDownGroup(GroupViewController controller) {
        int index = groupViewControllerList.indexOf(controller);
        groupViewControllerList.remove(index);
        groupViewControllerList.add(index + 1, controller);
        groupList.getPanes().remove(index);
        groupList.getPanes().add(index + 1, controller.getTitledPane());
        controller.getTitledPane().setExpanded(true);
        refreshGroupState();
        refreshCombination();
    }

    private void refreshGroupState() {
        for (int i = 1; i < groupViewControllerList.size()-1; i++)
            groupViewControllerList.get(i).setAsMiddleGroup();
        groupViewControllerList.get(0).setAsFirstGroup();
        groupViewControllerList.get(groupViewControllerList.size()-1).setAsLastGroup();
    }

    public void refreshCombination() {
        modified = true;
        Map<String, Set<String>> values = new HashMap<>();

        for (GroupViewController controller : groupViewControllerList) {
            if (!controller.isEnabled()) continue;
            if (values.containsKey(controller.getName())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Group name should be unique");
                alert.setContentText("Do not use same name with other group");
                alert.showAndWait();
                return;
            }

            values.put(controller.getName(), new HashSet<>(controller.getElements()));
        }

        combinationSolver = new CombinationSolver<>(values);

        ObservableList<Map.Entry<Set<String>, Set<String>>> entryList =
                FXCollections.observableArrayList(combinationSolver.getCombinationResult().entrySet());
        combinationTable.itemsProperty().setValue(entryList);

        switch (combinationSolver.getValues().size()) {
            case 2:
            case 3:
            case 4:
                // create venn figure
                this.keyList = groupViewControllerList.stream().filter(GroupViewController::isEnabled).
                                map(it -> new VennFigureParameters.Attribute<>(it.getName(), it.getColor())).collect(Collectors.toList());
                currentVennFigure = VennFigureCreator.createVennFigure(new VennFigureParameters<String>(combinationSolver, keyList));
                break;
            default:
                keyList = null;
                break;
        }

        refreshVennFigure();
    }

    public void refreshVennFigure() {
        if (combinationSolver == null) {
            refreshCombination();
            return;
        }

        switch (combinationSolver.getValues().size()) {
            case 2:
            case 3:
            case 4:
                resultImageView.setVisible(true);
                Dimension dimension = new Dimension((int) (splitter.getDividerPositions()[0] * splitter.getWidth()),
                        (int) (splitter2.getDividerPositions()[0] * splitter2.getHeight()));

                if (dimension.width > 0 && dimension.height > 0) {
                    // prepare image
                    BufferedImage image = new BufferedImage(dimension.width*IMAGE_HIDPI, dimension.height*IMAGE_HIDPI,
                            BufferedImage.TYPE_3BYTE_BGR);
                    Graphics2D graphics = (Graphics2D) image.getGraphics();
                    graphics.setColor(Color.WHITE);
                    graphics.fillRect(0, 0, dimension.width*IMAGE_HIDPI, dimension.height*IMAGE_HIDPI);
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics.setColor(Color.BLACK);
                    graphics.translate(IMAGE_MARGIN / 2, IMAGE_MARGIN / 2);
                    graphics.setFont(VennDraw.mplus);


                    pointConverter = VennDrawGraphics2D.draw(currentVennFigure, graphics,
                            new Dimension((dimension.width - IMAGE_MARGIN)*IMAGE_HIDPI, (dimension.height - IMAGE_MARGIN)*IMAGE_HIDPI));

                    resultImageView.setImage(SwingFXUtils.toFXImage(image, null));
                    resultImageView.setFitHeight(dimension.height);
                    resultImageView.setFitWidth(dimension.width);
                }
                break;
            default:
                currentVennFigure = null;
                resultImageView.setVisible(false);
                break;
        }
    }

    /**
     * Show alert and confirm about discarding
     * @return true if confirmed
     */
    public boolean confirmDiscard() {
        if (!modified) return true;
        Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.YES, ButtonType.CANCEL);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(mainStage);
        alert.setTitle("Discard changes");
        alert.setHeaderText("Do you want to discard changes?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().equals(ButtonType.YES);
    }
}
