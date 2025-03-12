package fr.univtours.models;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString
public class Site {

    private SiteType type;
    private double x, y;
    private double score;

}
