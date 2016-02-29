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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class GroupViewController implements Initializable {

    public static final String[] DEFAULT_COLORS = {
            "#bce2e8a0",
            "#d8e698a0",
            "#fddea5a0",
            "#f6bfbca0",
            "#fef263a0"
    };

    @Getter
    @Setter
    private MainWindowController mainWindowController;

    @FXML
    @Getter
    private TitledPane titledPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private CheckBox enabledBox;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea textArea;

    @FXML
    private Button upButton;

    @FXML
    private Button downButton;

    @FXML
    private ColorPicker colorPicker;

    @Getter
    private List<String> elements = new ArrayList<>();

    @FXML
    void onDelete(ActionEvent event) {
        mainWindowController.removeGroup(this);
    }

    @FXML
    void onEnableChanged(ActionEvent event) {
        mainWindowController.refreshCombination();
    }

    @FXML
    void onTextAreaTyped(KeyEvent event) {

    }

    @FXML
    void onDown(ActionEvent event) {
        mainWindowController.moveDownGroup(this);
    }

    @FXML
    void onUp(ActionEvent event) {
        mainWindowController.moveUpGroup(this);
    }

    @FXML
    void onColorChanged(ActionEvent event) {
        log.info("color {}", colorPicker.getValue());
        mainWindowController.refreshCombination();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("\n"))
                newValue = newValue.replaceAll("\n", ", ");
            if (newValue.contains("\t"))
                newValue = newValue.replaceAll("\t", ", ");
            newValue = newValue.replaceAll(",\\b", ", ");
            if (newValue.equals(oldValue)) return;
            textArea.setText(newValue);
            parseText(newValue);
        });

        textArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) return;

            parseText(textArea.getText());
            formatText(textArea.getText());
        });


        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            titledPane.setText(newValue + " [" + elements.size() + "]");
            mainWindowController.refreshCombination();
        });

        colorPicker.setValue(Color.valueOf("#ffffff00"));
        colorPicker.getCustomColors().add(Color.valueOf("#ffffff00"));
        colorPicker.getCustomColors().addAll(Stream.of(DEFAULT_COLORS).map(Color::valueOf).collect(Collectors.toSet()));

        // blurred text area workaround
        textArea.setLayoutX(0);
        textArea.setLayoutY(50);

        anchorPane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            textArea.setPrefSize(newValue.getWidth(), newValue.getHeight()-50);
        });
    }


    public void setName(String name) {
        nameField.setText(name);
    }

    public String getName() {
        return nameField.getText();
    }

    public boolean isEnabled() {
        return enabledBox.isSelected();
    }

    public void setColor(String color) {
        colorPicker.setValue(Color.valueOf(color));
    }

    public String getColor() {
        return "#" + colorPicker.getValue().toString().substring(2);
    }

    public void setAsFirstGroup() {
        upButton.setDisable(true);
        downButton.setDisable(false);
    }

    public void setAsMiddleGroup() {
        upButton.setDisable(false);
        downButton.setDisable(false);
    }

    public void setAsLastGroup() {
        upButton.setDisable(false);
        downButton.setDisable(true);
    }

    public void setElements(Collection<String> elements) {
        this.elements.clear();
        this.elements.addAll(elements);
        formatText(textArea.getText());
        mainWindowController.refreshCombination();
    }

    private void parseText(String value) {
        List<String> newElements = new ArrayList<>();

        for (String one : value.split("[,\n\t]")) {
            String clean = one.trim();
            if (clean.length() == 0) continue;
            if (newElements.contains(clean)) continue;

            newElements.add(clean);
        }

        elements = newElements;
        titledPane.setText(nameField.getText() + " [" + elements.size() + "]");
        mainWindowController.refreshCombination();
    }

    private void formatText(String value) {
        StringBuilder formattedString = new StringBuilder();
        for (String one : elements) {
            if (formattedString.length() > 0)
                formattedString.append(", ");
            formattedString.append(one);
        }

        if (!formattedString.toString().equals(value)) {
            textArea.textProperty().setValue(formattedString.toString());
        }
    }
}
