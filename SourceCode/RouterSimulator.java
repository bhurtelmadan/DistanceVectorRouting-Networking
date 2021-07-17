/**
 * Madan Bhurtel 
 * 1001752499
 */
package app;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import javax.swing.*;

/**
 * 
 * Distance vector routing algorithm simulator class
 */
public class RouterSimulator {
	
	public static final int NUM_NODES = 5;
	public static final int INFINITY = 16;

	private Node[] nodes;
	
	private JFrame mainFrame;
	private JLabel statusLabel;
	private JPanel filePanel;	
	private JLabel fPathLabel;
	private JButton fileOpenButton;
	private JCheckBox intervenCheckBox;
	private JButton stepSimualButton;
	private JButton initSimulButton;
	private JPanel simulPanel;
	private JPanel linkCostPanel;
	private JTextField jt[];
	private boolean bStepSimul; 
	private boolean bSimulEnd;
	private boolean bReviseCost;
	private int[][] connectCosts = new int[NUM_NODES][NUM_NODES];
	private int[][] reviseCosts = new int[NUM_NODES][NUM_NODES];
	
	private static ArrayList<RouterEvent> evlist = new ArrayList<RouterEvent>();   /* the event list */
	private ArrayList<RouterEvent> oldEveList = new ArrayList<RouterEvent>();
	private int cycle=0;
	
	String labelNm[]= {"1-2","1-3","1-4","1-5","2-3","2-4","2-5","3-4","3-5","4-5"}; 

    /* possible events: */
    final static int FROMNODE = 2;
    final static int LINK_CHANGE   =  10;

    Instant instance =Instant.now(); 
    long clocktime = instance.getEpochSecond();
	  
