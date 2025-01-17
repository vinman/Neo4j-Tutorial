package org.neo4j.tutorial;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class DatabaseHelper {

    private final GraphDatabaseService db;

    public DatabaseHelper(GraphDatabaseService db) {
        this.db = db;

    }

    public static EmbeddedGraphDatabase createDatabase() {
        return new EmbeddedGraphDatabase(createTempDatabaseDir().getAbsolutePath());
    }
    
    public static EmbeddedGraphDatabase createDatabase(String dbDir) {
		return new EmbeddedGraphDatabase(dbDir);
    }

    public static File createTempDatabaseDir() {

        File d;
        try {
            d = File.createTempFile("neo4j-koans", "dir");
            System.out.println(String.format("Created a new Neo4j database at [%s]", d.getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!d.delete()) {
            throw new RuntimeException("temp config directory pre-delete failed");
        }
        if (!d.mkdirs()) {
            throw new RuntimeException("temp config directory not created");
        }
        d.deleteOnExit();
        return d;
    }

    public static void ensureRelationshipInDb(Node startNode, RelationshipType relType, Node endNode) {
        for (Relationship r : startNode.getRelationships(relType, Direction.OUTGOING)) {
            if(r.getEndNode().equals(endNode)) {
                return;
            }
        }
        
        startNode.createRelationshipTo(endNode, relType);
    }
    
    public void dumpGraphToConsole() {
        for (Node n : db.getAllNodes()) {
            Iterable<String> propertyKeys = n.getPropertyKeys();
            for (String key : propertyKeys) {
                System.out.print(key + " : ");
                System.out.println(n.getProperty(key));
            }
        }
    }

    public int countNodesWithAllGivenProperties(Iterable<Node> allNodes, String... propertyNames) {
        Iterator<Node> iterator = allNodes.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Node next = iterator.next();

            boolean hasAllPropertyNames = true;
            for (String propertyName : propertyNames) {
                hasAllPropertyNames = hasAllPropertyNames && next.hasProperty(propertyName);
                if (!hasAllPropertyNames)
                    break; // Modest optimisation
            }
            if (hasAllPropertyNames) {
                count++;
            }
        }
        return count;
    }

    public int countNodes(Iterable<Node> allNodes) {
        return destructivelyCount(allNodes);
    }

    public boolean nodeExistsInDatabase(Node node) {
        return db.getNodeById(node.getId()) != null;
    }

    public int countRelationships(Iterable<Relationship> relationships) {
        return destructivelyCount(relationships);
    }

    public void dumpNode(Node node) {
        if(node == null) {
            System.out.println("Null Node");
            return;
        }
        System.out.println(String.format("Node ID [%d]", node.getId()));
        for (String key : node.getPropertyKeys()) {
            System.out.print(key + " : ");
            System.out.println(node.getProperty(key));
        }
    }

    public List<Relationship> toListOfRelationships(Iterable<Relationship> iterable) {
        ArrayList<Relationship> rels = new ArrayList<Relationship>();
        for (Relationship r : iterable) {
            rels.add(r);
        }
        return rels;
    }

    public List<Node> toListOfNodes(Iterable<Node> nodes) {
        ArrayList<Node> rels = new ArrayList<Node>();
        for (Node n : nodes) {
            rels.add(n);
        }
        return rels;
    }

    public int count(IndexHits<Node> indexHits) {
        return destructivelyCount(indexHits);
    }

    private int destructivelyCount(Iterable<?> iterable) {
        int count = 0;

        for (@SuppressWarnings("unused")
        Object o : iterable) {
            count++;
        }

        return count;
    }
}
