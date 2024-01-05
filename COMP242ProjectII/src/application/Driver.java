package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Driver extends Application {

	private Button loadButton, prevButton, nextButton;
	private Text selectedFileText, equationsText, equationsTitleText;
	private DataStructure data = new DataStructure();
	private int equationIndex = 1;

	@Override
	public void start(Stage primaryStage) {
		try {
			// Scene and Stage
			Scene scene = new Scene(getUI(), 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Equation Processor");
			primaryStage.setMaximized(true);
			primaryStage.setScene(scene);
			primaryStage.show();

			// START Event Handlers

			// Saving the data into a file
			loadButton.setOnAction(event -> {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Select the file");
				fileChooser.setInitialDirectory(new File("C:/"));
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("242 Files (*.242)", "*.242");
				fileChooser.getExtensionFilters().add(extFilter);
				File selectedFile = fileChooser.showOpenDialog(primaryStage);
				if (selectedFile != null) {
					if (readFile(selectedFile)) {
						equationsText.setText(data.nextEquation());
						equationsTitleText.setText("Section 1:");
						if (data.hasNext())
							nextButton.setDisable(false);
					}
				}
			});

			nextButton.setOnAction(event -> {
				prevButton.setDisable(false);
				equationsText.setText(data.nextEquation());
				if (!data.hasNext())
					nextButton.setDisable(true);
				equationsTitleText.setText("Section " + ++equationIndex + ":");
			});

			prevButton.setOnAction(event -> {
				nextButton.setDisable(false);
				equationsText.setText(data.prevEquation());
				if (!data.hasPrev())
					prevButton.setDisable(true);
				equationsTitleText.setText("Section " + --equationIndex + ":");
			});

			// END Event Handlers

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	// Creating the main user interface
	private VBox getUI() {

		// File Section HBox
		HBox topHBox = new HBox(30);

		Text fileText = new Text("File:");
		fileText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

		selectedFileText = new Text("");
		selectedFileText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		selectedFileText.setFill(Color.LEMONCHIFFON);

		loadButton = new Button("Load");
		loadButton.setScaleX(2);
		loadButton.setScaleY(2);
		topHBox.setAlignment(Pos.CENTER);
		topHBox.getChildren().addAll(fileText, selectedFileText, loadButton);

		// Equation Section HBox
		HBox centerHBox = new HBox(30);

		equationsTitleText = new Text("Equation:");
		equationsTitleText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		equationsText = new Text("");
		equationsText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

		centerHBox.setAlignment(Pos.CENTER);
		centerHBox.getChildren().addAll(equationsTitleText, equationsText);
		// Prev and Next Section HBox
		HBox bottomHBox = new HBox(100);

		prevButton = new Button("Previous");
		prevButton.setDisable(true);
		prevButton.setScaleX(2);
		prevButton.setScaleY(2);

		nextButton = new Button("Next");
		nextButton.setDisable(true);
		nextButton.setScaleX(2);
		nextButton.setScaleY(2);

		bottomHBox.setAlignment(Pos.CENTER);
		bottomHBox.getChildren().addAll(prevButton, nextButton);
		// General HBox
		VBox mainHBox = new VBox(100);
		mainHBox.setAlignment(Pos.CENTER);
		mainHBox.getChildren().addAll(topHBox, centerHBox, bottomHBox);
		return mainHBox;
	}

	private boolean readFile(File file) {
		if (!data.checkDelimiters(file)) {
			selectedFileText.setText("Delimiters are not Balanced");
			selectedFileText.setFill(Color.RED);
			return false;
		}
		// Checking tags
		if (!data.checkTags(file)) {
			selectedFileText.setText("Tags are not Balanced");
			selectedFileText.setFill(Color.RED);
			return false;
		}
		selectedFileText.setText(file.getName());
		selectedFileText.setFill(Color.BLACK);

		try {
			// Reading from file
			Scanner scanFile = new Scanner(file);
			while (scanFile.hasNext()) {
				String p = scanFile.next().trim();

				if (p.equals("<section>")) {
					String section = "";
					while (!p.equals("</section>")) {
						p = scanFile.next().trim();

						switch (p) {
						case "<infix>":
							while (!p.equals("</infix>")) {
								p = scanFile.nextLine().trim();
								if (!p.isBlank()) {
									int startTagIndex = p.indexOf("<equation>");
									int endTagIndex = p.indexOf("</equation>");

									if (startTagIndex != -1 && endTagIndex != -1) {
										// Creating a Substring of the data inside the tags
										String equation = p.substring(startTagIndex + 10, endTagIndex);
										String postfix = data.infixToPostfix(equation);
										section += "Infix=> " + equation + " | Postfix => " + postfix + " | Result => "
												+ data.calculatePostfix(postfix) + "\n";
									}
								}
							}
							break;

						case "<postfix>":
							while (!p.equals("</postfix>")) {
								p = scanFile.nextLine().trim();

								if (!p.isBlank()) {
									int startIndex = p.indexOf("<equation>");
									int endIndex = p.indexOf("</equation>");

									if (startIndex != -1 && endIndex != -1) {
										// Creating a Substring of the data inside the tags
										String equation = p.substring(startIndex + 10, endIndex);
										String prefix = data.postfixToPrefix(equation);
										section += "Postfix=> " + equation + " | Prefix => " + prefix + " | Result => "
												+ data.calculatePrefix(prefix) + "\n";
									}
								}
							}
						}
					}
					data.saveEquation(section);
				}
			}
			scanFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

}
