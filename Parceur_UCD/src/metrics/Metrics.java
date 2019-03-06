package metrics;

/**
 * Classe permettant de définir une métrique 
 * par son nom, sa valeur et sa définition
 * 
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 *
 */
public class Metrics {
	
	protected String name;
	protected int value = -1;
	protected float valueF;
	protected String definition;
	
	/**
	 * Constructeur de la classe metrics.
	 * Instancie une métrique selon son nom et sa valeur INTEGER.
	 * Il va également aller chercher la définition de la métrique 
	 * selon le nom passé en paramètre.
	 * 
	 * @param name
	 * @param value
	 */
	public Metrics(String name, int value) {
		this.name = name;
		this. value = value;
		setDef();
	}
	
	/**
	 * Constructeur de la classe metrics.
	 * Instancie une métrique selon son nom et sa valeur FLOAT.
	 * Il va également aller chercher la définition de la métrique 
	 * selon le nom passé en paramètre.
	 * 
	 * @param name
	 * @param value
	 */
	public Metrics(String name, float valueF) {
		this.name = name;
		this. valueF =  (float) (Math.floor(valueF * 100) / 100);
		setDef();
	}
	
	/**
	 * 
	 * @return value valeur d'une métrique
	 */
	public int getValue() {
		return this.value;
	}
	
	/**
	 * 
	 * @return valueF valeur d'une metrique en float
	 */
	public float getValueF() {
		return this.valueF;
	}
	
	/**
	 * 
	 * @return definition définition d'une métrique
	 */
	public String getDefinition() {
		return this.definition;
	}
	
	//retourne le nom avec la valeur en float 
	//si la value est un float sinon la value en integer
	@Override
	public String toString() {
		
		if(this.value == -1) {
			return name + " = " + valueF;
		}
		
		return name + " = " + value;
		
	}
	
	
	/**
	 * 
	 * @return name nom de la métrique
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Méthode qui permet d'associer la définition d'une métrique à son nom.
	 */
	public void setDef() {
				
		switch(this.name) {
			case "ANA" : this.definition = "Nombre moyen d’arguments des méthodes locales de la classe.";
						break;
			case "CAC" : this.definition = "Nombre d’associations (incluant les agrégations) locales/héritées auxquelles participe la classe.";
						break;
			case "CLD" : this.definition = "Taille du chemin le plus long reliant une classe ci à une classe feuille dans le graphe d’héritage";
						break;
			case "DIT" : this.definition = "Taille du chemin le plus long reliant la classe à une classe racine dans le graphe d’héritage.";
						break;
			case "ETC" : this.definition = "Nombre de fois où ci apparaît comme type des arguments dans les méthodes des autres classes du diagramme";
						break;
			case "ITC" : this.definition = "Nombre de fois où d’autres classes du diagramme apparaissent comme types des arguments des méthodes de ci.";
						break;
			case "NOA" : this.definition = "Nombre d’attributs locaux/hérités de la classe";
						break;
			case "NOC" : this.definition = "Nombre de sous-classes directes.";
						break;
			case "NOD" : this.definition = "Nombre de sous-classes directes et indirectes de ci.";
						break;
			case "NOM" : this.definition = "Nombre de méthodes locales/héritées de la classe";
						break;
		}
	}

}
