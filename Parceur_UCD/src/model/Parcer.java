package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Cette classe effectue l’ensemble du parcing du fichier soit:
 * la lecture, la validation du format,
 * le découpage des différents éléments,
 * le formatage de ces éléments,
 * la création des éléments UML et la gestion de la structure de donnée.
 *
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 */

public class Parcer {

	// Variables

	private ArrayList<UMLClass> arrayClass = new ArrayList<UMLClass>();
	private boolean validParse = true;
	private String details = "";
	private String errorMsg = "";
	private String errorDetails = "";

	// Setters Getters

	/**
	 * 
	 * @return errorMsg le type de l'erreur
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * 
	 * @return errorDetails les raisons de l'erreur
	 */
	public String getErrorDetails() {
		return errorDetails;
	}
	
	/**
	 * 
	 * @return validParse true si le parsing est valide sinon false.
	 */
	public boolean isValidParse() {
		return validParse;
	}

	/**
	 * Charge une a une les lignes du fichier et constuit des lignes jusqu'au point
	 * terminal.
	 *
	 * Lorsque le point terminal est atteint, la method parce est appelée pour
	 * pouvsuivre la parsing.
	 *
	 * Cette method valide aussi que la première ligne du fichier contient "MODEL
	 * <identifiant> ... "
	 *
	 * @param fileName
	 *            Nom du fichier a parce
	 */
	public void parceFile(String fileName) {

		if (!validExtension(fileName)) {
			validParse = false;
			errorMsg = "Extension du fichier invalide!";
			errorDetails = "Le fichier doit être en .ucd";

		}
		else {

			boolean validModel = false;
			int line = 1;
			String completeString = "";

			try {
				Scanner scan = new Scanner(new File(fileName));

				if (!scan.hasNext()) { //Validation que le fichier n'est pas vide
					validParse = false;
					errorMsg = "Le fichier est vide";
					errorDetails = "Le fichier ne contient rien";
				} else {

					while (scan.hasNext() && validParse) {

						String tmpString = scan.nextLine();
						String tmpDetails = tmpString;
						tmpString = tmpString.trim();

						if (tmpString.matches("MODEL [\\w$éèà\\s]+")) { // Valid que MODEL la synthaxe de MODEL et la retire de
							// la chaine a parce

							// Assure que le fichié a commencé par "MODEL <identifiant>"
							if (line == 1) {
								validModel = true;
								completeString = "";

							} else {
								validModel = false;
								validParse = false;
								errorMsg = "Synthaxe invalide!";
								errorDetails = "Le fichier ne commence pas par MODEL <IDENTIFIANT>";

							}

						} else if (tmpString.matches(";")) { // Cas terminal

							String[] endTab = tmpString.split(";");

							// completeString += endTab[0];
							checkDeclaration(completeString.trim());

							// Conserve ce qui reste après le ";"

							if (endTab.length == 2) {
								completeString = endTab[1];
							} else {
								completeString = "";
								details = "";
							}

						} else { // Cas de chaine non terminale

							completeString += tmpString + " ";
							details += tmpDetails + "\n";
						}

						line++;

					}

				}

				validArrayInheritance();

			} catch (FileNotFoundException E) {
				errorMsg = "Fichier inexistant";
				errorDetails = " Le fichier recherché n'existe pas.";
				validParse = false;
			} catch (Exception E){ //Erreur par défaut en cas d'exception imprévue.
				errorMsg = "Erreur de parcing!";
				errorDetails = "Une erreur s'est produite durant le parcing du fichier";
				validParse = false;
			}
		}
	}

