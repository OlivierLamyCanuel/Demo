import java.time.LocalDate;

public class Player implements Comparable<Player>{

    protected String name;
    protected LocalDate birthDate;

    public Player(String name, int year, int month, int day){
        this.name = name;
        this.birthDate = LocalDate.of(year,month,day);
    }

    /**
     * Ordering by birthDate.
     * @param p
     * @return
     */
    public int compareTo(Player p)
    {
        return this.birthDate.compareTo(p.birthDate);
    }

    @Override
    public String toString(){
        return this.name + " (Mois : " + birthDate.getMonth() + " Jour : " + birthDate.getDayOfMonth() + ")";
    }
}
