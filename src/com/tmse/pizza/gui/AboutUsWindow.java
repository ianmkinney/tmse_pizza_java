package com.tmse.pizza.gui;

import com.tmse.pizza.models.Order;
import com.tmse.pizza.models.OrderItem;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * About Us page matching hand-drawn design
 */
public class AboutUsWindow {
    private Stage stage;
    private Order currentOrder;
    private List<OrderItem> cartItems;

    public AboutUsWindow(Stage primaryStage, Order order, List<OrderItem> items) {
        this.stage = primaryStage;
        this.currentOrder = order;
        this.cartItems = items;
    }

    public void show() {
        stage.setTitle("TMSE Pizza - About Us");
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ffffff;");

        CommonLayout.HeaderInfo headerInfo = CommonLayout.createHeader(stage, currentOrder, cartItems, () -> {
            LandingWindow landingWindow = new LandingWindow(stage, currentOrder, cartItems);
            landingWindow.show();
        }, "about-us");
        root.setTop(headerInfo.getHeaderContainer());

        VBox centerBox = new VBox(30);
        centerBox.setPadding(new Insets(40));
        centerBox.setStyle("-fx-background-color: #f9fafb;");

        Label titleLabel = new Label("About Us");
        titleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");
        centerBox.getChildren().add(titleLabel);

        HBox storyBox = new HBox(30);
        storyBox.setPadding(new Insets(20));
        storyBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 8;");

        VBox imageBox = new VBox();
        imageBox.setPrefWidth(300);
        imageBox.setPrefHeight(200);
        imageBox.setStyle("-fx-background-color: #f3f4f6; -fx-border-color: #d1d5db; -fx-border-width: 1; -fx-border-radius: 5;");
        imageBox.setAlignment(Pos.CENTER);
        Label imageLabel = new Label("Image of Store");
        imageLabel.setStyle("-fx-text-fill: #9ca3af;");
        imageBox.getChildren().add(imageLabel);

        VBox storyText = new VBox(15);
        Label storyTitle = new Label("Our story");
        storyTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label storyContent = new Label("TMSE Pizza began as a tiny weekend pop-up near campus. We hand-toss every dough, use seasonal produce, and keep a small, honest menu. Busy day or family night, swing by our quick-pickup window and enjoy pies made with care.");
        storyContent.setStyle("-fx-font-size: 14px; -fx-text-fill: #4b5563;");
        storyContent.setWrapText(true);
        storyText.getChildren().addAll(storyTitle, storyContent);
        HBox.setHgrow(storyText, Priority.ALWAYS);

        storyBox.getChildren().addAll(imageBox, storyText);
        centerBox.getChildren().add(storyBox);

        Label whatWeHaveLabel = new Label("What we have?");
        whatWeHaveLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        centerBox.getChildren().add(whatWeHaveLabel);

        GridPane featuresGrid = new GridPane();
        featuresGrid.setHgap(20);
        featuresGrid.setVgap(20);
        featuresGrid.setPadding(new Insets(20));

        VBox freshBox = createFeatureBox("Fresh Ingredients", "", "Farm Fresh", "Seasonal", "Raw & Real");
        VBox handTossedBox = createFeatureBox("Hand-Tossed", "", "Hand Toss", "Knead-love", "From Dough");
        VBox familyBox = createFeatureBox("Family Friendly", "", "All ages", "Bring Kids", "Love");
        VBox fastBox = createFeatureBox("Fast pick", "", "No wait", "Grab & Go", "Quick Pick");

        featuresGrid.add(freshBox, 0, 0);
        featuresGrid.add(handTossedBox, 1, 0);
        featuresGrid.add(familyBox, 0, 1);
        featuresGrid.add(fastBox, 1, 1);

        centerBox.getChildren().add(featuresGrid);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button orderNowButton = new Button("Order Now");
        orderNowButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 30;");
        orderNowButton.setOnAction(e -> {
            LandingWindow landingWindow = new LandingWindow(stage, currentOrder, cartItems);
            landingWindow.show();
        });

        Button reviewsButton = new Button("See Reviews");
        reviewsButton.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 30;");
        reviewsButton.setOnAction(e -> {
            ReviewsWindow reviewsWindow = new ReviewsWindow(stage, currentOrder, cartItems);
            reviewsWindow.show();
        });

        buttonBox.getChildren().addAll(orderNowButton, reviewsButton);
        centerBox.getChildren().add(buttonBox);

        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        VBox footerBox = CommonLayout.createFooter();
        root.setBottom(footerBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Full screen is already set at startup, no need to toggle
    }

    private VBox createFeatureBox(String title, String emoji, String... features) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 2; -fx-border-radius: 8;");
        box.setPrefWidth(300);

        HBox titleBox = new HBox(10);
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 32px;");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        titleBox.getChildren().addAll(emojiLabel, titleLabel);

        VBox featuresList = new VBox(5);
        for (String feature : features) {
            Label featureLabel = new Label("• " + feature);
            featureLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");
            featuresList.getChildren().add(featureLabel);
        }

        box.getChildren().addAll(titleBox, featuresList);
        return box;
    }
}

