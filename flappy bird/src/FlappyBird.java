import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import javax.print.attribute.standard.RequestingUserName;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
   int boardwidth=360;
   int boardheight=640; 
   //immagini
   Image backgroundImg;
   Image birdImg;
   Image topPipeImg;
   Image bottomPipeImg;

   //Pennuto
   int birdX=boardwidth/8;
   int birdY=boardheight/2;
   int birdWidth=34;
   int birdHeight=24;

   class Bird {
   int x=birdX;
   int y=birdY;
   int width=birdWidth;
   int height=birdHeight;
   Image img;

   Bird(Image img){
    this.img = img;
   }
   }

   //tubi
   int pipeX=boardwidth;
   int pipeY=0;
   int pipeWidth=64;
   int pipeHeight=512;

   class Pipe{
   int x = pipeX;
   int y = pipeY;
   int width = pipeWidth;
   int height = pipeHeight;
   Image img;
   boolean passed=false;
   
   Pipe(Image img){
    this.img=img;
   }
   }


   //logica di gioco
   Bird bird;
   int velocityX=-4;
   int velocityY=0;
   int gravity=1;

   ArrayList<Pipe> pipes;
   Random random=new Random();

   Timer gameLoop;
   Timer placePipesTimer;
   boolean gameOver=false;
   double score=0;

   FlappyBird(){
    setPreferredSize(new Dimension(boardwidth,boardheight));
    //setBackground(Color.red);
    setFocusable(true);
    addKeyListener(this);

    //visualizzazione delle immagini
    backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
    birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
    topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
    bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
    
    //Pennuto
    bird = new Bird(birdImg);
    pipes=new ArrayList<Pipe>();

    //posizionamento dei tubi
    placePipesTimer = new Timer(1500,new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e){
         placePipes();   
        }
    });
    placePipesTimer.start();
    //timer di gioco
    gameLoop = new Timer(1000/60,this);
    gameLoop.start();

   } 

   public void placePipes() {
   int randomPipeY= (int)(pipeY-pipeHeight/4-Math.random()*(pipeHeight/2));
   int openingSpace=boardheight/4;
   Pipe topPipe = new Pipe(topPipeImg);
   topPipe.y=randomPipeY;
   pipes.add(topPipe);
   Pipe bottomPipe = new Pipe(bottomPipeImg);
   bottomPipe.y=topPipe.y+pipeHeight+openingSpace;
   pipes.add(bottomPipe);
   }

public void paintComponent(Graphics g){
super.paintComponent(g);
draw(g);    
}
public void draw(Graphics g){
    //immagine di fondo
    g.drawImage(backgroundImg,0,0,boardwidth,boardheight,null);
    //Pennuto
    g.drawImage(bird.img,bird.x,bird.y,bird.width,bird.height,null);
    //tubi
    for(int i=0;i<pipes.size();i++){
    Pipe pipe =pipes.get(i);
    g.drawImage(pipe.img,pipe.x,pipe.y,pipe.width,pipe.height,null);    
    }
    //punteggio
    g.setColor(Color.pink);
    g.setFont(new Font("Arial",Font.PLAIN,32));
    if(gameOver){
        g.drawString("GAME OVER : "+String.valueOf((int)score),boardwidth/15,boardheight/2);
        g.drawString("spazio = resusciti",45,boardheight/4);
    }   
    else{
        g.drawString("PUNTEGGIO : "+String.valueOf((int)score),10,35);
    }
}

public void move(){
//Pennuto
velocityY+=gravity;
bird.y+=velocityY;
bird.y=Math.max(bird.y,0);
//tubi
for(int i=0;i<pipes.size();i++){
    Pipe pipe =pipes.get(i);
    pipe.x+=velocityX;  
    
    if(!pipe.passed && bird.x>pipe.x+pipe.width){
        pipe.passed=true;
        //passa sopra tubo sotto e sotto tubo sopra quindi supera due tubi (0.5*2=1)
        score+=0.5;
    }

    if(collision(bird,pipe)){
       gameOver=true; 
    }
    }
    if(bird.y>boardheight){
        gameOver=true;
    }
}

public boolean collision(Bird a,Pipe b){
   return a.x<b.x+b.width //angolo alto sinistra del uccello non sorpassa l angolo u destra del tubo
   &&a.x +a.width>b.x//angolo alto destra dell uccello non sorpassa l angolo alto sinistro del tubo
   &&a.y<b.y+b.height//angolo alto sinistra non sorpassa angolo basso sinistra tubo
   &&a.y+a.height>b.y;//angolo basso sinistra non sorpassa angolo alto sinistra del tubo
}

@Override
public void actionPerformed(ActionEvent e) {
    move();
    repaint();
    if(gameOver){
        placePipesTimer.stop();
        gameLoop.stop();
    }
}

@Override
public void keyPressed(KeyEvent e) {
    if(e.getKeyCode()==KeyEvent.VK_SPACE){
        velocityY=-9;   
        if(gameOver){
            //resetti tutte le condizioni
            bird.y=birdY;
            velocityY=0;
            pipes.clear();
            score=0;
            gameOver=false;
            gameLoop.start();
            placePipesTimer.start();

        }
    }
}
@Override
public void keyTyped(KeyEvent e) {
   
}
@Override
public void keyReleased(KeyEvent e) {
   
}
}
