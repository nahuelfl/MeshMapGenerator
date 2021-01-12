package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs;
import ca.uqam.ace.inf5153.mesh.io.Structs.*;

import java.util.List;

public class GestionAltitude
{
    private int maxAltitude; //altitude maximale en entier
    private Shape shape; //Shape du terrain associé
    private Tuile[] tuiles; //Liste de tuiles
    private Point centre; //Point centre du Terrain
    private List<Point> points; //Liste de points

    // Constructeur
    public GestionAltitude(String altitude, Shape shape, Tuile[] tuiles, Point centre, List<Point> points)
    {
        int maxAltitude = Integer.parseInt(altitude);

        this.maxAltitude = maxAltitude;
        this.shape = shape;
        this.tuiles = tuiles;
        this.centre = centre;
        this.points = points;
    }

    /**
     *  Fonction chargee de faire les bons appels pour bien attribuer les différentes altitudes aux Tuiles[]
     * @return
     */
    public Tuile[] setAltitude()
    {
        if(this.shape instanceof Atoll)
        {
            // maxAltitude circonference cercle entre deux frontieres
            altitudeMaximaleAtoll();
        }
        else if(this.shape instanceof Tortuga)
        {
            // maxAltitude segment entre deux foyers
            altitudeMaximaleTortuga();
        }

        // Propagation altitude
        propragationAltitude(this.maxAltitude);

        // Lacs et Ocean doivent êtres plats
        for(int i = 0; i < this.tuiles.length; i++) // Parcourt toutes les tuiles
        {
            if(this.tuiles[i].getBiome() instanceof LagonBiome || this.tuiles[i].getBiome() instanceof OceanBiome)
                this.tuiles[i].setAltitude(0);
        }

        return this.tuiles;
    }

    /**
     * Définit les tuiles ayant l'altitude maximale pour la shape Atoll
     * soit celles sur la circonference d'un cercle entre les deux cercles de r1 et r2
     */
    public void altitudeMaximaleAtoll()
    {
        // maxAltitude circonference cercle entre deux frontieres
        double rayon = ( ((Atoll) this.shape).getR1() + ((Atoll) this.shape).getR2() )/2;

        for(int i = 0; i < this.tuiles.length; i++) // Parcourt toutes les tuiles
        {
            // Point centroid du Polygone(i)
            Structs.Point point = this.points.get(tuiles[i].polygon.getCentroidIdx());

            Double distance = Math.sqrt(
                    ((point.getX() - this.centre.getX()) * (point.getX() - this.centre.getX())) +
                            ((point.getY() - this.centre.getY()) * (point.getY() - this.centre.getY())));

            // Si la tuile est sur la circonference voulue avec une approximation de 27
            if (distance <= rayon + 27 && distance >= rayon - 27)
            {
                tuiles[i].setAltitude(maxAltitude);
            }
        }
    }

    /**
     * Définit les tuiles ayant l'altitude maximale pour la shape Tortuga
     * soit celles dans le segment entre les deux foyers
     */
    public void altitudeMaximaleTortuga()
    {
        // maxAltitude segment entre deux foyers
        double rX = ((Tortuga) this.shape).getRX();
        double rY = ((Tortuga) this.shape).getRY();
        double angle = ((Tortuga) this.shape).getAngle();

        // Coordonnees foyers
        double c = Math.sqrt( (rX * rX) - (rY * rY) );
        double foyer1X = this.centre.getX() + c;
        double foyer2X = this.centre.getX() - c;
        double foyerY = this.centre.getY();

        // Rotation des coordonees foyer
        double foyer1XRot = ((foyer1X - this.centre.getX()) * Math.cos(angle)) - ((foyerY - this.centre.getY()) * Math.sin(angle)) + centre.getX();
        double foyer2XRot = ((foyer2X - this.centre.getX()) * Math.cos(angle)) - ((foyerY - this.centre.getY()) * Math.sin(angle)) + centre.getX();

        double foyer1Y = ((foyer1X - this.centre.getX()) * Math.sin(angle)) + ((foyerY - this.centre.getY()) * Math.cos(angle)) + centre.getY();
        double foyer2Y = ((foyer2X - this.centre.getX()) * Math.sin(angle)) + ((foyerY - this.centre.getY()) * Math.cos(angle)) + centre.getY();


        for(int i = 0; i < this.tuiles.length; i++) // Parcourt toutes les tuiles
        {
            Structs.Point pt = this.points.get(this.tuiles[i].polygon.getCentroidIdx()); // Point centroid du Polygone(i)

            Double distanceF1EtPt = Math.sqrt(((foyer1XRot - pt.getX()) * (foyer1XRot - pt.getX())) +
                    ((foyer1Y - pt.getY()) * (foyer1Y - pt.getY())));

            Double distancePtEtF2 = Math.sqrt((((pt.getX() - foyer2XRot) * (pt.getX() - foyer2XRot)) +
                    ((pt.getY() - foyer2Y) * (pt.getY() - foyer2Y))));

            Double distanceF1EtF2 = Math.sqrt((((foyer1XRot - foyer2XRot) * (foyer1XRot - foyer2XRot)) +
                    ((foyer1Y - foyer2Y) * (foyer1Y - foyer2Y))));

            // Si tuile est dans le segment entre les deux foyers -> altitude maximale lui est associée
            if( (distanceF1EtPt + distancePtEtF2 <= distanceF1EtF2 + 5) && (distanceF1EtPt + distancePtEtF2 >= distanceF1EtF2 - 5))
            {
                this.tuiles[i].setAltitude(maxAltitude);
            }
        }
    }

    /**
     * Fonction de propragation linéaire des altitudes pour chaque tuile
     *
     * @param maxAltitude
     */
    public void propragationAltitude(int maxAltitude)
    {
        int currentAltitude = maxAltitude;

        do
        {
            for(int i = 0; i < this.tuiles.length; i++) // Parcourt toutes les tuiles
            {
                if(this.tuiles[i].getAltitude() == currentAltitude)
                {
                    for (int j = 0; j < this.tuiles[i].polygon.getNeighborsCount(); j++) // Parcourt les voisins de la Tuile j
                    {
                        int centroidVoisin = this.tuiles[i].polygon.getNeighbors(j);

                        Tuile tuileVoisine = null;

                        for (int k = 0; k < this.tuiles.length; k++) // Parcourt toutes les tuiles pour trouver la bonne tuile
                        {
                            if (this.tuiles[k].polygon.getCentroidIdx() == centroidVoisin)
                            {
                                tuileVoisine = tuiles[k];
                                break;
                            }
                        }

                        if(tuileVoisine.getAltitude() == -1) // Si la tuile n'a pas d'altitude associée -> currentAltitude - 1 est attribuée
                        {
                            tuileVoisine.setAltitude(currentAltitude - 1);
                        }
                    }
                }
            }
            currentAltitude -= 1; // Diminution de currentAltitude après avoir tout parcouru les tuiles
        }
        while(currentAltitude >= 0);
    }
}
