//Madan Bhurtel 
//1001752499

package app;
import javax.swing.*;        

/*
 * GUI class
 */
public class NodeTextRegion {
    
    JTextArea printRegion;

    //--------------------
    NodeTextRegion(String title,int mId) {
	
	//Create and set up the window
	JFrame frame = new JFrame(title);
	frame.setLocation(mId*200, (mId % 2)*350);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	printRegion = new JTextArea(20, 30);
	printRegion.setEditable(false);
	JScrollPane scrollPane = 
	    new JScrollPane(printRegion,
			    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	frame.getContentPane().add(scrollPane);
	
	//Display the window.
	frame.pack();
	frame.setVisible(true);
    }

    //--------------------
    public void print(String s)   { 
	printRegion.append(s); 
        printRegion.setCaretPosition(printRegion.getDocument().getLength());
    }
    public void println(String s) { print(s+"\n"); }
    public void println()         { print("\n"); }
    public void initArea() {printRegion.setText("");}
}
