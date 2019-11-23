

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package project;

import com.sun.opengl.util.*;
import com.sun.opengl.util.j2d.TextRenderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.awt.color.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.media.opengl.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class Anim extends JFrame implements KeyListener, FocusListener {
	static Animator animator;
	GLCanvas glcanvas;
	Clip clip;
	AudioInputStream audio;
	AnimListener listener;
    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Anim.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        new Anim();
    }


    public Anim() {
       
        listener = new AnimGLEventListener3();
        glcanvas = new GLCanvas();
        glcanvas.addGLEventListener(listener);
        glcanvas.addKeyListener(listener);
        glcanvas.addKeyListener(this);
        glcanvas.addFocusListener((FocusListener) this);
        glcanvas.addMouseMotionListener(listener);
        
        getContentPane().add(glcanvas, BorderLayout.CENTER);
        animator = new FPSAnimator(12);
        animator.add(glcanvas);
        animator.start();
       
        setTitle("Catch the Egg");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);
       // setVisible(true);
        setFocusable(true);
        glcanvas.requestFocus();
        
        try {
        	String SoundName = "BackGroundSound.wav";
        	audio = AudioSystem.getAudioInputStream(new File(SoundName).getAbsoluteFile());
        	clip = AudioSystem.getClip();
        	clip.open(audio);
        	//clip.start();
        	//clip.loop(555);
        } catch(Exception e) {
        	System.err.println(e.getMessage());
        }
    }


	@Override
	public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == (KeyEvent.VK_SPACE)) {
        	if(Anim.animator.isAnimating()) {
        		Anim.animator.stop();  
        		AnimGLEventListener3.pause = true;
        		glcanvas.repaint();
        		
        	} else if(!Anim.animator.isAnimating()) {
        		Anim.animator.start();
        		AnimGLEventListener3.pause = false;
        		
        	}
        }
        if (e.getKeyCode() == (KeyEvent.VK_ESCAPE)) {
                if(Anim.animator.isAnimating()){
                    Anim.animator.stop(); 
                    AnimGLEventListener3.pause = true;
                }
                new Esc().setVisible(true);
        }
		
	}


	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void focusGained(FocusEvent fe) {
        
        
    }

    @Override
    public void focusLost(FocusEvent fe) {
       if(Anim.animator.isAnimating()) {
           Anim.animator.stop();
           AnimGLEventListener3.pause = true;
           glcanvas.repaint();
       }
    }

    private void repaint(GLCanvas glcanvas) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
