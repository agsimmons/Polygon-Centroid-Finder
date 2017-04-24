package com.github.agsimmons.polygoncentroidfinder;

public class Edge {
    Vertex v1;
    Vertex v2;
    
    Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append('{')
                                  .append(v1)
                                  .append(',')
                                  .append(v2)
                                  .append('}')
                                  .toString();
    }
}
