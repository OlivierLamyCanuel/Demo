package model;

/**
 * Classe mère des classes UMLAggregate et UMLRelation
 * 
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 */
public class UMLAssoAggrega {

    protected String details;
    protected String name;
    /**
     * 
     * @return details la section du fichier .ucd à afficher dans la section Détails
     */
	public String getDetails() {
		return details;
	}
}
