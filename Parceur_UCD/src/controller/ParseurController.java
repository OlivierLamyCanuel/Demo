package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import metrics.CSV;
import metrics.CalcMetrics;
import metrics.Metrics;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import model.*;

/**
 * La classe ParseurController permet l'intéraction en la view et le model du parseur.
 * On retrouve ici, la gestion des ListView (clique et modification) et l'affichage dans la section détails.
 * Y est également géré le bouton "Charger Fichier" permettant de verifier puis parcer le fichier 
 * avant d'en afficher son contenu
 * 
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 *
 */
public class ParseurController implements Initializable {

	// déclaration des listView du fichier XML
	@FXML
	private ListView<UMLClass> listClass = new ListView<UMLClass>();
	@FXML
	private ListView<UMLAttributes> listAttributes = new ListView<UMLAttributes>();
	@FXML
	private ListView<UMLMethode> listMethodes = new ListView<UMLMethode>();
	@FXML
	private ListView<UMLClass> listSousClasses = new ListView<UMLClass>();
	@FXML
	private ListView<UMLAssoAggrega> listAssoAgrega = new ListView<UMLAssoAggrega>();
	@FXML
	private ListView<Metrics> listMetrics = new ListView<Metrics>();
	
	@FXML
	private TextField fileName = new TextField();
	@FXML
	private TextArea details = new TextArea();
	
	private ArrayList<UMLClass> arrayClass = new ArrayList<UMLClass>();     // contient toute les classes du diagramme
	private ArrayList<CalcMetrics> metrics = new ArrayList<CalcMetrics>();     // contient toute les classes du diagramme
	private boolean fileLoaded = false;
	private boolean metricsDone = false;
	private boolean csvDone = false;
	private UMLClass classActive;
	private String fileLoadedName = "";
	private CSV csv;

	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		listClass.setOrientation(Orientation.HORIZONTAL);
		
		fileName.setOnKeyReleased(event -> {
			  if (event.getCode() == KeyCode.ENTER){
			     loadAndParce();
			  }
		});
		
		listClass.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

