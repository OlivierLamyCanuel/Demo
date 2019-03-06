package metrics;

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;


/**
 * La classe CSV permet de récupérer la liste des metrique de chaque classe
 * afin de générer un fichier CSV ayant pour ligne les classes 
 * et pour colonnes les métriques.
 * 
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 */
public class CSV {
	
	
	private ArrayList<CalcMetrics> metrics = new ArrayList<CalcMetrics>();
	
	//Delimiteur pour le CSV
	private final String COMMA_DELIMITER = ",";
	private final String NEW_LINE_SEPARATOR = "\n";
	private final String FILE_HEADER = "Nom, ANA, NOM, NOA, ITC, ETC, CAC, DIT, CLD, NOC, NOD";
	private String filename;

	
	/**
	 * Constructeur de la classe CSV
	 * @param metrics contient des objets calcMetrics contenant un arraylist de toutes les metriques de la classe
	 * @param fileName nom du .ucd pour nommer le .csv
	 */
	public CSV(ArrayList<CalcMetrics> metrics, String fileName) {
		this.metrics = metrics;
		writeCsvFile( fileName+ ".csv");
	}
	

	/**
	 * Méthode permettant de générer le CSV.
	 * Va parcourir les métriques de chacune des classes
	 * et inscrire dans le .csv le résulats des calculs métrique, 
	 * une classe par ligne.
	 * 
	 * @param fileName
	 */
	public void writeCsvFile(String fileName) {
		
		
		FileWriter fileWriter = null;
				
		try {
			fileWriter = new FileWriter(fileName);

			//écrit le header et passe à la ligne
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			//met tout le contenu d'un CalcMetric dans le CSV
			for (CalcMetrics metric : metrics) {
				
				
				//Nom
				fileWriter.append((metric.getUmlClass().getName()));
				fileWriter.append(COMMA_DELIMITER);
				
				//ANA
				fileWriter.append(String.valueOf(metric.getMetrics().get(0).getValueF()));
				fileWriter.append(COMMA_DELIMITER);
	
				//le reste des métriques
				for (int i = 1 ;i < metric.getMetrics().size(); i++) {
					fileWriter.append(String.valueOf(metric.getMetrics().get(i).getValue()));
					fileWriter.append(COMMA_DELIMITER);
				}
				
				fileWriter.append(NEW_LINE_SEPARATOR);
			}

			
			
			System.out.println("CSV créé.");
			
		} catch (Exception e) {
			System.out.println("Erreur dans la création du CSV.");
			e.printStackTrace();
		} finally {
			
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
                e.printStackTrace();
			}
			
		}
	}
}




