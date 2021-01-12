package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs;

import java.util.List;
import java.util.Random;

public class Tortuga extends Shape
{

    private double rX; //rayon X de l'ellipse
    private double rY; //rayon Y de l'ellipse
    private double angle; //angle de l'ellipse
    private Seed seed; //Seed de l'ellipse

    // Constructeur
    public Tortuga(Seed seed)
    {
        super();
        this.seed = seed;
    }

    /**
     * Fonction qui attribue les biomes respectifs aux tuiles d'une Tuile[]
     * selon un algorithme précis dépendamment de leur Shape spécifique
     *
     * Pour Tortuga, il s'agit d'un terrain élliptique avec un angle et une taille aléatoire
     *
     * Des biomes sont associés à chaque tuile selon leur position dans la forme élliptique aléatoire choisie,
     * Aux frontières de l'éllipse on retrouve des PlageBiome et à son intérieur des VegetationBiome
     *
     *
     * @param height height du Terrain
     * @param width width du Terrain
     * @param tuiles List de tuiles
     * @param centre Point centre du Terrain
     * @param points
     * @return
     */
    @Override
    protected Tuile[] definitionTerrain(int height, int width, Tuile[] tuiles, Structs.Point centre, List<Structs.Point> points)
    {
        Random rand = new Random(seed.getValue()); //le rand est desormais parametrer selon la valeur de seed

        double angle = rand.nextDouble() * (360); // Angle aleatoire (d'inclinaison) de l'eclipse

        double rX; // Rayon X de l'ellipse
        double rY; // Rayon Y de l'ellipse

        do
        {
            rX = ((height/2) * (rand.nextFloat()) ); // Rayon aleatoire X de l'ellipse
            rY = ((height/2) * (rand.nextFloat()) ); // Rayon aleatoire Y de l'ellipse
        }
        while(rX <= rY || rX > (width/2) || rY > (height/2) || rX < 20 || rY < 20);  // A repeter tant que les rayons ne soient pas valides pour un ellipse

        this.rX = rX;
        this.rY = rY;
        this.angle = angle;

        // Définis si une tuile est dans l'ellipse ou pas, et l'affectation des Biomes respectifs soit VegetationBiome ou OceanBiome
        for(int i = 0; i < tuiles.length; i++) // Parcourt toutes les tuiles
        {
            Structs.Point pt = points.get(tuiles[i].polygon.getCentroidIdx()); // Point centroid du Polygone(i)

            Double distance = ((((Math.cos(angle) * (pt.getX() - centre.getX())) + (Math.sin(angle) * (pt.getY() - centre.getY()))
                    * ((Math.cos(angle) * (pt.getX() - centre.getX())) + (Math.sin(angle) * (pt.getY() - centre.getY())))) / (rX * rX))
                    +  ((((Math.sin(angle) * (pt.getX() - centre.getX())) - (Math.cos(angle) * (pt.getY() - centre.getY())))
                    * ((Math.sin(angle) * (pt.getX() - centre.getX())) - (Math.cos(angle) * (pt.getY() - centre.getY()))) / (rY * rY))));


            if(distance <= 1) // Point est dans l'ellipse => Tuiles de vegetation
            {
                tuiles[i].setBiome(new VegetationBiome());
            }
            else // Pas dans l'ellipse => Tuiles d'ocean
            {
                tuiles[i].setBiome(new OceanBiome());
            }
        }

        // Définis les tuiles à l'intérieur de l'éllipse qui se retrouvent aux frontières pour leur associer un PlageBiome
        for (int i = 0; i < tuiles.length; i++) // Parcourt toutes les tuiles
        {
            for (int j = 0; j < tuiles[i].polygon.getNeighborsCount(); j++) // Parcourt les voisins de la Tuile j
            {
                int centroidVoisin = tuiles[i].polygon.getNeighbors(j);

                Tuile tuileVoisine = null;

                for(int k = 0; k < tuiles.length; k++) // Parcourt toutes les tuiles pour vérifier que c'est la bonne tuile
                {
                    if(tuiles[k].polygon.getCentroidIdx() == centroidVoisin)
                    {
                        tuileVoisine = tuiles[k];
                        break;
                    }
                }

                if(tuileVoisine.getBiome() != null) // Si la tuileVoisine a un Biome associé
                {
                    if(tuiles[i].getBiome() instanceof VegetationBiome) // Si la Tuile a un biome Vegetation => Fait partie de l'ellipse
                    {
                        // Une tuile ayant pour voisin immédiat un polygone d'eau devient de la plage
                        if (tuileVoisine.getBiome() instanceof OceanBiome)
                        {
                            tuiles[i].setBiome(new PlageBiome());
                            break;
                        }
                    }
                }
            }
        }

        return tuiles;
    }

    /* Getters et setters de la classe */
    public double getRX(){ return this.rX; }
    public void setRX(double rX){ this.rX = rX; }

    public double getRY(){ return this.rY; }
    public void setRY(double rY){ this.rY = rY; }

    public double getAngle(){ return this.angle; }

}
