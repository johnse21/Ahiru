
// John G. Aparejado
// v1.0 - april 12, 2014
// v1.1
// ---> updates - fixed memory leaks 

package main;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

public class Ahiru extends JFrame{

	private static final long serialVersionUID = 1L;

	private final static int PWIDTH = 700;
	private final static int PHEIGHT = 620;
	
	public Ahiru(){
		
		final AhiruPanel ahiruPanel = new AhiruPanel();
		add(ahiruPanel);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setSize(PWIDTH,PHEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		//setResizable(false);
		
		addWindowFocusListener(new WindowFocusListener() {

			public void windowLostFocus(WindowEvent arg0) {
				ahiruPanel.pauseGame();
			}

			public void windowGainedFocus(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public static void main(String args[]){
		new Ahiru();
	}
	
}
