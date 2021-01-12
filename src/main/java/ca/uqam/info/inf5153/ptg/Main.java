package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.*;
import ca.uqam.ace.inf5153.mesh.io.Structs.*;
import com.google.protobuf.Message;
import org.apache.commons.cli.*;
import org.apache.maven.model.Build;

import javax.management.RuntimeErrorException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class Main
{

    public static void main(String[] args)
    {
        try
        {
            CommandLine options = configure(args);
            String inputFile = options.getOptionValue("i");
            String outputFile = options.getOptionValue("o");
            String shapeInput = options.getOptionValue("shape");
            String waterSpot = options.getOptionValue("water");
            String soilValue = (options.getOptionValue("soil")).toUpperCase();
            String altitudeInput = options.getOptionValue("altitude");
            String seedInput;
            String heatmapInput;
            String archipelagoInput;

            if(options.hasOption("seed"))
            {
                seedInput = options.getOptionValue("seed");
            }
            else
            {
                // on utilise un random seed si il n'est pas donné en paramètre
                Random rand = new Random();
                seedInput = Integer.toString(rand.nextInt(99) + 1);
            }

            if(options.hasOption("heatmap"))
            {
                heatmapInput = options.getOptionValue("heatmap");
            }
            else
            {
                heatmapInput = "";
            }

            if(options.hasOption("archipelago"))
            {
                archipelagoInput = options.getOptionValue("archipelago");
            }
            else
            {
                archipelagoInput = "1";
            }

            Seed currentSeed = new Seed(seedInput);
            Mesh updated = enrich(inputFile, shapeInput, altitudeInput, waterSpot, soilValue, seedInput, heatmapInput, archipelagoInput);

            MeshWriter writer = new MeshWriter();
            writer.writeToFile(updated, outputFile);

            // Si aucun seed est passé en paramère, le seed random genere est montré à l'usager
            if(!options.hasOption("seed"))
            {
                System.out.println("Seed : " + seedInput);
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
            System.exit(1);
        }
    }

    /**
     * Fonction qui configure les parametres données en input par l'usager et qui vont affecter directement le
     * fonctionnement du logiciel. Leurs compatibilités respectives sont aussi vérifiées.
     *
     * @param args String[] qui contient la lsite des commandes (input de l'usager)
     * @return
     * @throws ParseException
     */
    private static CommandLine configure(String[] args) throws ParseException
    {
        // Gestion des options et initialisation des parsers pour lire le fichier input
        Options opts = new Options();
        opts.addOption(new Option("i", "input", true,"Input mesh" ));
        opts.addOption(new Option("o", "output", true,"output file" ));
        opts.addOption(new Option("shape", "shape", true,"Terrain shape" ));
        opts.addOption(new Option("water", "water", true,"sources eau" ));
        opts.addOption(new Option("soil", "soil", true,"densité" ));
        opts.addOption(new Option("altitude", "altitude", true,"Max altitude" ));
        opts.addOption(new Option("seed", "seed", true,"seed input" ));
        opts.addOption(new Option("heatmap", "heatmap", true,"heatmap input" ));
        opts.addOption(new Option("archipelago", "archipelago", true,"archipelago input" ));
        CommandLineParser parser = new DefaultParser();
        CommandLine cl = parser.parse(opts, args);


        // Vérification des compatibilites des parametres donnés en input
        if (! cl.hasOption("i") || ! cl.hasOption("o") || ! cl.hasOption("shape"))
            throw new IllegalArgumentException("-i , -o and -shape must be provided!"); // message à l'usager s'il manque une option de base

        if  (! cl.hasOption("altitude"))
            throw new IllegalArgumentException("-altitude <number> must be provided"); // message à l'usager si l'option altitude n'est pas ok

        if ((!cl.hasOption("water") && cl.hasOption("soil")) ||(cl.hasOption("water") && !cl.hasOption("soil")))
            throw new IllegalArgumentException("--water and --soil need to be used together, one is missing!"); // message à l'usager si water/soil incomplet

        if((cl.getOptionValue("shape").equals("atoll")) && (cl.hasOption("archipelago")))
            throw new IllegalArgumentException("--shape atoll and --archipelago cant be used together!"); // message à l'usager sur l'incompatibilité de atoll et archipelago

        return cl;
    }

    private static Mesh enrich(String inputFile, String shape, String altitude, String waterSpot, String soilValue, String seed, String heatmap, String archipelago) throws IOException
    {
        // Etabli grandeur de la grille
        Mesh mesh = new MeshReader().readFromFile(inputFile);
        int height = Integer.parseInt(readMetadata(mesh, "height"));
        int width = Integer.parseInt(readMetadata(mesh, "width"));

        return process(mesh, width, height, shape, altitude, waterSpot, soilValue, seed, heatmap, archipelago);
    }

    private static Mesh process(Mesh mesh, int w, int h, String shape, String altitude, String waterSpot, String soilValue, String seed, String heatmap, String archipelago)
    {
        // Switch to "edition" mode
        Mesh.Builder builder = mesh.toBuilder();

        if(heatmap.equals(""))
            identifyBorders(builder, w, h, Color.BLEU_FONCE.associerValeursRGBA(Color.BLEU_FONCE));
        else
            identifyBorders(builder, w, h, Color.NOIR.associerValeursRGBA(Color.NOIR));

        int nombreIles = Integer.parseInt(archipelago);
        Ile[] iles = new Ile[nombreIles];

        boolean ileVisible = false;
        boolean collisionEntreIles = true;

        for(int i = 0; i < nombreIles; i++)
        {
            Terrain terrain;
            int centre;
            Seed currentSeed;
            int compteurBoucles = 0;
            int maxBoucles = 100;

            do
            {
                compteurBoucles++;

                if(nombreIles == 1)
                {
                    centre = findCentralPolygon(builder, w, h);
                    Seed defaultSeed = new Seed();
                    Seed newSeed = new Seed(seed);
                    currentSeed = defaultSeed.effectiveSeed(newSeed);
                    terrain = new Terrain(builder, centre, w, h);
                }
                else
                {
                    Random rand = new Random();
                    centre = rand.nextInt(builder.getPolygonsCount());
                    currentSeed = new Seed();
                    terrain = new Terrain(builder, centre, w/4, h/4);
                }

                if (!soilValue.equals("WET") && !soilValue.equals("DRY") && !soilValue.equals("REGULAR"))
                {
                    throw new IllegalArgumentException("soil must be one of the following: regular, dry, wet");
                }

                switch (shape)
                {
                    case "atoll":
                        terrain.buildTerrain(new Atoll());
                        break;

                    case "tortuga":
                        terrain.buildTerrain(new Tortuga(currentSeed));
                        break;

                    default:
                        terrain.buildTerrain(new Atoll());
                }

                iles[i] = new Ile(terrain);
                ileVisible = iles[i].ileTotalementVisible();
                collisionEntreIles = iles[i].collisionEntreIles();


                if(nombreIles == 1)
                {
                    collisionEntreIles = false;
                    //ileVisible = true;
                }

                if(compteurBoucles >= maxBoucles)
                    throw new RuntimeException("Les iles ne rentrent pas dans la carte, veuillez choisir un plus petit nombre");

            }
            while( ileVisible == false || collisionEntreIles == true);

            System.out.println("L'ile #" + (i + 1) + " a ete generee.");

            terrain.gestionAltitude(altitude);
            terrain.gestionAquifere(waterSpot, soilValue);
            terrain.gestionColorier(heatmap);

            if(i == nombreIles - 1)
            {
                if(heatmap.equals(""))
                {
                    terrain.gestionColorier("ocean");
                }
                else if(heatmap.equals("altitude") || heatmap.equals("humidity"))
                {
                    terrain.gestionColorier("oceanNoir");
                }
            }

            builder = terrain.getBuilder();
        }

        // finalizing the updated mesh
        return builder.build();
    }

    private static void identifyBorders(Mesh.Builder builder, int width, int height, String couleur)
    {
        Set<Integer> border = new HashSet<>();

        // Finding all points touching the border
        for (int i = 0; i < builder.getPointsCount(); i++)
        {
            Point p = builder.getPoints(i);

            if (p.getX() <= 0 || p.getX() >= width || p.getY() <= 0 || p.getY() >= height)
                border.add(i);
        }

        // Identifying associated segments
        Set<Integer> frontier = new HashSet<>();

        for (int i = 0; i < builder.getSegmentsCount(); i++)
        {
            Segment s = builder.getSegments(i);
            if (border.contains(s.getV1Idx()) || border.contains(s.getV2Idx()))
                frontier.add(i);
        }

        // Marking associated polygons in light blue
        for (int i = 0; i < builder.getPolygonsCount(); i++)
        {
            Polygon p = builder.getPolygons(i);
            Set<Integer> involved = new HashSet<Integer>(frontier);
            involved.retainAll(p.getSegmentIdxList());

            if (involved.size() != 0)
            {
                Property color = Property.newBuilder().setKey("color").setValue(couleur).build();
                builder.getPolygonsBuilder(i).addProperties(color);
            }
        }
    }

    private static int findCentralPolygon(Mesh.Builder builder, int width, int height)
    {
        double idealX = width / 2.0;
        double idealY = height / 2.0;
        double distanceToIdeal = Double.MAX_VALUE;
        int candidate = -1;

        for (int i = 0; i < builder.getPolygonsCount(); i++)
        {
            Point centroid = builder.getPoints(builder.getPolygons(i).getCentroidIdx());

            double distance = Math.sqrt(
                    ((idealX - centroid.getX()) * (idealX - centroid.getX())) +
                            ((idealY - centroid.getY()) * (idealY - centroid.getY())));

            if (distance < distanceToIdeal)
            {
                distanceToIdeal = distance;
                candidate = i;
            }
        }

        // Tagging the most central centroid
        Point.Builder center = builder.getPointsBuilder(builder.getPolygons(candidate).getCentroidIdx());
        Property color = Property.newBuilder().setKey("color").setValue("42:78:113").build();
        Property size = Property.newBuilder().setKey("thickness").setValue("3").build();
        center.addProperties(color).addProperties(size);

        return candidate;
    }

    private static int findCentralPolygonMultiple(Mesh.Builder builder, int width, int height, int plusWidth, int plusHeight)
    {
        double idealX = width / 2.0 + plusWidth;
        double idealY = height / 2.0 + plusHeight;
        double distanceToIdeal = Double.MAX_VALUE;
        int candidate = -1;

        for (int i = 0; i < builder.getPolygonsCount(); i++)
        {
            Point centroid = builder.getPoints(builder.getPolygons(i).getCentroidIdx());

            double distance = Math.sqrt(
                    ((idealX - centroid.getX()) * (idealX - centroid.getX())) +
                            ((idealY - centroid.getY()) * (idealY - centroid.getY())));

            if (distance < distanceToIdeal)
            {
                distanceToIdeal = distance;
                candidate = i;
            }
        }

        // Tagging the most central centroid
        Point.Builder center = builder.getPointsBuilder(builder.getPolygons(candidate).getCentroidIdx());
        Property color = Property.newBuilder().setKey("color").setValue("42:78:113").build();
        Property size = Property.newBuilder().setKey("thickness").setValue("3").build();
        center.addProperties(color).addProperties(size);

        return candidate;
    }

    private static void drawPath(Mesh.Builder builder, int start, int length)
    {
        if(length <= 0)
            return;

        Polygon p = builder.getPolygons(start);
        Random bag = new Random();
        Integer nextCentroid = p.getNeighbors(bag.nextInt(p.getNeighborsCount()));
        Integer nextPolygon = -1;

        for (int i = 0; i < builder.getPolygonsCount(); i++)
        {
            Polygon candidate = builder.getPolygons(i);

            if(candidate.getCentroidIdx() == nextCentroid)
            {
                nextPolygon = i;
                break;
            }
        }

        Property color = Property.newBuilder().setKey("color").setValue("169:169:169").build();
        Property size = Property.newBuilder().setKey("thickness").setValue(length+"").build();
        Property style = Property.newBuilder().setKey("style").setValue("dashed").build();

        Segment s = Segment.newBuilder()
                .setV1Idx(p.getCentroidIdx()).setV2Idx(nextCentroid)
                .addProperties(color).addProperties(size).addProperties(style)
                .build();
        builder.addSegments(s);

        drawPath(builder, nextPolygon, length-1);
    }

    private static String readMetadata(Mesh m, String key)
    {
        Optional<Property> prop = m.getPropertiesList().stream().filter(p -> p.getKey().equals(key)).findFirst();

        if (prop.isPresent())
        {
            return prop.get().getValue();
        }
        else
        {
            throw new IllegalArgumentException("Missing property [" + key + "]");
        }
    }

}
