package fr.univtours.models;

import lombok.*;

@AllArgsConstructor
@Getter @Setter @ToString
public abstract class Node {

    private double x;
    private double y;
    private double score;
    

}
