package com.tmse.pizza.gui;

import com.tmse.pizza.models.Order;
import com.tmse.pizza.models.OrderItem;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

// Reviews page
public class ReviewsWindow {
    private Stage stage;
    private Order currentOrder;
    private List<OrderItem> cartItems;
    private List<Review> reviews;

    public ReviewsWindow(Stage primaryStage, Order order, List<OrderItem> items) {
        this.stage = primaryStage;
        this.currentOrder = order;
        this.cartItems = items;
        this.reviews = new ArrayList<>();
        initializeReviews();
    }

    private void initializeReviews() {
        reviews.add(new Review("Ima Customer", 4, "10.25.2025", "Great pizza! The crust was perfect and the toppings were fresh. Will definitely order again!"));
        reviews.add(new Review("Shesa Student", 5, "10.24.2025", "Amazing service and delicious pizza. The delivery was fast and the pizza was still hot when it arrived."));
        reviews.add(new Review("John Doe", 5, "10.23.2025", "Best pizza in town! The Cowabunga Classic is my favorite."));
        reviews.add(new Review("Jane Smith", 4, "10.22.2025", "Really good pizza, especially the specialty options. Great value for money."));
    }

    public void show() {
        stage.setTitle("TMSE Pizza - Reviews");
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ffffff;");

        CommonLayout.HeaderInfo headerInfo = CommonLayout.createHeader(stage, currentOrder, cartItems, () -> {
            LandingWindow landingWindow = new LandingWindow(stage, currentOrder, cartItems);
            landingWindow.show();
        }, "reviews");
        root.setTop(headerInfo.getHeaderContainer());

        VBox centerBox = new VBox(20);
        centerBox.setPadding(new Insets(30));
        centerBox.setStyle("-fx-background-color: #f9fafb;");

        Label titleLabel = new Label("Reviews");
        titleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");
        centerBox.getChildren().add(titleLabel);

        HBox summaryBox = new HBox(30);
        summaryBox.setPadding(new Insets(20));
        summaryBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 8;");

        VBox averageBox = new VBox(5);
        double averageRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        Label averageLabel = new Label("Average " + String.format("%.1f", averageRating));
        averageLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label starsLabel = new Label("★★★★★");
        starsLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #fbbf24;");
        Label countLabel = new Label(reviews.size() + " reviews");
        countLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");
        averageBox.getChildren().addAll(averageLabel, starsLabel, countLabel);

        VBox filterBox = new VBox(10);
        Label sortLabel = new Label("sort by:");
        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Recent", "Highest Rated", "Lowest Rated", "Oldest");
        sortCombo.setValue("Recent");

        Label filterLabel = new Label("Filter:");
        HBox filterButtons = new HBox(10);
        ToggleGroup filterGroup = new ToggleGroup();
        RadioButton allFilter = new RadioButton("All");
        allFilter.setToggleGroup(filterGroup);
        allFilter.setSelected(true);
        RadioButton fiveStar = new RadioButton("5★");
        fiveStar.setToggleGroup(filterGroup);
        RadioButton fourStar = new RadioButton("4★");
        fourStar.setToggleGroup(filterGroup);
        filterButtons.getChildren().addAll(allFilter, fiveStar, fourStar);

        filterBox.getChildren().addAll(sortLabel, sortCombo, filterLabel, filterButtons);
        HBox.setHgrow(filterBox, Priority.ALWAYS);
        filterBox.setAlignment(Pos.CENTER_RIGHT);

        summaryBox.getChildren().addAll(averageBox, filterBox);
        centerBox.getChildren().add(summaryBox);

        Button writeReviewButton = new Button("Write a review");
        writeReviewButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20;");
        writeReviewButton.setOnAction(e -> showWriteReviewDialog());
        centerBox.getChildren().add(writeReviewButton);

        VBox reviewsList = new VBox(15);
        reviewsList.setPadding(new Insets(10));

        for (Review review : reviews) {
            VBox reviewBox = createReviewBox(review);
            reviewsList.getChildren().add(reviewBox);
        }

        ScrollPane reviewsScroll = new ScrollPane(reviewsList);
        reviewsScroll.setFitToWidth(true);
        reviewsScroll.setPrefHeight(400);
        centerBox.getChildren().add(reviewsScroll);

        Button loadMoreButton = new Button("Load More");
        loadMoreButton.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-padding: 8 20;");
        loadMoreButton.setOnAction(e -> {
            reviews.add(new Review("New Customer", 5, "10.21.2025", "Great experience!"));
            show();
        });
        centerBox.getChildren().add(loadMoreButton);

        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        VBox footerBox = CommonLayout.createFooter(stage, currentOrder, cartItems);
        root.setBottom(footerBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Full screen is already set at startup, no need to toggle
    }

    private VBox createReviewBox(Review review) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 8;");

        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label userIcon = new Label("👤");
        userIcon.setStyle("-fx-font-size: 24px;");
        Label nameLabel = new Label(review.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        String stars = "";
        for (int i = 0; i < 5; i++) {
            stars += (i < review.getRating()) ? "★" : "☆";
        }
        Label starsLabel = new Label(stars);
        starsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #fbbf24;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dateLabel = new Label(review.getDate());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        headerBox.getChildren().addAll(userIcon, nameLabel, starsLabel, spacer, dateLabel);
        box.getChildren().add(headerBox);

        Label reviewText = new Label(review.getText());
        reviewText.setStyle("-fx-font-size: 14px; -fx-text-fill: #4b5563;");
        reviewText.setWrapText(true);
        box.getChildren().add(reviewText);

        return box;
    }

    private void showWriteReviewDialog() {
        Dialog<Review> dialog = new Dialog<>();
        dialog.setTitle("Write a Review");
        dialog.setHeaderText("Share your experience");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label ratingLabel = new Label("Rating:");
        ComboBox<Integer> ratingCombo = new ComboBox<>();
        ratingCombo.getItems().addAll(1, 2, 3, 4, 5);
        ratingCombo.setValue(5);

        Label reviewLabel = new Label("Review:");
        TextArea reviewField = new TextArea();
        reviewField.setPrefRowCount(5);
        reviewField.setWrapText(true);

        content.getChildren().addAll(nameLabel, nameField, ratingLabel, ratingCombo, reviewLabel, reviewField);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new Review(nameField.getText(), ratingCombo.getValue(), 
                    java.time.LocalDate.now().toString(), reviewField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(review -> {
            if (review != null) {
                reviews.add(0, review);
                show();
            }
        });
    }

    private static class Review {
        private String name;
        private int rating;
        private String date;
        private String text;

        public Review(String name, int rating, String date, String text) {
            this.name = name;
            this.rating = rating;
            this.date = date;
            this.text = text;
        }

        public String getName() { return name; }
        public int getRating() { return rating; }
        public String getDate() { return date; }
        public String getText() { return text; }
    }
}

