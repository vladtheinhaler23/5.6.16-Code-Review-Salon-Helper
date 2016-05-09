import java.util.Map;
import java.util.List;
import java.util.HashMap;
import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

public class App {

  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
         HashMap<String, Object> model = new HashMap<String, Object>();
         model.put("template", "templates/index.vtl");
         return new ModelAndView(model, layout);
       }, new VelocityTemplateEngine());

    post("/stylists", (request, response) -> {
        HashMap<String, Object> model = new HashMap<String, Object>();
        String name = request.queryParams("name");
        Stylist newStylist = new Stylist(name);
        newStylist.save();
        model.put("template", "templates/stylist-success.vtl");
        return new ModelAndView(model, layout);
      }, new VelocityTemplateEngine());

    get("/stylists", (request, response) -> {
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("stylists", Stylist.all());
        model.put("template", "templates/stylists.vtl");
        return new ModelAndView(model, layout);
      }, new VelocityTemplateEngine());

    get("/stylists/new", (request, response) -> {
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("template", "templates/stylist-form.vtl");
        return new ModelAndView(model, layout);
      }, new VelocityTemplateEngine());

    get("/stylists/:id", (request, response) -> {
       HashMap<String, Object> model = new HashMap<String, Object>();
       Stylist stylist = Stylist.find(Integer.parseInt(request.params(":id")));
       model.put("stylist", stylist);
       model.put("template", "templates/stylist.vtl");
       return new ModelAndView(model, layout);
     }, new VelocityTemplateEngine());

     get("/stylists/:id/clients/new", (request, response) -> {
         HashMap<String, Object> model = new HashMap<String, Object>();
         Stylist stylist = Stylist.find(Integer.parseInt(request.params(":id")));
         model.put("stylist", stylist);
         model.put("template", "templates/client-form.vtl");
         return new ModelAndView(model, layout);
       }, new VelocityTemplateEngine());

     get("/stylists/:stylist_id/clients/:id", (request, response) -> {
        HashMap<String, Object> model = new HashMap<String, Object>();
        Stylist stylist = Stylist.find(Integer.parseInt(request.params(":stylist_id")));
        Client client = Client.find(Integer.parseInt(request.params(":id")));
        model.put("stylist", stylist);
        model.put("client", client);
        model.put("template", "templates/client.vtl");
        return new ModelAndView(model, layout);
      }, new VelocityTemplateEngine());


    post("/clients", (request, response) -> {
       HashMap<String, Object> model = new HashMap<String, Object>();
       Stylist stylist = Stylist.find(Integer.parseInt(request.queryParams("stylist_id")));
       String name = request.queryParams("name");
       Client newClient = new Client(name, stylist.getId());
       newClient.save();
       model.put("stylist", stylist);
       model.put("template", "templates/stylist-clients-success.vtl");
       return new ModelAndView(model, layout);
     }, new VelocityTemplateEngine());

     post("/stylists/:stylist_id/clients/:id", (request, response) -> {
        HashMap<String, Object> model = new HashMap<String, Object>();
        Client client = Client.find(Integer.parseInt(request.params("id")));
        String name = request.queryParams("name");
        Stylist stylist = Stylist.find(client.getStylistId());
        client.update(name);
        String url = String.format("/stylists/%d/clients/%d", stylist.getId(), client.getId());
        response.redirect(url);
        return new ModelAndView(model, layout);
      }, new VelocityTemplateEngine());

    post("/stylists/:stylist_id/clients/:id/delete", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Client client = Client.find(Integer.parseInt(request.params("id")));
      Stylist stylist = Stylist.find(client.getStylistId());
      client.delete();
      model.put("stylist", stylist);
      model.put("template", "templates/stylist.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());





   }
 }
