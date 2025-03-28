package main;

import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
// KeyListener class is a listener interface for receiving keyboard events(keystrokes)
public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, 
    rightPressed, enterPressed, shotKeyPressed;

    GamePanel gp;

    //DEBUG
    boolean showDebugText = false;

    public KeyHandler(GamePanel gp){
        this.gp = gp;
    }
    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        int code = e.getKeyCode(); // returns the number of the key that was pressed
        // TITLE STATE
        if(gp.gameState == gp.titleState){
            titleState(code);
        }
        // PLAY STATE
       else if(gp.gameState == gp.playState){
            playState(code);
        }
        // PAUSE STATE
        else if(gp.gameState == gp.pauseState){
           pauseState(code);
        }
        // DIALOGUE STATE
       else if(gp.gameState == gp.dialogueState){
            dialogueState(code);
        }
        // CHARACTER STATE
        else if(gp.gameState == gp.characterState){
           characterState(code);
        }
        // OPTION STATE
        else if(gp.gameState == gp.optionState){
           optionState(code);
        }
        // GAME OVER STATE
        else if(gp.gameState == gp.gameOverState){
           gameOverState(code);
        }
        else if(gp.gameState == gp.tradeState){
            tradeState(code);
        }
        // DEBUG DRAW TIME
        if(code == KeyEvent.VK_T){
            if(showDebugText == false){
                showDebugText = true;
            }else if(showDebugText == true){
                showDebugText = false;
            }
            
        }
        if(code == KeyEvent.VK_R){
            switch(gp.currentMap){
                case 0 : gp.tileM.loadMap("/world01/worldV3.txt",0);
                        break;
                case 1 : gp.tileM.loadMap("/world01/interior01.txt",1);
                        break;
            }
        }
        
    }

    public void tradeState(int code){

        if(code == KeyEvent.VK_ENTER){
            enterPressed = true;
        }

        if(gp.ui.subState == 0 ){
            if(code == KeyEvent.VK_W){
                gp.ui.commandNum--;
                if(gp.ui.commandNum < 0){
                    gp.ui.commandNum = 2;
                }
                gp.playSE(9);
            }
            if(code == KeyEvent.VK_S){
                gp.ui.commandNum++;
                if(gp.ui.commandNum > 2){
                    gp.ui.commandNum = 0;
                }
                gp.playSE(9);
            }
        }
        if(gp.ui.subState == 1){
            npcInventory(code);
            if(code == KeyEvent.VK_ESCAPE){
                gp.ui.subState = 0;
            }
          }
          if(gp.ui.subState == 2){
            playerInventory(code);
            if(code == KeyEvent.VK_ESCAPE){
                gp.ui.subState = 0;
            }
          }
    }

    public void gameOverState(int code){

        if(code == KeyEvent.VK_W){
            gp.ui.commandNum--;
            if(gp.ui.commandNum < 0){
                gp.ui.commandNum = 1;
            }
            gp.playSE(9);
        }
        if(code == KeyEvent.VK_S){
            gp.ui.commandNum++;
            if(gp.ui.commandNum > 1){
                gp.ui.commandNum = 0;
            }
            gp.playSE(9);
        }
        if(code == KeyEvent.VK_ENTER){
            if(gp.ui.commandNum == 0){
                gp.gameState = gp.playState;
                gp.retry();
                gp.playMusic(0);
            }
            else if(gp.ui.commandNum == 1){
                gp.gameState = gp.titleState;
                gp.restart();
            }
        }
    }

    public void optionState(int code){

        if(code == KeyEvent.VK_ESCAPE){
            gp.gameState = gp.playState;
        }
        if(code == KeyEvent.VK_ENTER){
            enterPressed = true;
        }

        int maxCommadNum = 5;
        switch(gp.ui.subState){
            case 0: maxCommadNum = 5; break;
            case 1: maxCommadNum = 1; break;
            case 2: maxCommadNum = 1; break;
            case 3: maxCommadNum = 1; break;
        }
         if(code == KeyEvent.VK_W){
                gp.ui.commandNum--;
                gp.playSE(9);
                if(gp.ui.commandNum < 0){
                    gp.ui.commandNum= maxCommadNum;
                }
            }
            if(code == KeyEvent.VK_S){
                gp.ui.commandNum++;
                gp.playSE(9);
                if(gp.ui.commandNum > maxCommadNum){
                    gp.ui.commandNum = 0 ;
                } 
        }
        if(code == KeyEvent.VK_A){
            if(gp.ui.subState == 0){
                if(gp.ui.commandNum == 1 && gp.music.volumeScale > 0){
                    gp.music.volumeScale--;
                    gp.music.checkVolume();
                    gp.playSE(9);
                }
                if(gp.ui.commandNum == 2 && gp.se.volumeScale > 0){
                    gp.se.volumeScale--;
                    gp.playSE(9);
                }
            }
        }
        if(code == KeyEvent.VK_D){
            if(gp.ui.subState == 0){
                if(gp.ui.commandNum == 1 && gp.music.volumeScale < 5){
                    gp.music.volumeScale++;
                    gp.music.checkVolume();
                    gp.playSE(9);
                }
                if(gp.ui.commandNum == 2 && gp.se.volumeScale < 5){
                    gp.se.volumeScale++;
                    gp.playSE(9);
                }
            }
        }
    }

    public void titleState(int code){
         if(code == KeyEvent.VK_W){
                gp.ui.commandNum--;
                if(gp.ui.commandNum < 0){
                    gp.ui.commandNum = 2;
                }
            }
            if(code == KeyEvent.VK_S){

                gp.ui.commandNum++;
                if(gp.ui.commandNum > 2){
                    gp.ui.commandNum = 0;
                }
        }
            if(code == KeyEvent.VK_ENTER){
                if(gp.ui.commandNum == 0){
                    gp.gameState = gp.playState;
                    gp.playMusic(0);
                }
                if(gp.ui.commandNum == 1){
                    // LOAD STATE
                }
                if(gp.ui.commandNum == 2){
                    System.exit(0);
                }
            }
    }

    public void playState(int code){

        if(code == KeyEvent.VK_W){
            upPressed = true;
        }
         if(code == KeyEvent.VK_S){
            downPressed = true;
        }
         if(code == KeyEvent.VK_A){
            leftPressed = true;
        }
         if(code == KeyEvent.VK_D){
            rightPressed = true;
        }
        if(code == KeyEvent.VK_P){
            gp.gameState = gp.pauseState;
        }
        if(code == KeyEvent.VK_C){
            gp.gameState = gp.characterState;
        }
        if(code == KeyEvent.VK_ENTER){
            enterPressed = true;
        }
        if(code == KeyEvent.VK_F){
            shotKeyPressed = true;
        }
        if(code == KeyEvent.VK_ESCAPE){
            gp.gameState = gp.optionState;
        }
    }

    public void pauseState(int code){
        if(code == KeyEvent.VK_P){
            gp.gameState = gp.playState;
        }   
    }

    public void dialogueState(int code){
        if(code == KeyEvent.VK_ENTER){
            gp.gameState = gp.playState;
        }
    }

    public void characterState(int code){
         if(code == KeyEvent.VK_C){
            gp.gameState = gp.playState;
          }
          
          if(code == KeyEvent.VK_ENTER){
            gp.player.selectItem();
          }
          playerInventory(code);
    }

    public void playerInventory(int code){

        if(code == KeyEvent.VK_W){
            if(gp.ui.playerSlotRow != 0){
                gp.ui.playerSlotRow--;
                gp.playSE(9);
            }
            
          }
          if(code == KeyEvent.VK_A){
            if(gp.ui.playerSlotCol != 0){
                gp.ui.playerSlotCol--;
                gp.playSE(9);
            }
            
          }
          if(code == KeyEvent.VK_S){
            if(gp.ui.playerSlotRow != 3){
                gp.ui.playerSlotRow++;
                gp.playSE(9);
            }
            
          }
          if(code == KeyEvent.VK_D){
            if(gp.ui.playerSlotCol != 4){
                gp.ui.playerSlotCol++;
                gp.playSE(9);
            }
            
          }
    }

    public void npcInventory(int code){

        if(code == KeyEvent.VK_W){
            if(gp.ui.npcSlotRow != 0){
                gp.ui.npcSlotRow--;
                gp.playSE(9);
            }
            
          }
          if(code == KeyEvent.VK_A){
            if(gp.ui.npcSlotCol != 0){
                gp.ui.npcSlotCol--;
                gp.playSE(9);
            }
            
          }
          if(code == KeyEvent.VK_S){
            if(gp.ui.npcSlotRow != 3){
                gp.ui.npcSlotRow++;
                gp.playSE(9);
            }
            
          }
          if(code == KeyEvent.VK_D){
            if(gp.ui.npcSlotCol != 4){
                gp.ui.npcSlotCol++;
                gp.playSE(9);
            }
            
          }
    }

    @Override
    public void keyReleased(KeyEvent e) {

       int code = e.getKeyCode();
            //to stop character from moving
       if(code == KeyEvent.VK_W){
            upPressed = false;
        }
         if(code == KeyEvent.VK_S){
            downPressed = false;
        }
         if(code == KeyEvent.VK_A){
            leftPressed = false;
        }
         if(code == KeyEvent.VK_D){
            rightPressed = false;
        }
        if(code == KeyEvent.VK_F){
            shotKeyPressed = false;
        }

    }
    
}

/* 
  
            if(code == KeyEvent.VK_ENTER){
                if(gp.ui.commandNum == 0){
                    // fighter stuff
                    gp.gameState = gp.playState;
                    gp.playMusic(0);
                }
                if(gp.ui.commandNum == 1){
                    // thief stuff
                    gp.gameState = gp.playState;
                    gp.playMusic(0);
                }
                if(gp.ui.commandNum == 2){
                    // sorcerer stuff
                    gp.gameState = gp.playState;
                    gp.playMusic(0);
                }
                if(gp.ui.commandNum == 3){
                    gp.ui.titleScreenState = 0;
                }
            }
            }

            */
