import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;
import java.util.Arrays;

public class StylistTest {

  @Before
  public void setUp() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/hair_salon_test", null, null);
  }

  @After
  public void tearDown() {
    try(Connection con = DB.sql2o.open()) {
      String deleteClientQuery = "DELETE FROM clients *;";
      String deleteStylistQuery = "DELETE FROM stylists *;";
      con.createQuery(deleteClientQuery).executeUpdate();
      con.createQuery(deleteStylistQuery).executeUpdate();
    }
  }

  @Test
  public void Stylist_instantiatesCorrectly_true() {
    Stylist myStylist = new Stylist("Mexican");
    assertEquals(true, myStylist instanceof Stylist);
  }

  @Test
  public void getName_stylistInstantiatesWithName_String() {
    Stylist myStylist = new Stylist("Mexican");
    assertEquals("Mexican", myStylist.getName());
  }

  @Test
  public void all_emptyAtFirst() {
    assertEquals(Stylist.all().size(), 0);
  }

  @Test
  public void equals_returnsTrueIfNamesAretheSame() {
    Stylist firstStylist = new Stylist("Mexican");
    Stylist secondStylist = new Stylist("Mexican");
    assertTrue(firstStylist.equals(secondStylist));
  }

  @Test
  public void save_savesIntoDatabase_true() {
    Stylist myStylist = new Stylist("Mexican");
    myStylist.save();
    assertTrue(Stylist.all().get(0).equals(myStylist));
  }
  @Test
  public void save_assignsIdToObject() {
    Stylist myStylist = new Stylist("Mexican");
    myStylist.save();
    Stylist savedStylist = Stylist.all().get(0);
    assertEquals(myStylist.getId(), savedStylist.getId());
  }
  @Test
  public void find_findStylistInDatabase_true() {
    Stylist myStylist = new Stylist("Mexican");
    myStylist.save();
    Stylist savedStylist = Stylist.find(myStylist.getId());
    assertTrue(myStylist.equals(savedStylist));
  }
  @Test
  public void getClients_retrievesAllClientsFromDataBase_clients() {
    Stylist myStylist = new Stylist("Mexican");
    myStylist.save();
    Client firstClient = new Client("Super Taco", myStylist.getId());
    firstClient.save();
    Client secondClient = new Client("Taqueria El Rodeo", myStylist.getId());
    secondClient.save();
    Client[] clients = new Client[] { firstClient, secondClient };
    assertTrue(myStylist.getClients().containsAll(Arrays.asList(clients)));
  }
}