	/**
	 * Valide la ligne fournie et appel la method de parcing correspondante.
	 *
	 * @param toParse
	 * @
	 */
	public void checkDeclaration(String toParse) {

		String CLASS = "CLASS ([\\w$éèà]+)";
		String ROLE = "CLASS ([\\w$éèà]+) (ONE|MANY|ONE_OR_MANY|OPTIONALLY_ONE|UNDEFINED)";
		String ATTRIBUTE = " ATTRIBUTES ([\\w$éèà]+ : [\\w$éèà\\-\\[\\]<>]+,?\\s?)*";
		String OPERATION = "OPERATIONS(\\s?[\\w$éèà]+\\s?" + //Nom methods
				"\\(([\\w$éèà]+ : [\\w$éèà\\-\\[\\]<>]+(,\\s)?)*\\)" + // Arg methods
				"( :\\s?[\\w$éèà\\-\\[\\]<>]*|),)*" + //retour methods
				"\\s?(\\s?[\\w$éèà]+\\s?" + //Nom method finale
				"\\(([\\w$éèà]+ : [\\w$éèà\\-\\[\\]<>]+(,\\s)?)*\\)" + //Arg method finale
				"( : [\\w$éèà\\-\\[\\]<>]*|))?"; //Retour method finale

		if(toParse.matches(CLASS + " ATTRIBUTES\\s*OPERATIONS\\s*")){
			String [] tmpParsed = toParse.split(" ");
			errorMsg = "Classe vide!";
			errorDetails = "La classe " + tmpParsed[1] + " ne contient pas d'attributs et d'opérations.";
			validParse = false;
		} else if (toParse.matches(CLASS + ATTRIBUTE + OPERATION)){
			parseClass(toParse);
		} else if (toParse.matches("GENERALIZATION [\\w$éèà]+ SUBCLASSES ([\\w$éèà]+,?\\s?)*")) {
			parceGeneralization(toParse);
		} else if (toParse.matches("(RELATION [\\w$éèà]+) ROLES " + ROLE + ",\\s?" + ROLE)) {
			parseRelation(toParse);
		} else if (toParse.matches("AGGREGATION CONTAINER " + ROLE + " PARTS " + ROLE)) {
			parseAggregation(toParse);
		} else {
			validParse = false;
			errorMsg = "Synthaxe invalide";
			errorDetails = "Une déclaration du fichier est invalide : \n" + toParse;
		}

	}

	// Fonctions de parcing spécifiques

	/**
	 * Cas class_dec Créée une nouvelle classe et spécifie ces attributs et
	 * opérations. La nouvelle classe est ensuite ajouté a arrayClass
	 *
	 * @param toParse
	 */
	public void parseClass(String toParse) {

		UMLClass tmpClass = new UMLClass(toParse, details.trim());

		// Validation de l'unicité de la classe
		if(validClassExists(tmpClass.getName())){
			validParse = false;
			errorMsg = "Présence de deux classes identiques.";
			errorDetails = "Deux classes sont nommées " + tmpClass.getName();
		} else {
			arrayClass.add(tmpClass);
		}
	}

	/**
	 * Cas generalization Identifie la classe root et y ajoute ses subClasses
	 *
	 * @param toParse
	 */
	public void parceGeneralization(String toParse) {

		String[] tmpParsed = toParse.split("SUBCLASSES");
		String rootClass = tmpParsed[0].replaceAll("GENERALIZATION ", "").trim();
		String[] subClasses = tmpParsed[1].split(",");

		for (int i = 0; i < subClasses.length; i++) {

			if (!validClassExists(subClasses[i].trim())) {
				validParse = false;
				errorMsg = "Sous-classe inexistante";
				errorDetails = "La sous-classe: " + subClasses[i].trim() + " n'existe pas.";
			} else if (!validClassExists(rootClass)) {
				validParse = false;
				errorMsg = "Classe mère inexistante";
				errorDetails = "La classe mère n'existe pas.";

			} else if (getClassFromName(subClasses[i].trim()).getParent() != null){
				validParse = false;
				errorMsg = "Héritage multiple non-supporté";
				errorDetails = "L'héritage multiple n'est pas supporté.";

			} else {
				getClassFromName(subClasses[i].trim()).setDetailsGen(details.trim());
				getClassFromName(rootClass).addSubClass(getClassFromName(subClasses[i].trim()));
				getClassFromName(subClasses[i].trim()).setParent(getClassFromName(rootClass));

			}
		}

	}

	/**
	 * Cas association
	 *
	 * @param toParse
	 */
	public void parseRelation(String toParse) {

		String[] tmpParsed = toParse.split("ROLES");
		String nomRelation = tmpParsed[0].replaceAll("RELATION ", "");

		UMLRelation relation = new UMLRelation(nomRelation, details);

		tmpParsed = tmpParsed[1].split(",");

		String class1 = getClassName(tmpParsed[0].trim());
		String class2 = getClassName(tmpParsed[1].trim());

		if (!validClassExists(class1)) {
			validParse = false;
			errorMsg = "Sous-classe inexistante";
			errorDetails = "La sous-classe: " + class1 + " n'existe pas.";
		} else if (!validClassExists(class2)) {
			validParse = false;
			errorMsg = "Sous-classe inexistante";
			errorDetails = "La sous-classe: " + class2 + " n'existe pas.";
		} else {

			getClassFromName(class1).addRelation(relation);
			getClassFromName(class2).addRelation(relation);
		}

	}

