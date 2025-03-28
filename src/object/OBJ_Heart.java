package object;



import entity.Entity;
import main.GamePanel;

public class OBJ_Heart extends Entity {
    
    GamePanel gp;
    
    public OBJ_Heart(GamePanel gp){

        super(gp);
        this.gp = gp;
        name = "Heart";
        type = type_pickupOnly;
        value = 2;

            down1 = setup("/objects/heart_full",gp.tileSize,gp.tileSize);
            image = setup("/objects/heart_full",gp.tileSize,gp.tileSize);
            image2 = setup("/objects/heart_half",gp.tileSize,gp.tileSize);
            image3 = setup("/objects/heart_blank",gp.tileSize,gp.tileSize);
            
        
    }

    public void use(Entity entity){

       
        gp.ui.addMessage("Life +" + value);
        entity.life += value;
        gp.playSE(2);
    }
}
