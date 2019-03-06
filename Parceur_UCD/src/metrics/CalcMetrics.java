package metrics;

import model.UMLAttributes;
import model.UMLClass;
import model.UMLMethode;

import java.util.ArrayList;

/**
 * La Classe CalcMetrics permet d'effectuer les 10 métriques pour une classe
 * et les stocker sous forme d'objet metrics dans un ArrayList.
 * 
 * 
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 *
 */
public class CalcMetrics {

	
	private UMLClass umlClass;
	private ArrayList<Metrics> metrics = new ArrayList<Metrics>();
	private ArrayList<UMLClass> modelClass = new ArrayList<UMLClass>();

	/**
	 * Constructeur de la classe CalcMetrics.
	 * Il appelle la méthode calculerMetrique afin de calculer les metriques 
	 * selon la UMLClass et le ArrayList de UMLClass passés en paramètre. 
	 * 
	 * @param umlClass
	 * @param modelClass
	 */
	public CalcMetrics(UMLClass umlClass, ArrayList<UMLClass> modelClass) {
		this.umlClass = umlClass;
		this.modelClass = modelClass;
		calculerMetrique();
	}

	/**
	 * Autre constructeur pour la classe CalcMetrics.
	 * Il est utilisé pour les tests.
	 */
	public CalcMetrics(){
		
	}

	/**
	 * Méthode qui va effectuer le calcul des métriques,
	 * creer un objet metrics avec le resulat et le nom de la métrique,
	 * puis l'ajouter dans le ArrayList metrics.
	 */
	public void calculerMetrique() {
		
		metrics.add(new Metrics("ANA", calcANA(this.umlClass) ));
		metrics.add(new Metrics("NOM", calcNOM(this.umlClass) ));
		metrics.add(new Metrics("NOA", calcNOA(this.umlClass) ));
		metrics.add(new Metrics("ITC", calcITC(this.umlClass, this.modelClass) ));		
		metrics.add(new Metrics("ETC", calcETC(this.umlClass, this.modelClass) ));
		metrics.add(new Metrics("CAC", calcCAC(this.umlClass) ));
		metrics.add(new Metrics("DIT", calcDIT(this.umlClass) ));
		metrics.add(new Metrics("CLD", calcCLD(this.umlClass) ));
		metrics.add(new Metrics("NOC", calcNOC(this.umlClass) ));
		metrics.add(new Metrics("NOD", calcNOD(this.umlClass) ));

	}

	/**
	 * 
	 * @return umlClass une classe UMLClass
	 */
	public UMLClass getUmlClass() {
		return umlClass;
	}

	/**
	 * 
	 * @param umlClass une classe UMLClass
	 */
	public void setUmlClass(UMLClass umlClass) {
		this.umlClass = umlClass;
	}

	/**
	 * 
	 * @return metrics ArrayList de métriques
	 */
	public ArrayList<Metrics> getMetrics() {
		return this.metrics;
	}

	/**
	 * Nombre moyen d’arguments des méthodes locales de umlClass.
	 * 
	 * @param umlClass
	 */
	public float calcANA(UMLClass umlClass) {

		float totalArgs = 0;
		float ana = 0;

		for (int i = 0; i < umlClass.getMethods().size(); i++) {

			UMLMethode tmpOperation = umlClass.getMethods().get(i);

			if (tmpOperation != null && tmpOperation.getArguments() != null) {

				totalArgs += tmpOperation.getArguments().size();
			}

		}

		if(totalArgs > 0){
			ana = totalArgs / umlClass.getMethods().size();
		}

		return ana;
	}



	/**
	 * Calcule le nombre de méthodes locales/héritées de umlClass
	 * 
	 * @param umlClass
	 */
	public int calcNOM(UMLClass umlClass) {

		ArrayList<UMLMethode> emptyArray = new ArrayList<UMLMethode>();

		return getUniqueMethods(umlClass, umlClass.getMethods()).size();

	}



	/**
	 * Calcule le nombre d’attributs locaux/hérités de la umlClass
	 * 
	 * @param umlClass
	 */
	public int calcNOA(UMLClass umlClass) {

		int noa = umlClass.getAttribute().size();;

		if (umlClass.getParent() != null) {

			noa += calcNOA(umlClass.getParent());

		}

		return noa;
	}



	/**
	 * Calcule le nombre de fois que les membres de modelClass apparaissent comme
	 * types des arguments des méthodes de umlClass.
	 * 
	 * @param umlClass
	 * @param modelClass
	 */
	public int calcITC(UMLClass umlClass, ArrayList<UMLClass> modelClass) {

		int itc = 0;
		int methodArraySize = umlClass.getMethods().size();
		int modelArraySize = modelClass.size();

		for (int i = 0; i < methodArraySize; i++) {

			UMLMethode tmpMethod = umlClass.getMethods().get(i);

			for (int j = 0; j < modelArraySize; j++) {

				itc += calcFreqClassAsArgsType(modelClass.get(j), tmpMethod);

			}

		}

		return itc;

	}

