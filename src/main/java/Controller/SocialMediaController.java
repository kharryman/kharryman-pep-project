package Controller;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.SocialMediaBlogApiService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your
 * controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in
     * the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * 
     * @return a Javalin app object which defines the behavior of the Javalin
     *         controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();// .start(8080);
        //ObjectMapper objectMapper = new ObjectMapper();

        //app.get("example-endpoint", this::exampleHandler);

        // REGISTER:
        app.post("/register", ctx -> {
            SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
            Account newAccount = ctx.bodyAsClass(Account.class);
            String username = newAccount.getUsername();
            String password = newAccount.getPassword();
            boolean isAccountUsernameExists = accountService.isAccountUsernameExists(username);
            if(isAccountUsernameExists == true){
                ctx.status(400);
                System.out.println("Username, " + username + ", already exists.");
                ctx.result("");
                return;
            }else if (username == "") {
                ctx.status(400);
                System.out.println("Username can not be blank");
                ctx.result("");
                return;
            } else if (password.length() < 4) {
                ctx.status(400);
                System.out.println("Password must be at least 4 characters long");
                ctx.result("");
                return;
            } else {
                ctx.status(200);                
                Account generatedNewAccount = accountService.registerAccount(newAccount);
                ctx.json(generatedNewAccount);
            }
        });

        // LOGIN:
        app.post("/login", ctx -> {
            Account getAccount = ctx.bodyAsClass(Account.class);
            SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
            Account myAccount = accountService.login(getAccount);
            if (myAccount == null) {
                ctx.status(401);
                System.out.println("User does not exist.");
                ctx.result("");
                return;
            } else {
                ctx.status(200);
                ctx.json(myAccount);
            }
        });

        // CREATE MESSAGE:
        app.post("/messages", ctx -> {
            Message message = ctx.bodyAsClass(Message.class);
            String text = message.getMessage_text();
            int postedByUserId = message.getPosted_by();
            if (text == "") {
                ctx.status(400);
                ctx.result("Message can not be blank");
                return;
            } else if (text.length() >= 255) {
                ctx.status(400);
                ctx.result("Message length must be less than 255 charaters.");
                return;
            } else {
                SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
                Account account = accountService.getAccountById(postedByUserId);
                if (account == null) {// ACCOUNT ID DOES NOT EXIST
                    ctx.status(400);
                    ctx.result("Account ID does not exist.");
                    return;
                } else {
                    ctx.status(200);
                    String jsonNewMessageString = accountService.createMessage(message);
                    ctx.result(jsonNewMessageString);
                }
            }
        });

        // GET ALL MESSAGES:
        app.get("/messages", ctx -> {
            SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
            List<Message> messages = accountService.getAllMessages();
            ctx.status(200);
            ctx.json(messages);
        });

        // GET A MESSAGE:
        app.get("/messages/{message_id}", ctx -> {
            SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
            String message_id_string = ctx.pathParam("message_id");
            int message_id = Integer.parseInt(message_id_string);
            Message message = accountService.getMessageById(message_id);
            ctx.status(200);
            ctx.json(message);
        });

        // DELETE A MESSAGE:
        app.delete("/messages/{message_id}", ctx -> {
            SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
            String message_id_string = ctx.pathParam("message_id");
            int message_id = Integer.parseInt(message_id_string);
            Message message = accountService.deleteMessageById(message_id);
            ctx.status(200);
            ctx.json(message);
        });

        // UPDATE MESSAGE:
        app.patch("/messages/{message_id}", ctx -> {
            String message_id_string = ctx.pathParam("message_id");
            int message_id = Integer.parseInt(message_id_string);
            Message updatedMessage = ctx.bodyAsClass(Message.class);
            String text = updatedMessage.getMessage_text();
            if (text == "") {
                ctx.status(400);
                ctx.result("Updated message can not be blank");
                return;
            } else if (text.length() >= 255) {
                ctx.status(400);
                ctx.result("Updated message length must be less than 255 charaters.");
                return;
            } else {
                SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
                Message newUpdatedMessage = accountService.updateMessageById(message_id, updatedMessage);
                if (newUpdatedMessage == null) {// ERROR UPDATING MESSAGE.
                    ctx.status(400);
                    ctx.result("Error updating message.");
                    return;
                } else {
                    ctx.status(200);
                    ctx.json(newUpdatedMessage);
                }
            }
        });

        // GET ALL MESSAGES:
        app.get( "accounts/{account_id}/messages", ctx -> {
            String account_id_string = ctx.pathParam("account_id");
            int account_id = Integer.parseInt(account_id_string);
            SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
            List<Message> messages = accountService.getMessagesByAccountId(account_id);
            ctx.status(200);
            ctx.json(messages);
        });

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * 
     * @param context The Javalin Context object manages information about both the
     *                HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

}