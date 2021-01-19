package model;

import java.util.ArrayList;

public class GameLevel {
    
    /* Adattagok */ 
    public final GameID        gameID;
    public final int           rows, cols;
    public final LevelItem[][] level;
    public Position            player = new Position(0, 0);
    public Position            ranger = new Position(0, 0);
    public ArrayList<Position> rangers;
    public Position            starting_pos = new Position(0,0);
    private int                fruits_num, collectedFruits, remainedLifes;
    private static int         points;
    private boolean            catched = false;
    
    /* Konstruktorok */
    public GameLevel(ArrayList<String> gameLevelRows, GameID gameID){
        this.gameID =   gameID;
        int c       =   0;
        for (String s : gameLevelRows)
            if (s.length() > c)
                c = s.length();
        
        rows            = gameLevelRows.size();
        cols            = c;
        level           = new LevelItem[rows][cols];
        rangers         = new ArrayList<Position>();
        remainedLifes   = 3;
        points          = 0;
        
        for (int i = 0; i < rows; i++){
            String s = gameLevelRows.get(i);
            for (int j = 0; j < s.length(); j++){
                switch (s.charAt(j)){
                    case '@': player = new Position(j, i);
                              starting_pos = new Position(j,i);
                              level[i][j] = LevelItem.EMPTY; break;
                    case '#': level[i][j] = LevelItem.MOUNTAIN; break;
                    case 'T': level[i][j] = LevelItem.TREE; break;
                    case 'R': rangers.add(new Position(j, i));
                              level[i][j] = LevelItem.EMPTY; 
                              break;
                    case 'F': level[i][j] = LevelItem.FRUITS; 
                              fruits_num++;
                              break;
                    case 'O': level[i][j] = LevelItem.EMPTY_OBSERVED; break;
                    default:  level[i][j] = LevelItem.EMPTY; break;
                }
            }
            for (int j = s.length(); j < cols; j++){
                level[i][j] = LevelItem.EMPTY;
            }
        }
    }
    public GameLevel(GameLevel gl) {
        gameID          = gl.gameID;
        rows            = gl.rows;
        cols            = gl.cols;
        fruits_num      = gl.fruits_num;
        remainedLifes   = gl.remainedLifes;
        level           = new LevelItem[rows][cols];
        player          = new Position(gl.player.x, gl.player.y);
        rangers         = gl.rangers;
        starting_pos    = new Position(gl.starting_pos.x, gl.starting_pos.y);
     
        for (int i = 0; i < rows; i++)
            System.arraycopy(gl.level[i], 0, level[i], 0, cols);
    }
    /* ==================================== */
    
    /* ==== Lekérdező műveletek ===== */
    public boolean isValidPosition(Position p){ return (p.x >= 0 && p.y >= 0 && p.x < cols && p.y < rows); }
    
    public boolean isFree(Position p){
        if (!isValidPosition(p)) return false;
        LevelItem li = level[p.y][p.x];
        return (li == LevelItem.EMPTY);
    }
    public boolean isFree(int y, int x){
        Position p = new Position(y,x);
        if (!isValidPosition(p)) return false;
        LevelItem li = level[p.y][p.x];
        return (li == LevelItem.EMPTY);
    }
    
    boolean isFruit(Position p) {
        if (!isValidPosition(p)) return false;
        LevelItem li = level[p.y][p.x];
        return (li == LevelItem.FRUITS);
    }
    
    public boolean isCatched() {
        for(Position ranger : rangers) {
            if(player.x == ranger.x && player.y == ranger.y)    return true;
            if(player.x == ranger.x+1 && player.y == ranger.y)  return true;
            if(player.x == ranger.x && player.y == ranger.y+1)  return true;
            if(player.x == ranger.x && player.y == ranger.y-1)  return true;
        }
        return false;  
    }
    /* ==================================== */
    
    /* ==== Játékos és vadőr mozgató metódusok ===== */
    public boolean movePlayer(Direction d){
        Position curr = player;
        Position next = curr.translate(d);
        
        if (isFree(next) || isFruit(next)) {
            if(isFruit(next)) {
                level[next.y][next.x] = LevelItem.EMPTY;
                collectedFruits++;
                points++;
            }
            player = next;
            if(isCatched()) {
                remainedLifes--;
                player = starting_pos;
                catched = true;
            }
            return true;
        }
        if(isCatched()) {
            remainedLifes--;
            player = starting_pos;
            catched = true;
        }
        return false;
    }
    public void moveRanger() {
        for(Position ranger : rangers) {
            int getRandomDirection = getRandomInteger(5,1);
            
            switch (getRandomDirection) {
                case 1:
                    if(isFree(ranger.x-1, ranger.y)) {
                        ranger.x = ranger.x-1;
                    } else if(isFree(ranger.x+1, ranger.y)) {
                        ranger.x = ranger.x+1;
                    } else if(isFree(ranger.x, ranger.y-1)) {
                        ranger.y = ranger.y-1;
                    } else if(isFree(ranger.x, ranger.y+1)) {
                        ranger.y = ranger.y+1;
                    }
                    break;
                case 2:
                    if(isFree(ranger.x+1, ranger.y)) {
                        ranger.x = ranger.x+1;
                    } else if(isFree(ranger.x-1, ranger.y)) {
                        ranger.x = ranger.x-1;
                    } else if(isFree(ranger.x, ranger.y-1)) {
                        ranger.y = ranger.y-1;
                    } else if(isFree(ranger.x, ranger.y+1)) {
                        ranger.y = ranger.y+1;
                    } 
                    break;
                case 3:
                    if(isFree(ranger.x, ranger.y-1)) {
                        ranger.y = ranger.y-1;
                    } else if(isFree(ranger.x, ranger.y+1)) {
                        ranger.y = ranger.y+1;
                    } else if(isFree(ranger.x-1, ranger.y)) {
                        ranger.x = ranger.x-1;
                    } else if(isFree(ranger.x+1, ranger.y)) {
                        ranger.x = ranger.x+1;
                    }
                    break;
                case 4:
                    if(isFree(ranger.x, ranger.y+1)) {
                        ranger.y = ranger.y+1;
                    } else if(isFree(ranger.x, ranger.y-1)) {
                        ranger.y = ranger.y-1;
                    } else if(isFree(ranger.x-1, ranger.y)) {
                        ranger.x = ranger.x-1;
                    } else if(isFree(ranger.x+1, ranger.y)) {
                        ranger.x = ranger.x+1;
                    }
                    break;
                default:
                    break;
            }
        }
    }
    /* ==================================== */
          
    public void printLevel(){
        int x = player.x, y = player.y;
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                if (i == y && j == x)
                    System.out.print('@');
                else 
                    System.out.print(level[i][j].representation);
            }
            System.out.println("");
        }
    }
    
    /*===== GETTER / SETTER metódusok =====*/
    public int getCollectedFruits() { return collectedFruits; }
    public int getTotalOfFruits() { return fruits_num; }
    public int getRemainedLifes() { return remainedLifes; }
    public int getPoints() { return points; }
    public boolean getCatched() { return catched; }
    public void setCatched() { catched = false; }
    public void setPoint(int point) { points = point; }
    public void lostLife() { remainedLifes--; }
    /* ==================================== */
    
    /**
     * Randomszámot generálunk maximum és minimum között.
     */
    public static int getRandomInteger(int maximum, int minimum){ return ((int) (Math.random()*(maximum - minimum))) + minimum; }

    
}