	/**
	 * Calcule le nombre de fois où la umlClass apparaît comme type des arguments dans modelClass
	 * 
	 * @param umlClass
	 * @param modelClass
	 */
	public int calcETC(UMLClass umlClass, ArrayList<UMLClass> modelClass) {

		int etc = 0;

		for (int i = 0; i < modelClass.size(); i++) {

			UMLClass tmpClass = modelClass.get(i);

			for (int j = 0; j < tmpClass.getMethods().size(); j++) {

				UMLMethode tmpMethod = tmpClass.getMethods().get(j);

				etc += calcFreqClassAsArgsType(umlClass, tmpMethod);

			}

		}

		return etc;
	}

	/**
	 * Calcule le nombre d’associations (incluant les agrégations) locales/héritées
	 * auxquelles participe umlClass.
	 * 
	 * @param umlclass
	 */
	public int calcCAC(UMLClass umlclass) {

		int cac = umlclass.getAggregates().size() + umlclass.getRelations().size();

		if (umlclass.getParent() != null) {
			cac += calcCAC(umlclass.getParent());
		}

		return cac;
	}

	/**
	 * Calcule la taille du chemin reliant umlClass à une classe racine dans le
	 * graphe d’héritage.
	 * 
	 * @param umlClass
	 */
	public int calcDIT(UMLClass umlClass) {

		int pathLength = 0;

		UMLClass tmpClass = umlClass;

		while (tmpClass.getParent() != null) {

			pathLength++;
			tmpClass = tmpClass.getParent();

		}

		return pathLength;
	}



	/**
	 * Calcule la taille du chemin le plus long reliant umlClass à une classe
	 * feuille dans le graphe d’héritage
	 * 
	 * @param umlClass
	 */
	public int calcCLD(UMLClass umlClass) {

		int cld = 0;
		ArrayList<UMLClass> arrayLeafs = getLeafs(umlClass);

		if (arrayLeafs != null) {

			int longestPathLeaf = 0;

			for (int i = 0; i < arrayLeafs.size(); i++) {

				if (calcDIT(arrayLeafs.get(i)) > longestPathLeaf) {

					longestPathLeaf = calcDIT(arrayLeafs.get(i));

				}

			}

			cld = longestPathLeaf - calcDIT(umlClass);

		}

		return cld;

	}

	/**
	 * Calcule le nombre de sous-classes directes à umlClass.
	 * 
	 * @param umlClass
	 */
	public int calcNOC(UMLClass umlClass) {

		return umlClass.getSubClass().size();
	}

	/**
	 * calcule le nombre de sous-classes directes et indirectes de la umlClass.
	 * 
	 * @param umlClass
	 */
	public int calcNOD(UMLClass umlClass) {

		int nod = calcNOC(umlClass);

		for (int i = 0; i < umlClass.getSubClass().size(); i++) {

				nod += calcNOD(umlClass.getSubClass().get(i));

		}

		return nod;
	}

	// Method d'appuie

	/**
	 * Parcout l'arbre d'héritage et construit un ArrayList des UMLMéthode sans les doublons (même opéraiton)
	 * @param umlClass
	 * @param prevArrayMethod
	 * @return
	 */
	public ArrayList<UMLMethode> getUniqueMethods (UMLClass umlClass, ArrayList<UMLMethode> prevArrayMethod){

		ArrayList<UMLMethode> tmpArray = new ArrayList<UMLMethode>();
		tmpArray.addAll(prevArrayMethod);

		for(int i = 0; i < umlClass.getMethods().size(); i++){

			boolean unique = true;

			String methodISignature = umlClass.getMethods().get(i).getOperation();

			for(int j =0; j < tmpArray.size(); j++){

				if(tmpArray.get(j).getOperation().equals(methodISignature)){
					unique = false;
				}

			}

			if(unique){

				tmpArray.add(umlClass.getMethods().get(i));
			}

		}

		if(umlClass.getParent() != null){

			UMLClass parent = umlClass.getParent();

			tmpArray = (getUniqueMethods(parent, tmpArray));
		}

		return tmpArray;

	}

	/**
	 * Identifie les feuilles de umlClass (incluant la classe elle-même si celle-ci
	 * est une feuille)
	 *
	 * @param umlClass
	 * @return
	 */
	public ArrayList<UMLClass> getLeafs(UMLClass umlClass) {

		ArrayList<UMLClass> arrayLeafs = new ArrayList<UMLClass>();

		if (umlClass.getSubClass().size() > 0) {

			for (int i = 0; i < umlClass.getSubClass().size(); i++) {

				UMLClass tmpSubClass = umlClass.getSubClass().get(i);
				arrayLeafs.addAll(getLeafs(tmpSubClass));

			}
		} else {

			arrayLeafs.add(umlClass);
		}

		return arrayLeafs;

	}

	/**
	 * Calcule la frequence à laquelle une umlClass est un type d'une UMLMethode
	 *
	 * @param umlClass
	 * @param method
	 * @return
	 */
	public int calcFreqClassAsArgsType(UMLClass umlClass, UMLMethode method) {

		int freq = 0;
		String className = umlClass.getName();

		if (method.getArguments() != null) {

			for (int i = 0; i < method.getArguments().size(); i++) {

				UMLAttributes tmpArgs = method.getArguments().get(i);

				if (tmpArgs.getType().equals(className)) {
					freq++;
				}

			}
		}
		return freq;

	}


}
