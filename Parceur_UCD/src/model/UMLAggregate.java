package model;

/**
 * 
 * La classe UMLAggregate permet d'instancier une aggregation entre 2 classes
 * 
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 */

public class UMLAggregate extends UMLAssoAggrega {

	/**
	 * Contructeur d'un UMLAggregate
	 * 
	 * @param relation
	 * @param details
	 */
	public UMLAggregate(String relation, String details) {
		this.name = relation;
		this.details = details.trim();
	}

	@Override
	public String toString() {
		return this.name;
	}
}
