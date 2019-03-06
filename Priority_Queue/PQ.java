import java.util.Arrays;

/**
 * Home made priority queue
 */
public class PQ <T extends Comparable<? super T>> {

    protected Object[] array;
    protected int root = 1;
    protected int emptyIndex = root;


    public PQ (){
        array = new Object[1000000];
    }

    public PQ (int initialSize){
        array = new Object[initialSize];
    }

    /**
     * Insert E
     * @param e
     * @param <T>
     */
    public <T extends Comparable<? super T>> void insert(T e){

        try {
            swim(e, emptyIndex);
            emptyIndex++;
        } catch (ArrayIndexOutOfBoundsException ex){
            extendArray();
            swim(e, emptyIndex);
        }
    }

    /**
     * Retourne et supprime l'élément le plus petit
     * @param <T>
     * @return
     */
    public <T extends Comparable<? super T>> T deleteMin(){
        emptyIndex--;
        T min = (T) array[1];
        T tmp = (T) array[emptyIndex];
        array[emptyIndex] = null;

        if(emptyIndex > 1){
            sink(tmp, 1);
        }

        return min;
    }

    /**
     * Retourne l'index de l'élément le plus petit à partir d'un index donné
     * @param i
     * @param <T>
     * @return
     */
    public <T extends Comparable<? super T>> int findMin(int i){

        try {

            int leftIndex = 2 * i;
            int rightIndex = 2 * i + 1;
            T right = (T) array[rightIndex];
            T left = (T) array[leftIndex];

            if (leftIndex > emptyIndex || left == null) {
                return 0; // Case  i is a leaf
            } else if (right == null){
                return leftIndex;
            }else if (rightIndex <= emptyIndex && right.compareTo(left) < 0) {
                return rightIndex;
            } else {
                return leftIndex;
            }

        } catch (ArrayIndexOutOfBoundsException ex){
            extendArray();
            return findMin(i);
        }
    }

    /**
     * Swim
     * @param e
     * @param index
     * @param <T>
     */
    public <T extends Comparable<? super T>> void swim(T e, int index){

        int i = index;
        int p = emptyIndex/2;
        T placement = (T) array[p];
            while (p != 0 && placement != null && placement.compareTo(e) > 0) {

                array[i] = array[p];
                i = p;
                p = i / 2;
                placement = (T) array[p];
            }

            this.array[i] = e;
    }

    /**
     * Sink
     * @param e
     * @param i
     * @param <T>
     */
    public <T extends Comparable<? super T>> void sink(T e, int i){

        int index = i;

        try {
            int indexChild = findMin(index);
            T tmpChild = (T) array[indexChild];

            while (indexChild != 0 && tmpChild.compareTo(e) < 0) {
                array[index] = array[indexChild];
                index = indexChild;
                indexChild = findMin(index);
            }

            this.array[index] = e;

        } catch (ArrayIndexOutOfBoundsException ex){
            extendArray();
            sink(e, i);
        }

    }

    public boolean isEmpty(){return this.emptyIndex == root;}

    /**
     * Double the size of an array
     */
    public void extendArray(){
        if(array.length == 0){
            array = Arrays.copyOf(array,1);
        } else {
            array = Arrays.copyOf(array,array.length*2);}
    }

    /**
     * Reduce the size of the Array by half
     */
    public void reduceArray(){ array = Arrays.copyOf(array, array.length / 2);}


    public int getEmptyIndex() {
        return emptyIndex;
    }

    public Object[] getArray() {
        return array;
    }


}
