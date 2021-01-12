package ca.uqam.info.inf5153.ptg;

public class Ile
{
    private Terrain terrain; //Terrain associé à l'ile
    private Shape shape; // Shape associé à l'île

    // Constructeur
    public Ile(Terrain terrain)
    {
        this.terrain = terrain;
        this.shape = terrain.getShape();
    }

    /**
     * Fonction qui permet de savoir si une ile est totalement visible dans le mesh
     * c'est à dire que le terrain rentre entièrement dans le Mesh
     *
     * @return vrai si ile totalement visible, faux dans le cas contraire
     */
    public boolean ileTotalementVisible()
    {
        Tuile[] tuiles = terrain.getTuiles();

        for(int i=0; i < tuiles.length; i++) //Parcourt toutes les tuiles
        {
            if (tuiles[i].getBiome() instanceof PlageBiome)
            {
                // Si une tuile ayant un PlageBiome touche les border deja colories de la carte -> l'île est trop grande
                if (tuiles[i].polygon.getPropertiesList().size() > 0 && tuiles[i].polygon.getProperties(0).getValue() != "00:00:00")
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Fonction qui permet de savoir si une ile est visiblement independante,
     * c'est à dire elle ne rentre pas en collision avec une autre
     *
     * @return vrai si l'ile entre en collision avec une autre (elle n'est pas independante), faux dans le cas contraire
     */
    public boolean collisionEntreIles()
    {
        Tuile[] tuiles = terrain.getTuiles();

        for(int i = 0; i < tuiles.length; i++) // Parcourt toutes les tuiles
        {
            if (tuiles[i].getBiome() instanceof PlageBiome)
            {
                int compteurOceans = 0;

                for (int j = 0; j < tuiles[i].polygon.getNeighborsCount(); j++) //Parcourt tous les voisins de la tuile
                {
                    int centroidVoisin = tuiles[i].polygon.getNeighbors(j);
                    Tuile tuileVoisine = tuiles[centroidVoisin];

                    if (tuileVoisine.getBiome() instanceof OceanBiome) // Si le voisin a un OceanBiome -> compteur augmente
                    {
                        compteurOceans++;
                    }
                }

                // Si une tuile  avec un Biome PlageBiome n'est pas au moins dans le voisinage d'une autre tuile OceanBiome
                if(compteurOceans <= 0)
                    return true;
            }
        }

        return false;
    }

    // Getter du terrain
    public Terrain getTerrain()
    {
        return this.terrain;
    }
}
