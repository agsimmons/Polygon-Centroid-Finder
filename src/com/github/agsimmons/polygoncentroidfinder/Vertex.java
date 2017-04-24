package com.github.agsimmons.polygoncentroidfinder;

class Vertex {
    double x;
    double y;
    
    Vertex(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append('{')
                                  .append(x)
                                  .append(',')
                                  .append(y)
                                  .append('}')
                                  .toString();
    }
}
