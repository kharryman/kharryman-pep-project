package Controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import Model.Account;
import Model.Message;
import Service.SocialMediaBlogApiService;
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
        Javalin app = Javalin.create();//.start(8080);
        ObjectMapper objectMapper = new ObjectMapper();

        app.get("example-endpoint", this::exampleHandler);
        
        //REGISTER:
        app.post("/register", ctx -> {
            Account newAccount = ctx.bodyAsClass(Account.class);
            String username = newAccount.getUsername();
            String password = newAccount.getPassword();
            if(username == ""){
                ctx.status(400);
                ctx.result("Username can not be blank");
                return;
            }else if(password.length()<4){
                ctx.status(400);
                ctx.result("Password must be at least 4 characters long");
                return;
            }else{
                ctx.status(200);
                SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
                String jsonNewAccountString = accountService.registerAccount(newAccount);
                ctx.result(jsonNewAccountString);
            }
        });

        //LOGIN:
        app.post("/login", ctx -> {
            Account newAccount = ctx.bodyAsClass(Account.class);
            SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
            String jsonAccountString = accountService.login(newAccount);
            if(jsonAccountString==null){
                ctx.status(401);
                ctx.result("User does not exist.");
                return;
            }else{
                ctx.status(200);
                ctx.result(jsonAccountString);
            }
        });

        //CREATE MESSAGE:
        app.post("/messages", ctx -> {
            Message message = ctx.bodyAsClass(Message.class);
            String text = message.getMessage_text();
            int postedByUserId = message.getPosted_by();
            if(text == ""){
                ctx.status(400);
                ctx.result("Message can not be blank");
                return;
            }else if(text.length()>=255){
                ctx.status(400);
                ctx.result("Message length must be less than 255 charaters.");
                return;
            }else{
                SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
                Account account = accountService.getAccountById(postedByUserId);
                if(account == null){//ACCOUNT ID DOES NOT EXIST
                    ctx.status(400);
                    ctx.result("Account ID does not exist.");
                    return;
                }else{
                    ctx.status(200);
                    String jsonNewMessageString = accountService.createMessage(message);
                    ctx.result(jsonNewMessageString);
                }
            }
        });        


        //GET ALL MESSAGES:
        app.get("/messages", ctx -> {
            SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
            List<Message> messages = accountService.getAllMessages();
            ctx.status(200);
            ctx.json(messages);
        });                

        //GET A MESSAGE:
        app.get("/messages:message_id}", ctx -> {
            SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
            String message_id_string = ctx.pathParam("message_id");
            int message_id = Integer.parseInt(message_id_string);
            Message message = accountService.getMessage(message_id);
            ctx.status(200);
            ctx.json(message);
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