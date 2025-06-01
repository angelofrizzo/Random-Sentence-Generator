package com.randomsentence;

import com.google.cloud.language.v1.AnalyzeSyntaxResponse;

import java.util.ArrayList;
import java.util.List;

class TreeNode {
    com.google.cloud.language.v1.Token name;
    int index;
    List<TreeNode> children;

    public TreeNode(com.google.cloud.language.v1.Token name, int index) {
        this.name = name;
        this.index=index;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }
}

class GenerateTree{
    List<TreeNode> listNode;
    TreeNode treeRoot;
    public GenerateTree(AnalyzeSyntaxResponse lista){

        List<com.google.cloud.language.v1.Token> parole=lista.getTokensList();
        listNode=new ArrayList<TreeNode>();
        int index=0;
        for(com.google.cloud.language.v1.Token parola:parole){
            listNode.add(new TreeNode(parola, index));
            index++;
        }
        for(TreeNode node:listNode){
            if(node.index!=node.name.getDependencyEdge().getHeadTokenIndex()){
                listNode.get(node.name.getDependencyEdge().getHeadTokenIndex()).addChild(node);
            }else{
                treeRoot=node;
            }
        }
    }

    public static String getTreeAsString(TreeNode node, String prefix, boolean isLast, List<Boolean> ancestors) {
        StringBuilder treeString = new StringBuilder();
        treeString.append(prefix);

        // Costruzione delle barre verticali per mantenere la gerarchia
        for (boolean ancestor : ancestors) {
            treeString.append(ancestor ? "|   " : "    ");
        }

        treeString.append(isLast ? "└── " : "├── ").append(node.name.getText().getContent()).append("\n");

        // Costruzione dei nuovi prefissi per gli elementi figli
        for (int i = 0; i < node.children.size(); i++) {
            List<Boolean> newAncestors = new ArrayList<>(ancestors);
            newAncestors.add(!isLast); // Mantieni la barra verticale solo quando necessario
            treeString.append(getTreeAsString(node.children.get(i), "", i == node.children.size() - 1, newAncestors));
        }

        return treeString.toString();
    }
}

public class SyntaxTree {
    public static String syntaxTreeGenerator(AnalyzeSyntaxResponse sentenceAnalyzed) {
        GenerateTree tree = new GenerateTree(sentenceAnalyzed);
        String syntaxTree = GenerateTree.getTreeAsString(tree.treeRoot,"",true, new ArrayList<>());
        return syntaxTree;
    }
}
