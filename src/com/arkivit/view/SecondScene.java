package com.arkivit.view;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * 
 * @author Kevin Olofsson, Roberto Blanco, Saikat Talukder
 * The view of the application
 *
 */

public class SecondScene{
	
	private GridPane firstGrid, secondGrid;
	private Scene secondScene;
	private Button btnOpenFile, btnSetPathLibOffice;
	private Button btnOverwrite;
	private Button btnSaveAs;
	private Button btnConvert;
	private Button btnBack, btnDelete;
	private TextField openTxtField, setLibreOfficePathField;// = new TextField();
	private TextField saveTxtField; //= new TextField();
	private Label dirLabel, setLibOffPathLabel;// = new Label("Directory");
	private Label outputLabel;// = new Label("Output");
	private Label mapLabel;// = new Label("Map");
	private Label overwriteLabel, waitLabel, confidentialLabel, personalDataLabel;
	private CheckBox mappCheckBox;
	private CheckBox overwriteCheckBox, confidentialCheckBox, personalDataBox, confidentialYesBox, personalDataYesBox;
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	private ProgressBar pb;
	private ProgressIndicator pi;
	private FileChooser fChooser = new FileChooser();
	
	
	/**
	 * Adds all the components for the second scene to the second scene
	 */
	public void startSecondScene()
	{
		VBox root2 = new VBox();
		setLibreOfficePathField = new TextField();
		openTxtField = new TextField();
		saveTxtField = new TextField();
		setLibOffPathLabel = new Label("LibreOffice Path");
		dirLabel = new Label("Directory");
		outputLabel = new Label("Output");
		mapLabel = new Label("Map");
		
		mappCheckBox = new CheckBox("");
		mappCheckBox.setDisable(true);
		Tooltip tooltip = new Tooltip();
		tooltip.setText(
		    "Replacing Illegal characters 'å, ä, ö, ü with aa, ae, oe, ue'\n" +
		    "and copies the original content to a backup folder. \n"  
		);
		mappCheckBox.setTooltip(tooltip);
		
		overwriteLabel = new Label("Overwrite");
		overwriteCheckBox = new CheckBox("");
		overwriteCheckBox.setDisable(true);
		tooltip = new Tooltip();
		tooltip.setText(
		    "By checking overwrite you will overwrite \n" +
		    "and replace the original content. \n"  
		);
		overwriteCheckBox.setTooltip(tooltip);
		
		btnSetPathLibOffice = new Button("Set LibreOffice path...");
		tooltip = new Tooltip();
		String exPathForWin = "Exempel path in Windows OS: C:/Program Files/LibreOffice/program/soffice.exe or \nC:/Program Files(x86)/LibreOffice/program/soffice.exe";
		String exPathForLin = "\nExempel path in Linux OS: /usr/bin/soffice";
		String exPathForMac = "\nExempel path in Mac OS: /Applications/LibreOffice.app/Contents/MacOS/";
		tooltip.setText(exPathForWin + exPathForLin + exPathForMac);	
		btnSetPathLibOffice.setTooltip(tooltip);
		btnSetPathLibOffice.setId("saveButton");
		btnSetPathLibOffice.setMaxWidth(Double.MAX_VALUE);
		
		btnOpenFile = new Button("Select folder...");
		btnOpenFile.setId("saveButton");
		btnOpenFile.setMaxWidth(Double.MAX_VALUE);
		
		btnOverwrite = new Button("Select directory...");
		btnOverwrite.setDisable(true);
		btnOverwrite.setId("saveButton");
		btnOverwrite.setMaxWidth(Double.MAX_VALUE);
		final Tooltip tooltip3 = new Tooltip();
		tooltip3.setText(
		    "Choose the directory where you want to save the  \n" +
		    "original content. \n"  
		);
		btnOverwrite.setTooltip(tooltip3);
		
		btnDelete = new Button("X");
		btnDelete.setId("deleteButton");
		btnDelete.setDisable(true);
		btnDelete.setMaxWidth(Double.MAX_VALUE);
		final Tooltip tooltip4 = new Tooltip();
		tooltip4.setText(
		    "Remove the chosen directory for the backup content"
		);
		tooltip4.setId("tooltip");
		btnDelete.setTooltip(tooltip4);
		
		confidentialLabel = new Label("Confidential");
		confidentialYesBox = new CheckBox("Yes ");
		final Tooltip confidentialYesTooltip = new Tooltip();
		confidentialYesTooltip.setText(
		    "Set 'Sekretessgrad hos myndighet' to 'JA'\n"
		);
		confidentialYesBox.setTooltip(confidentialYesTooltip);
		confidentialCheckBox = new CheckBox("No");
		final Tooltip confidentialTooltip = new Tooltip();
		confidentialTooltip.setText(
		    "Set 'Sekretessgrad hos myndighet' to 'NEJ'\n"
		);
		confidentialCheckBox.setTooltip(confidentialTooltip);
		personalDataLabel = new Label("Personal Data");
		personalDataYesBox = new CheckBox("Yes ");
		final Tooltip personalDataYesTooltip = new Tooltip();
		personalDataYesTooltip.setText(
		    "Set 'Behandling av personuppgifter' to 'JA'\n"
		);
		personalDataYesBox.setTooltip(personalDataYesTooltip);
		personalDataBox = new CheckBox("No");
		final Tooltip personalDataTooltip = new Tooltip();
		personalDataTooltip.setText(
		    "Set 'Behandling av personuppgifter' to 'NEJ'\n"
		);
		personalDataBox.setTooltip(personalDataTooltip);
		
		btnSaveAs = new Button("Save As...");
		btnSaveAs.setDisable(true);
		btnSaveAs.setId("saveButton");
		btnSaveAs.setMaxWidth(Double.MAX_VALUE);
		
		btnConvert = new Button("Create");
		btnConvert.setDisable(true);
		btnConvert.setId("saveButton");
		//pb = new ProgressBar(0);
		//pb.setMaxWidth(Double.MAX_VALUE);
		pi = new ProgressIndicator();
		waitLabel = new Label("Please wait...");
		waitLabel.setId("waitLabel");
		btnBack = new Button("◀ Back");
		btnBack.setId("saveButton");
		
		firstGrid = new GridPane();
		firstGrid.setAlignment(Pos.CENTER);
		firstGrid.setHgap(10);
		firstGrid.setVgap(10);
		firstGrid.setPadding(new Insets(250, 100, 50, 100));
		HBox hBox = new HBox(mapLabel, mappCheckBox);
		hBox.setAlignment(Pos.CENTER_LEFT);
		HBox.setMargin(mappCheckBox,new Insets(10,10,10,10));
		HBox hBox2 = new HBox(overwriteLabel, overwriteCheckBox);
		hBox2.setAlignment(Pos.CENTER_LEFT);
		HBox.setMargin(overwriteCheckBox,new Insets(10,10,10,10));
		HBox hBox3 = new HBox(btnOverwrite, btnDelete);
		hBox3.setAlignment(Pos.CENTER_RIGHT);
		HBox hBox4 = new HBox(confidentialLabel, confidentialYesBox, confidentialCheckBox);
		hBox4.setAlignment(Pos.CENTER_LEFT);
		HBox.setMargin(confidentialLabel,new Insets(0,10,0,0));
		HBox hBox5 = new HBox(personalDataLabel, personalDataYesBox, personalDataBox);
		hBox5.setAlignment(Pos.CENTER_RIGHT);
		HBox.setMargin(personalDataLabel,new Insets(0,10,0,0));
		
		
		secondGrid = new GridPane();
		secondGrid.setAlignment(Pos.BASELINE_LEFT);
		secondGrid.setHgap(10);
		secondGrid.setVgap(10);
		secondGrid.setPadding(new Insets(-40, 0, 0, 15));
		
		//Set path for LibreOffice
		firstGrid.add(setLibOffPathLabel, 0, 0);
		firstGrid.add(setLibreOfficePathField, 1, 0);
		setLibreOfficePathField.setEditable(false);
		firstGrid.add(btnSetPathLibOffice, 2, 0);
		
		//Open dir components
		firstGrid.add(dirLabel, 0, 1);
		firstGrid.add(openTxtField, 1, 1);
		openTxtField.setEditable(false);
		firstGrid.add(btnOpenFile, 2, 1);

		//mapp
		firstGrid.add(hBox, 0, 2);
		
		//overwrite
		firstGrid.add(hBox2, 1, 2);
		firstGrid.add(hBox3, 2, 2);
		//firstGrid.add(btnOverwrite, 2, 1);
		
		//classification and personal data
		firstGrid.add(hBox4, 1, 3);
		firstGrid.add(hBox5, 2, 3);

		//Out dir components
		firstGrid.add(outputLabel, 0, 4);
		firstGrid.add(saveTxtField, 1, 4);
		saveTxtField.setEditable(false);
		firstGrid.add(btnSaveAs, 2, 4);
		
		//Create Excel button
		firstGrid.add(btnConvert, 1, 5);
		
		/*firstGrid.add(pb, 1, 5);
		pb.setVisible(false);*/
		
		pi.setMinSize(80, 80);
		firstGrid.add(pi, 1, 6);
		firstGrid.add(waitLabel, 2, 6);
		pi.setVisible(false);
		waitLabel.setVisible(false);
		
		//back button
		secondGrid.add(btnBack, 0, 0);
		
		root2.getChildren().add(firstGrid);
		root2.getChildren().add(secondGrid);
		secondScene = new Scene(root2, 800, 620);
		secondScene.getStylesheets().add("resources/style/style.css");
	}
	
	
	/**
	 * Adds action listeners to all the buttons in second scene
	 * @param listenForEvent
	 */
	public void addActionListenerForButton(EventHandler<ActionEvent> listenForEvent)
	{
		btnSetPathLibOffice.setOnAction(listenForEvent);
		btnOpenFile.setOnAction(listenForEvent);
		btnSaveAs.setOnAction(listenForEvent);
		btnConvert.setOnAction(listenForEvent);
		btnBack.setOnAction(listenForEvent);
		btnOverwrite.setOnAction(listenForEvent);
		mappCheckBox.setOnAction(listenForEvent);
		overwriteCheckBox.setOnAction(listenForEvent);
		btnDelete.setOnAction(listenForEvent);
		confidentialYesBox.setOnAction(listenForEvent);
		confidentialCheckBox.setOnAction(listenForEvent);
		personalDataYesBox.setOnAction(listenForEvent);
		personalDataBox.setOnAction(listenForEvent);
	}
		
