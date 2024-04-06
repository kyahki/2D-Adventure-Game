package main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D; // Graphics2D class extends the Graphics class to provide more control and functions
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JPanel;

import ai.PathFinder;
import entity.Entity;
import entity.Player;

import tile.TileManager;
import tileinteractive.InteractiveTile;

public class GamePanel extends JPanel implements Runnable{


    //SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile size
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; //48x48 tile size
    public final int maxScreenCol =  20;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; //768 pixels
    public final int screenHeight = tileSize * maxScreenRow; //576 pixels

    //WORLD SETTINGS

    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int maxMap = 10;
    public int currentMap = 0;
    
    //FOR FULL SCREEN
    int screenWidth2 = screenWidth;
    int screenHeight2 = screenHeight;
    BufferedImage tempScreen;
    Graphics2D g2;
    public boolean fullScreenOn = false;

    //FPS
    int fps = 60;
    // SYSTEM
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public UI ui = new UI(this);
    public PathFinder pFinder = new PathFinder(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    Thread gameThread;
    public EventHandler eHandler = new EventHandler(this);
    Config config = new Config(this);

    // ENTITY AND OBJECT
    public Player player = new Player(this,keyH);
    public Entity obj[][] = new Entity[maxMap][20];
    public Entity npc[][] = new Entity[maxMap][10];
    public Entity monster[][] = new Entity[maxMap][20];
    public InteractiveTile iTile[][] = new InteractiveTile[maxMap][50];
    public ArrayList<Entity> projectileList = new ArrayList<>();
    public ArrayList<Entity> particleList = new ArrayList<>();
    ArrayList<Entity> entityList = new ArrayList<>();
    
    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int characterState = 4;
    public final int optionState = 5;
    public final int gameOverState = 6;
    public final int transitionState = 7;
    public final int tradeState = 8;
   

    public GamePanel(){

        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // default for games for better rendering
        this.addKeyListener(keyH);
        this.setFocusable(true); // gamepanel can be focused on receveing input
    }

    public void setUpGame(){

        aSetter.setObject();
        aSetter.setNPC();
        aSetter.setMonster();
        aSetter.setInteractiveTile();
      //  playMusic(0);
        gameState = titleState;

        tempScreen = new BufferedImage(screenWidth,screenHeight, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D)tempScreen.getGraphics();
        if(fullScreenOn == true){
            setFullScreen();
        }

    }

    public void startGameThread(){

        gameThread = new Thread(this); //gamepanel class to this thread constructor (instantiate)
        gameThread.start();
        
    }
    /*  SLEEPER METHOD
    @Override
    public void run() {
        
        double drawInterval = 1000000000/fps; //the amount of times u can draw in the screen 0.01666 seconds
        double nextDrawTime = System.nanoTime() + drawInterval;


        while(gameThread != null){
            
            long currentTime = System.nanoTime(); // more precise 1,000,000,000 = 1 second
            

            // 1 UPDATE: update information such as character position
                update();
            // 2 DRAW: draw the screen with the updated information
                repaint(); // how you call the paintComponent method

            

            try {
                double remainingTime = nextDrawTime - System.nanoTime(); // how much time remaining before the nextdraw time
                remainingTime = remainingTime/1000000; //convert nano to millisecond

                if(remainingTime < 0 ){
                    remainingTime = 0;
                }

                Thread.sleep((long)remainingTime);

                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
    */

    // DELTA METHOD
    public void run() {
        
        double drawInterval = 1000000000/fps; //the amount of times u can draw in the screen 0.01666 seconds
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while(gameThread != null){
            
            currentTime = System.nanoTime(); // more precise 1,000,000,000 = 1 second
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if(delta >= 1){
            // 1 UPDATE: update information such as character position
                update();
            // 2 DRAW: draw the screen with the updated information
                drawToTempScreen(); // draw everything to the buffered image
                drawToScreen(); // draw the buffered image to the screen
            // repaint() // how you call the paintComponent method
                delta--;    
                drawCount++;

            }

            if(timer >= 1000000000){
                System.out.println("FPS: "+ drawCount);
                drawCount = 0;
                timer = 0;
            }

        }
    }
        // X values increases to the right
        // Y values increases as they go down
    public void update(){

        if(gameState == playState){
            // PLAYER
            player.update();
            // NPC
            for(int i = 0 ; i < npc[1].length ; i++){
                if(npc[currentMap][i] != null){
                    npc[currentMap][i].update();
                }
            }
            for(int i = 0 ; i < monster[1].length ; i++){
                if(monster[currentMap][i] != null){
                    if(monster[currentMap][i].alive == true && monster[currentMap][i].dying == false){
                        monster[currentMap][i].update();
                    }
                    if(monster[currentMap][i].alive == false){
                        monster[currentMap][i].checkDrop();
                        monster[currentMap][i] = null;
                    }
                    
                }
            }
            for(int i = 0 ; i < projectileList.size() ; i++){
                if(projectileList.get(i) != null){
                    if(projectileList.get(i).alive == true){
                        projectileList.get(i).update();
                    }
                    if(projectileList.get(i).alive == false){
                        projectileList.remove(i);
                    }
                    
                }
            }
            for(int i = 0 ; i < particleList.size() ; i++){
                if(particleList.get(i) != null){
                    if(particleList.get(i).alive == true){
                        particleList.get(i).update();
                    }
                    if(particleList.get(i).alive == false){
                        particleList.remove(i);
                    }
                    
                }
            }
            for(int i = 0 ; i < iTile[1].length ; i++){
                if(iTile[currentMap][i] != null){
                    iTile[currentMap][i].update();
                }
            }
        }
        if(gameState == pauseState){

        }
        
    }

    public void retry(){

        player.setDefaultPositions();
        player.restoreLifeAndMana();
        aSetter.setNPC();
        aSetter.setMonster();
    }

    public void restart(){

        player.setDefaultValues();
        player.setDefaultPositions();
        aSetter.setObject();
        aSetter.setNPC();
        aSetter.setMonster();
        aSetter.setInteractiveTile();

    }
    public void setFullScreen(){

        // GET LOCAL SCREEN DEVICE
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(Main.window);

        // GET FULL SCREEN WIDTH AND HEIGHT
        screenWidth2 = Main.window.getWidth();
        screenHeight2 = Main.window.getHeight();
    }
    public void drawToTempScreen(){
        // DEBUG
        long drawStart = 0;
        if(keyH.showDebugText == true){
            drawStart = System.nanoTime();

        }
        
        // TITLE SCREEN
        if(gameState == titleState){
            ui.draw(g2);
        }
        // OTHERS
        else{
              
        //TILE
        tileM.draw(g2); // draw tiles first then player
        
        // INTERACTIVE TILE
        for(int i = 0 ; i< iTile[1].length ; i++){
            if(iTile[currentMap][i] != null){
                iTile[currentMap][i].draw(g2);
            }
        }
        // ADD ENTITIES TO THE LIST
        entityList.add(player);
        
        for(int i = 0 ; i < npc[1].length ; i++){
            if(npc[currentMap][i] != null){
                entityList.add(npc[currentMap][i]);
            }
        }

        for(int i = 0 ; i < obj[1].length ; i++){
            if(obj[currentMap][i] != null){
                entityList.add(obj[currentMap][i]);
            }
        }

        for(int i = 0 ; i < monster[1].length ; i++){
            if(monster[currentMap][i] != null){
                entityList.add(monster[currentMap][i]);
            }
        }
        for(int i = 0 ; i < projectileList.size() ; i++){
            if(projectileList.get(i) != null){
                entityList.add(projectileList.get(i));
            }
            }   
        for(int i = 0 ; i < particleList.size() ; i++){
            if(particleList.get(i) != null){
                entityList.add(particleList.get(i));
            }
            }   
       
        // SORT
        Collections.sort(entityList, new Comparator<Entity>() {
            
            @Override
            public int compare(Entity e1 ,Entity e2){
                
                int result = Integer.compare(e1.worldY, e2.worldY);
                return result;
            }
        });

        // DRAW ENTITIES
        for (int i = 0 ; i < entityList.size() ; i++){
            entityList.get(i).draw(g2);
        }
        // EMPTY ENTITY LIST
        entityList.clear();
        //UI
        ui.draw(g2);
        }
        
      

        //DEBUG 
        if(keyH.showDebugText == true){
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;

            g2.setFont(new Font("Arial", Font.PLAIN,20));
            g2.setColor(Color.white);
            int x = 10;
            int y = 400;
            int lineHeight = 20;

            g2.drawString("WorldX"+ player.worldX, x, y); y+= lineHeight;
            g2.drawString("WorldY"+ player.worldY, x, y); y+= lineHeight;
            g2.drawString("Col"+ (player.worldX + player.solidArea.x)/tileSize, x, y); y+= lineHeight;
            g2.drawString("Row"+ (player.worldY + player.solidArea.y)/tileSize, x, y); y+= lineHeight;
            g2.drawString("Draw Time: " + passed , x, y);
            
        }
    }
    // Graphics is a class that has many functions to draw objects on the screen
    
    public void drawToScreen(){

        Graphics g = getGraphics();
        g.drawImage(tempScreen,0 , 0, screenWidth2,screenHeight2,null);
        g.dispose();
    }
    public void playMusic(int i){

        music.setFile(i);
        music.play();
        music.loop();

    }

    public void stopMusic(){

        music.stop();
    }

    public void playSE(int i){

        se.setFile(i);
        se.play();

    }


}
