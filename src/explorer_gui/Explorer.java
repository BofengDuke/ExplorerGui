package explorer_gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.sun.org.apache.xml.internal.utils.StringVector;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.*;

import zipUtil.ZipUtil;
import zipUtil.ZipCipherUtil;

public class Explorer {

//	protected Object frame;
	private static JFrame frame;
	private static JMenuBar menuBar;
	private static JSplitPane splitPane;
	private static JScrollPane scrollTreePane;
	private static JScrollPane scrollTablePane ;
	private static JTree tree;
	private static JTable table ;
	private static Icon draggedIcon;
	private static String pathOfFileToMove;
	private static String nameOfFileToMove;
	private static String nameOfFileToCopy;
	private static String pathOfFileToCopy;
	private static JPopupMenu tablePopupMenu;
	private static JPopupMenu popupMenu;
	

	public static void main(String[] args){
		// TODO Auto-generated method stub
		
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				try{
//					Explorer ex = new Explorer();
					createExplorerGUI();
				}catch(Exception e){
					
					e.printStackTrace();
				}
			}
		});
	}
	
	private static void createExplorerGUI(){
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("资源管理器");
		frame.setBounds(100,100,714,438);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		scrollTreePane = new JScrollPane();
		scrollTreePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollTreePane.setBorder(null);
		
		tree = new JTree();
		scrollTreePane.getViewport().add(tree);
		
		table = new JTable();
		scrollTablePane = new JScrollPane();
		scrollTablePane.getViewport().add(table);
		
		splitPane.setLeftComponent(scrollTreePane);
		splitPane.setRightComponent(scrollTablePane);
		splitPane.setDividerLocation(200);
		frame.add(splitPane);
		
		JMenu mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);
		ImageIcon icon = new ImageIcon("image/addFolder.png");
		icon.setImage(icon.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
		JMenuItem menuItem = new JMenuItem("New Folder",icon);
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				createFolder();
			}

		});
		ImageIcon iconAddFile = new ImageIcon("image/addFile.png");
		iconAddFile.setImage(iconAddFile.getImage().getScaledInstance(24, 24,Image.SCALE_DEFAULT));
		JMenuItem menuItemAddFile = new JMenuItem("New File",iconAddFile);
		menuItemAddFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				try{
					createFile();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		});
		mnMenu.add(menuItem);
		mnMenu.add(menuItemAddFile);
		
		tablePopupMenu = new JPopupMenu();
		JMenuItem itemDel = new JMenuItem("Delete");
		JMenuItem itemCopy = new JMenuItem("Copy");
		JMenuItem itemRename = new JMenuItem("Rename");
		JMenuItem itemRefresh = new JMenuItem("Refresh");
		JMenuItem itemCompress = new JMenuItem("Compress");
		JMenuItem itemDecompress = new JMenuItem("Decompress");
		JMenuItem itemEncrypt = new JMenuItem("Encrypt");
		JMenuItem itemDecrypt = new JMenuItem("Decrypt");
		itemDel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				delete();
			}
		});
		itemCopy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				copyFile();
			}
		});
		itemRename.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				rename();
			}
		});
		itemRefresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		itemCompress.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				compress();
			}
		});
		itemDecompress.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				decompress();
			}
		});
		itemEncrypt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				encrypt();
			}
		});
		itemDecrypt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				decrypt();
			}
		});
		
		tablePopupMenu.add(itemCopy);
		tablePopupMenu.add(itemDel);
		tablePopupMenu.add(itemRename);
		tablePopupMenu.add(itemRefresh);
		tablePopupMenu.add(itemCompress);
		tablePopupMenu.add(itemDecompress);
		tablePopupMenu.add(itemEncrypt);
		tablePopupMenu.add(itemDecrypt);

		
		popupMenu = new JPopupMenu();
		JMenuItem itemPaste = new JMenuItem("Paste");
		itemPaste.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				pasteFile();
			}
		});
		popupMenu.add(itemPaste);
		
		scrollTablePane.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == 3){
					popupMenu.show(scrollTablePane,e.getX(),e.getY());
				}
			}
			public void mouseReleased(MouseEvent arg0) {}
		});
		
		
		frame.setVisible(true);
		initData();
		
	}

	private static void initData(){
		tree.setCellRenderer(new MyTreeCellRenderer());
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("PC");
		((DefaultTreeModel) tree.getModel()).setRoot(root);
	
		
		DragSource dragSource = DragSource.getDefaultDragSource();
		DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(table, DnDConstants.ACTION_MOVE,new DragGestureListener(){
			public void dragGestureRecognized(DragGestureEvent dge){
				Toolkit tk = Toolkit.getDefaultToolkit();
				Dimension dim = tk.getBestCursorSize(draggedIcon.getIconWidth(), draggedIcon.getIconHeight());
				BufferedImage buff = new BufferedImage(dim.width,dim.height,BufferedImage.TYPE_INT_ARGB);
				draggedIcon.paintIcon(table, buff.getGraphics(),0,0);
				if(DragSource.isDragImageSupported()){
					Transferable tr = new StringSelection(pathOfFileToMove);
					dge.startDrag(DragSource.DefaultMoveDrop,buff,new Point(0,0),tr,new DragSourceListener(){
						public void dropActionChanged(DragSourceDragEvent arg0) {}
						public void dragOver(DragSourceDragEvent arg0){}
						public void dragExit(DragSourceEvent arg0){}
						public void dragEnter(DragSourceDragEvent arg0){}
						public void dragDropEnd(DragSourceDropEvent arg0){}
					});
				}
			
			}
		});

		DropTarget dropTarget = new DropTarget(tree,DnDConstants.ACTION_MOVE,new DropTargetListener(){
			public void dropActionChanged(DropTargetDragEvent arg0){}
			public void drop(DropTargetDropEvent dte){
				TreePath path = tree.getPathForLocation(dte.getLocation().x, dte.getLocation().y);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getPathComponent(2);
				String str = getCurrentNodePath(node);
				str += nameOfFileToMove;
				
				File t = new File(str);
				File s = new File(pathOfFileToMove);
				fileChannelCopy(s,t);
				s.delete();
				showFiles(node);
			}

			public void dragEnter(DropTargetDragEvent arg0) {}
			public void dragExit(DropTargetEvent arg0) {}
			public void dragOver(DropTargetDragEvent arg0) {}
		});
		
		table.addMouseListener(new MouseListener(){
			public void mouseReleased(MouseEvent arg0){}
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1){
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if(node == null){
						return;
					}
					if(!node.isRoot()){
						node.removeAllChildren();
					}
					createSubTree(node);
					showFiles(node);
				}
			}
			public void mouseEntered(MouseEvent arg0) {				}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == 3){
					tablePopupMenu.show(table,e.getX(),e.getY());
					table.clearSelection();
					DefaultListSelectionModel model = (DefaultListSelectionModel) table.getSelectionModel();
					model.setSelectionInterval(table.rowAtPoint(new Point(e.getX(),e.getY())), table.rowAtPoint(new Point(e.getX(),e.getY())));
				}
				if(e.getButton() == 1){
					if(table.isRowSelected(table.rowAtPoint(new Point(e.getX(),e.getY())))){
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
						String str = getCurrentNodePath(node);
						nameOfFileToMove = (String) table.getModel().getValueAt(table.rowAtPoint(new Point(e.getX(),e.getY())),table.rowAtPoint(new Point(e.getX(),e.getY())));
						pathOfFileToMove = str + nameOfFileToMove;
						File file = new File(str);
						FileSystemView fsv = new JFileChooser().getFileSystemView();
						draggedIcon = fsv.getSystemIcon(file);
					}
				}
			}
		});

		
		File[] roots = File.listRoots();
		for(int i=0; i<roots.length; i++){
			root.add(new DefaultMutableTreeNode(roots[i]));	
		}
		
		
		tree.addTreeSelectionListener(new TreeSelectionListener(){
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if(node == null){
					return ;
				}
				if(node == tree.getModel().getRoot()){
					((DefaultTableModel)table.getModel()).setRowCount(0);
					table.getTableHeader().setVisible(false);
				}
				showFiles(node);
			}
		});
		tree.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1){
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if(node == null){
						return;
					}
					if(!node.isRoot()){
						node.removeAllChildren();
					}
					createSubTree(node);
				}
			}
			public void mouseEntered(MouseEvent e) {			}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
		
	}
	
	private static void encrypt(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(node == null){
			return;
		}
		String key = JOptionPane.showInputDialog("输入加密密码");
		if(key == null){
			return;
		}
		
		String path = getCurrentNodePath(node);
		String name = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
		String beforeName = path+name;
		String afterName;
		File file = new File(beforeName);
		if(file.isFile()){
			afterName  = path+getFileNameNoEx(name) + "(E).zip";
		}else{
			afterName = path + name + "(E).zip";
		}
		
		try {
			new ZipCipherUtil().encryptZip(beforeName, afterName, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		createSubTree(node);
		showFiles(node);	
	}
	
	private static void decrypt(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(node == null){
			return;
		}
		
		String key = JOptionPane.showInputDialog("输入解密密码：");
		if(key == null){
			return;
		}
		
		String path = getCurrentNodePath(node);
		String name = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
		String afterName = path + getFileNameNoEx(name);
		try{
			new ZipCipherUtil().decryptUnzip(path+name, afterName, key);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		createSubTree(node);
		showFiles(node);
	}
	
	private static void compress(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(node == null){
			return;
		}
		String path = getCurrentNodePath(node);
		String name = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
		String beforeName = path+name;
		String afterName = JOptionPane.showInputDialog("输入压缩后的名字（不输入则以原名字压缩）：");
		if(afterName == null){
			// 取消压缩文件
			return;
		}
		File file = new File(beforeName);
		if(file.isFile()){	// 判断是否是文件，是文件则取文件名，不带文件类型
			name = getFileNameNoEx(name);
			name = path+name;
		}else{
			// 文件夹
			name = beforeName;
		}
		System.out.println(name);
		if(afterName == ""){
			afterName = name + ".zip";
		}else{
			afterName = path+afterName;
		}
		System.out.println(afterName);
		File afterFile = new File(afterName);
		if(afterFile.exists()){
			System.out.println("111");
			int overlap = JOptionPane.showConfirmDialog(null,"是否要覆盖同名文件？","是否覆盖文件",JOptionPane.YES_NO_OPTION);
			// overlap = 0/1  0：覆盖
			if(overlap == 0){
				afterFile.delete();
			}else{
				return;
			}
		}
		
			
		ZipUtil zipUtil = new ZipUtil();
		zipUtil.zip(beforeName, afterName);
		
		createSubTree(node);
		showFiles(node);
		
	}

	private static void decompress(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(node == null){
			return;
		}
		String path = getCurrentNodePath(node);
		String oldName = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
		String ex = getExtensionName(oldName);
		if(!ex.equals("zip")){
			JOptionPane.showMessageDialog(null, "不是ZIP压缩文件", "解压缩错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String afterName = getFileNameNoEx(oldName);
		
		ZipUtil zipUtil = new ZipUtil();
		zipUtil.unzip(path+oldName, path+afterName);
		createSubTree(node);
		showFiles(node);
	}
	
	private static void refresh(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(node == null){
			return ;
		}
		createSubTree(node);
		showFiles(node);
		
	}
	
	private static void rename(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(node == null){
			return;
		}
		String oldPath = getCurrentNodePath(node);
		String oldName = table.getModel().getValueAt(table.getSelectedRow(),0).toString();
		oldName = oldPath+oldName;
		
		String newName = JOptionPane.showInputDialog("Input new name: ");
		if(newName == null){
			// 取消重命名
//			JOptionPane.showMessageDialog(null, "文件名不能为空","重命名错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
		newName = oldPath+newName;
		
		File oldFile = new File(oldName);
		File newFile = new File(newName);
		
		if(newFile.exists()){
			int overlap = JOptionPane.showConfirmDialog(null,"是否要覆盖同名文件？","是否覆盖文件",JOptionPane.YES_NO_OPTION);
			// overlap = 0/1  0：覆盖
			if(overlap == 0){
				if(oldFile.isFile()){
					newFile.delete();
				}else{
					deleteFolder(oldFile);
				}
				
				oldFile.renameTo(newFile);
			}else{
				return;
			}
		}
		oldFile.renameTo(newFile);
		
		createSubTree(node);
		showFiles(node);
		
	}
	
	private static void copyFile(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(node == null){
			return;
		}
		String str = getCurrentNodePath(node);
		nameOfFileToCopy = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
		pathOfFileToCopy = str+nameOfFileToCopy;
	}
	
	private static void pasteFile(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(node == null){
			return;
		}
		String str = getCurrentNodePath(node);
		str += nameOfFileToCopy;
		File t = new File(str);
		File s = new File(pathOfFileToCopy);
		System.out.println("src: "+str+" target: "+pathOfFileToCopy);
		fileChannelCopy(s,t);
		showFiles(node);
	}
	
	private static void createFile() throws IOException{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(node == null){
			return;
		}
		String str = getCurrentNodePath(node);
		str += "New file.txt";
		File file = new File(str);
		file.createNewFile();
		showFiles(node);
	}
	
	private static void createFolder(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(node == null){
			return;
		}
		String str = getCurrentNodePath(node);
		str += "New Folder";
		File file = new File(str);
		file.mkdir();
		showFiles(node);
		node.removeAllChildren();
		createSubTree(node);
	}
	
	private static void delete(){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(node == null){
			return;
		}
		String str = getCurrentNodePath(node);
		str += table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
		File file = new File(str);
		if(file.isDirectory()){
			deleteFolder(file);
			node.removeAllChildren();
			createSubTree(node);
		}
		else{
			file.delete();
		}
		showFiles(node);
	}
	
	private static void deleteFolder(File file){
		if(!file.exists() || !file.isDirectory()){
			return;
		}
		File[] files = file.listFiles();
		if(files == null){
			file.delete();
			return ;
		}
		for(int i=0;i<files.length;i++){
			if(files[i].isFile()){
				files[i].delete();
			}
			else{
				deleteFolder(files[i]);
			}
		}
		file.delete();
	}
	
	private static void createSubTree(DefaultMutableTreeNode node){
		String str = getCurrentNodePath(node);
		File file = new File(str);
		File[] roots = file.listFiles();
		if(roots == null){
			return;
		}
		for(int i=0; i<roots.length; i++){
			if(!roots[i].isDirectory()){
//				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode("- "+roots[i].getName());
//				node.add(subNode);
				continue;
			}
			DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(roots[i].getName());
			node.add(subNode);
		}
		
		tree.repaint();
		tree.updateUI();
		scrollTreePane.updateUI();
	}
	
	private static void showFiles(DefaultMutableTreeNode node){
		String str = getCurrentNodePath(node);
		File file = new File(str);
		File[] roots = file.listFiles();
		if(roots == null){
			return ;
		}
		Object[] headers = {"Name","ModifiedTime","Type","Size"};
		DefaultTableModel model = new DefaultTableModel(null,headers){
			private static final long serialVersionUID = 1L;
			
			public boolean isCellEditable(int row,int column){
				return false;
			}
		};
		String[] strArray = new String[4];
		for(int i=0;i<roots.length;i++){	// 遍历文件
			strArray[0] = roots[i].getName();
			Date modifiedTime = new Date(roots[i].lastModified());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:MM");
			strArray[1] = sdf.format(modifiedTime);
			JFileChooser chooser = new JFileChooser();
			strArray[2] = chooser.getTypeDescription(roots[i]);
			strArray[3] = String.valueOf(roots[i].length()/1024) + " M";
			model.addRow(strArray);
		}
		table.setModel(model);
		table.getTableHeader().setVisible(true);
		table.getColumnModel().getColumn(0).setCellRenderer(new MyTableCellRenderer()); // 设置图标
	}
	
	private static void fileChannelCopy(File s,File t){
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try{
			fi = new FileInputStream(s);
			fo = new FileOutputStream(t);
			in = fi.getChannel();
			out = fo.getChannel();
			in.transferTo(0, in.size(), out);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				fi.close();
				in.close();
				fo.close();
				out.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	private static String getExtensionName(String filename){
		if((filename != null) && (filename.length() > 0)){
			int dot = filename.lastIndexOf('.');
			if((dot > -1) && (dot < (filename.length() - 1))){
				return filename.substring(dot+1);
			}else{
				return "";
			}
		}
		return filename;
	}
	
	private static String getFileNameNoEx(String filename){
		if((filename != null) && (filename.length() > 0)){
			int dot = filename.lastIndexOf('.');
			if((dot > -1) && (dot < (filename.length()-1))){
				return filename.substring(0,dot);
			}
		}
		return filename;
	}
	
	private static String getCurrentNodePath(DefaultMutableTreeNode node){
		StringVector stringVector = new StringVector();
		DefaultMutableTreeNode tempNode = node;
		while(tempNode != null){
			String str = tempNode.toString();
			stringVector.push(str);
			tempNode = (DefaultMutableTreeNode) tempNode.getParent();
		}
		String str = "";
		for(int i=stringVector.getLength() - 2;i >= 0;i--){
			str += stringVector.elementAt(i);
			if( i != stringVector.getLength() - 2){
				str += "//";
			}
		}
		return str;
	}
	
//	 重载图标，使用系统图标
	private static class MyTableCellRenderer extends DefaultTableCellRenderer{
		
		private static final long serialVersionUID = 1L;
		
		public java.awt.Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
			if(column == 0){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				String str = getCurrentNodePath(node);
				str += value;
				File file = new File(str);
				FileSystemView fsv = new JFileChooser().getFileSystemView();
				Icon icon = fsv.getSystemIcon(file);
				setIcon(icon);
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
			
		}
		
		
	}
	
}

// 重载左边树目录的图标
class MyTreeCellRenderer extends DefaultTreeCellRenderer{
	private static final long serialVersionUID = 1;
	
	public java.awt.Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus){
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,hasFocus);
//		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if(value.toString().equals("PC")){
			ImageIcon icon = new ImageIcon("image/computer.png");
			icon.setImage(icon.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
			setIcon(icon);
		}else if(leaf){
			ImageIcon icon = new ImageIcon("image/emptyFolder.png");
			icon.setImage(icon.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
			setIcon(icon);
		}else if(expanded){
			ImageIcon icon = new ImageIcon("image/openedFolder.png");
			icon.setImage(icon.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
			setIcon(icon);
		}else{
			ImageIcon icon = new ImageIcon("image/closedFolder.png");
			icon.setImage(icon.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
			setIcon(icon);
		}
		return this;
	}
}