			Thread changeClassInfos = new Thread(() -> {
				Platform.runLater(() -> {
					if (newValue != null) {
						displayClassInfos(newValue);
					}
				});
			});
			changeClassInfos.start();
		});
		
		// listenner de la ListView "Sous-Classes"
		listSousClasses.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

			Thread changeSousClassInfos = new Thread(() -> {

				Platform.runLater(() -> {
					if (newValue != null) {		
						details.setText(newValue.getDetailsGen());
					}
				});
			});
			changeSousClassInfos.start();

		});

		// listenner de la ListView "Attributs"
		listAttributes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

			Thread changeDetailsAttributes = new Thread(() -> {

				Platform.runLater(() -> {

					details.setText(classActive.getDetails());
					clearSelection(0,1,1);

				});
			});
			changeDetailsAttributes.start();
		});

		// listenner de la ListView "Methodes"
		listMethodes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

			Thread changeDetailsMethodes = new Thread(() -> {

				Platform.runLater(() -> {

					details.setText(classActive.getDetails());
					clearSelection(1,0,1);

				});
			});
			changeDetailsMethodes.start();
		});

		// listenner de la ListView "Asso/Agrega"
		listAssoAgrega.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {


			Thread changeDetailsAssoAggrega = new Thread(() -> {
				Platform.runLater(() -> {
					
					clearSelection(1,1,0);
					if (newValue != null) {
						details.setText(newValue.getDetails());
						details.setText(newValue.getDetails());
					}
				});
			});
			changeDetailsAssoAggrega.start();
		});
		// listenner de la ListView "Asso/Agrega"
		listMetrics.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {


			Thread changeDetailsMetric = new Thread(() -> {
				Platform.runLater(() -> {
					
					if (newValue != null) {
						details.setText(newValue.getDefinition());
					}
				});
			});
			changeDetailsMetric.start();
		});
		
	}
	
	
	/**
	 * Vide les ListViews et affiche les infos d'une classe dans le ListView
	 * correspondant.
	 * 
	 * @param name
	 */
	public void displayClassInfos(UMLClass newClass) {

		clearList();

		for (int i = 0; i < arrayClass.size(); i++) {

			if (newClass.equals(arrayClass.get(i))) {

				classActive = arrayClass.get(i);
				
				listAttributes.getItems().addAll(classActive.getAttribute());
				listMethodes.getItems().addAll(classActive.getMethods());
				listSousClasses.getItems().addAll(classActive.getSubClass());
				listAssoAgrega.getItems().addAll(classActive.getRelations());
				listAssoAgrega.getItems().addAll(classActive.getAggregates());
				if(this.metricsDone == true) {
					listMetrics.getItems().addAll(metrics.get(i).getMetrics());
				}
					
				details.setText(classActive.getDetails());

			}
		}
	}
	
	
	
	/**
	 * Retire la selection dans les ListView mise en parametre, sélectionnée par leur code
	 * 
	 * @param liste1
	 * @param liste2
	 */
	public void clearSelection(int code, int code2, int code3) {
		
			if(code == 1) {
				listAttributes.getSelectionModel().clearSelection();
			}
			if(code2 == 1) {
				listMethodes.getSelectionModel().clearSelection();		
			}
			if(code3 == 1) {
				listAssoAgrega.getSelectionModel().clearSelection();
			}
	}

	/**
	 * Vide les ListViews (pour pouvoir y mettre ensuite les nouveaux élements)
	 * 
	 */
	public void clearList() {

		listAttributes.getItems().clear();
		listMethodes.getItems().clear();
		listAssoAgrega.getItems().clear();
		listSousClasses.getItems().clear();
		listMetrics.getItems().clear();
		details.clear();
	}

	/**
	 * Gestion du clique du bouton "Charger Fichier". 
	 * Appelle la methode pour loader et parcer le fichier
	 * 
	 * @param event
	 */
	public void onClick() {
		
		loadAndParce();
		
	}
	/**
	 * Gestion du clique du bouton "Calculer Métriques". 
	 * Boucle sur chacune des classes, et ajoute leur metriques
	 * dans la list metrics.
	 * Lance un popup si le calcul n'est pas possible.
	 * 
	 */
	public void calculMetric() {
		
		if(this.fileLoaded == true && this.metricsDone == false){
			if(metrics.size() != 0 ) {
				metrics.clear();
			}
			for(int i=0; i< arrayClass.size(); i++) {
				metrics.add(new CalcMetrics(arrayClass.get(i), arrayClass));
				
				if(arrayClass.get(i).equals(classActive)){
					listMetrics.getItems().addAll(metrics.get(i).getMetrics());
				}
				
			}
			this.metricsDone = true;
		}
		else {
			if(this.metricsDone == false)
			errorPopup("Calcul impossible.", "Veuillez d'abord parser un fichier.");
		}
		
		
	}
	/**
	 * Gestion du clique du bouton "Charger Fichier". 
	 * Genere le CSV.
	 * Lance un popup si la création du CSV n'est pas possible
	 */
	public void generateCSV() {

		if(this.metricsDone == true && this.csvDone == false) {
			String tmpString = fileLoadedName.replaceAll("/","\\.");
	
			tmpString = tmpString.replaceAll("\\\\" , "\\.");
			
			String[] csvFileName = tmpString.split("\\.");
		
			csv = new CSV(metrics, csvFileName[csvFileName.length-2]);
			this.csvDone = true;
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Création fichier.");
			alert.setHeaderText("CSV.");
			alert.setContentText("Le fichier " +  csvFileName[csvFileName.length-2] + ".csv a été créé.");

			alert.showAndWait();
		}
		else {
			if(this.csvDone == false)
			errorPopup("Création CSV impossible.", "Veuillez d'abord calculer les métriques.");

		}
		
	}
	
	
	/**
	 * Charge le fichier puis le parse.
	 * La liste des classes est récupérée puis les classes sont affichées
	 * dans la ListView.
	 * 
	 */
	public void loadAndParce() {
		
		if(fileLoadedName != fileName.getText()) {
			
			fileLoaded = false;
			
			if (fileLoaded == false) {
				
				fileLoadedName = fileName.getText();
				Parcer parcer = new Parcer();
				parcer.parceFile(fileName.getText());
				arrayClass = parcer.getArrayClass();
	
				if (parcer.isValidParse()) {
	
					this.fileLoaded = true;
					this.metricsDone = false;
					this.csvDone = false;
					listClass.getItems().clear();
					clearList();
					
					if(metrics.size() != 0)metrics.clear();
					
					listClass.getItems().addAll(arrayClass);
										
				} else {
					errorPopup(parcer.getErrorMsg(), parcer.getErrorDetails());
				}
			}
		}
	}

	/**
	 * Affichage popup d'alerte personnalisable en cas d'erreur sur le fichier.
	 *
	 * @param typeError
	 * @param message
	 */
	public static void errorPopup(String typeError, String message) {

		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText(typeError);
		alert.setContentText(message);

		alert.showAndWait();
	}
}
