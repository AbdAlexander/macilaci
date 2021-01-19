package database;

public class HighScore {
    
    /* Adattagok */ 
    private final String name;
    private final int score;
    
    /* Konstruktor */
    public HighScore(String name, int score) {
        this.name = name;
        this.score = score;
    }
    /* Getter metódusok */
    public String getName() {   return name; }
    public int getScore() {     return score; }
    /* Tesztelés miatt */ 
    @Override
    public String toString() {  return "HighScore{" + "name=" + name + ", score=" + score + "}"; }
    

}
