package com.kenyajug.dashboard;
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
import com.kenyajug.core.ImageNotFoundException;
import com.kenyajug.core.UIReusableActions;
import com.kenyajug.platform.PlatformListView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
public class AppDashboardView extends SplitPane {
    public AppDashboardView(){
        var brandView = new HBox();
        try {
            var brandImage = new ImageView(UIReusableActions.loadImage("logo.png"));
            brandImage.setFitHeight(42);
            brandImage.setFitWidth(42);
            brandView.getChildren().add(brandImage);
            brandView.setAlignment(Pos.BASELINE_LEFT);
        } catch (ImageNotFoundException ex){
            System.out.println("Failed to load brand image: " + ex.getLocalizedMessage());
        }
        var brandTitleLabel = new Label("Regression");
        brandTitleLabel.setFont(Font.font("",FontWeight.BOLD,21));
        brandView.getChildren().add(brandTitleLabel);

        var qaMenuContainer = new VBox(12);
        var platformsMenu = new Hyperlink("Target Platforms");
        var promptsMenu = new Hyperlink("Prompts");
        var analysisResults = new Hyperlink("Results");
        platformsMenu.setFont(Font.font("",17));
        promptsMenu.setFont(Font.font("",17));
        analysisResults.setFont(Font.font("",17));
        qaMenuContainer.getChildren().addAll(platformsMenu,promptsMenu,analysisResults);
        var qaMenuView = new TitledPane("Quality Assurance", qaMenuContainer);
        qaMenuView.setFont(Font.font("",FontWeight.BOLD,17));

        var logsMenuContainer = new VBox();
        var localLogsMenu = new Hyperlink("Local Logs");
        var remoteLogsMenu = new Hyperlink("Remote Logs");
        logsMenuContainer.getChildren().add(localLogsMenu);
        logsMenuContainer.getChildren().add(remoteLogsMenu);
        var logsMenu = new TitledPane("Platform Logs", logsMenuContainer);
        logsMenu.setFont(Font.font("",FontWeight.BOLD,17));

        var sideMenuView = new VBox(12);
        sideMenuView.setPadding(new Insets(12));
        sideMenuView.getChildren().add(brandView);
        sideMenuView.getChildren().add(qaMenuView);
        sideMenuView.getChildren().add(new Separator());
        sideMenuView.getChildren().add(logsMenu);
        sideMenuView.setBackground(Background.fill(Color.WHITE));
        getItems().add(sideMenuView);
        getItems().add(new VBox());
        setDividerPositions(0,0.1);
        setRightPane(new PlatformListView());
    }
    public void setRightPane(Pane view){
        getItems().set(1,view);
    }
}
