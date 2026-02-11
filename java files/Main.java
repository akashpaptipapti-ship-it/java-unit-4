import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class Main extends Application {

    TableView<Student> table = new TableView<>();

    @Override
    public void start(Stage stage) {

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField deptField = new TextField();
        deptField.setPromptText("Department");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button insertBtn = new Button("Insert");
        Button viewBtn = new Button("View");
        Button deleteBtn = new Button("Delete");

        insertBtn.setOnAction(e -> insertStudent(
                nameField.getText(),
                deptField.getText(),
                emailField.getText()
        ));

        viewBtn.setOnAction(e -> viewStudents());

        deleteBtn.setOnAction(e -> deleteStudent());

        VBox layout = new VBox(10, nameField, deptField, emailField,
                insertBtn, viewBtn, deleteBtn, table);

        Scene scene = new Scene(layout, 500, 500);
        stage.setScene(scene);
        stage.setTitle("Student Management System");
        stage.show();
    }

    private void insertStudent(String name, String dept, String email) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:student.db")) {
            String sql = "INSERT INTO students(name, department, email) VALUES(?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, dept);
            stmt.setString(3, email);
            stmt.executeUpdate();
            System.out.println("Inserted Successfully!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void viewStudents() {
        table.getItems().clear();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:student.db")) {
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM students");

            while (rs.next()) {
                table.getItems().add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("email")
                ));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void deleteStudent() {
        Student selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:student.db")) {
                String sql = "DELETE FROM students WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
                viewStudents();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