	public RouterSimulator()
	{
		for(int i=0;i<NUM_NODES;i++)
		{
			for(int j=0;j<NUM_NODES;j++)
			{
				connectCosts[i][j]=INFINITY;
				if(i==j) {
					connectCosts[i][j]=0;
				}
			}
		}
		
		prepareGUI();
		nodes = new Node[NUM_NODES];
		for(int i=0;i<NUM_NODES;i++) {
            nodes[i] = new Node(i, this);
        }
	}	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		RouterSimulator rs = new RouterSimulator();
		
	}
	/*
	 * main window's GUI
	 */
	private void prepareGUI() {
		mainFrame = new JFrame("Distance Vector Routing");
		mainFrame.setSize(400,400);
		JPanel mainPn = new JPanel();
		GridBagLayout gb=new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		
		mainPn.setLayout(gb);
		mainFrame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
	         }        
	      });   
		filePanel = new JPanel();
		filePanel.setLayout(new FlowLayout());
		fPathLabel = new JLabel("");
		//fPathLabel.setSize(250, 30);
		fileOpenButton = new JButton("Open File");		
		filePanel.add(fPathLabel);
		filePanel.add(fileOpenButton);
		JFileChooser  fileDialog = new JFileChooser();
	  /*
	   * actionListener for file open button click    
	   */
      fileOpenButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            int returnVal = fileDialog.showOpenDialog(mainFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               java.io.File file = fileDialog.getSelectedFile();
               fPathLabel.setText("File Path :" + file.getName());
	            try (BufferedReader br = new BufferedReader(new FileReader(file))){
					String line=null; 
					while ((line = br.readLine()) != null) {
			            String n[] = line.split(" ");
			            connectCosts[Integer.parseInt(n[0])-1][Integer.parseInt(n[1])-1]=
			            		Integer.parseInt(n[2]);
			            connectCosts[Integer.parseInt(n[1])-1][Integer.parseInt(n[0])-1]=
			            		Integer.parseInt(n[2]);
			            try {
			            	for (JTextField jtf : jt) {
								if(jtf.getName().equals(n[0]+"-"+n[1])||
									jtf.getName().equals(n[1]+"-"+n[0])) {
									jtf.setText(n[2]);
									break;
								}
							}
			            }catch(Exception ed)
			            {}
			            
			             //System.out.println(n[0]+","+n[1]+","+n[2]);			            
			        }
					//System. reviseCosts = connectCosts
					//System.arraycopy(connectCosts, 0,reviseCosts,0, RouterSimulator.NUM_NODES);
					//reviseCosts = Arrays.copyOf(connectCosts, connectCosts.length);
					for(int i=0;i<NUM_NODES;i++) {
						for(int j=0;j<NUM_NODES;j++) {
							reviseCosts[i][j]=connectCosts[i][j];
						}
					}
					bSimulEnd=false;
					for(int i=0;i<NUM_NODES;i++) {
						
		                nodes[i].setCost(connectCosts[i],false);
		            }
		            
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
            else{
            	fPathLabel.setText("" );           
            }      
         }
      });
      simulPanel =  new JPanel();
      //simulPanel.setSize(400,50);
      simulPanel.setLayout(new FlowLayout());
      intervenCheckBox = new JCheckBox("Step");
      intervenCheckBox.addItemListener(new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			bStepSimul = e.getStateChange()==1?true:false;
		}
    	  
      });
      stepSimualButton = new JButton("Step Simutator");
      stepSimualButton.addActionListener(new SimulatorListener());
      initSimulButton = new JButton("Init Link Cost");
      initSimulButton.addActionListener(new InitListener());
      simulPanel.add(intervenCheckBox);
      simulPanel.add(stepSimualButton);
      simulPanel.add(initSimulButton);
      
      linkCostPanel = new JPanel();
      //linkCostPanel.setSize(400,100);
      GridLayout ly=new GridLayout(5,2);      
      ly.setHgap(1);
      ly.setVgap(1);
      linkCostPanel.setLayout(ly);
      
      jt= new JTextField[10];
      for(int i=0;i<2;i++) {
    	  for(int j=0;j<5;j++) {
    		  JPanel jp = new JPanel(new FlowLayout());   
    		  jp.setSize(100,10);
    		  JLabel jl=new JLabel(labelNm[i*5+j]);
    		  //jl.setSize(20,30);
    		  jt[i*5+j] = new JTextField(3);
    		  jt[i*5+j].addMouseListener(new CostLabelListener());
    		  //jt[i*5+j].addKeyListener(new CostLabelListener());
    		  jt[i*5+j].setName(labelNm[i*5+j]);
    		  jp.add(jl);
    		  jp.add(jt[i*5+j]);
    		  linkCostPanel.add(jp);
    	  }
      }
      
      statusLabel = new JLabel("");
	    
      //gbc.gridheight=50;
      gbc.gridx=0;
      gbc.gridy=0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      mainPn.add(filePanel,gbc);
	  //gbc.gridheight=2;
	  gbc.gridx=0;
      gbc.gridy=1;
      gbc.ipady=20;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      mainPn.add(simulPanel,gbc);
	  //gbc.gridheight=200;
	  gbc.gridx=0;
      gbc.gridy=2;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      mainPn.add(linkCostPanel,gbc);
	  //gbc.gridheight=50;
	  gbc.gridx=0;
      gbc.gridy=3;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      mainPn.add(statusLabel,gbc);
      mainFrame.add(mainPn);
	  mainFrame.setVisible(true);
	}
	/*
	 * get the simulation time
	 */
	 public long getClocktime() {
		    return clocktime;
	 }
	 /*
	  * get cycle count
	  */
	 public int getCycle()
	 {
		 return cycle;
	 }
	 /*
	  * mouseListener for link cost labels 
	  */
	private class CostLabelListener implements MouseListener
	{		
		@Override
		public void mouseClicked(MouseEvent arg0) {			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub			
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			for(JTextField t : jt) {
				if(!t.getText().isEmpty()) {					
					String s[]=t.getName().split("-");
					if(Integer.parseInt(t.getText())!=reviseCosts[Integer.parseInt(s[0])-1][Integer.parseInt(s[1])-1]) {
						reviseCosts[Integer.parseInt(s[0])-1][Integer.parseInt(s[1])-1]=
			            		Integer.parseInt(t.getText());
						reviseCosts[Integer.parseInt(s[1])-1][Integer.parseInt(s[0])-1]=
			            		Integer.parseInt(t.getText());
						bReviseCost=true;
						break;
					}
					
				}
			}
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub			
		}	
		
	}
	/*
	 * actionListener for simulator button click
	 */
	private class SimulatorListener implements ActionListener 
	{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
			if(bStepSimul) {
				if(bReviseCost) {
					for(int i=0;i<NUM_NODES;i++) {
		                nodes[i].setCost(reviseCosts[i],true);
		            }
					bReviseCost=false;
					runStepSimulate();
				}else
					runStepSimulate();	
			}else
			{
				runContinueSimulate();
			}
			
		}
		
	}
	/*
	 * actionListener for Init Link Cost button click event
	 */
	private class InitListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			bSimulEnd=false;
			cycle=0;
			statusLabel.setText("");
			for(int i=0;i<NUM_NODES;i++) {
				for(int j=0;j<NUM_NODES;j++) {
					if(connectCosts[i][j]!=INFINITY) {
						for (JTextField jtf : jt) {
							if(jtf.getName().equals(String.valueOf(i+1)+"-"+String.valueOf(j+1))||
								jtf.getName().equals(String.valueOf(j+1)+"-"+String.valueOf(i+1))) {
								jtf.setText(""+connectCosts[i][j]);
								break;
							}
						}
					}
					
				}
			}
			
			for(int i=0;i<NUM_NODES;i++) {
                nodes[i].setCost(connectCosts[i],false);
            }
		}
	}
	/*
	 * event process for receiving the distance vector change from each route node
	 */
	public void addRouterEvent(RouterData pkt) {
		
		RouterEvent e = new RouterEvent();		
		e.ptrData = pkt;
		e.dest = pkt.destid;
		evlist.add(e);
		
	}
	/*
	 * simulation without intervention
	 */
	private void runContinueSimulate()
	{		
		clocktime=System.currentTimeMillis();
		while(true)
		{			
			if(bSimulEnd) {				
				break;
			}				
			runStepSimulate();			
		}
		clocktime = System.currentTimeMillis()-clocktime;
		statusLabel.setText("Simulation Time:"+clocktime+"ms Cycle count: "+cycle);
	}
	/*
	 * simulation with step by step
	 */
	private void runStepSimulate()
	{		
		if(evlist.size()==0) {
			if(!bSimulEnd) {
				for(int i=0;i<NUM_NODES;i++) {
					nodes[i].printNodeRegion();
				}
				bSimulEnd=true;
			}			
			return;
		}
		cycle++;	
		statusLabel.setText(""+cycle);
		for(int i=0;i<NUM_NODES;i++) {
			nodes[i].printNodeRegion();
		}
		oldEveList.addAll(evlist);
		for(int i=0;i<oldEveList.size();i++)
		{
			RouterEvent e = oldEveList.get(i);						
			nodes[e.dest].receiveUpdate(e.ptrData);
		}
		evlist.removeAll(oldEveList);
		oldEveList.clear();		
	}
	
}
class  RouterEvent {	  
	  int eventId;           /* source where event occurs */
	  RouterData ptrData; /* router data for this event */
	  int dest;        /* for link cost change */	
	}

