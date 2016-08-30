import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.beans.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import java.io.*;
    
public class AttendanceJFX extends Application{
    private String ProgramName = " ";
    private Stage stage;
    private File programFile;
    private int j = 4;
    @Override
    public void start(Stage primaryStage){
	stage = primaryStage;
	Scene scene = ProgramScene();
	stage.setScene(scene);
	stage.setTitle("Program Name");
	stage.show();
	
    }//primary stage
    public Scene ProgramScene(){
	GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
	grid.setHgap(5);
	grid.setVgap(10);
	grid.setPadding(new Insets(5,5,5,5));
	grid.setStyle("-fx-background-color: #bed6ff;");

	
	Label ProgramTitle = new Label("Program Title:");
	grid.add(ProgramTitle, 0, 1);

	TextField ProgramField = new TextField();
	grid.add(ProgramField, 1, 1);

	Button ProgramBtn = new Button();
	ProgramBtn.setText("Enter");
	ProgramBtn.setOnAction(new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent event){
		    ProgramName = ProgramField.getText();
		    programFile = new File(ProgramName + ".txt");
		    try{
			if (!programFile.exists()){
			    programFile.createNewFile();
			}
		    }//try
		    catch (IOException ex){
			System.out.print("Could not create file");
		    }//catch
		    stage.setTitle(ProgramName);
		    stage.setScene(Attendance());
		}

	    });
	grid.add(ProgramBtn, 1, 2);
	Scene scene = new Scene(grid, 400, 400);
	return scene;
    }//ProgramScene

    protected Scene Attendance(){
	GridPane gridA = new GridPane();
        gridA.setAlignment(Pos.TOP_LEFT);
	gridA.setHgap(5);
	gridA.setVgap(10);
	gridA.setPadding(new Insets(10,10,10,10));
	gridA.setStyle("-fx-background-color: #bed6ff;");
	

	Label Name = new Label("Name:");
	gridA.add(Name, 0, 1);

	TextField NameField = new TextField();
	gridA.add(NameField, 1, 1);

	Label Email = new Label("Email:");
	gridA.add(Email, 0, 2);

	TextField EmailField = new TextField();
	gridA.add(EmailField, 1, 2);

	Scene scene = new Scene(gridA, 800, 800);

	Button enter = new Button();
	enter.setText("Enter");
	enter.setOnAction(new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent event){
		    String name = NameField.getText();
		    String nameF = String.format("%-10s", name);
		    String email = EmailField.getText();
		    String emailF = String.format("%10s", email);
		    try{
			FileWriter fw = new FileWriter(programFile, true);
			PrintWriter output = new PrintWriter(fw);
			output.println(nameF + "    " + emailF);
			output.close();
		    }
		    catch (IOException e){
			System.out.println();
		    }
		    NameField.setText("");
		    EmailField.setText("");
		    Label nameL = new Label(name + ", " + email);
		    if (j < 19){
			gridA.add(nameL, 2, j);
			j++;
		    }
		    if (j > 18 && j < 34){
			int k = j;
			k -= 15;
			gridA.add(nameL, 3, k);
			j++;
		    }
		    if (j > 33 && j < 49){
			int k = j;
			k -= 30;
			gridA.add(nameL, 4, k);
			j++;
		    }
		    if (j > 48 && j < 63){
			int k = j;
			k -= 45;
			gridA.add(nameL, 5, k);
			j++;
			
		    }//
		}
	    }//event handler
	    );
	
	Button exit = new Button();
	exit.setText("Exit");
	exit.setOnAction(new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent event){
		    System.exit(0);
		}
	    });
	
	gridA.add(enter, 0, 3);

	gridA.add(exit, 1, 3);
	return scene;
       
    }//Attendance Scene


}//public class
