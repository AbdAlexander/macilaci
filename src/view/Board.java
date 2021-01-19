package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import model.Game;
import model.LevelItem;
import model.Position;
import res.ResourceLoader;

public class Board extends JPanel {
    /* Adattagok */ 
    private Game game;
    private final Image mountain, player, tree, empty, ranger, fruits, observed;
    private double scale;
    private int scaled_size;
    private final int tile_size = 32;
    
    /* Konstruktor */
    public Board(Game g) throws IOException{
        game = g;
        scale = 1.0;
        scaled_size = (int)(scale * tile_size);
        mountain = ResourceLoader.loadImage("res/mountain.png");
        player = ResourceLoader.loadImage("res/player.png");
        tree = ResourceLoader.loadImage("res/tree.png");
        empty = ResourceLoader.loadImage("res/empty.png");
        ranger = ResourceLoader.loadImage("res/ranger.png");
        fruits = ResourceLoader.loadImage("res/fruit.png");
        observed = ResourceLoader.loadImage("res/empty_observed.png");
    }
    /* Nagyítás / Kicsinyítés */ 
    public boolean setScale(double scale){
        this.scale = scale;
        scaled_size = (int)(scale * tile_size);
        return refresh();
    }
    /* Újratöltés */
    public boolean refresh(){
        if (!game.isLevelLoaded()) return false;
        Dimension dim = new Dimension(game.getLevelCols() * scaled_size, game.getLevelRows() * scaled_size);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setSize(dim);
        repaint();
        return true;
    }
    /* Kirajzolás karakterek alapján */ 
    @Override
    protected void paintComponent(Graphics g) {
        if (!game.isLevelLoaded()) return;
        Graphics2D gr = (Graphics2D)g;
        int w = game.getLevelCols();
        int h = game.getLevelRows();
        Position p = game.getPlayerPos();
        ArrayList<Position> rangers = game.getRangersPos();
        for (int y = 0; y < h; y++){
            for (int x = 0; x < w; x++){
                Image img = null;
                LevelItem li = game.getItem(y, x);
                switch (li){
                    case MOUNTAIN: img = mountain; break;
                    case TREE: img = tree; break;
                    case FRUITS: img = fruits; break;
                    case EMPTY: img = empty; break;
                    case EMPTY_OBSERVED: img = observed; break;
                
                }
                if (p.x == x && p.y == y) img = player;
                for(int i=0; i<rangers.size(); i++) {
                    if(rangers.get(i).x == x && rangers.get(i).y == y) {
                        img = ranger;
                    }
                }
                if (img == null) continue;
                gr.drawImage(img, x * scaled_size, y * scaled_size, scaled_size, scaled_size, null);
            }
        }
    }
    
}
