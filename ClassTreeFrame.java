import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class ClassTreeFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private DefaultMutableTreeNode root;
    private DefaultTreeModel model;
    private JTree tree;
    private JTextField textField;
    private JTable table;
    private JPanel panel;

    public ClassTreeFrame() {
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        root = new DefaultMutableTreeNode("List of books");
        model = new DefaultTreeModel(root);
        tree = new JTree(model);

        makeTree();

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = tree.getSelectionPath();
                if (path == null) {
                    return;
                }
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object c = selectedNode.getUserObject();
                if (c.getClass().getName().equals(Kartoteka.class.getName())) {
                	Kartoteka n = (Kartoteka) c;
                	BookTableModel newModel = new BookTableModel(n.getParameters(), n.getParameterNames(), n.getData());
                    table.setModel(newModel);
                }
            }
        });

        int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
        tree.getSelectionModel().setSelectionMode(mode);

        table = new JTable();

        panel = new JPanel();
        panel.setLayout(new GridLayout(2,1));
        panel.add(new JScrollPane(tree));
        panel.add(new JScrollPane(table));
        add(panel, BorderLayout.CENTER);

        addTextField();
    }

    private void addTextField() {
        JPanel panel = new JPanel();

        ActionListener addListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    String text = textField.getText();
                    addBook(new Kartoteka(text));
                    textField.setText("");
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        };

        textField = new JTextField(50);
        textField.addActionListener(addListener);
        panel.add(textField);
        textField.setText("About book: Author; Name of the book; Genre; Language; Date; Date of the first publishing");

        JButton addButton = new JButton("Add");//
        addButton.addActionListener(addListener);
        panel.add(addButton);

        JButton removeButton = new JButton("Delete");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath path = tree.getSelectionPath();
                if (path == null) {
                    return;
                }
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (selectedNode.getParent() != null) {
                    model.removeNodeFromParent(selectedNode);
                }
            }
        });
        panel.add(removeButton);

        JButton changeButton = new JButton("Change");
        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath path = tree.getSelectionPath();
                if (path == null) {
                    return;
                }
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();

                JDialog input = new JDialog(ClassTreeFrame.this, true);
                input.setTitle("Enter criterion; new value");
                JTextField param = new JTextField(30);
                input.add(param, BorderLayout.NORTH);
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        input.setVisible(false);
                        Object c = selectedNode.getUserObject();
                        if (c.getClass().getName().equals(Kartoteka.class.getName())) {
                        	Kartoteka n = (Kartoteka) c;
                            boolean changed = n.changeBook(param.getText());
                            if (changed) {
                            	BookTableModel newModel = new BookTableModel(n.getParameters(), n.getParameterNames(), n.getData());
                                table.setModel(newModel);
                            }
                        }
                    }
                });
                input.add(okButton, BorderLayout.SOUTH);
                input.pack();
                input.setVisible(true);
            }
        });
        panel.add(changeButton);

        add(panel, BorderLayout.SOUTH);
    }

    @SuppressWarnings("unchecked")
    public DefaultMutableTreeNode findUserObject(Object obj) {
        Enumeration<TreeNode> e = (Enumeration<TreeNode>) root.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.getUserObject().getClass().getName().equals(obj.getClass().getName()) && node.getUserObject().equals(obj)) {
                return node;
            }
        }
        return null;
    }

    public DefaultMutableTreeNode addBook(Kartoteka n) {
        DefaultMutableTreeNode node = findUserObject(n);
        if (node != null) {
            return node;
        }

        DefaultMutableTreeNode parent = root;
        String d = n.getData();
        try {
            DefaultMutableTreeNode fdata = findUserObject(d);
            if (fdata == null) {
                model.insertNodeInto(new DefaultMutableTreeNode(d), root, root.getChildCount());
                parent = findUserObject(d);
            } else {
                parent = fdata;
            }
        } catch (Exception e) {

        }

        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(n);
        model.insertNodeInto(newNode, parent, parent.getChildCount());

        TreePath path = new TreePath(model.getPathToRoot(newNode));
        tree.makeVisible(path);

        return newNode;
    }

    public void makeTree() {
    	Kartoteka[] autors = new Kartoteka[] {
                new Kartoteka("Булгаков М.А.;Мастер и Маргарита;Роман;Русский;1928 - 1940;1966 - 1967"),
                new Kartoteka("Булгаков М.А.;Собачье сердце;Повесть;Русский;1925;1987"),
                new Kartoteka("Булгаков М.А.;Белая гвардия;Роман;Русский;Неизвестно;1925"),
                new Kartoteka("Достоевский Ф.М.;Идиот;Роман;Русский;1867 - 1869;1868"),
                new Kartoteka("Достоевский Ф.М.;Преступление и наказание;Роман;Русский;1865 - 1866;1866"),
                new Kartoteka("Достоевский Ф.М.;Бесы;Антинигилистический роман;Русский;1870 - 1872;1872"),
                new Kartoteka("Достоевский Ф.М.;Подросток;Роман воспитания;Русский;1874 - 1875;1875"),
                new Kartoteka("Фрэнсис Скотт Фицджеральд;Великий Гэтсби;Роман;Английский;1925;1925"),
                new Kartoteka("Джером Дэвид Сэлинджер;Над пропостью во ржи;Роман;Английский;16 июля 1951;16 июля 1951"),
                new Kartoteka("Габриэль Гарсиа Маркес;Сто лет одиночества;Роман;Испанский;1967;1967"),
                new Kartoteka("Габриэль Гарсиа Маркес;Поковнику никто не пишет;Реализм;Испанский;1961;1961"),
                new Kartoteka("Пушкин А.С.;Евгений Онегин;Роман в стихах;Русский;1823 - 1831;1825 - 1837"),
                new Kartoteka("Пушкин А.С.;Руслан и Людмила;Поэма;Русский;1818 - 1820;1820"),
                new Kartoteka("Пушкин А.С.;Метель;Проза;Русский;1830;1831"),
                new Kartoteka("Пушкин А.С.;Сказка о мёртвой царевне и о семи богатырях;Сказка;Русский;1833;Неизвестно"),
               
        };

        for (Kartoteka n : autors) {
        	addBook(n);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClassTreeFrame().setVisible(true);
            }
        });
    }
}

class BookTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private String[] paramNames;
    private String[] params;
    private String data;

    public BookTableModel(String[] params, String[] paramNames, String data) {
        this.params = params;
        this.paramNames = paramNames;
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return paramNames.length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return paramNames[rowIndex];
        } else {
            if (rowIndex == 0) {
                return params[rowIndex];
            }
            return params[rowIndex];
        }
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Criterions:";
        } else {
            return "About book:";
        }
    }
}