	/**
	 * Getters and setters for variables
	 * @return s all the variables that have getters
	 */
	
	
	public Button getBtnOpenFile() {
		return btnOpenFile;
	}

	public Button getBtnSetPathLibOffice() {
		return btnSetPathLibOffice;
	}

	public Button getBtnSaveAs() {
		return btnSaveAs;
	}

	public Button getBtnConvert() {
		return btnConvert;
	}

	public TextField getOpenTxtField() {
		return openTxtField;
	}

	public TextField getSaveTxtField() {
		return saveTxtField;
	}
	
	public TextField getSetLibreOfficePathField() {
		return setLibreOfficePathField;
	}

	public FileChooser getfileChooser() {
		return fChooser;
	}

	public DirectoryChooser getDirectoryChooser() {
		return directoryChooser;
	}

	public Scene getSecondScene() {
		return secondScene;
	}

	public Button getBtnBack() {
		return btnBack;
	}

	public ProgressBar getPb() {
		return pb;
	}

	public ProgressIndicator getPi() {
		return pi;
	}

	public CheckBox getMappCheckBox() {
		return mappCheckBox;
	}

	public Button getBtnOverwrite() {
		return btnOverwrite;
	}

	public CheckBox getOverwriteCheckBox() {
		return overwriteCheckBox;
	}
	
	public Button getBtnDelete() {
		return btnDelete;
	}
	
	public Label getWaitLabel() {
		return waitLabel;
	}

	public CheckBox getConfidentialCheckBox() {
		return confidentialCheckBox;
	}

	public CheckBox getPersonalDataBox() {
		return personalDataBox;
	}

	public CheckBox getConfidentialYesBox() {
		return confidentialYesBox;
	}

	public CheckBox getPersonalDataYesBox() {
		return personalDataYesBox;
	}

}
