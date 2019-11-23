

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package project;

import java.awt.event.*;
import java.io.IOException;
import javax.media.opengl.*;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;

import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.j2d.TextRenderer;

public class AnimGLEventListener3 extends AnimListener implements MouseMotionListener, KeyListener {

	static GL gl;

    public static void RePlay() {
        Anim.animator.start();
        dx = 0;
        dy = 1;
        mod = 250;
        frame = 0;
        whiteRatio = 0.9;
        blackRatio = 0.8;
        eggRate = 7;
        cnt = 0;
        life = 5 ;
        score = 0;
        sec = 0;
        min = 0;
        if(level == "Meduim") {
            mod = 500;
            dy = 1.5;
            whiteRatio = 0.75;
            blackRatio = 0.95;
            eggRate = 6;
        } else if (level == "hard") {
            mod = 250;
            dy = 2;
            whiteRatio = 0.7;
            blackRatio = 0.98;
            eggRate = 5;
        }
    }
	
    int animationIndex = 0;
    int maxWidth = 100;
    int maxHeight = 100;
    double x = maxWidth/2, y = maxHeight/ 2;
    boolean movingUp = false;
    boolean movingRight = false;
    boolean f = false;
    boolean begin = true;
    static boolean pause = false;
    boolean retPlay = false;
    double chickenMotion = 77;
    static double dx = 0;
    static double dy = 1;
    static int mod = 250;
    static int frame = 0;
    static double whiteRatio = 0.9;
    static double blackRatio = 0.8;
    static int eggRate = 7;
    static String level = "";
    
    Queue<Pair> q = new LinkedList<Pair>();
    Queue<Pair> b = new LinkedList<Pair>();
    static int size, cnt = 0, life = 5 ;

    static int score;
    int ChickPos[] = {10, 25, 40, 55, 70};
    double lifePos[] = {95, 92.5, 90, 87.5, 85};
    //double t0, t1, p0, p1, ptotal;
    static double sec, min;
    static Esc e = new Esc();
    
    TextRenderer text = new TextRenderer(Font.decode("Showcard Gothic"));
    
    static String textureNames[] = {"chicken.png", "WhiteEgg.png", "BlackEgg.png",  "LiveEgg.png", "EggCrack.png", "Nest.png","BackGround.jpg"};
    TextureReader.Texture texture[] = new TextureReader.Texture[textureNames.length];
    static int textures[] = new int[textureNames.length];
    
