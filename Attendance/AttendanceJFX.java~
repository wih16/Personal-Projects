

/* Stylistc: 


Code:


 */
import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.beans.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import java.io.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class AttendanceJFX extends Application{
    private String ProgramName = " ";
    private Stage stage;
    private File programFile;
    private int counter = 1;
    private File nameFile;
    private File emailFile;
    private File programDir;


    
    @Override
    public void start(Stage primaryStage){
	stage = primaryStage;
	Scene scene = ProgramScene(); //Creating the entry scene
	stage.setScene(scene); 
	stage.setTitle("Program Name");
	stage.show(); //Showing the "Enter Program Title" stage
	
    }//primary stage
    public Scene ProgramScene(){

	//Creating a grid for the Program Scene
	GridPane grid = new GridPane(); 
        grid.setAlignment(Pos.CENTER); //Aligning the grid in the center
	grid.setHgap(5); //setting the gaps between the columns to be 5 pixels
	grid.setVgap(10); //setting the caps between the rows to be 10 pixels
	//grid.setPadding(new Insets(5,5,5,5));
	grid.setStyle("-fx-background-color: #CCDBE1;");

	
	Label ProgramTitle = new Label("Program Title:");
	grid.add(ProgramTitle, 0, 1);

	TextField ProgramField = new TextField();
	grid.add(ProgramField, 1, 1);

	ProgramField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			
		public void handle(KeyEvent event){
		    if (event.getCode() == KeyCode.ENTER){ 
			try{
			    ProgramName = ProgramField.getText();
			    Boolean dir = new File("Attendance", ProgramName).mkdir();
			    programDir = new File("Attendance", ProgramName);
			    programFile = new File(programDir, ProgramName + ".txt");		    
			    if (!programFile.exists()){
				programFile.createNewFile();
			    }
			    nameFile = new File(programDir, "name.txt");
			    emailFile = new File(programDir, "email.txt");			
			}//try
			catch (IOException ex){
			    System.out.print("Could not create file");
			}//catch
		    
			stage.setTitle(ProgramName);
			stage.setScene(Attendance());
		    }
		}
	    });
	
	Button ProgramBtn = new Button();
	ProgramBtn.setText("Enter");
	ProgramBtn.setOnAction(new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent event){
		    try{
			ProgramName = ProgramField.getText();
			Boolean dir = new File("Attendance", ProgramName).mkdir();
			programDir = new File("Attendance", ProgramName);
			programFile = new File(programDir, ProgramName + ".txt");		    
			if (!programFile.exists()){
			    programFile.createNewFile();
			}
			nameFile = new File(programDir, "name.txt");
			emailFile = new File(programDir, "email.txt");			
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
        gridA.setAlignment(Pos.CENTER);
	gridA.setHgap(5);
	gridA.setVgap(10);
	gridA.setPadding(new Insets(10,10,10,10));
	gridA.setStyle("-fx-background-color: #CCDBE1;");
	

	Label Name = new Label("Name:");
	gridA.add(Name, 0, 1);

	TextField NameField = new TextField();
	gridA.add(NameField, 1, 1);

	Label Email = new Label("Email:");
	gridA.add(Email, 0, 2);

	TextField EmailField = new TextField();
	gridA.add(EmailField, 1, 2);


	GridPane gridB = new GridPane();
	
	final int numColumns = 3 ;
        int numRows = 3;
        for (int i = 0; i < numColumns; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / numColumns);
            gridB.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / numRows);
            gridB.getRowConstraints().add(rowConst);    
	}
       
	gridB.setStyle("-fx-background-color: #CCDBE1;");
	gridB.add(gridA, 1, 0);

	
	GridPane gridC = new GridPane();

	numRows = 30;
	for (int i = 0; i < numColumns; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / numColumns);
            gridC.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / numRows);
            gridC.getRowConstraints().add(rowConst);    
	}

	gridC.setAlignment(Pos.CENTER);
	gridC.setStyle("-fc-background-color: #CCDBE1;");
	gridC.setHgap(5);
	gridC.setVgap(5);
	gridC.setPadding(new Insets(10,10,10,10));

	gridB.add(gridC, 0, 1, 3, 2);
	
	Scene scene = new Scene(gridB, 800, 600);


	EmailField.setOnKeyPressed(new EventHandler<KeyEvent>() {

		public void handle(KeyEvent event){
		    if (event.getCode() == KeyCode.ENTER){
			String name = NameField.getText();
			String nameF = String.format("%-10s", name);
			String email = EmailField.getText();
			String emailF = String.format("%10s", email);
			try{
			    //writing to the main file
			    FileWriter fw = new FileWriter(programFile, true);
			    PrintWriter output = new PrintWriter(fw);
			    output.println(nameF + "    " + emailF);
			    output.close();

			    //writing to the name file
			    PrintWriter outputName = new PrintWriter(new FileWriter(nameFile, true));
			    outputName.println(name);
			    outputName.close();

			    //writing to the email file
			    PrintWriter outputEmail = new PrintWriter(new FileWriter(emailFile, true));
			    outputEmail.println(email);
			    outputEmail.close();			    
			}
			catch (IOException e){
			    System.out.println();
			}
			NameField.setText("");
			EmailField.setText("");
			Label nameL = new Label(name + ", " + email);
			int j;


			if (counter <= 30){
			    gridC.add(nameL, 0, counter);
			    counter++;
			}
			else if (counter <= 60){
			    j = counter - 30;
			    gridC.add(nameL, 1, j);
			    counter++;
			}
			else if (counter <= 90){
			    j = counter - 60;
			    gridC.add(nameL, 2, j);
			    counter++;
			}			
		    }
		    if (event.getCode() == KeyCode.ESCAPE){
			System.exit(0);
		    }
		}
	    });
		
	Button exit = new Button();
	exit.setText("Exit");
	exit.setOnAction(new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent event){
		    System.exit(0);
		}
	    });
       
	
	EventHandler<KeyEvent> keyExit = new EventHandler<KeyEvent>(){
		public void handle(KeyEvent event){
		    if (event.getCode() == KeyCode.ESCAPE){
			System.exit(0);
		    }
		}
	    };

	NameField.setOnKeyPressed(keyExit);
	    
	
	gridA.add(exit, 1, 3);
	return scene;
       
    }//Attendance Scene


}//public class



