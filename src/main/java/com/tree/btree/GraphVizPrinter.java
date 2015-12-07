package com.tree.btree;

import com.tree.Data;
import org.kohsuke.graphviz.Graph;
import org.kohsuke.graphviz.Node;
import org.kohsuke.graphviz.Style;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by vinay.madhusudhan on 01/12/15.
 */
public class GraphVizPrinter {


    private static int i= 0;

    private static Node createNodeFor(BTree.Node node) {
        Node gNode = new Node();

        Style style = new Style();
        style.attr("shape", "record");
        gNode.style(style);
        gNode.id("Node" + node.getId());
        gNode.attr("label", node.getName());
        return gNode;
    }

    private static void writeGraphToFile(Graph graph) {
        try (OutputStream out = new FileOutputStream("dotfile", true)) {
            graph.writeTo(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printTree(BTree.Node root, Data data) {
        Graph graph = new Graph();
        graph.id("Graph" + i++);
        graph.attr("label", "Inserting value: " + data);
        graph.attr("labelloc","t");
        final BTree.Node temp = root;

        graph.node(createNodeFor(temp));
        if (temp.isLeaf()) {
            writeGraphToFile(graph);
            return;
        }

        Queue<BTree.Node> nodeQueue = new ArrayDeque<>();
        nodeQueue.add(temp);

        while (!nodeQueue.isEmpty()) {

            final BTree.Node curr = nodeQueue.remove();
            Node gSrcNode = createNodeFor(curr);
            graph.node(gSrcNode);

            curr.getPointers().stream().forEach((node) -> {
                Node gDstNode = createNodeFor(node);
                graph.node(gDstNode);
                graph.edge(gSrcNode, gDstNode);
                if (!node.isLeaf()) {
                    nodeQueue.add(node);
                }
            });
        }
        writeGraphToFile(graph);
    }

}
