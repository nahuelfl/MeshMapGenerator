package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs;
import ca.uqam.ace.inf5153.mesh.io.Structs.*;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Terrain
{
    private int height;
    private int width;

    private Tuile[] tuiles;
    private Point centre;
    private List<Point> points;
    private Mesh.Builder builder;
    private Shape shape;
    private int maxAltitude;

    // Constructeur
    public Terrain(Mesh.Builder builder, int centre, int h, int w)
    {
        Tuile[] ts = new Tuile[builder.getPolygonsCount()];

        // Initialisation de chaque tuile avec leur polygon respectifs
        for(int i = 0; i < builder.getPolygonsList().size(); i++)
        {
            ts[i] = new Tuile(builder.getPolygons(i));
        }

        this.tuiles = ts;
        this.builder = builder;
        this.height = h;
        this.width = w;
        this.centre = builder.getPoints(builder.getPolygons(centre).getCentroidIdx());
        this.points = builder.getPointsList();
    }

    /**
     * Fonction qui définie le terrain selon sa shape
     * Pour ce faire, la fonction fait appel à la fct précise definitionTerrain() que chaque type Shape possède
     *
     * @param shape classe Shape du terrain
     */
    public void buildTerrain(Shape shape)
    {
        tuiles = shape.definitionTerrain(this.height, this.width, this.tuiles, this.centre, this.points);
        this.shape = shape;
    }

    /**
     * Fonction qui se charge de faire d'initialiser une classe GestionTerrain et faire appel à ses
     * fonctions pertinentes pour la gestion des aquiferes du terrain
     *
     * @param nombreAquiferes String indiquant le nombre d'aquiferes que le terrain doit avoir
     * @param maxHumidite String indiquant la valeur maximale pour l'humidité
     */
    public void gestionAquifere(String nombreAquiferes, String maxHumidite)
    {
       GestionAquifere gestionAquifere = new GestionAquifere(nombreAquiferes, maxHumidite, this.tuiles);
       this. tuiles = gestionAquifere.placerAquiferes();
    }

    /**
     * Fonction qui se charge d'initialiser une classe GestionAltitude et faire appel à ses
     * fonctions pertinentes pour la gestion de l'altitude du terrain
     *
     * @param maxAltitude String indiquant la valeur maximale de l'altitude pour ce terrain
     */
    public void gestionAltitude(String maxAltitude)
    {
        this.maxAltitude = Integer.parseInt(maxAltitude);

        GestionAltitude gestionAltitude = new GestionAltitude(maxAltitude, shape, tuiles,centre, points);
        this. tuiles =  gestionAltitude.setAltitude();
    }

    /**
     * Fonction qui se charge d'initialiser une classe GestionColorier et faire appel à ses
     * fonctions pertinentes pour la gestion du coloriage du terrain
     *
     * @param heatmap String indiquant quel type de coloriage doit être fait
     */
    public void gestionColorier(String heatmap)
    {
        GestionColorier gestionColorier = new GestionColorier(heatmap, tuiles, builder, maxAltitude);
        this.builder = gestionColorier.colorier(heatmap);
    }


    /* getters et setters de la classe */

    public int getHeight()
    {
        return this.height;
    }

    public void setHeight()
    {
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public Tuile[] getTuiles()
    {
        return this.tuiles;
    }

    public void setTuiles(Tuile[] tuiles)
    {
        this.tuiles = tuiles;
    }

    public Point getCentre()
    {
        return this.centre;
    }

    public void setCentre(Point centre)
    {
        this.centre = centre;
    }

    public List<Point> getPoints()
    {
        return this.points;
    }

    public void setPoints(List<Point> points)
    {
        this.points = points;
    }

    public Mesh.Builder getBuilder()
    {
        return this.builder;
    }

    public void setBuilder(Mesh.Builder builder)
    {
        this.builder = builder;
    }

    public Shape getShape(){ return this.shape; }
}

