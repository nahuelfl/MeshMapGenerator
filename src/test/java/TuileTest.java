import ca.uqam.ace.inf5153.mesh.io.MeshReader;
import ca.uqam.ace.inf5153.mesh.io.Structs;
import ca.uqam.info.inf5153.ptg.Biome;
import ca.uqam.info.inf5153.ptg.LagonBiome;
import ca.uqam.info.inf5153.ptg.Tuile;
import ca.uqam.info.inf5153.ptg.VegetationBiome;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;


public class TuileTest extends TestCase {

    private Structs.Mesh mesh;
    Structs.Mesh.Builder builder;
    public TuileTest() throws IOException {
        try {
            mesh = new MeshReader().readFromFile("src/test/samplesTest/sample-500-500-256T.mesh");
            builder = this.mesh.toBuilder();
        } catch (Exception e){
            System.out.println("Une erreur est survenue lors de la lecture du fichier dans les tests de Tuile");
            e.printStackTrace();
        }
    }

    @Test
    public void testGetBiome() {
        // testSetbiome et testGetBiome sont interdependants
        //contexte
        Tuile tuileBiome = new Tuile(builder.getPolygons(0));
        tuileBiome.setBiome(new VegetationBiome());

        //oracle
        String b = new VegetationBiome().getClass().getSimpleName();

        //experience
        String t = tuileBiome.getBiome().getClass().getSimpleName();

        //assertion
        assertEquals(b, t);
    }

    @Test
    public void testSetBiome(){
        // testSetbiome et testGetBiome sont interdependants
        //contexte
        Tuile tuileLagon = new Tuile(builder.getPolygons(0));

        //oracle
        Biome b = new LagonBiome();

        //experience
        tuileLagon.setBiome(new LagonBiome());

        //assertion
        assertEquals(b.getClass(), tuileLagon.getBiome().getClass());
    }


    public void testGetAltitude() {
        // testSetAltitude et testGetAltitude sont interdependants
        //contexte
        int altitudeSet = 10;
        Tuile tuileAltitude = new Tuile(builder.getPolygons(1));
        tuileAltitude.setAltitude(altitudeSet);

        //oracle
        int altitudeExpected = 10;

        //experience
        int a = tuileAltitude.getAltitude();

        //assertion
        assertEquals(altitudeExpected, a);
    }

    public void testSetAltitude() {
        // testSetAltitude et testGetAltitude sont interdependants
        //contexte
        Tuile tuileAltitude = new Tuile(builder.getPolygons(1));

        //oracle
        int altitudeExpected = 15;
        int altitudeSet = 15;

        //experience
        tuileAltitude.setAltitude(altitudeSet);

        //assertion
        assertEquals(altitudeExpected, tuileAltitude.getAltitude());
    }

    public void testGetHumidite() {
        // testSetHumidite et testGetHumidite sont interdependants
        //contexte
        int humiditeSet = 70;
        Tuile tuileHumidite = new Tuile(builder.getPolygons(2));
        tuileHumidite.setHumidite(humiditeSet);

        //oracle
        int humiditeExpected = 70;

        //experience
        int h = tuileHumidite.getHumidite();

        //assertion
        assertEquals(humiditeExpected, h);
    }

    public void testSetHumidite() {
        // testSetHumidite et testGetHumidite sont interdependants
        //contexte
        Tuile tuileHumidite = new Tuile(builder.getPolygons(2));

        //oracle
        int humiditeExpected = 15;
        int humiditeSet = 15;

        //experience
        tuileHumidite.setHumidite(humiditeSet);

        //assertion
        assertEquals(humiditeExpected, tuileHumidite.getHumidite());
    }


}