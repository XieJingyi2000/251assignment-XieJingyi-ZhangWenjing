
/**
 * @author xiejingyi
 */
package massey.ac.nz.Assignment1.textEditor;
import java.awt.*; 
import java.awt.event.*;  
import java.text.*;  
import java.util.*;  
import java.io.*;  
import javax.swing.undo.*;  
import javax.swing.border.*;  
import javax.swing.*;  
import javax.swing.text.*;  
import javax.swing.event.*;  
import java.awt.datatransfer.*;  

public class textEditor extends JFrame implements ActionListener,DocumentListener  
{ 	
	PrintJob p=null;  //声明一个PrintJob对象 即打印
	Graphics g=null;
	JMenu fileMenu,editMenu,formatMenu,viewMenu,helpMenu,searchMenu; //菜单      	
	JPopupMenu popupMenu;    //右键弹出菜单项  
	JMenuItem popupMenu_Undo,popupMenu_Cut,popupMenu_Copy,popupMenu_Paste,popupMenu_Delete,popupMenu_SelectAll;	
	JMenuItem fileMenu_New,fileMenu_Open,fileMenu_Save,fileMenu_SaveAs,fileMenu_PageSetUp,fileMenu_Print,fileMenu_Exit; //“文件”的菜单项  
	JMenuItem editMenu_Cut,editMenu_Copy,editMenu_Paste,editMenu_Delete,editMenu_Find,editMenu_FindNext,editMenu_GoTo,editMenu_SelectAll,editMenu_TimeDate;  //“编辑”的菜单项  	
	JCheckBoxMenuItem viewMenu_Status;  //“查看”的菜单项  	
	JMenuItem helpMenu_HelpTopics,helpMenu_AboutNotepad; //“帮助”的菜单项   	
	JMenuItem searchMenu_Search;//"查找"的菜单项	
	JTextArea editArea;  //“文本”编辑区域  	
	JLabel statusLabel;  //状态栏标签   
	Toolkit toolkit=Toolkit.getDefaultToolkit(); //系统剪贴板   
	Clipboard clipBoard=toolkit.getSystemClipboard();
	//其他变量  
	String oldValue;//存放编辑区原来的内容，用于比较文本是否有改动  
	boolean isNewFile=true;//是否新文件(未保存过的)  
	File currentFile;//当前文件名 
	//构造函数开始  
	public textEditor(){     
		super("Text Editor"); 
		//创建菜单条  
	    JMenuBar menuBar=new JMenuBar(); 
	    //创建文件菜单及菜单项并注册事件监听  
	    fileMenu=new JMenu("File(F)");  
	    fileMenu.setMnemonic('F');//设置快捷键ALT+F  
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
	  //创建查找菜单及菜单项并注册事件监听
	    searchMenu=new JMenu("Search(S)");
	    searchMenu.setMnemonic('S');//设置快捷键ALT+E  
	    searchMenu_Search=new JMenuItem("Search(S)...");  
	    searchMenu_Search.setAccelerator(KeyStroke.getKeyStroke  (KeyEvent.VK_F,InputEvent.CTRL_MASK));  
	    searchMenu_Search.addActionListener(this);  
	  //创建编辑菜单及菜单项并注册事件监听  
	    editMenu=new JMenu("Edit(E)");  
	    editMenu.setMnemonic('E');//设置快捷键ALT+E  	  	 
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
	  //创建查看菜单及菜单项并注册事件监听  
	    viewMenu=new JMenu("View(V)");  
	    viewMenu.setMnemonic('V');//设置快捷键ALT+V  
	    viewMenu_Status=new JCheckBoxMenuItem("Status(S)");  
	    viewMenu_Status.setMnemonic('S');//设置快捷键ALT+S  
	    viewMenu_Status.setState(true);  
	    viewMenu_Status.addActionListener(this); 
	  //创建帮助菜单及菜单项并注册事件监听  
	       helpMenu = new JMenu("Help(H)");  
	       helpMenu.setMnemonic('H');//设置快捷键ALT+H   	 
	       helpMenu_AboutNotepad = new JMenuItem("About(A)");   
	       helpMenu_AboutNotepad.addActionListener(this);  	    
	   //向菜单条添加"文件"菜单及菜单项  
	       menuBar.add(fileMenu);   
	       fileMenu.add(fileMenu_New);   
	       fileMenu.add(fileMenu_Open);   
	       fileMenu.add(fileMenu_Save);   
	       fileMenu.add(fileMenu_SaveAs);   
	       fileMenu.addSeparator();        //分隔线     
	       fileMenu.add(fileMenu_Print);   
	       fileMenu.addSeparator();        //分隔线   
	       fileMenu.add(fileMenu_Exit);  
	    //向菜单条添加"查找"菜单及菜单项 
	       menuBar.add(searchMenu);  
	       searchMenu.add(searchMenu_Search); 
	    //向菜单条添加"编辑"菜单及菜单项   
	       menuBar.add(editMenu);       
	       editMenu.addSeparator();        //分隔线   
	       editMenu.add(editMenu_Cut);   
	       editMenu.add(editMenu_Copy);   
	       editMenu.add(editMenu_Paste);   
	       editMenu.add(editMenu_Delete);     
	       editMenu.addSeparator();        //分隔线  
	       editMenu.add(editMenu_SelectAll);   
	       editMenu.add(editMenu_TimeDate);  
	    //向菜单条添加"查看"菜单及菜单项   
	       menuBar.add(viewMenu);   
	       viewMenu.add(viewMenu_Status);  	 
	     //向菜单条添加"帮助"菜单及菜单项  
	       menuBar.add(helpMenu);  
	       helpMenu.add(helpMenu_AboutNotepad);  	    
	     //向窗口添加菜单条                
	       this.setJMenuBar(menuBar);  	    
	     //创建文本编辑区并添加滚动条  
	       editArea=new JTextArea(20,50);  
	       JScrollPane scroller=new JScrollPane(editArea);  
	       scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
	       this.add(scroller,BorderLayout.CENTER);//向窗口添加文本编辑区  
	       editArea.setWrapStyleWord(true);//设置单词在一行不足容纳时换行  
	       editArea.setLineWrap(true);//设置文本编辑区自动换行默认为true,即会"自动换行"  
	       //this.add(editArea,BorderLayout.CENTER);//向窗口添加文本编辑区  
	       oldValue=editArea.getText();//获取原文本编辑区的内容        
	       //创建右键弹出菜单  
	       popupMenu=new JPopupMenu();  
	       popupMenu_Undo=new JMenuItem("Undo(U)");  
	       popupMenu_Cut=new JMenuItem("Cut(T)");  
	       popupMenu_Copy=new JMenuItem("Copy(C)");  
	       popupMenu_Paste=new JMenuItem("Paste(P)");  
	       popupMenu_Delete=new JMenuItem("Delete(D)");  
	       popupMenu_SelectAll=new JMenuItem("SelectAll(A)");  
	       popupMenu_Undo.setEnabled(false);  	 
	     //向右键菜单添加菜单项和分隔符  
	       popupMenu.add(popupMenu_Undo);  
	       popupMenu.addSeparator();  
	       popupMenu.add(popupMenu_Cut);  
	       popupMenu.add(popupMenu_Copy);  
	       popupMenu.add(popupMenu_Paste);  
	       popupMenu.add(popupMenu_Delete);  
	       popupMenu.addSeparator();  
	       popupMenu.add(popupMenu_SelectAll);  	       
	       //文本编辑区注册右键菜单事件  
	       popupMenu_Cut.addActionListener(this);  
	       popupMenu_Copy.addActionListener(this);  
	       popupMenu_Paste.addActionListener(this);  
	       popupMenu_Delete.addActionListener(this);  
	       popupMenu_SelectAll.addActionListener(this);  	 
	       //文本编辑区注册右键菜单事件  
	       editArea.addMouseListener(new MouseAdapter()  {
	    	   public void mousePressed(MouseEvent e)  {
	              if(e.isPopupTrigger())//返回此鼠标事件是否为该平台的弹出菜单触发事件  
	               {   popupMenu.show(e.getComponent(),e.getX(),e.getY());//在组件调用者的坐标空间中的位置 X、Y 显示弹出菜单  
	               }  
	               checkMenuItemEnabled();//设置剪切，复制，粘帖，删除等功能的可用性  
	               editArea.requestFocus();//编辑区获取焦点  
	           }  
	           public void mouseReleased(MouseEvent e)  {
	              if(e.isPopupTrigger())//返回此鼠标事件是否为该平台的弹出菜单触发事件  
	               {   popupMenu.show(e.getComponent(),e.getX(),e.getY());//在组件调用者的坐标空间中的位置 X、Y 显示弹出菜单  
	               }  
	               checkMenuItemEnabled();//设置剪切，复制，粘帖，删除等功能的可用性  
	               editArea.requestFocus();//编辑区获取焦点  
	           }  
	       });//文本编辑区注册右键菜单事件结束  	       
	       //创建和添加状态栏  
	       statusLabel=new JLabel("　This is the view.");  
	       this.add(statusLabel,BorderLayout.SOUTH);//向窗口添加状态栏标签  	 
	       //设置窗口在屏幕上的位置、大小和可见性   
	       this.setLocation(100,100);  
	       this.setSize(650,550);  
	       this.setVisible(true);  
	       //添加窗口监听器  
	       addWindowListener(new WindowAdapter()  
	       {   public void windowClosing(WindowEvent e)  
	           {   exitWindowChoose();  
	           }  
	       });  	 
	       checkMenuItemEnabled();  
	       editArea.requestFocus();  
	   }//构造函数textEditor结束  	     
 //设置菜单项的可用性：剪切，复制，粘帖，删除功能  
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
	       //粘帖功能可用性判断  
	       Transferable contents=clipBoard.getContents(this);  
	       if(contents==null)  
	       {   editMenu_Paste.setEnabled(false);  
	           popupMenu_Paste.setEnabled(false);  
	       }else  
	       {   editMenu_Paste.setEnabled(true);  
	           popupMenu_Paste.setEnabled(true);     
	       }  
	   }//方法checkMenuItemEnabled()结束    
	   //关闭窗口时调用  
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
	   }//关闭窗口时调用方法结束  	     
	   //查找方法  
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
	       //取消按钮事件处理  
	       cancel.addActionListener(new ActionListener()  
	       {   public void actionPerformed(ActionEvent e)  
	           {   searchDialog.dispose();  
	           }  
	       }); 	       
	       //"查找下一个"按钮监听  
	       searchNextButton.addActionListener(new ActionListener()  
	       {   public void actionPerformed(ActionEvent e)  
	           {   //"区分大小写(C)"的JCheckBox是否被选中  
	               int k=0,m=0;  
	               final String str1,str2,str3,str4,strA,strB;  
	               str1=editArea.getText();  
	               str2=searchText.getText();  
	               str3=str1.toUpperCase();  
	               str4=str2.toUpperCase();  
	               if(matchCheckBox.isSelected())//区分大小写  
	               {   strA=str1;  
	                   strB=str2;  
	               }else//不区分大小写,此时把所选内容全部化成大写(或小写)，以便于查找   
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
	       });//"查找下一个"按钮监听结束  	       	       	       
	       //创建"查找"对话框的界面  
	       JPanel panel1=new JPanel();  
	       JPanel panel2=new JPanel();  
	       JPanel panel3=new JPanel();  
	       JPanel directionPanel=new JPanel();  
	       directionPanel.setBorder(BorderFactory.createTitledBorder("方向"));  
	       //设置directionPanel组件的边框;  
	       //BorderFactory.createTitledBorder(String title)创建一个新标题边框，使用默认边框（浮雕化）、默认文本位置（位于顶线上）、默认调整 (leading) 以及由当前外观确定的默认字体和文本颜色，并指定了标题文本。  
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
	       searchDialog.setResizable(false);//不可调整大小  
	       searchDialog.setLocation(230,280);  
	       searchDialog.setVisible(true);  
	   }//查找方法结束  	     	 	         	   
	   public void actionPerformed(ActionEvent e)  
	   {   //新建  
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
	       }//新建结束  	       
	       //打开  
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
	       }//打开结束  	       
	       //保存  
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
	       }//保存结束  	       
	       //另存为  
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
	       }//另存为结束 	      
	       //打印  
	       else if(e.getSource()==fileMenu_Print)  
	       {   p=getToolkit().getPrintJob(this,"ok",null);
           	   g=p.getGraphics();
           	   g.translate(120,200);
           	   this.printAll(g);
           	   g.dispose();
           	   p.end();
	       }//打印结束  	       
	       //退出  
	       else if(e.getSource()==fileMenu_Exit)  
	       {   int exitChoose=JOptionPane.showConfirmDialog(this,"确定要退出吗?","退出提示",JOptionPane.OK_CANCEL_OPTION);  
	           if(exitChoose==JOptionPane.OK_OPTION)  
	           {   System.exit(0);  
	           }  
	           else  
	           {   return;  
	           }  	           	           	        	           	           
	       }//退出结束  
	       //剪切  
	       else if(e.getSource()==editMenu_Cut )  
	       {   editArea.requestFocus();  
	           String text=editArea.getSelectedText();  
	           StringSelection selection=new StringSelection(text);  
	           clipBoard.setContents(selection,null);  
	           editArea.replaceRange("",editArea.getSelectionStart(),editArea.getSelectionEnd());  
	           checkMenuItemEnabled();//设置剪切，复制，粘帖，删除功能的可用性  
	       }//剪切结束  
	       //复制  
	       else if(e.getSource()==editMenu_Copy )  
	       {   editArea.requestFocus();  
	           String text=editArea.getSelectedText();  
	           StringSelection selection=new StringSelection(text);  
	           clipBoard.setContents(selection,null);  
	           checkMenuItemEnabled();//设置剪切，复制，粘帖，删除功能的可用性  
	       }//复制结束  
	       //粘帖  
	       else if(e.getSource()==editMenu_Paste)  
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
	       }//粘帖结束  
	       //删除  
	       else if(e.getSource()==editMenu_Delete)  
	       {   editArea.requestFocus();  
	           editArea.replaceRange("",editArea.getSelectionStart(),editArea.getSelectionEnd());  
	           checkMenuItemEnabled(); //设置剪切、复制、粘贴、删除等功能的可用性    
	       }//删除结束  
	       //查找  
	       else if(e.getSource()==searchMenu_Search)  
	       {   editArea.requestFocus();  
	           find();  
	       }//查找结束        
	       //时间日期  
	       else if(e.getSource()==editMenu_TimeDate)  
	       {   editArea.requestFocus(); 
	           Calendar rightNow=Calendar.getInstance();  
	           Date date=rightNow.getTime();  
	           editArea.insert(date.toString(),editArea.getCaretPosition());  
	       }//时间日期结束  
	       //全选  
	       else if(e.getSource()==editMenu_SelectAll)  
	       {   editArea.selectAll();  
	       }//全选结束  	         	     	
	       //设置状态栏可见性  
	       else if(e.getSource()==viewMenu_Status)  
	       {   if(viewMenu_Status.getState())  
	               statusLabel.setVisible(true);  
	           else   
	               statusLabel.setVisible(false);  
	       }//设置状态栏可见性结束  
	       //帮助主题  
	       else if(e.getSource()==helpMenu_HelpTopics)  
	       {   editArea.requestFocus();  
	           JOptionPane.showMessageDialog(this,"这是一个文本编译器","帮助主题",JOptionPane.INFORMATION_MESSAGE);  
	       }//帮助主题结束  
	       //关于  
	       else if(e.getSource()==helpMenu_AboutNotepad)  
	       {   editArea.requestFocus();  
	           JOptionPane.showMessageDialog(this,  
	        		   "251Assignment1 Team Members:\n"+  
	        	               " Xie Jinyi        ID number:19023266 \n"+  
	        	               " Zhang Whenjing   ID number:19023266 \n"+
	        	               "这是一个文本编译器",  "记事本",JOptionPane.INFORMATION_MESSAGE);  
	       }//关于结束  
	   }//方法actionPerformed()结束  	 
	   //main函数开始  
	   public static void main(String args[])  
	   {   textEditor notepad=new textEditor();  
	       notepad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//使用 System exit 方法退出应用程序  
	   }//main函数结束  
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