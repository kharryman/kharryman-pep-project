package Controller;

import Model.Account;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create().start(8080);
        app.get("example-endpoint", this::exampleHandler);
        
        //REGISTER:
        app.post("/register", ctx -> {
            Account newAccount = ctx.bodyAsClass(Account.class);
            String username = newAccount.getUsername();
            String password = newAccount.getPassword();
            if(username == ""){
                ctx.status(400);
                ctx.result("Username is blank");
                return;
            }else if(password.length()<4){
                ctx.status(400);
                ctx.result("Password must be at least 4 characters long");
                return;
            }else{
                ctx.status(200);
                String jsonNewAccountString = "";
                //TO DO: Use service to call DAO to create account and return it as jsonString..parse?
                ctx.result(jsonNewAccountString);
            }
        });

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }


}