import java.util.ArrayList;

/**
 * @Author Olivier Lamy-Canuel
 * Implemente encode-Omega
 */
public class Encode {

    /**
     * Convert an int into an ArrayList of binaries
     * @param num
     * @return
     */
    protected static ArrayList<Boolean> integerToBinary(int num){

        ArrayList<Boolean> list = new ArrayList(1);

        if (num == 0){
            //DO Nothing
        } else if (num == 1){
            list.add(true);
        } else if (num % 2 == 0){
            list.addAll(integerToBinary(num / 2));
            list.add(false);
        } else {
            list.addAll(integerToBinary((num-1)/ 2));
            list.add(true);
        }

        return list;
    }

    /**
     * Enccode num avec l'encodage omega
     * @param num
     * @return
     */
    protected static ArrayList<Boolean> encodeOmega(int num){

        ArrayList<Boolean> list = new ArrayList(1);

        if(num == 0){
            // Do nothing
        } else if(num > 0){

            ArrayList<Boolean> listBin = integerToBinary(num);

            list.addAll(encodeOmega((listBin.size() -1))); // Length
            list.addAll(listBin); // Value 

        } else {
            throw new NumberFormatException("Input négatif!");
        }

        return list;

    }

}
