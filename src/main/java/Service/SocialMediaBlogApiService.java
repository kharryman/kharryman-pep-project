package Service;

import java.util.List;

import DAO.SocialMediaBlogApiDAO;
import Model.Account;
import Model.Message;

public class SocialMediaBlogApiService {
    private SocialMediaBlogApiDAO socialMediaDA0 ;
    public SocialMediaBlogApiService(){
        socialMediaDA0 = new SocialMediaBlogApiDAO();
    }

    public String registerAccount(Account account){
        return socialMediaDA0.registerAccount(account);
    }

    public String login(Account account){
        return socialMediaDA0.login(account);
    }

    public Account getAccountById(int accountId){
        return socialMediaDA0.getAccountById(accountId);
    }

    public String createMessage(Message message){
        return socialMediaDA0.createMessage(message);
    }    

    public List<Message> getAllMessages(){
        return socialMediaDA0.getAllMessages();
    }

    public Message getMessage(int message_id){
        return socialMediaDA0.getMessageById(message_id);
    }
}
