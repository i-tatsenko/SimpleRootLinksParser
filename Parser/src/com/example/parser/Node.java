package com.example.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Node {

    private Node parent;
    private String root;
    private Map<String, Node> children = new HashMap<>();

    public Node(Node parent, String root) {
        this.parent = parent;
        this.root = root;
        if (parent != null) {
            Node out = parent.children.put(root, this);
            if (out != null) {
                System.err.println("Some problems in logic");
            }
        }
    }

    public static Node linksToTree(Set<String> links, String rootName) {
        Node root = new Node(null, rootName);
        links.stream().forEach(link -> {
            String[] path = link.split("/");
            Node currentNode = root;
            for (int i = 0; i < path.length; i++) {
                if (currentNode.children.containsKey(path[i])) {
                    currentNode = currentNode.children.get(path[i]);
                } else {
                    createPath(Arrays.copyOfRange(path, i, path.length), currentNode);
                    break;
                }
            }
        });
        return root;
    }

    private static void createPath(String[] path, Node parent) {
        Node current = parent;
        for (String chunk : path) {
            current = new Node(current, chunk);
        }
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public Map<String, Node> getChildren() {
        return children;
    }

    public void setChildren(Map<String, Node> children) {
        this.children = children;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (parent != null ? !parent.equals(node.parent) : node.parent != null) return false;
        return !(root != null ? !root.equals(node.root) : node.root != null);

    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + (root != null ? root.hashCode() : 0);
        return result;
    }
}
