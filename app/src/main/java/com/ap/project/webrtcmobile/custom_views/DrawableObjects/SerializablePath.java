package com.ap.project.webrtcmobile.custom_views.DrawableObjects;


import java.util.ArrayList;


public class SerializablePath {


    private ArrayList<Position> moveTos;
    private ArrayList<QuadPosition> quadTos;
    private ArrayList<Position> lineTos;

    public SerializablePath(ArrayList<Position> moveTos, ArrayList<QuadPosition> quadTos, ArrayList<Position> lineTos) {
        this.moveTos = moveTos;
        this.quadTos = quadTos;
        this.lineTos = lineTos;
    }

    public SerializablePath(){
        moveTos = new ArrayList<>();
        quadTos = new ArrayList<>();
        lineTos = new ArrayList<>();
    }

    public void lineTo(float x, float y) {
        lineTos.add(new Position(x, y));
    }

    public void quadTo(float x1, float y1, float x2, float y2) {
        quadTos.add(new QuadPosition(x1, y1, x2, y2));
    }

    public void moveTo(float x, float y) {
        moveTos.add(new Position(x, y));

    }

    public CPath toCPath() {
        return new CPath(moveTos,quadTos,lineTos);
    }


    static class Position {
        private final float x;
        private final float y;

        public Position(float x, float y) {

            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }

    static class QuadPosition {

        private final float x1;
        private final float y1;
        private final float x2;
        private final float y2;

        public QuadPosition(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public float getX1() {
            return x1;
        }

        public float getY1() {
            return y1;
        }

        public float getX2() {
            return x2;
        }

        public float getY2() {
            return y2;
        }
    }
}