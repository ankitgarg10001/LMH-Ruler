package com.lmh.ruler;

/**
 * Created by Ankit Garg on 25-06-2016.
 */
public class RemovedPiece {
    private Piece piece;
    private int pos;

    public RemovedPiece(Integer removedPos, Piece remove) {
        this.piece = remove;
        this.pos = removedPos;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
