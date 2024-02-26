import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class BatchUpdateComparison extends Application {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/database_name";
    private static final String USER = "username";
    private static final String PASSWORD = "password";

    private DBConnectionPanel dbConnectionPanel;
    private Button insertWithoutBatchButton, insertWithBatchButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Batch Update Comparison");

        dbConnectionPanel = new DBConnectionPanel();

        insertWithoutBatchButton = new Button("Insert Without Batch");
        insertWithoutBatchButton.setOnAction(e -> insertWithoutBatch());

        insertWithBatchButton = new Button("Insert With Batch");
        insertWithBatchButton.setOnAction(e -> insertWithBatch());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        grid.add(dbConnectionPanel, 0, 0, 2, 1);
        grid.add(insertWithoutBatchButton, 0, 1);
        grid.add(insertWithBatchButton, 1, 1);

        Scene scene = new Scene(grid, 400, 200);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void insertWithoutBatch() {
        try (Connection connection = dbConnectionPanel.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Temp VALUES (?, ?, ?)");

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < 1000; i++) {
                preparedStatement.setDouble(1, Math.random());
                preparedStatement.setDouble(2, Math.random());
                preparedStatement.setDouble(3, Math.random());
                preparedStatement.executeUpdate();
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Insert Without Batch took: " + (endTime - startTime) + " milliseconds");

            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertWithBatch() {
        try (Connection connection = dbConnectionPanel.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Temp VALUES (?, ?, ?)");

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < 1000; i++) {
                preparedStatement.setDouble(1, Math.random());
                preparedStatement.setDouble(2, Math.random());
                preparedStatement.setDouble(3, Math.random());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

            long endTime = System.currentTimeMillis();
            System.out.println("Insert With Batch took: " + (endTime - startTime) + " milliseconds");

            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class DBConnectionPanel extends GridPane {
        private TextField urlField, userField, passwordField;

        public DBConnectionPanel() {
            setHgap(5);
            setVgap(5);

            urlField = new TextField("jdbc:mysql://localhost:3306/your_database_name");
            userField = new TextField("your_username");
            passwordField = new PasswordField();

            add(new Label("Database URL:"), 0, 0);
            add(urlField, 1, 0, 2, 1);
            add(new Label("Username:"), 0, 1);
            add(userField, 1, 1);
            add(new Label("Password:"), 0, 2);
            add(passwordField, 1, 2);
            add(new Button("Connect to Database"), 0, 3, 3, 1);
        }

        public Connection getConnection() throws SQLException {
            String url = urlField.getText();
            String user = userField.getText();
            String password = passwordField.getText();
            return DriverManager.getConnection(url, user, password);
        }
    }
}
