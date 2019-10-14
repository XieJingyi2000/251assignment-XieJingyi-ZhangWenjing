
/**
 * @author xiejingyi
 */
package massey.ac.nz.Assignment1.textEditor;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.util.JSONUtils;





public class textEditor extends JFrame implements ActionListener,DocumentListener  
{ 	
	
	PrintJob p=null;  //Declare a PrintJob object , prints
	Graphics g=null;
	JMenu fileMenu,editMenu,formatMenu,viewMenu,helpMenu,searchMenu; //menu  
	
	JPopupMenu popupMenu;    //Right-click the pop-up menu item  
	JMenuItem popupMenu_Cut,popupMenu_Copy,popupMenu_Paste,popupMenu_Delete,popupMenu_SelectAll;	
	JMenuItem fileMenu_New,fileMenu_Open,fileMenu_Save,fileMenu_SaveAs,fileMenu_PageSetUp,fileMenu_Print,fileMenu_Exit; //File menu item 
	JMenuItem editMenu_Cut,editMenu_Copy,editMenu_Paste,editMenu_Delete,editMenu_Find,editMenu_FindNext,editMenu_GoTo,editMenu_SelectAll,editMenu_TimeDate;  //Edit menu item
	JCheckBoxMenuItem viewMenu_Status;  //View menu item 	
	JMenuItem helpMenu_HelpTopics,helpMenu_AboutNotepad; //Help menu item   	
	JMenuItem searchMenu_Search;//Search menu item	
	JTextArea editArea;  //Text edit area  	
	JLabel statusLabel;  //Status bar label
	Toolkit toolkit=Toolkit.getDefaultToolkit(); //System clipboard
	Clipboard clipBoard=toolkit.getSystemClipboard();
	//其他变量  
	String oldValue;//Store the original contents of the edit area to compare whether the text has changed
	boolean isNewFile=true;//New file (not saved)  
	File currentFile;//Current filename
	//Constructor start
	public textEditor(){     
		super("Text Editor");   
	    JMenuBar menuBar=new JMenuBar(); 
	    //Create file menu and menu items and register event listeners 
	    fileMenu=new JMenu("File(F)"); 
	    fileMenu.setMnemonic('F');//Set ALT+F 
	    fileMenu_New=new JMenuItem("New(N)");  
	    fileMenu_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));  
	    fileMenu_New.addActionListener(this);  	 
	    fileMenu_Open=new JMenuItem("Open(O)...");  
	    fileMenu_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));  
	    fileMenu_Open.addActionListener(this);  	 
	    fileMenu_Save=new JMenuItem("Save(S)");  
	    fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));  
	    fileMenu_Save.addActionListener(this);  	 
	    fileMenu_SaveAs=new JMenuItem("SaveAs(A)...");  
	    fileMenu_SaveAs.addActionListener(this);  	    
	    fileMenu_Print=new JMenuItem("Print(P)...");  
	    fileMenu_Print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));   
	    fileMenu_Print.addActionListener(this);  	 
	    fileMenu_Exit=new JMenuItem("Exit(X)");  
	    fileMenu_Exit.addActionListener(this); 
	  //Create a search menu and menu items and register event listeners
	    searchMenu=new JMenu("Search(S)");
	    searchMenu.setMnemonic('S');//Set ALT+E  
	    searchMenu_Search=new JMenuItem("Search(S)...");  
	    searchMenu_Search.setAccelerator(KeyStroke.getKeyStroke  (KeyEvent.VK_F,InputEvent.CTRL_MASK));  
	    searchMenu_Search.addActionListener(this);  
	  //Create a edit menu and menu items and register event listeners
	    editMenu=new JMenu("Edit(E)");  
	    editMenu.setMnemonic('E');//set ALT+E  	  	 
	    editMenu_Cut=new JMenuItem("Cut(T)");  
	    editMenu_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));  
	    editMenu_Cut.addActionListener(this);  	 
	    editMenu_Copy=new JMenuItem("Copy(C)");  
	    editMenu_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));  
	    editMenu_Copy.addActionListener(this);   
	    editMenu_Paste=new JMenuItem("Paste(P)");  
	    editMenu_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));  
	    editMenu_Paste.addActionListener(this);   
	    editMenu_Delete=new JMenuItem("Delete(D)");  
	    editMenu_Delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));  
	    editMenu_Delete.addActionListener(this);      
	    editMenu_SelectAll = new JMenuItem("SelectAll",'A');   
	    editMenu_SelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));   
	    editMenu_SelectAll.addActionListener(this);  	 
	    editMenu_TimeDate = new JMenuItem("TimeDate(D)",'D');  
	    editMenu_TimeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0));  
	    editMenu_TimeDate.addActionListener(this);  	 
	  //Create view menu and menu items and register event listeners
	    viewMenu=new JMenu("View(V)");  
	    viewMenu.setMnemonic('V');//set ALT+V  
	    viewMenu_Status=new JCheckBoxMenuItem("Status(S)");  
	    viewMenu_Status.setMnemonic('S');//set ALT+S  
	    viewMenu_Status.setState(true);  
	    viewMenu_Status.addActionListener(this); 
	  //Create help menus and menu items and register event listeners
	       helpMenu = new JMenu("Help(H)");  
	       helpMenu.setMnemonic('H');//set ALT+H   	 
	       helpMenu_AboutNotepad = new JMenuItem("About(A)");   
	       helpMenu_AboutNotepad.addActionListener(this);  	    
	   //Adds a file menu and menu item to the menu bar 
	       menuBar.add(fileMenu);   
	       fileMenu.add(fileMenu_New);   
	       fileMenu.add(fileMenu_Open);   
	       fileMenu.add(fileMenu_Save);   
	       fileMenu.add(fileMenu_SaveAs);   
	       fileMenu.addSeparator();            
	       fileMenu.add(fileMenu_Print);   
	       fileMenu.addSeparator();        
	       fileMenu.add(fileMenu_Exit);  
	    //Adds a search menu and menu item to the menu bar
	       menuBar.add(searchMenu);  
	       searchMenu.add(searchMenu_Search); 
	    //Adds a edit menu and menu item to the menu bar
	       menuBar.add(editMenu);       
	       editMenu.addSeparator();       
	       editMenu.add(editMenu_Cut);   
	       editMenu.add(editMenu_Copy);   
	       editMenu.add(editMenu_Paste);   
	       editMenu.add(editMenu_Delete);     
	       editMenu.addSeparator();      
	       editMenu.add(editMenu_SelectAll);   
	       editMenu.add(editMenu_TimeDate);  
	    //Adds a view menu and menu item to the menu bar
	       menuBar.add(viewMenu);   
	       viewMenu.add(viewMenu_Status);  	 
	     //Adds a help menu and menu item to the menu bar
	       menuBar.add(helpMenu);  
	       helpMenu.add(helpMenu_AboutNotepad);  	    
	     //Add a menu bar to the window            
	       this.setJMenuBar(menuBar);  	    
	     //Create a text editing area and add a scroll bar
	       editArea=new JTextArea(20,50);  
	       JScrollPane scroller=new JScrollPane(editArea);  
	       scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
	       this.add(scroller,BorderLayout.CENTER);//Adds a text editing area to the window
	       editArea.setWrapStyleWord(true);//Sets the word to wrap when a line is not large enough
	       editArea.setLineWrap(true);
	       
	       Font f = new Font(Font.DIALOG, 8, Json());
	       editArea.setFont(f);
	       oldValue=editArea.getText();//Gets the content of the original text edit area  
	       //Create a right-click pop-up menu
	       popupMenu=new JPopupMenu();  
	       popupMenu_Cut=new JMenuItem("Cut(T)");  
	       popupMenu_Copy=new JMenuItem("Copy(C)");  
	       popupMenu_Paste=new JMenuItem("Paste(P)");  
	       popupMenu_Delete=new JMenuItem("Delete(D)");  
	       popupMenu_SelectAll=new JMenuItem("SelectAll(A)");  

	     //Adds menu items and delimiters to the right-click menu
	       popupMenu.add(popupMenu_Cut);  
	       popupMenu.add(popupMenu_Copy);  
	       popupMenu.add(popupMenu_Paste);  
	       popupMenu.add(popupMenu_Delete);  
	       popupMenu.addSeparator();  
	       popupMenu.add(popupMenu_SelectAll);  	       
	       //The text edit area registers right-click menu events
	       popupMenu_Cut.addActionListener(this);  
	       popupMenu_Copy.addActionListener(this);  
	       popupMenu_Paste.addActionListener(this);  
	       popupMenu_Delete.addActionListener(this);  
	       popupMenu_SelectAll.addActionListener(this);  	

	       editArea.addMouseListener(new MouseAdapter()  {
	    	   public void mousePressed(MouseEvent e)  {
	              if(e.isPopupTrigger())//Returns whether this mouse event is triggered for the platform's pop-up menu
	               {   popupMenu.show(e.getComponent(),e.getX(),e.getY());//Positions X and Y in the component caller's coordinate space display pop-up menus
	               }  
	               checkMenuItemEnabled();//Set the availability of cut, copy, paste, delete, etc
	               editArea.requestFocus(); 
	           }  
	           public void mouseReleased(MouseEvent e)  {
	              if(e.isPopupTrigger())//Returns whether this mouse event is triggered for the platform's pop-up menu
	               {   popupMenu.show(e.getComponent(),e.getX(),e.getY());//Positions X and Y in the component caller's coordinate space display pop-up menus
	               }  
	               checkMenuItemEnabled();//Set the availability of cut, copy, paste, delete, etc
	               editArea.requestFocus();
	           }  
	       });//Text edit area register right menu event end 	       
	       //Create and add status bars
	       statusLabel=new JLabel("　This is the view.");  
	       this.add(statusLabel,BorderLayout.SOUTH);//Adds a status bar label to the window	 
	       //Sets the window's position, size, and visibility on the screen
	       this.setLocation(100,100);  
	       this.setSize(650,550);  
	       this.setVisible(true);  
	       //Add a window listener
	       addWindowListener(new WindowAdapter()  
	       {   public void windowClosing(WindowEvent e)  
	           {   exitWindowChoose();  
	           }  
	       });  	 
	       checkMenuItemEnabled();  
	       editArea.requestFocus();  
	   }//The constructor textEditor ends     
 //Set the availability of menu items: cut, copy, paste, delete functions
	   public void checkMenuItemEnabled()  
	   {   String selectText=editArea.getSelectedText();  
	       if(selectText==null)  
	       {   editMenu_Cut.setEnabled(false);  
	           popupMenu_Cut.setEnabled(false);  
	           editMenu_Copy.setEnabled(false);  
	           popupMenu_Copy.setEnabled(false);  
	           editMenu_Delete.setEnabled(false);  
	           popupMenu_Delete.setEnabled(false);  
	       }else  
	       {   editMenu_Cut.setEnabled(true);  
	           popupMenu_Cut.setEnabled(true);   
	           editMenu_Copy.setEnabled(true);  
	           popupMenu_Copy.setEnabled(true);  
	           editMenu_Delete.setEnabled(true);  
	           popupMenu_Delete.setEnabled(true);  
	       }  
	       //Paste function usability judgment
	       Transferable contents=clipBoard.getContents(this);  
	       if(contents==null)  
	       {   editMenu_Paste.setEnabled(false);  
	           popupMenu_Paste.setEnabled(false);  
	       }else  
	       {   editMenu_Paste.setEnabled(true);  
	           popupMenu_Paste.setEnabled(true);     
	       }  
	   }//The checkMenuItemEnabled() method ends
	   //Called when the window is closed
	   public void exitWindowChoose() { 
	      editArea.requestFocus();  
	       String currentValue=editArea.getText();  
	       if(currentValue.equals(oldValue)==true)  {
	          System.exit(0);  
	       }else  {
	          int exitChoose=JOptionPane.showConfirmDialog(this,"您的文件尚未保存，是否保存？","退出提示",JOptionPane.YES_NO_CANCEL_OPTION);  
	           if(exitChoose==JOptionPane.YES_OPTION)   
	           {   //boolean isSave=false;  
	               if(isNewFile)  
	               {     
	                   String str=null;  
	                   JFileChooser fileChooser=new JFileChooser();  
	                   fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
	                   fileChooser.setApproveButtonText("确定");  
	                   fileChooser.setDialogTitle("另存为");  
	                     
	                   int result=fileChooser.showSaveDialog(this);  
	                     
	                   if(result==JFileChooser.CANCEL_OPTION)  
	                   {   statusLabel.setText("　您没有保存文件");  
	                       return;  
	                   }                     
	     
	                   File saveFileName=fileChooser.getSelectedFile();  
	                 
	                   if(saveFileName==null||saveFileName.getName().equals(""))  
	                   {   JOptionPane.showMessageDialog(this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);  
	                   }else   
	                   {   try  
	                       {   FileWriter fw=new FileWriter(saveFileName);  
	                           BufferedWriter bfw=new BufferedWriter(fw);  
	                           bfw.write(editArea.getText(),0,editArea.getText().length());  
	                           bfw.flush();  
	                           fw.close();  
	                             
	                           isNewFile=false;  
	                           currentFile=saveFileName;  
	                           oldValue=editArea.getText();  
	                             
	                           this.setTitle(saveFileName.getName()+"  - 记事本");  
	                           statusLabel.setText("　当前打开文件:"+saveFileName.getAbsoluteFile());  
	                           //isSave=true;  
	                       }                             
	                       catch(IOException ioException){                   
	                       }                 
	                   }  
	               }else  
	               {  
	                   try  
	                   {   FileWriter fw=new FileWriter(currentFile);  
	                       BufferedWriter bfw=new BufferedWriter(fw);  
	                       bfw.write(editArea.getText(),0,editArea.getText().length());  
	                       bfw.flush();  
	                       fw.close();  
	                       //isSave=true;  
	                   }                             
	                   catch(IOException ioException){                   
	                   }  
	               }  
	               System.exit(0);  
	               //if(isSave)System.exit(0);  
	               //else return;  
	           }else if(exitChoose==JOptionPane.NO_OPTION)  
	           {   System.exit(0);  
	           }else  
	           {   return;  
	           }  
	       }  
	   }//Called when the window is closed ends 	     
	   //search method 
	   public void find()  
	   {   final JDialog searchDialog=new JDialog(this,"查找",false);//false时允许其他窗口同时处于激活状态(即无模式)  
	       Container con=searchDialog.getContentPane();//返回此对话框的contentPane对象      
	       con.setLayout(new FlowLayout(FlowLayout.LEFT));  
	       JLabel searchContentLabel=new JLabel("查找内容(N)：");  
	       final JTextField searchText=new JTextField(15);  
	       JButton searchNextButton=new JButton("查找下一个(F)：");  
	       final JCheckBox matchCheckBox=new JCheckBox("区分大小写(C)");  
	       ButtonGroup bGroup=new ButtonGroup();  
	       final JRadioButton upButton=new JRadioButton("向上(U)");  
	       final JRadioButton downButton=new JRadioButton("向下(U)");  
	       downButton.setSelected(true);  
	       bGroup.add(upButton);  
	       bGroup.add(downButton);  
	       JButton cancel=new JButton("取消");  	       
	       //Cancel button event processing
	       cancel.addActionListener(new ActionListener()  
	       {   public void actionPerformed(ActionEvent e)  
	           {   searchDialog.dispose();  
	           }  
	       }); 	       
	       //The find next button listens
	       searchNextButton.addActionListener(new ActionListener()  
	       {   public void actionPerformed(ActionEvent e)  
	           {   //Whether the "case-sensitive (C)" JCheckBox is selected
	               int k=0,m=0;  
	               final String str1,str2,str3,str4,strA,strB;  
	               str1=editArea.getText();  
	               str2=searchText.getText();  
	               str3=str1.toUpperCase();  
	               str4=str2.toUpperCase();  
	               if(matchCheckBox.isSelected())
	               {   strA=str1;  
	                   strB=str2;  
	               }else//Case-insensitive, in which case the selection is all uppercase (or lowercase) for easy lookup
	               {   strA=str3;  
	                   strB=str4;  
	               }  
	               if(upButton.isSelected())  
	               {   //k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);  
	                   if(editArea.getSelectedText()==null)  
	                       k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);  
	                   else  
	                       k=strA.lastIndexOf(strB, editArea.getCaretPosition()-searchText.getText().length()-1);      
	                   if(k>-1)  
	                   {   //String strData=strA.subString(k,strB.getText().length()+1);  
	                       editArea.setCaretPosition(k);  
	                       editArea.select(k,k+strB.length());  
	                   }else  
	                   {   JOptionPane.showMessageDialog(null,"找不到您查找的内容！","查找",JOptionPane.INFORMATION_MESSAGE);  
	                   }  
	               }else if(downButton.isSelected())  
	               {   if(editArea.getSelectedText()==null)  
	                       k=strA.indexOf(strB,editArea.getCaretPosition()+1);  
	                   else  
	                       k=strA.indexOf(strB, editArea.getCaretPosition()-searchText.getText().length()+1);      
	                   if(k>-1)  
	                   {   //String strData=strA.subString(k,strB.getText().length()+1);  
	                       editArea.setCaretPosition(k);  
	                       editArea.select(k,k+strB.length());  
	                   }else  
	                   {   JOptionPane.showMessageDialog(null,"找不到您查找的内容！","查找",JOptionPane.INFORMATION_MESSAGE);  
	                   }  
	               }  
	           }  
	       });//The "find next" button ends the listening      	       	       
	       //Create an interface for the find dialog box
	       JPanel panel1=new JPanel();  
	       JPanel panel2=new JPanel();  
	       JPanel panel3=new JPanel();  
	       JPanel directionPanel=new JPanel();  
	       directionPanel.setBorder(BorderFactory.createTitledBorder("方向"));  
	       //Set the frame of the directionPanel component;
	       directionPanel.add(upButton);  
	       directionPanel.add(downButton);  
	       panel1.setLayout(new GridLayout(2,1));  
	       panel1.add(searchNextButton);  
	       panel1.add(cancel);  
	       panel2.add(searchContentLabel);  
	       panel2.add(searchText);  
	       panel2.add(panel1);  
	       panel3.add(matchCheckBox);  
	       panel3.add(directionPanel);  
	       con.add(panel2);  
	       con.add(panel3);  
	       searchDialog.setSize(410,180);  
	       searchDialog.setResizable(false);//non-resize
	       searchDialog.setLocation(230,280);  
	       searchDialog.setVisible(true);  
	   }//End of search method   	 	         	   
	   public void actionPerformed(ActionEvent e)  
	   {   //new  
	       if(e.getSource()==fileMenu_New)  
	       {   editArea.requestFocus();  
	           String currentValue=editArea.getText();  
	           boolean isTextChange=(currentValue.equals(oldValue))?false:true;  
	           if(isTextChange)  
	           {   int saveChoose=JOptionPane.showConfirmDialog(this,"您的文件尚未保存，是否保存？","提示",JOptionPane.YES_NO_CANCEL_OPTION);  
	               if(saveChoose==JOptionPane.YES_OPTION)  
	               {   String str=null;  
	                   JFileChooser fileChooser=new JFileChooser();  
	                   fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
	                   fileChooser.setDialogTitle("另存为");  
	                   int result=fileChooser.showSaveDialog(this);  
	                   if(result==JFileChooser.CANCEL_OPTION)  
	                   {   statusLabel.setText("您没有选择任何文件");  
	                       return;  
	                   }  
	                   File saveFileName=fileChooser.getSelectedFile();  
	                   if(saveFileName==null || saveFileName.getName().equals(""))  
	                   {   JOptionPane.showMessageDialog(this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);  
	                   }else   
	                   {   try  
	                       {   FileWriter fw=new FileWriter(saveFileName);  
	                           BufferedWriter bfw=new BufferedWriter(fw);  
	                           bfw.write(editArea.getText(),0,editArea.getText().length());  
	                           bfw.flush();//刷新该流的缓冲  
	                           bfw.close();  
	                           isNewFile=false;  
	                           currentFile=saveFileName;  
	                           oldValue=editArea.getText();  
	                           this.setTitle(saveFileName.getName()+" - 记事本");  
	                           statusLabel.setText("当前打开文件："+saveFileName.getAbsoluteFile());  
	                       }  
	                       catch (IOException ioException)  
	                       {  
	                       }  
	                   }  
	               }else if(saveChoose==JOptionPane.NO_OPTION)  
	               {   editArea.replaceRange("",0,editArea.getText().length());  
	                   statusLabel.setText(" 新建文件");  
	                   this.setTitle("无标题 - 记事本");  
	                   isNewFile=true;  
	                   oldValue=editArea.getText();  
	               }else if(saveChoose==JOptionPane.CANCEL_OPTION)  
	               {   return;  
	               }  
	           }else  
	           {   editArea.replaceRange("",0,editArea.getText().length());  
	               statusLabel.setText(" 新建文件");  
	               this.setTitle("无标题 - 记事本");  
	               isNewFile=true;  
	               oldValue=editArea.getText();  
	           }  
	       }//new ends  	       
	       //open  
	       else if(e.getSource()==fileMenu_Open)  
	       {   editArea.requestFocus();  
	           String currentValue=editArea.getText();  
	           boolean isTextChange=(currentValue.equals(oldValue))?false:true;  
	           if(isTextChange)  
	           {   int saveChoose=JOptionPane.showConfirmDialog(this,"您的文件尚未保存，是否保存？","提示",JOptionPane.YES_NO_CANCEL_OPTION);  
	               if(saveChoose==JOptionPane.YES_OPTION)  
	               {   String str=null;  
	                   JFileChooser fileChooser=new JFileChooser();  
	                   fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
	                   fileChooser.setDialogTitle("另存为");  
	                   int result=fileChooser.showSaveDialog(this);  
	                   if(result==JFileChooser.CANCEL_OPTION)  
	                   {   statusLabel.setText("您没有选择任何文件");  
	                       return;  
	                   }  
	                   File saveFileName=fileChooser.getSelectedFile();  
	                   if(saveFileName==null || saveFileName.getName().equals(""))  
	                   {   JOptionPane.showMessageDialog(this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);  
	                   }else   
	                   {   try  
	                       {   FileWriter fw=new FileWriter(saveFileName);  
	                           BufferedWriter bfw=new BufferedWriter(fw);  
	                           bfw.write(editArea.getText(),0,editArea.getText().length());  
	                           bfw.flush();//Flush the buffer of the stream
	                           bfw.close();  
	                           isNewFile=false;  
	                           currentFile=saveFileName;  
	                           oldValue=editArea.getText();  
	                           this.setTitle(saveFileName.getName()+" - 记事本");  
	                           statusLabel.setText("当前打开文件："+saveFileName.getAbsoluteFile());  
	                       }  
	                       catch (IOException ioException)  
	                       {  
	                       }  
	                   }  
	               }else if(saveChoose==JOptionPane.NO_OPTION)  
	               {   String str=null;  
	                   JFileChooser fileChooser=new JFileChooser();  
	                   fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
	                   fileChooser.setDialogTitle("打开文件");  
	                   int result=fileChooser.showOpenDialog(this);  
	                   if(result==JFileChooser.CANCEL_OPTION)  
	                   {   statusLabel.setText("您没有选择任何文件");  
	                       return;  
	                   }  
	                   File fileName=fileChooser.getSelectedFile();  
	                   if(fileName==null || fileName.getName().equals(""))  
	                   {   JOptionPane.showMessageDialog(this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);  
	                   }else  {   
	                	   try  
	                       {   FileReader fr=new FileReader(fileName);  
	                           BufferedReader bfr=new BufferedReader(fr);  
	                           editArea.setText("");  
	                           while((str=bfr.readLine())!=null)  
	                           {   editArea.append(str);  
	                           }  
	                           this.setTitle(fileName.getName()+" - 记事本");  
	                           statusLabel.setText(" 当前打开文件："+fileName.getAbsoluteFile());  
	                           fr.close();  
	                           isNewFile=false;  
	                           currentFile=fileName;  
	                           oldValue=editArea.getText();  
	                       }  
	                       catch (IOException ioException)  
	                       {  
	                       }  
	                   }  
	               } else  
	               {   return;  
	               }  
	           }else  
	           {   String str=null;  
	               JFileChooser fileChooser=new JFileChooser();  
	               fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
	               fileChooser.setDialogTitle("打开文件");  
	               int result=fileChooser.showOpenDialog(this);  
	               if(result==JFileChooser.CANCEL_OPTION)  
	               {   statusLabel.setText(" 您没有选择任何文件 ");  
	                   return;  
	               }  
	               File fileName=fileChooser.getSelectedFile();  
	               if(fileName==null || fileName.getName().equals(""))  
	               {   JOptionPane.showMessageDialog(this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);  
	               }else  
	               {   try  
	                   {   FileReader fr=new FileReader(fileName);  
	                       BufferedReader bfr=new BufferedReader(fr);  
	                       editArea.setText("");  
	                       while((str=bfr.readLine())!=null)  
	                       {   editArea.append(str);  
	                       }  
	                       this.setTitle(fileName.getName()+" - 记事本");  
	                       statusLabel.setText(" 当前打开文件："+fileName.getAbsoluteFile());  
	                       fr.close();  
	                       isNewFile=false;  
	                       currentFile=fileName;  
	                       oldValue=editArea.getText();  
	                   }  
	                   catch (IOException ioException)  
	                   {  
	                   }  
	               }  
	           }  
	       }//open ends  	       
	       //save
	       else if(e.getSource()==fileMenu_Save)  
	       {   editArea.requestFocus();  
	           if(isNewFile)  
	           {   String str=null;  
	               JFileChooser fileChooser=new JFileChooser();  
	               fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);    
	               fileChooser.setDialogTitle("保存");  
	               int result=fileChooser.showSaveDialog(this);  
	               if(result==JFileChooser.CANCEL_OPTION)  
	               {   statusLabel.setText("您没有选择任何文件");  
	                   return;  
	               }  
	               File saveFileName=fileChooser.getSelectedFile();  
	               if(saveFileName==null || saveFileName.getName().equals(""))  
	               {   JOptionPane.showMessageDialog(this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);  
	               }else   
	               {   try  
	                   {   FileWriter fw=new FileWriter(saveFileName);  
	                       BufferedWriter bfw=new BufferedWriter(fw);  
	                       bfw.write(editArea.getText(),0,editArea.getText().length());  
	                       bfw.flush();//刷新该流的缓冲  
	                       bfw.close();  
	                       isNewFile=false;  
	                       currentFile=saveFileName;  
	                       oldValue=editArea.getText();  
	                       this.setTitle(saveFileName.getName()+" - 记事本");  
	                       statusLabel.setText("当前打开文件："+saveFileName.getAbsoluteFile());  
	                   }  
	                   catch (IOException ioException)  
	                   {  
	                   }  
	               }  
	           }else  
	           {   try  
	               {   FileWriter fw=new FileWriter(currentFile);  
	                   BufferedWriter bfw=new BufferedWriter(fw);  
	                   bfw.write(editArea.getText(),0,editArea.getText().length());  
	                   bfw.flush();  
	                   fw.close();  
	               }                             
	               catch(IOException ioException)  
	               {                     
	               }  
	           }  
	       }//save ends	       
	       //save as  
	       else if(e.getSource()==fileMenu_SaveAs)  
	       {   editArea.requestFocus();  
	           String str=null;  
	           JFileChooser fileChooser=new JFileChooser();  
	           fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);    
	           fileChooser.setDialogTitle("另存为");  
	           int result=fileChooser.showSaveDialog(this);  
	           if(result==JFileChooser.CANCEL_OPTION)  
	           {   statusLabel.setText("　您没有选择任何文件");  
	               return;  
	           }                 
	           File saveFileName=fileChooser.getSelectedFile();  
	           if(saveFileName==null||saveFileName.getName().equals(""))  
	           {   JOptionPane.showMessageDialog(this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);  
	           }else   
	           {   try  
	               {   FileWriter fw=new FileWriter(saveFileName);  
	                   BufferedWriter bfw=new BufferedWriter(fw);  
	                   bfw.write(editArea.getText(),0,editArea.getText().length());  
	                   bfw.flush();  
	                   fw.close();  
	                   oldValue=editArea.getText();  
	                   this.setTitle(saveFileName.getName()+"  - 记事本");  
	                   statusLabel.setText("　当前打开文件:"+saveFileName.getAbsoluteFile());  
	               }                         
	               catch(IOException ioException)  
	               {                     
	               }                 
	           }  
	       }//save as ends	      
	       //print
	       else if(e.getSource()==fileMenu_Print)  
	       {   p=getToolkit().getPrintJob(this,"ok",null);
           	   g=p.getGraphics();
           	   g.translate(120,200);
           	   this.printAll(g);
           	   g.dispose();
           	   p.end();
	       }//print ends	       
	       //exit  
	       else if(e.getSource()==fileMenu_Exit)  
	       {   int exitChoose=JOptionPane.showConfirmDialog(this,"确定要退出吗?","退出提示",JOptionPane.OK_CANCEL_OPTION);  
	           if(exitChoose==JOptionPane.OK_OPTION)  
	           {   System.exit(0);  
	           }  
	           else  
	           {   return;  
	           }  	           	           	        	           	           
	       }//exit ends
	       //cut
	       else if(e.getSource()==editMenu_Cut  || e.getSource()==popupMenu_Cut)  
	       {   editArea.requestFocus();  
	           String text=editArea.getSelectedText();  
	           StringSelection selection=new StringSelection(text);  
	           clipBoard.setContents(selection,null);  
	           editArea.replaceRange("",editArea.getSelectionStart(),editArea.getSelectionEnd());  
	           checkMenuItemEnabled();//Sets the availability of cut, copy, paste, delete, and so on
	       }//cut ends  
	       //copy 
	       else if(e.getSource()==editMenu_Copy || e.getSource()==popupMenu_Copy)  
	       {   editArea.requestFocus();  
	           String text=editArea.getSelectedText();  
	           StringSelection selection=new StringSelection(text);  
	           clipBoard.setContents(selection,null);  
	           checkMenuItemEnabled();//Sets the availability of cut, copy, paste, delete, and so on  
	       }//copy ends  
	       // paste
	       else if(e.getSource()==editMenu_Paste|| e.getSource()==popupMenu_Paste) 
	       {   editArea.requestFocus();  
	           Transferable contents=clipBoard.getContents(this);  
	           if(contents==null)return;  
	           String text="";  
	           try  
	           {   text=(String)contents.getTransferData(DataFlavor.stringFlavor);  
	           }  
	           catch (Exception exception)  
	           {  
	           }  
	           editArea.replaceRange(text,editArea.getSelectionStart(),editArea.getSelectionEnd());  
	           checkMenuItemEnabled();  
	       }//paste ends  
	       //delete  
	       else if(e.getSource()==editMenu_Delete || e.getSource()==popupMenu_Paste)  
	       {   editArea.requestFocus();  
	           editArea.replaceRange("",editArea.getSelectionStart(),editArea.getSelectionEnd());  
	           checkMenuItemEnabled(); //Sets the availability of cut, copy, paste, delete, and so on
	       }//delete ends 
	       //search 
	       else if(e.getSource()==searchMenu_Search)  
	       {   editArea.requestFocus();  
	           find();  
	       }//search ends      
	       //T / D 
	       else if(e.getSource()==editMenu_TimeDate)  
	       {   editArea.requestFocus(); 
	           Calendar rightNow=Calendar.getInstance();  
	           Date date=rightNow.getTime();  
	           editArea.insert(date.toString(),editArea.getCaretPosition());  
	       }//T /D ends  
	       //select all 
	       else if(e.getSource()==editMenu_SelectAll)  
	       {   editArea.selectAll();  
	       }//select all ends	         	     	
	       //Sets the visibility of the status bar 
	       else if(e.getSource()==viewMenu_Status)  
	       {   if(viewMenu_Status.getState())  
	               statusLabel.setVisible(true);  
	           else   
	               statusLabel.setVisible(false);  
	       }//Sets the visibility of the status bar ends 
	       //help 
	       else if(e.getSource()==helpMenu_HelpTopics)  
	       {   editArea.requestFocus();  
	           JOptionPane.showMessageDialog(this,"这是一个文本编译器","帮助主题",JOptionPane.INFORMATION_MESSAGE);  
	       }//help ends 
	       //about  
	       else if(e.getSource()==helpMenu_AboutNotepad)  
	       {   editArea.requestFocus();  
	           JOptionPane.showMessageDialog(this,  
	        		   "251Assignment1 Team Members:\n"+  
	        	               " Xie Jinyi        ID number:19023266 \n"+  
	        	               " Zhang Whenjing   ID number:19023272 \n"+
	        	               "这是一个文本编译器",  "记事本",JOptionPane.INFORMATION_MESSAGE);  
	       }//about ends
	   }//method actionPerformed() ends  	
	   
	   
	   //main starts  
	   public static void main(String args[]) throws  NullPointerException
	   {   textEditor notepad=new textEditor();  
	       notepad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   }
	   public int Json()  {
	       String path="src//main//ass1.Json";
	       String s = readJsonFile(path);
	       System.out.println(s);
	       
//	      com.alibaba.fastjson.JSONObject jobj =JSON.parseObject(s);
//	      int size = jobj.getIntValue("size");    

	      return 20;

	   }//main ends
	
	public static String readFileByLines(String fileName) {
        FileInputStream file = null;
        BufferedReader reader = null;
        InputStreamReader inputFileReader = null;
        String content = "";
        String tempString = null;
        try {
            file = new FileInputStream(fileName);
            inputFileReader = new InputStreamReader(file, "utf-8");
            reader = new BufferedReader(inputFileReader);
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                content += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return content;
    }
 
	private static String readJsonFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}
	public void insertUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
	}
	

	
	
	
		
}



