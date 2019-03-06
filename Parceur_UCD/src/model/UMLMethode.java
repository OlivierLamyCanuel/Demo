package model;


import java.util.ArrayList;

/**
 * 
 * La classe UMLMethode permet d'instancier une methode d'une UMLClass.
 * Elle se compose d'une operation et d'une string correspondant au bloc 
 * du fichier où on la retrouve.
 * 
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 */

public class UMLMethode extends UMLComponent {


	private String operation;
	private ArrayList<UMLAttributes> arguments = new ArrayList<UMLAttributes>();
	private String output;
	private String name;

	
	/**
	 * Constructeur de la classe UMLMethode
	 * 
	 * @param operation
	 * @param details
	 */
	public UMLMethode(String operation, String details) {
		this.operation = operation;
		this.details = details;
		parceOutput();
		parceName();
		parceArguments();
	}

	@Override
    public String toString()
    {
	    return this.operation;
    }

	public ArrayList<UMLAttributes> getArguments() {
		return arguments;
	}

	public String getOutput() {
		return output;
	}

	public String getName() {
		return name;
	}


	/**
	 * Extrait le output de la fonction a partir de operation
	 */
	public void parceOutput(){

		String [] tmpString = operation.split(":");

		this.output = tmpString[tmpString.length-1].trim();

	}

	/**
	 * Parce le nom de la méthode et place la valeur dans l'attribut name
	 */
	public void parceName(){
		String [] tmpString = operation.split("\\(");

		this.name = tmpString[0].trim();
	}

	/**
	 * Parce les arguments de la méthode, sous la forme de UMLAttributes, et les places dans l'attribut arguments
	 */
	public void parceArguments(){

		String [] tmpString = operation.split("\\)");

		tmpString = tmpString[0].split("\\(");

		if(tmpString.length > 1){

		tmpString = tmpString[1].split(",");

			for(int i = 0; i<tmpString.length;i++){

				String [] splitArgs = tmpString[i].split(" : ");

				String arg = splitArgs[1] + " " + splitArgs[0];

				this.arguments.add(new UMLAttributes(arg, ""));

			}

		} else {
			this.arguments = null;
		}

	}

	public String getOperation() {
		return operation;
	}

}
