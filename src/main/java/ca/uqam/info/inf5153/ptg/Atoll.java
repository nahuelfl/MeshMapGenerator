package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs;

import java.util.List;

public class Atoll extends Shape
{
    private double r1; // Rayon grand cercle 80% de la taille du terrain
    private double r2; // Rayon petit cercle 40% de la taille du terrain

    /**
     * Fonction qui attribue les biomes respectifs aux tuiles d'une Tuile[]
     * selon un algorithme précis dépendamment de leur Shape spécifique
     *
     * Pour Atoll, les tuiles à l'extérieur du cercle de r2 sont des OceanBiome,
     *             les tuiles à l'intérieur du cercle de r1 sont des LagonBiome.
     *             les tuiles à l'intérieur de r2 mais à l'extérieur de r1 sont soit des PLageBiome si elles
     *             ont un voisin d'eau, ou des VegetationBiome dans le cas contraire.
     *
     *
     * @param height height du Terrain
     * @param width width du Terrain
     * @param tuiles List de tuiles
     * @param centre Point centre du Terrain
     * @param points Liste de points
     * @return
     */
    @Override
    protected Tuile[] definitionTerrain(int height, int width, Tuile[] tuiles, Structs.Point centre, List<Structs.Point>points)
    {
        double r2 = (height * 0.8) /2; // Rayon grand cercle 80%
        double r1 = (height * 0.4) /2; // Rayon petit cercle 40%

        this.r1 = r1;
        this.r2 = r2;

        // Définition des tuiles aux limites des deux cercles respectifs, soit des LagonBiome ou des OceanBiome
        for(int i = 0; i < tuiles.length; i++) // Parcourt toutes les tuiles
        {
            Structs.Point point = points.get(tuiles[i].polygon.getCentroidIdx()); // Point centroid du Polygone(i)

            // Calcul de distance entre le point centre du terrain et le point centre du polygone
            Double distance = Math.sqrt(
                    ((point.getX() - centre.getX()) * (point.getX() - centre.getX())) +
                            ((point.getY() - centre.getY()) * (point.getY() - centre.getY())));

            if (distance <= r1) // Tuiles de lagon (bleu turquoise)
            {
                tuiles[i].setBiome(new LagonBiome());
            }
            else if (distance > r2) // Tuiles d'ocean (bleu marine)
            {
                tuiles[i].setBiome(new OceanBiome());
            }
        }

        // Définition des tuiles entres les deux limites des deux cercles, soit PlageBiome et VegetationBiome
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
                    if(tuiles[i].getBiome() == null) // Si la tuile n'as pas déjà de Biome associé
                    {
                        // Une tuile ayant pour voisin immédiat un polygone d'eau devient de la plage
                        if (tuileVoisine.getBiome() instanceof LagonBiome || tuileVoisine.getBiome() instanceof OceanBiome)
                        {
                            tuiles[i].setBiome(new PlageBiome());
                            break;
                        }
                    }
                }
            }

            if (tuiles[i].getBiome() == null) // Les polygones restants deviennent de la vegetation
            {
                tuiles[i].setBiome(new VegetationBiome());
            }
        }
        return tuiles;
    }

    /* getters et setters de la classe */
    public double getR1(){ return this.r1; }
    public void setR1(double r1){ this.r1 = r1; }

    public double getR2(){ return this.r2; }
    public void setR2(double r2){ this.r2 = r2; }
}
