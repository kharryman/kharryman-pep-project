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

    public boolean isAccountUsernameExists(String username){
      return socialMediaDA0.isAccountUsernameExists(username);
    }

    public Account registerAccount(Account account){
        return socialMediaDA0.registerAccount(account);
    }

    public Account login(Account account){
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

    public Message getMessageById(int message_id){
        return socialMediaDA0.getMessageById(message_id);
    }

    public Message deleteMessageById(int message_id){
        return socialMediaDA0.deleteMessageById(message_id);
    }

    public Message updateMessageById(int message_id, Message updatedMessage){
        return socialMediaDA0.updateMessageById(message_id, updatedMessage);
    }

    public List<Message> getMessagesByAccountId(int account_id){
        return socialMediaDA0.getMessagesByAccountId(account_id);
    }
}
