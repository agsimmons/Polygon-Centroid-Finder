package com.github.agsimmons.polygoncentroidfinder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PolygonCentroidFinder extends Application {
    
    static final DecimalFormat DF = new DecimalFormat("0.00");

    static final int CANVAS_SIZE_X = 500;
    static final int CANVAS_SIZE_Y = 500;
    static final double VERTEX_SCALAR = 20;
    static final double VERTEX_SIZE = 10;
    static final double LINE_WIDTH = 3;
    static final Color COLOR_VERTEX = Color.BLACK;
    static final Color COLOR_EDGE = Color.BLUE;
    static final Color COLOR_CENTROID = Color.GREEN;

    static ArrayList<Vertex> vertices;
    static ArrayList<Edge> edges;

    public static void main(String[] args) {
        vertices = getVertices();
        edges = getEdges(vertices);
        
        launch(args);
    }

    static ArrayList<Vertex> getVertices() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Vertex> returnVertices = new ArrayList<>();

        System.out.println("Input vertices of a non-self-intersecting polygon in order of connectedness (Make sure to input at least 3 vertices)");
        System.out.println("Input Format: 4~10 8~7 11~2 2~2");

        System.out.print("Input: ");
        String input = scanner.nextLine();

        String[] vertexStrings = input.split(" ");
        // debug("vertexString: " + Arrays.toString(vertexStrings));

        for (String vertexString : vertexStrings) {
            String[] xyStrings = vertexString.split("~");
            // debug("xyStrings: " + Arrays.toString(xyStrings));

            Vertex vertex = new Vertex(Double.parseDouble(xyStrings[0]) * VERTEX_SCALAR,
                                       Double.parseDouble(xyStrings[1]) * VERTEX_SCALAR);

            returnVertices.add(vertex);
        }

        debug("Vertices: " + returnVertices.toString());

        return returnVertices;
    }

    static ArrayList<Edge> getEdges(ArrayList<Vertex> inputVertices) {
        ArrayList<Edge> returnEdges = new ArrayList<>();

        // Add all but last edge to returnEdges
        for (int i = 0; i < inputVertices.size() - 1; i++) {
            Edge edge = new Edge(inputVertices.get(i), inputVertices.get(i + 1));

            returnEdges.add(edge);
        }

        // Add last edge to returnEdges
        returnEdges.add(new Edge(inputVertices.get(inputVertices.size() - 1), inputVertices.get(0)));

        debug("Edges: " + returnEdges.toString());

        return returnEdges;
    }

    @Override
    public void start(Stage stage) {
        // Initialize Stage

        Group root = new Group();
        Canvas canvas = new Canvas(CANVAS_SIZE_X, CANVAS_SIZE_Y);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawEdges(edges, gc);
        drawVertices(vertices, gc);
        
        double signedArea = computeSignedArea(vertices);
        drawCentroid(vertices, signedArea, gc);

        // After drawing
        stage.setTitle("Polygon Centroid Finder - Area: "
                       + DF.format(Math.abs(signedArea)));
        root.getChildren().add(canvas);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void drawEdges(ArrayList<Edge> e, GraphicsContext gc) {
        gc.setStroke(COLOR_EDGE);
        gc.setLineWidth(LINE_WIDTH);

        for (Edge edge : e) {
            gc.strokeLine(edge.v1.x, edge.v1.y, edge.v2.x, edge.v2.y);
        }
    }

    private void drawVertices(ArrayList<Vertex> v, GraphicsContext gc) {
        gc.setFill(COLOR_VERTEX);

        for (Vertex vertex : v) {
            gc.fillOval(vertex.x - (VERTEX_SIZE / 2), vertex.y - (VERTEX_SIZE / 2), VERTEX_SIZE, VERTEX_SIZE);
        }
    }

    private void drawCentroid(ArrayList<Vertex> v, double area, GraphicsContext gc) {
        int x = 0;
        int y = 0;

        // TODO: Currently returns incorrect value
        for (int i = 0; i < v.size() - 1; i++) {
            x += ((v.get(i).x / VERTEX_SCALAR) + (v.get(i + 1).x / VERTEX_SCALAR)) * ((v.get(i).x / VERTEX_SCALAR) * (v.get(i + 1).y / VERTEX_SCALAR) - (v.get(i + 1).x / VERTEX_SCALAR) * (v.get(i).y / VERTEX_SCALAR));
            y += ((v.get(i).y / VERTEX_SCALAR) + (v.get(i + 1).y / VERTEX_SCALAR)) * ((v.get(i).x / VERTEX_SCALAR) * (v.get(i + 1).y / VERTEX_SCALAR) - (v.get(i + 1).x / VERTEX_SCALAR) * (v.get(i).y / VERTEX_SCALAR));
        }
        
        // Edge back to starting vertex
        x += ((v.get(v.size() - 1).x / VERTEX_SCALAR) + (v.get(0).x / VERTEX_SCALAR)) * ((v.get(v.size() - 1).x / VERTEX_SCALAR) * (v.get(0).y / VERTEX_SCALAR) - (v.get(0).x / VERTEX_SCALAR) * (v.get(v.size() - 1).y / VERTEX_SCALAR));
        y += ((v.get(v.size() - 1).y / VERTEX_SCALAR) + (v.get(0).y / VERTEX_SCALAR)) * ((v.get(v.size() - 1).x / VERTEX_SCALAR) * (v.get(0).y / VERTEX_SCALAR) - (v.get(0).x / VERTEX_SCALAR) * (v.get(v.size() - 1).y / VERTEX_SCALAR));
        
        x *= 1 / (6 * area);
        y *= 1 / (6 * area);

        gc.setFill(COLOR_CENTROID);
        gc.fillOval(x - (VERTEX_SIZE / 2), y - (VERTEX_SIZE / 2), VERTEX_SIZE, VERTEX_SIZE);
        
        debug(new StringBuilder("Centroid: ").append('{')
                                             .append(x / VERTEX_SCALAR)
                                             .append(',')
                                             .append(y / VERTEX_SCALAR)
                                             .append('}')
                                             .toString());
    }

    private double computeSignedArea(ArrayList<Vertex> v) {
        double area = 0;
        
        for (int i = 0; i < v.size() - 1; i++) {
            area += (((v.get(i).x / VERTEX_SCALAR) * (v.get(i + 1).y / VERTEX_SCALAR)) - ((v.get(i).y / VERTEX_SCALAR) * (v.get(i + 1).x / VERTEX_SCALAR)));
        }
        
        area += (((v.get(v.size() - 1).x / VERTEX_SCALAR) * (v.get(0).y / VERTEX_SCALAR)) - ((v.get(v.size() - 1).y / VERTEX_SCALAR) * (v.get(0).x / VERTEX_SCALAR)));
        
        area = area / 2;
        
        return area;
    }

    private static void debug(String debugMessage) {
        System.err.println("DEBUG: " + debugMessage);
    }

}
