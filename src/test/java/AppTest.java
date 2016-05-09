import org.fluentlenium.adapter.FluentTest;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.sql2o.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import static org.fluentlenium.core.filter.FilterConstructor.*;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest extends FluentTest {
  public WebDriver webDriver = new HtmlUnitDriver();

  @Override
   public WebDriver getDefaultDriver() {
     return webDriver;
   }

   @ClassRule
   public static ServerRule server = new ServerRule();


   @Before
   public void setUp(){
     DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/hair_salon_test", null, null);

   }
   @After
     public void tearDown() {
     try(Connection con = DB.sql2o.open()) {
       String deleteClientsQuery = "DELETE FROM clients *;";
       String deleteStylistsQuery = "DELETE FROM stylists *;";
       con.createQuery(deleteClientsQuery).executeUpdate();
       con.createQuery(deleteStylistsQuery).executeUpdate();
     }
   }
   @Test
   public void rootTest() {
     goTo("http://localhost:4567/");
     assertThat(pageSource()).contains("Salon Helper");
     assertThat(pageSource()).contains("View Stylists List");
     assertThat(pageSource()).contains("Add a New Stylist");
   }

   @Test
   public void stylistIsCreatedTest() {
     goTo("http://localhost:4567/");
     click("a", withText("Add a New Stylist"));
     fill("#name").with("Mexican");
     submit(".btn");
     assertThat(pageSource()).contains("Your stylist has been saved.");
   }

   @Test
   public void stylistIsDisplayedTest() {
     Stylist myStylist = new Stylist("Mexican");
     myStylist.save();
     String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
     goTo(stylistPath);
     assertThat(pageSource()).contains("Mexican");
   }

   @Test
   public void stylistShowPageDisplaysName() {
     goTo("http://localhost:4567/stylists/new");
     fill("#name").with("Mexican");
     submit(".btn");
     click("a", withText("View Stylists"));
     click("a", withText("Mexican"));
     assertThat(pageSource()).contains("Mexican");
   }

   @Test
   public void stylistClientsFormIsDisplayed() {
     goTo("http://localhost:4567/stylists/new");
     fill("#name").with("Thai");
     submit(".btn");
     click("a", withText("View Stylists"));
     click("a", withText("Thai"));
     click("a", withText("Add a new client"));
     assertThat(pageSource()).contains("Add a new client:");
   }

   @Test
   public void clientsIsAddedAndDisplayed() {
     goTo("http://localhost:4567/stylists/new");
     fill("#name").with("Mexican");
     submit(".btn");
     click("a", withText("View Stylists"));
     click("a", withText("Mexican"));
     click("a", withText("Add a new client"));
     fill("#name").with("Thai E San");
     submit(".btn");
     click("a", withText("View stylists"));
     click("a", withText("Mexican"));
     assertThat(pageSource()).contains("All stylists");
   }
   @Test
   public void allClientsDisplayNameOnStylistPage() {
     Stylist myStylist = new Stylist("Mexican");
     myStylist.save();
     Client firstClient = new Client("El Jefes", myStylist.getId());
     firstClient.save();
     Client secondClient = new Client("Thai E San", myStylist.getId());
     secondClient.save();
     String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
     goTo(stylistPath);
     assertThat(pageSource()).contains("El Jefes");
     assertThat(pageSource()).contains("Thai E San");
   }

   @Test
    public void clientUpdate() {
      Stylist myStylist = new Stylist("Janice");
      myStylist.save();
      Client myClient = new Client("Sarah", myStylist.getId());
      myClient.save();
      String clientPath = String.format("http://localhost:4567/stylists/%d/clients/%d", myStylist.getId(), myClient.getId());
      goTo(clientPath);
      fill("#name").with("Julie");
      submit("#update-client");
      assertThat(pageSource()).contains("Julie");
    }

    @Test
    public void clientDelete() {
      Stylist myStylist = new Stylist("Janice");
      myStylist.save();
      Client myClient = new Client("Sarah", myStylist.getId());
      myClient.save();
      String clientPath = String.format("http://localhost:4567/stylists/%d/clients/%d", myStylist.getId(), myClient.getId());
      goTo(clientPath);
      submit("#delete-client");
      assertEquals(0, Client.all().size());
    }

    @Test
     public void stylistUpdate() {
       Stylist myStylist = new Stylist("Janice");
       myStylist.save();
       String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
       goTo(stylistPath);
       fill("#name").with("Julie");
       submit("#update-stylist");
       assertThat(pageSource()).contains("Julie");
     }

     @Test
     public void stylistDelete() {
       Stylist myStylist = new Stylist("Janice");
       myStylist.save();
       String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
       goTo(stylistPath);
       submit("#delete-stylist");
       assertEquals(0, Client.all().size());
     }
 }
