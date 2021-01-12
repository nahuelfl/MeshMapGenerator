package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs;

public abstract class Biome
{
    protected Color color; // Couleur associée au biome

    protected abstract Structs.Property colorier(); // Fonction abstraite pour colorier
}
