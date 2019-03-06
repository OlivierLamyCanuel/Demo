package model;

/**
 * 
 * Classe mère des classes UMLClass, UMLAttributes et UMLMethode
 * Permet de retourner une string correspondant au bloc 
 * du fichier où se trouve la classe.
 * 
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 *
 */
public abstract class UMLComponent {

    protected String details;

    /**
     * 
     * @return details la section du fichier .ucd à afficher dans la section Détails
     */
    public String getDetails() {
		return details;
	}
}
