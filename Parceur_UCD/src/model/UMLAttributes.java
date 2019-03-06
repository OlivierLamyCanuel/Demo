package model;

/**
 * 
 * La classe UMLAttribute permet d'instancier un attribut d'une UMLClass.
 * Elle se compose d'une String attribut et d'une string correspondant au bloc 
 * du fichier où on la retrouve.
 * 
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 */

public class UMLAttributes extends UMLComponent{
	
	private String attribute;
	private String type;
	private String name;
	
	/**
	 * Constructeur de la classe UMLAttributes
	 * @param attribute
	 * @param details
	 */
	public UMLAttributes(String attribute, String details) {
		this.attribute = attribute;
		this.details = details;
		String tmpString[] = attribute.split(" ");

			this.type = tmpString[0].trim();
			this.name = tmpString[1].trim();
	}
	
	@Override
	   public String toString()
	   {
		   return this.attribute;
	   }

	/**
	 * 
	 * @return type le type de l'attribut
	 */
	public String getType() { return type; }

	
	public void setType(String type) { this.type = type; }
}