	/**
	 * Cas aggregation
	 *
	 * @param toParse
	 */
	public void parseAggregation(String toParse) {
		String[] tmpParsed = toParse.split("PARTS");

		// Separation de role et roles
		String classID = tmpParsed[0].trim();
		String agregate = tmpParsed[1].trim();

		// Extraction de role
		tmpParsed = classID.split(" ");
		classID = tmpParsed[3];

		// Extraction de roles
		tmpParsed = agregate.split(" ");
		agregate = getClassName(agregate);

		// creation des 2 classes Aggregates et ajout dans les listes

		if (!validClassExists(classID)) {
			validParse = false;
			errorMsg = "Classe manquante!";
			errorDetails = "La classe " + classID + " n'existe pas.";

		} else if (!validClassExists(agregate)) {
			validParse = false;
			errorMsg = "Classe manquante!";
			errorDetails = "La classe " + agregate + " n'existe pas.";
		} else {
			getClassFromName(agregate).addAggregates(new UMLAggregate("(A) C_" + classID, details));
			getClassFromName(classID).addAggregates(new UMLAggregate("(A) P_" + agregate, details));
		}

	}

	// Fonctions de gestion de classe et gestion d'arrayList

	/**
	 * Prend en parametre le nom d'une classe et retourne la classe du meme nom
	 *
	 * @param name
	 * @return une UMLClass
	 */
	public UMLClass getClassFromName(String name) {

		UMLClass tmpClass = null;

		for (int i = 0; i < arrayClass.size(); i++) {
			if (arrayClass.get(i).toString().equals(name)) {
				tmpClass = arrayClass.get(i);
			}
		}

		return tmpClass;
	}

	/**
	 * Method qui permet d'extraire le nom d'une class lorsque celle-ci est a la 2nd
	 * position
	 *
	 * @param stringClass
	 * @return String
	 */
	public static String getClassName(String stringClass) {

		String[] className = stringClass.split(" ");

		return className[1];

	}

	/**
	 * Retourne arrayClass si le parsing est valide. Retourne null autrement.
	 *
	 * @return arrayClass || null
	 */
	public ArrayList<UMLClass> getArrayClass() {

		return arrayClass;
	}

	/**
	 * Valide l'unicité de la classe par rapport a l'array
	 * @param newClass
	 */
	public boolean validClassExists(String newClass) {

		if (getClassFromName(newClass) != null) {

			return true;

		} else {
			return false;
		}

	}

	/**
	 * Valide l'extension par rapport au format .ucd
	 *
	 * @param fileName
	 * @return boolean
	 */
	public boolean validExtension(String fileName) {

		if (fileName.matches(".*\\.ucd")) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Valide l'héritage de la classe en marquant les classes visitées. Si une classe marquée est revisitée, l'héritage est invalide.
	 * @param umlClass
	 * @return
	 */
	public boolean validClassInheritance(UMLClass umlClass){
		boolean validInheritance = true;

		if(umlClass.getParent() != null) {

			if (umlClass.getParent().isMark() == true) {
				validInheritance = false;
			} else {
				umlClass.setMark(true);
				validInheritance = validClassInheritance(umlClass.getParent());
			}
		}

		return validInheritance;
	}

	/**
	 * Réinitialize toutes les marks des classes à faux
	 */
	public void resetMark(){

		for (int i = 0; i < arrayClass.size(); i++) {
			arrayClass.get(i).setMark(false);
		}

	}

	/**
	 * Valide l'héritage de toute les classes du modèle.
	 */
	public void validArrayInheritance(){

		for (int i = 0; i < arrayClass.size(); i++) {

			boolean validClass = validClassInheritance(arrayClass.get(i));

			if(!validClass){
				validParse = false;
				errorMsg = "Héritage invalide!";
				errorDetails = "La classe " + arrayClass.get(i).getName() + " est à la fois parent et enfant!";

				i = arrayClass.size();

			}
			resetMark();
		}
	}
}