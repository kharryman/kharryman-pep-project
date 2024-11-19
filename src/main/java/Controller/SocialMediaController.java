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
        // ObjectMapper objectMapper = new ObjectMapper();

        // app.get("example-endpoint", this::exampleHandler);

        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("accounts/{account_id}/messages", this::getAccountMessagesHandler);
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
    //REGISTER ACCOUNT:
    private void registerHandler(Context ctx) {
        SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
        Account newAccount = ctx.bodyAsClass(Account.class);
        String username = newAccount.getUsername();
        String password = newAccount.getPassword();
        boolean isAccountUsernameExists = accountService.isAccountUsernameExists(username);
        if (isAccountUsernameExists == true) {
            ctx.status(400);
            System.out.println("Username, " + username + ", already exists.");
            ctx.result("");
            return;
        } else if (username == "") {
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
    }
    //LOGIN:
    private void loginHandler(Context ctx) {
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
    }
    //CREATE MESSAGE:
    private void createMessageHandler(Context ctx) {
        Message message = ctx.bodyAsClass(Message.class);
        String text = message.getMessage_text();
        int postedByUserId = message.getPosted_by();
        if (text == "") {
            ctx.status(400);
            System.out.println("Message can not be blank");
            ctx.result("");
            return;
        } else if (text.length() >= 255) {
            ctx.status(400);
            System.out.println("Message length must be less than 255 charaters.");
            ctx.result("");
            return;
        } else {
            SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
            Account account = accountService.getAccountById(postedByUserId);
            if (account == null) {// ACCOUNT ID DOES NOT EXIST
                ctx.status(400);
                System.out.println("Account ID does not exist.");
                ctx.result("");
                return;
            } else {
                ctx.status(200);
                String jsonNewMessageString = accountService.createMessage(message);
                ctx.result(jsonNewMessageString);
            }
        }
    }
    //GET ALL MESSAGES:
    private void getAllMessagesHandler(Context ctx) {
        SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
        List<Message> messages = accountService.getAllMessages();
        ctx.status(200);
        ctx.json(messages);
    }
    //GET MESSAGE:
    private void getMessageHandler(Context ctx) {
        SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
        String message_id_string = ctx.pathParam("message_id");
        int message_id = Integer.parseInt(message_id_string);
        Message message = accountService.getMessageById(message_id);
        ctx.status(200);
        if (message == null) {
            System.out.println("Get message by ID, message not found");
            ctx.result("");
        } else {
            ctx.json(message);
        }
    }
    //DELETE MESSAGE:
    private void deleteMessageHandler(Context ctx) {
        SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
        String message_id_string = ctx.pathParam("message_id");
        int message_id = Integer.parseInt(message_id_string);
        Message message = accountService.deleteMessageById(message_id);
        ctx.status(200);
        if (message == null) {
            System.out.println("Delete message by ID, message not found");
            ctx.result("");
        } else {
            ctx.json(message);
        }
    }
    //UPDATE MESSAGE:
    private void updateMessageHandler(Context ctx) {
        String message_id_string = ctx.pathParam("message_id");
        int message_id = Integer.parseInt(message_id_string);
        Message updatedMessage = ctx.bodyAsClass(Message.class);
        String text = updatedMessage.getMessage_text();
        if (text == "") {
            ctx.status(400);
            System.out.println("Updated message can not be blank");
            ctx.result("");
            return;
        } else if (text.length() >= 255) {
            ctx.status(400);
            System.out.println("Updated message length must be less than 255 charaters.");
            ctx.result("");
            return;
        } else {
            SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
            boolean isMessageExists = accountService.isMessageByIdExists(message_id);
            if (!isMessageExists) {
                ctx.status(400);
                System.out.println("Message does not exist.");
                ctx.result("");
                return;
            } else {
                Message newUpdatedMessage = accountService.updateMessageById(message_id, updatedMessage);
                if (newUpdatedMessage == null) {// ERROR UPDATING MESSAGE.
                    ctx.status(400);
                    System.out.println("Error updating message.");
                    ctx.result("");
                    return;
                } else {
                    ctx.status(200);
                    ctx.json(newUpdatedMessage);
                }
            }
        }
    }
    //GET ACCOUNT MESSAGES:
    private void getAccountMessagesHandler(Context ctx) {
        String account_id_string = ctx.pathParam("account_id");
        int account_id = Integer.parseInt(account_id_string);
        SocialMediaBlogApiService accountService = new SocialMediaBlogApiService();
        List<Message> messages = accountService.getMessagesByAccountId(account_id);
        ctx.status(200);
        ctx.json(messages);
    }

}