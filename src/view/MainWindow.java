package view;

import database.HighScore;
import database.HighScores;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import model.Direction;
import model.Game;
import model.GameID;
import model.Position;

public class MainWindow extends JFrame{
    /* Adattagok */ 
    private final Game      game;
    private HighScores      highScores;
    private Board           board;
    private final JLabel    gameStatLabel;
    private Timer           Timer;
    private int             timeleft = 0;
    
    /* Konstruktor */ 
    public MainWindow() throws IOException, SQLException{
        game =          new Game();
        highScores =    new HighScores(10);
        
        setTitle("Maci Laci");
        setSize(800, 800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        URL url = MainWindow.class.getClassLoader().getResource("res/player.png");
        setIconImage(Toolkit.getDefaultToolkit().getImage(url));
        
        JMenuBar menuBar =      new JMenuBar();
        JMenu menuGame =        new JMenu("Játék");
        JMenu menuGameLevel =   new JMenu("Pálya");
        JMenu menuGameScale =   new JMenu("Kicsinyítés / Nagyítás");
        createGameLevelMenuItems(menuGameLevel);
        createScaleMenuItems(menuGameScale, 1.0, 2.0, 0.5);

        JMenuItem highscore = new JMenuItem(new AbstractAction("Eredmények") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDatabase();
            }
        });
        JMenuItem menuGameExit = new JMenuItem(new AbstractAction("Kilépés") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuGame.add(menuGameLevel);
        menuGame.add(menuGameScale);
        menuGame.addSeparator();
        menuGame.add(menuGameExit);
        menuBar.add(menuGame);
        menuBar.add(highscore);
        setJMenuBar(menuBar);
        
        setLayout(new BorderLayout(0, 10));
        gameStatLabel = new JLabel("label");

        add(gameStatLabel, BorderLayout.NORTH);
        try { add(board = new Board(game), BorderLayout.CENTER); } catch (IOException ex) {}
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                super.keyPressed(ke); 
                if (!game.isLevelLoaded()) return;
                int kk = ke.getKeyCode();
                Direction d = null;
                switch (kk){
                    case KeyEvent.VK_A:  d = Direction.LEFT; break;
                    case KeyEvent.VK_D: d = Direction.RIGHT; break;
                    case KeyEvent.VK_W:    d = Direction.UP; break;
                    case KeyEvent.VK_S:  d = Direction.DOWN; break;
                }
                board.repaint();
                if (d != null && game.step(d)){
                    if (game.getCollectedFruits() == game.getTotalOfFruits()){
                        Timer.stop();
                        JOptionPane.showMessageDialog(
                                MainWindow.this, 
                                "Gratulálok! Nyertél!\nSok sikert a következő pályához!", 
                                "Gratulálok!", 
                                JOptionPane.INFORMATION_MESSAGE); 
                        if(game.getGameID().level+1 <= 10) {
                            game.loadGame(new GameID(game.getGameID().map, game.getGameID().level+1));
                        } else {
                            String name = JOptionPane.showInputDialog(
                                    MainWindow.this, 
                                    "Gratulálok az összes pályát teljesítetted!"
                                            + "\nÖsszes összeszedett gyümölcskosár: " + game.getPoints() + " | Elteld idő: "
                                            + timeleft + "\nKérlek írd be a neved, hogy elmenthessük az eredményed:",
                                    "Eredményed elmentése", 
                                    JOptionPane.INFORMATION_MESSAGE);
                            try {
                                highScores.putHighScore(name, game.getPoints());
                                game.setPoint(0);
                                timeleft = 0;
                                showDatabase();
                                game.loadGame(new GameID("MAP", 1));
                                board.refresh();
                                refreshGameStatLabel();
                                pack();
                                Timer.restart();
                            } catch (SQLException ex) {
                                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                            }
                                    
                        }
                        
                        board.refresh();
                        pack();
                        Timer.restart();
                    }
                }
                if(game.getCatched()) {
                    game.setCatched();
                    if(game.getRemainedLifes() > 0) {
                        JOptionPane.showMessageDialog(MainWindow.this, "A vadőr elkapott!\n Elvesztettél egy életet", "Elkaptak", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        String name = JOptionPane.showInputDialog(
                                MainWindow.this, 
                                "Összes életedet elvesztetted!"
                                        + "\nÖsszes összeszedett gyümölcskosár: " + game.getPoints() + " | Elteld idő: "
                                        + timeleft + "\nKérlek írd be a neved, hogy elmenthessük az eredményed:",
                                "Eredményed elmentése", 
                                JOptionPane.INFORMATION_MESSAGE);
                        try {
                            highScores.putHighScore(name, game.getPoints());
                            game.setPoint(0);
                            timeleft = 0;
                            Timer.stop();
                            showDatabase();
                            game.loadGame(new GameID("MAP", 1));
                            board.refresh();
                            refreshGameStatLabel();
                            pack();
                            Timer.restart();
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                }
                refreshGameStatLabel();
            }
        });

        setResizable(false);
        setLocationRelativeTo(null);
        game.loadGame(new GameID("MAP", 1));
        timeleft = 0;
        board.setScale(2.0);
        board.refresh();
        pack();
        refreshGameStatLabel();
        setVisible(true);
        
        Timer = new Timer(1000, new NewFrameListener());
        Timer.start();
    }
    /* Újrageneráló függvény: Újrageneráljuk a pontokat és az eltelt időt */
    private void refreshGameStatLabel(){
        String s = "<html>Összeszedett gyümölcskosarak: " + game.getCollectedFruits() 
                + " / " + game.getTotalOfFruits()
                + " | Fennmaradt életek száma: " + game.getRemainedLifes() 
                + "<br>Összes pontok: " + game.getPoints() + " | Eltelt idő: "
                + timeleft + " mp</html>";
        gameStatLabel.setText(s);
    }
    /* Játékpálya menüpontok generálása fálj alapján */ 
    private void createGameLevelMenuItems(JMenu menu){
        for (String s : game.getDifficulties()){
            JMenu difficultyMenu = new JMenu(s);
            menu.add(difficultyMenu);
            for (Integer i : game.getLevelsOfDifficulty(s)){
                JMenuItem item = new JMenuItem(new AbstractAction("Level-" + i) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        game.loadGame(new GameID(s, i));
                        board.refresh();
                        pack();
                        Timer.restart();
                    }
                });
                difficultyMenu.add(item);
            }
        }
    } 
    /* Nagyítás / Kicsinyítés menüpontok generálása és eseménykezelő */ 
    private void createScaleMenuItems(JMenu menu, double from, double to, double by){
        while (from <= to){
            final double scale = from;
            JMenuItem item = new JMenuItem(new AbstractAction(from + "x") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (board.setScale(scale)) pack();
                }
            });
            menu.add(item);
            
            if (from == to) break;
            from += by;
            if (from > to) from = to;
        }
    }
    /* Segéd: Megmutatja az adatbázistáblát */ 
    private void showDatabase() {
        try {
            ArrayList<HighScore> dataList = highScores.getHighScores();
            Object[][] data = new Object[10][2];
            int counter = 0;
            for(HighScore hs : dataList) {
                Object row[] = new Object[2];
                row[0] = hs.getName();
                row[1] = hs.getScore();
                data[counter++] = row;
            }
            JFrame f;      
            f=new JFrame();    

            String column[]={"Név","Pontszám"};         
            JTable jt=new JTable(data,column);    
            jt.setBounds(30,40,200,100);          
            JScrollPane sp=new JScrollPane(jt);    
            f.add(sp);          
            f.setSize(300,400);    
            f.setVisible(true);    
        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /* Időíző class-a */ 
    class NewFrameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            game.moveRanger();
            board.refresh();
            checkCatching();     
            refreshGameStatLabel();
            timeleft++;
        }
        /* ====== Segédmetódusok ======  */
        public void checkCatching() {
            ArrayList<Position> rangersPos = game.getRangersPos();
            Position playerPos = game.getPlayerPos();
            for(int i=0; i<rangersPos.size(); i++) {
                if(playerPos.x == rangersPos.get(i).x && playerPos.y == rangersPos.get(i).y) {
                    catched();
                }
                if(playerPos.x == rangersPos.get(i).x+1 && playerPos.y == rangersPos.get(i).y) {
                    catched();
                }
                if(playerPos.x == rangersPos.get(i).x-1 && playerPos.y == rangersPos.get(i).y) {
                    catched();
                }
                if(playerPos.x == rangersPos.get(i).x && playerPos.y == rangersPos.get(i).y+1) {
                    catched();
                }
                if(playerPos.x == rangersPos.get(i).x && playerPos.y == rangersPos.get(i).y-1) {
                    catched();
                } 
            }            
        }
        public void catched() {
            if(game.getRemainedLifes() > 0) {
                JOptionPane.showMessageDialog(MainWindow.this, "A vadőr elkapott!\n Elvesztettél egy életet", "Elkaptak", JOptionPane.INFORMATION_MESSAGE);
                game.respawn();
            } else {
                String name = JOptionPane.showInputDialog(
                    MainWindow.this, 
                    "Összes életedet elvesztetted!"
                            + "\nÖsszes összeszedett gyümölcskosár: " + game.getPoints() + " | Elteld idő: "
                            + timeleft + "\nKérlek írd be a neved, hogy elmenthessük az eredményed:",
                    "Eredményed elmentése", 
                    JOptionPane.INFORMATION_MESSAGE);
                try {
                    highScores.putHighScore(name, game.getPoints());
                    game.setPoint(0);
                    timeleft = 0;
                    Timer.stop();
                    showDatabase();
                    game.loadGame(new GameID("MAP", 1));
                    board.refresh();
                    refreshGameStatLabel();
                    pack();
                    Timer.restart();
                    
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        /* =================================================== */

    }    
    
    /* Főprogram */ 
    public static void main(String[] args) {
        try {
            new MainWindow();
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
}
