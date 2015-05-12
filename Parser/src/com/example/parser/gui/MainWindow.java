package com.example.parser.gui;

import com.example.parser.Node;
import com.example.parser.grabber.TaskExecutor;
import com.example.parser.util.SimpleMouseListener;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

public class MainWindow {

    private final JTextField urlTextField = new JTextField("http://volia.com");
    private final JButton goButton = new JButton("Go!");
    private final JFrame frame = getFrame();
    private final TaskExecutor taskExecutor = new TaskExecutor();
    private final JProgressBar progressBar = new JProgressBar();
    private JTree tree;

    public MainWindow() {
        startGui();
        goButton.addMouseListener((SimpleMouseListener) event -> {
            String url = urlTextField.getText();
            try {
                URI uri = new URI(url);
                showProgressbar();
                taskExecutor.newTask(uri, (result) -> {
                    hideProgressBar();
                    showAlert(String.format("Found %d links for %d ms", result.getLinks().size(), result.getTimeSpendInMS()));
                    Node node = Node.linksToTree(result.getLinks(), url);
                    updateTreeModel(node);
                });
            } catch (URISyntaxException e) {
                showAlert(url + " is not a valid URL. Reason: " + e.getMessage());
            }
        });
    }

    private void updateTreeModel(Node node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node.getRoot());
        fillChildren(treeNode, node.getChildren().values());
        DefaultTreeModel newModel = new DefaultTreeModel(treeNode);
        tree.setModel(newModel);
        newModel.reload();

    }

    private void fillChildren(DefaultMutableTreeNode treeNode, Collection<Node> children) {
        for (Node child : children) {
            DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(child.getRoot());
            fillChildren(childTreeNode, child.getChildren().values());
            treeNode.add(childTreeNode);
        }
    }


    public void startGui() {
        frame.setLayout(new GridBagLayout());

        GridBagConstraints labelConstr = getSimpleConstraints(0, 0, 1, 1);
        labelConstr.weightx = 0.1;
        frame.add(new Label("Url:"), labelConstr);
        frame.add(urlTextField, getSimpleConstraints(1, 0, 7, 1));
        frame.add(goButton, getSimpleConstraints(8, 0, 1, 1));

        tree = new JTree(getDefaultModel());
        JScrollPane treeView = new JScrollPane(tree);
        GridBagConstraints treeViewConstr = getSimpleConstraints(0, 1, 9, 3);
        treeViewConstr.weighty = 1;
        treeViewConstr.weightx = 1;
        frame.add(treeView, treeViewConstr);

        frame.add(progressBar, getSimpleConstraints(0, 5, 9, 1));

        frame.setVisible(true);
    }

    private DefaultMutableTreeNode getDefaultModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Site");
        root.add(new DefaultMutableTreeNode("Children"));
        return root;
    }

    private void showProgressbar() {
        progressBar.setIndeterminate(true);

    }

    private void hideProgressBar() {
        progressBar.setIndeterminate(false);
    }

    private GridBagConstraints getSimpleConstraints(int gridX, int gridY, int gridWidth, int gridHeight) {
        GridBagConstraints constr = new GridBagConstraints();
        constr.fill = GridBagConstraints.HORIZONTAL;
        constr.insets = new Insets(5, 5, 5, 5);
        constr.gridx = gridX;
        constr.gridy = gridY;
        constr.gridwidth = gridWidth;
        constr.gridheight = gridHeight;
        constr.weightx = gridWidth;
        constr.weighty = gridHeight;
        return constr;
    }

    private JFrame getFrame() {
        JFrame frame = new JFrame("Parser");
        frame.setResizable(false);
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame;
    }

    private void showAlert(String text) {
        JOptionPane.showMessageDialog(frame, text);
    }

}