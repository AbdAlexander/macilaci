package model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import res.ResourceLoader;

public class Game {
    /* Adattagok */
    private final HashMap<String, HashMap<Integer, GameLevel>>  gameLevels;
    private       GameLevel                                     gameLevel = null;

    /* Konstruktor */
    public Game() {
        gameLevels = new HashMap<>();
        readLevels();
    }

    /* ===== Játék főmetódusai ===== */
    public void loadGame(GameID gameID){ gameLevel = new GameLevel(gameLevels.get(gameID.map).get(gameID.level));}
    public void printGameLevel(){ gameLevel.printLevel(); }
    public boolean step(Direction d){ return (gameLevel.movePlayer(d)); }
    public void moveRanger() { gameLevel.moveRanger(); }
    /* ============================== */

    /* ===== Getter / Setter metódok ===== */
    public Collection<Integer> getLevelsOfDifficulty(String difficulty){
        if (!gameLevels.containsKey(difficulty)) return null;
        return gameLevels.get(difficulty).keySet();
    }
    public Collection<String> getDifficulties(){    return gameLevels.keySet(); }
    public boolean isLevelLoaded(){                 return gameLevel != null; }
    public int getLevelRows(){                      return gameLevel.rows; }
    public int getLevelCols(){                      return gameLevel.cols; }
    public LevelItem getItem(int row, int col){     return gameLevel.level[row][col]; }
    public GameID getGameID(){                      return (gameLevel != null) ? gameLevel.gameID : null; }
    public int getCollectedFruits(){                return (gameLevel != null) ? gameLevel.getCollectedFruits(): 0; }
    public int getTotalOfFruits(){                  return (gameLevel != null) ? gameLevel.getTotalOfFruits(): 0; }
    public int getRemainedLifes() {                 return (gameLevel != null) ? gameLevel.getRemainedLifes(): 0; }
    public int getPoints() {                        return (gameLevel != null) ? gameLevel.getPoints(): 0; }
    public boolean getCatched() {                   return (gameLevel != null) ? gameLevel.getCatched(): false; }
    public void setCatched() {                      if(gameLevel != null) { gameLevel.setCatched(); } }
    public void setPoint(int point) {               if(gameLevel != null) { gameLevel.setPoint(point); } }
    public Position getPlayerPos(){                 return new Position(gameLevel.player.x, gameLevel.player.y); }
    public ArrayList<Position> getRangersPos() {    return gameLevel.rangers; }
    public void respawn() {
        if(gameLevel != null) {
            gameLevel.player = gameLevel.starting_pos;
            gameLevel.lostLife();   
        }
    }
    /* ============================== */
    
    /* További metórusok melyek a betöltésért felel */
    private void readLevels(){
        //ClassLoader cl = getClass().getClassLoader();
        InputStream is;// = cl.getResourceAsStream("res/levels.txt");
        is = ResourceLoader.loadResource("res/levels.txt");
        
        try (Scanner sc = new Scanner(is)){
            String line = readNextLine(sc);
            ArrayList<String> gameLevelRows = new ArrayList<>();
            
            while (!line.isEmpty()){
                GameID id = readGameID(line);
                if (id == null) return;
                
                // System.out.println(id.difficulty + " " + id.id);

                gameLevelRows.clear();
                line = readNextLine(sc);
                while (!line.isEmpty() && line.trim().charAt(0) != ';'){
                    gameLevelRows.add(line);                    
                    line = readNextLine(sc);
                }
                addNewGameLevel(new GameLevel(gameLevelRows, id));
            }
            //if (is != null) is.close();
        } catch (Exception e){
            System.out.println("Ajaj");
        }
        
    }   
    private void addNewGameLevel(GameLevel gameLevel){
        HashMap<Integer, GameLevel> levelsOfDifficulty;
        if (gameLevels.containsKey(gameLevel.gameID.map)){
            levelsOfDifficulty = gameLevels.get(gameLevel.gameID.map);
            levelsOfDifficulty.put(gameLevel.gameID.level, gameLevel);
        } else {
            levelsOfDifficulty = new HashMap<>();
            levelsOfDifficulty.put(gameLevel.gameID.level, gameLevel);
            gameLevels.put(gameLevel.gameID.map, levelsOfDifficulty);
        }
    }
    private String readNextLine(Scanner sc){
        String line = "";
        while (sc.hasNextLine() && line.trim().isEmpty()){
            line = sc.nextLine();
        }
        return line;
    }
    private GameID readGameID(String line){
        line = line.trim();
        if (line.isEmpty() || line.charAt(0) != ';') return null;
        Scanner s = new Scanner(line);
        s.next(); // skip ';'
        if (!s.hasNext()) return null;
        String difficulty = s.next().toUpperCase();
        if (!s.hasNextInt()) return null;
        int id = s.nextInt();
        return new GameID(difficulty, id);
    }    
}
