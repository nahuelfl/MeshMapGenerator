package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs;

import java.util.List;

public abstract class Shape
{
    protected abstract Tuile[] definitionTerrain(int height, int width, Tuile[] tuiles, Structs.Point centre, List<Structs.Point> points);
}
