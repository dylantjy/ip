package bruh;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {
    private final GuiLogic logic = new GuiLogic();

    @Override
    public void start(Stage stage) {
        final ListView<Message> messages = new ListView<>();
        messages.setFocusTraversable(false);

        // Load avatars from classpath
        final Image imgBruh = loadImage("/img/bruh.png");
        final Image imgUser = loadImage("/img/user.png");

        // Custom cell: left for Bruh, right for You
        messages.setCellFactory(lv -> new ListCell<>() {
            private static final double AVATAR = 34;

            private final HBox container = new HBox(8);
            private final Label bubble = new Label();
            private final Region spacer = new Region();
            private final ImageView avatar = new ImageView();

            {
                bubble.getStyleClass().add("bubble");
                bubble.setWrapText(true);
                bubble.setMaxWidth(360);

                avatar.setFitWidth(AVATAR);
                avatar.setFitHeight(AVATAR);
                avatar.setClip(new Circle(AVATAR / 2.0, AVATAR / 2.0, AVATAR / 2.0));

                HBox.setHgrow(spacer, Priority.ALWAYS);
                container.setPadding(new Insets(6, 10, 6, 10));
            }

            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);
                setText(null);
                setGraphic(null);
                if (empty || msg == null) return;

                bubble.setText(msg.getText());
                bubble.getStyleClass().removeAll("user", "bruh");
                container.getChildren().clear();

                if (msg.isFromUser()) {
                    avatar.setImage(imgUser);
                    bubble.getStyleClass().add("user");
                    container.setAlignment(Pos.CENTER_RIGHT);
                    container.getChildren().addAll(spacer, bubble, avatar); // right
                } else {
                    avatar.setImage(imgBruh);
                    bubble.getStyleClass().add("bruh");
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.getChildren().addAll(avatar, bubble, spacer); // left
                }
                setGraphic(container);
            }
        });

        final TextField input = new TextField();
        input.setPromptText("Try: list | todo read book | deadline X /by 2025-10-23 | event Y /from ... /to ... | find book | bye");
        final Button send = new Button("Send");

        final HBox bar = new HBox(8, input, send);
        bar.setPadding(new Insets(8));
        HBox.setHgrow(input, Priority.ALWAYS);

        final VBox root = new VBox(8, messages, bar);
        root.setPadding(new Insets(8));
        VBox.setVgrow(messages, Priority.ALWAYS);

        // Greeting
        messages.getItems().add(new Message("Hello! I'm Bruh — what can I do for you?", false));

        // Submit behavior
        Runnable submit = () -> {
            String text = input.getText();
            if (text == null || text.isBlank()) return;

            messages.getItems().add(new Message(text, true));        // you (right)
            String reply = logic.handle(text);
            messages.getItems().add(new Message(reply, false));      // bruh (left)
            input.clear();
            messages.scrollTo(messages.getItems().size() - 1);

            if (logic.isExitRequested()) {
                stage.close();
            }
        };

        send.setOnAction(e -> submit.run());
        input.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) submit.run(); });

        Scene scene = new Scene(root, 480, 620);
        var css = getClass().getResource("/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.err.println("Warning: /styles.css not found; continuing without stylesheet.");
        }
        stage.setTitle("bruh");
        stage.setScene(scene);
        stage.show();
    }

    private Image loadImage(String path) {
        var url = getClass().getResource(path);
        if (url == null) {
            System.err.println("Image not found on classpath: " + path);
            // 1×1 transparent fallback
            return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAoMBgZ6m3QAAAAAASUVORK5CYII=");
        }
        return new Image(url.toExternalForm(), true);
    }
}

