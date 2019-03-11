import java.util.Arrays;

/**
 * @Author Amaury Matu
 * Implemente Move-To-Front
 */


public class MoveToFront {

    private Integer[] PERMUTATION;
    private int MAXIMUM;

    /* ------------- CONSTRUCTORS ------------- */

    /**
     * MoveToFront Constructor
     *
     * @param maximum : tel que tout entier à encodé sera entre 0 et (maximum − 1)
     */
    public MoveToFront(int maximum) {
        this.MAXIMUM = maximum;
        this.PERMUTATION = new Integer[this.MAXIMUM];
        this.generateAlphabet();
    }

    /* ------------- ENCODE / DECODE ------------- */

    private void generateAlphabet() {
        for (int i = 0; i < this.MAXIMUM; i++) {
            this.PERMUTATION[i] = i;
        }
    }

    public int encodeMTF(int entierNaturel) {
        int index = Arrays.asList(this.PERMUTATION).indexOf(entierNaturel);
        Integer[] result = new Integer[this.PERMUTATION.length];
        result[0] = entierNaturel;

        System.arraycopy(this.PERMUTATION, 0, result, 1, index);
        System.arraycopy(this.PERMUTATION, index + 1, result, index + 1, this.PERMUTATION.length - index - 1);
        this.PERMUTATION = result;

        return index;
    }

    public int decodeMTF(int index) {
        int entierNaturel = this.PERMUTATION[index];
        Integer[] result = new Integer[this.PERMUTATION.length];
        result[0] = entierNaturel;

        System.arraycopy(this.PERMUTATION, 0, result, 1, index);
        System.arraycopy(this.PERMUTATION, index + 1, result, index + 1, this.PERMUTATION.length - index - 1);
        this.PERMUTATION = result;

        return entierNaturel;
    }

}
