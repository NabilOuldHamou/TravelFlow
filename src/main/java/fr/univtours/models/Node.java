package fr.univtours.models;

import lombok.*;

@AllArgsConstructor
@Getter @Setter @ToString
public abstract class Node {

    private int id;
    private double x;
    private double y;
    private double score;
    private Node Next;
    private Node Prev;


}
