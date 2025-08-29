package com.kenyajug.platform;
/*
 * MIT License
 *
 * Copyright (c) 2025 Kenya JUG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;
public class PlatformListView extends VBox {
    private final ListView<TargetPlatform> platformsListView;
    public PlatformListView(){
        var platformsTitleLabel = new Label("Platforms");
        platformsTitleLabel.setFont(Font.font("", FontWeight.BOLD,21));
        platformsListView = new ListView<TargetPlatform>();
        var createPlatformButton = new Button("Create Platform");
        createPlatformButton.setFont(Font.font("",17));
        var bottomControls = new HBox();
        bottomControls.getChildren().add(createPlatformButton);
        bottomControls.setAlignment(Pos.BOTTOM_RIGHT);
        getChildren().add(platformsTitleLabel);
        getChildren().add(platformsListView);
        getChildren().add(bottomControls);
        setPadding(new Insets(12d));
        VBox.setMargin(platformsListView,new Insets(12));
        VBox.setMargin(platformsTitleLabel, new Insets(12d));
        VBox.setMargin(bottomControls, new Insets(12d));
        setBackground(Background.fill(Color.WHITE));
        loadData();
    }
    public void loadData(){
        var platforms = List.of(
                new TargetPlatform("f276b13d-42de-40a5-b59c-ea6317ccc14e","Android"),
                new TargetPlatform("c8c97503-4702-4f8c-8735-fe356ac3b240","iOS")
        );
        ObservableList<TargetPlatform> obsList = FXCollections.observableArrayList(platforms);
        platformsListView.setItems(obsList);
    }
}