    public void init(GLAutoDrawable gld) {
    	gl = gld.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);    //This Will Clear The Background Color To Black
        
        
        gl.glEnable(GL.GL_TEXTURE_2D);  // Enable Texture Mapping
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);	
        gl.glGenTextures(textureNames.length, textures, 0);

        for(int i = 0; i < textureNames.length; i++){
            try {
                texture[i] = TextureReader.readTexture(assetsFolderName + "//" + textureNames[i] , true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);

//                mipmapsFromPNG(gl, new GLU(), texture[i]);
                new GLU().gluBuild2DMipmaps(
                    GL.GL_TEXTURE_2D,
                    GL.GL_RGBA, // Internal Texel Format,
                    texture[i].getWidth(), texture[i].getHeight(),
                    GL.GL_RGBA, // External format from image,
                    GL.GL_UNSIGNED_BYTE,
                    texture[i].getPixels() // Imagedata
                    );
            } catch( IOException e ) {
              System.out.println(e);
              e.printStackTrace();
            }
        }
        

    }
    
    public void display(GLAutoDrawable gld) {
    	
    	GL gl = gld.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);       //Clear The Screen And The Depth Buffer
        gl.glLoadIdentity(); 
        
        DrawBackground(gl);
        handleKeyPress();
	////////////Draw egg/////////////
        
	    size = q.size();
	    while(size-- > 0) {
	    	Pair p = q.peek();
	    	q.poll();
	    	/////////////Collision/////////////
	    	if((p.f - x) * (p.f - x) + (p.s - 10) * (p.s - 10) <=  36 && p.t != 4)  {
	    		if(p.t == 1) {
	    			score += 50;
	    		} else if (p.t == 2) {
	    			life--;
	    			if(life == 0){
                                    new lose().setVisible(true);
                                    Anim.animator.stop();
                                    
                                    //System.exit(0);
                                    
                                    //Anim.animator.stop();
                                    
                                    
                                }
	    		} else if (p.t == 3 & life < 5) {
	    			life++;
	    		}
	    		//System.out.println(score + " " + life);
	    	} else {
	    		if(p.s <= 10 && p.cnt < 5) {
	    			DrawEgg(gl, p.f, p.s, 4, 1);
	    			q.add(new Pair(p.f, p.s, 4, p.cnt + 1));
	    			
	    		} else if (p.t != 4) {
		    		DrawEgg(gl, p.f, p.s - dy, p.t, 1);
			    	q.add(new Pair(p.f, p.s - dy, p.t));
	    		}
		    	
	    	}
	    	
	    }
	    
	    if (cnt++ % eggRate == 0) {
	    	///////////Random Egg Position//////
	    	int random = ChickPos[(int) Math.floor(Math.random() * 5)];
	    	
	    	///////////Random Egg type//////////
	    	
	    	
    		if(Math.random() < whiteRatio) {
		    	DrawEgg(gl, random + dx, 75, 1, 1);
		    	q.add(new Pair(random + dx, 75, 1));
    		} else if(Math.random() < blackRatio){
    			DrawEgg(gl, random + dx, 75, 2, 1);
		    	q.add(new Pair(random + dx, 75, 2));
    		} else {
    			DrawEgg(gl, random + dx, 75, 3, 1);
		    	q.add(new Pair(random + dx, 75, 3));
    		}
	    }
	    
        ////////////Draw Life /////////////////
        for(int i = 0; i < life; ++i)
                    DrawLife(gl, lifePos[i], 95, 3, 1);

        /////////////Draw Nest///////////////
        DrawNest(gl, x, 10, 0, 1);
		    
        
        ///////////Chicken Motion/////////////
        
        if(movingUp) {
        	if(chickenMotion < 80) {
        		chickenMotion += 0.5;
        	}else {
        		movingUp = false;
        	}
        }
        if(!movingUp) {
        	if(chickenMotion > 77) {
        		chickenMotion -= 0.5;
        	}else {
        		movingUp = true;
        	}
        }
        if (score > 1000 || level == "hard") {
        	 if(movingRight) {
             	if(dx < 7) {
             		dx += 0.5;
             	} else {
             		movingRight = false;
             	}
             }
             if(!movingRight) {
             	if(dx > 0) {
             		dx -= 0.5;
             	}else {
             		movingRight = true;
             	}
             }
        }
        
        /////////////Draw Chicken/////////////
        for (int i = 0; i < ChickPos.length; ++i) 
        	DrawChicken(gl, ChickPos[i] + dx, chickenMotion, 0, 1);
        
        if(pause) 
        	DrawPause();

        DrawScore();
        if(!pause && ((score % mod == 0 && score != 0) || (frame < 5 && frame != 0))) {
        	DrawUpLevel();
        	if(++frame == 5) {
        		frame = 0;
        		mod += mod;
        		eggRate = Math.max(3, eggRate - 2);
        		whiteRatio -= 0.07;
        		dy = Math.min(dy + 0.1, 3);
        		
        	}
        
        }
        Timer();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
    
    public void DrawChicken(GL gl,double x, double y, int index, float scale){
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);	// Turn Blending On

        gl.glPushMatrix();
            gl.glTranslated( x/(maxWidth/2.0) - 1, y/(maxHeight/2.0) - 0.9, 0);
            gl.glScaled(0.17*scale, 0.3*scale, 1);
            gl.glBegin(GL.GL_QUADS);
            // Front Face
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex3f(-1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex3f(1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex3f(1.0f, 1.0f, -1.0f);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex3f(-1.0f, 1.0f, -1.0f);
            gl.glEnd();
        gl.glPopMatrix();
        
        gl.glDisable(GL.GL_BLEND);
    }
    public void DrawNest(GL gl,double x, double y, int index, float scale){
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[5]);	// Turn Blending On

        gl.glPushMatrix();
            gl.glTranslated( x/(maxWidth/2.0) - 1, y/(maxHeight/2.0) - 0.9, 0);
            gl.glScaled(0.1*scale, 0.2*scale, 1);
            gl.glBegin(GL.GL_QUADS);
            // Front Face
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex3f(-1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex3f(1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex3f(1.0f, 1.0f, -1.0f);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex3f(-1.0f, 1.0f, -1.0f);
            gl.glEnd();
        gl.glPopMatrix();
        
        gl.glDisable(GL.GL_BLEND);
    }
    
    public void DrawEgg(GL gl,double x, double y, int index, double scale){
    	
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);	// Turn Blending On

        gl.glPushMatrix();
            gl.glTranslated( x/(maxWidth/2.0) - 1, y/(maxHeight/2.0) - 1, 0);
            gl.glScaled(0.02*scale, 0.02*scale, 1);
        
            gl.glBegin(GL.GL_QUADS);
            // Front Face
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex3f(-1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex3f(1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex3f(1.0f, 1.0f, -1.0f);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex3f(-1.0f, 1.0f, -1.0f);
            gl.glEnd();
        gl.glPopMatrix();
        
        gl.glDisable(GL.GL_BLEND);
    }
    
    public void DrawSnowFlake(GL gl,double x, double y, int index, double scale){
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[5]);	// Turn Blending On

        gl.glPushMatrix();
            gl.glTranslated( x/(maxWidth/2.0) - 0.9, y/(maxHeight/2.0) - 0.8, 0);
            gl.glScaled(0.04*scale, 0.04*scale, 1);
        
            gl.glBegin(GL.GL_QUADS);
            // Front Face
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex3f(-1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex3f(1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex3f(1.0f, 1.0f, -1.0f);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex3f(-1.0f, 1.0f, -1.0f);
            gl.glEnd();
        gl.glPopMatrix();
        
        gl.glDisable(GL.GL_BLEND);
    }
    
    public void DrawBackground(GL gl){
        gl.glEnable(GL.GL_BLEND);	
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[textures.length - 1]);	// Turn Blending On

        gl.glPushMatrix();
            gl.glBegin(GL.GL_QUADS);
            // Front Face
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex3f(-1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex3f(1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 1.0f);
                gl.glVertex3f(1.0f, 1.0f, -1.0f);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex3f(-1.0f, 1.0f, -1.0f);
            gl.glEnd();
        gl.glPopMatrix();
        
        gl.glDisable(GL.GL_BLEND);
    }

    public void DrawLife(GL gl,double x, double y, int index, float scale) {

    gl.glEnable(GL.GL_BLEND);
    gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);	// Turn Blending On

    gl.glPushMatrix();
        gl.glTranslated( x/(maxWidth/2.0) - 1, y/(maxHeight/2.0) - 1, 0);
        gl.glScaled(0.03*scale, 0.03*scale, 1);

        gl.glBegin(GL.GL_QUADS);
        // Front Face
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f(-1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f(1.0f, -1.0f, -1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f(1.0f, 1.0f, -1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
    gl.glPopMatrix();

    gl.glDisable(GL.GL_BLEND);
    }


    public void DrawPause(){
            text.beginRendering(100, 100);
            text.setColor(Color.GRAY);
            text.draw("Pause", 30, 50);
            text.setColor(Color.WHITE);
            text.endRendering();
    }
    public void DrawScore(){

            text.beginRendering(300, 150);
            text.setColor(Color.GRAY);
            text.draw(" Score", 250, 130);
            text.setColor(Color.WHITE);
            text.endRendering();

            text.beginRendering(400, 200);
            text.setColor(Color.GRAY);
            String s = String.valueOf(score);
            text.draw(" " + s, 335, 160);
            text.setColor(Color.WHITE);
            text.endRendering();
    }
    public void DrawUpLevel(){

            text.beginRendering(300, 150);
            text.setColor(Color.GRAY);
            text.draw("Good Job ^_^ ", 105, 75);
            text.draw("Speed Up", 115, 60);
            text.setColor(Color.WHITE);
            text.endRendering();

    }
    public void Timer() {

            sec += 0.5;
            if((int)(sec % 60) ==  59 && (int)((sec + 0.5) % 60) !=  59) min++;
            DrawTime((int)(sec % 60), 350, ":");
            //System.out.println(min);
            DrawTime((int)(min % 60),340, "");
    }
    public void DrawTime(int min2, int x, String semicolin) {
            text.beginRendering(400, 200);
            text.setColor(Color.GRAY);
            String s =  semicolin + String.valueOf(min2);
            text.draw("Time", 340, 145);
            text.draw(s, x, 130);
            //text.setColor(Color.BLUE);
            //text.draw("Pause", 50, 50);
            text.setColor(Color.WHITE);
            text.endRendering();
    }
    public void handleKeyPress() {

    }
        
    public BitSet keyBits = new BitSet(256);
 
    @Override 
    public void keyPressed(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.set(keyCode);
    } 
 
    @Override 
    public void keyReleased(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.clear(keyCode);
    } 
 
    @Override 
    public void keyTyped(final KeyEvent event) {
        // don't care 
    } 
 
    public boolean isKeyPressed(final int keyCode) {
        return keyBits.get(keyCode);
    }

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		double X = e.getX();
		double Y = e.getY();

		Component c = e.getComponent();
		double width = c.getWidth();
		
		x = (int) ((X / width) * 100);
		if(x > 80)
                    x = 80;
	}
}