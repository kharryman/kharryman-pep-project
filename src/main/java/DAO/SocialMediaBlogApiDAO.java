import Model.Account;
import Util.ConnectionUtil;
import java.sql.*;

import org.h2.util.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SocialMediaBlogApiDAO{

   public String registerUser(JSONObject passedAccount){    
      Connection connection = ConnectionUtil.getConnection();
      Account account = new Account(passedAccount.getFirst("username").toString(), passedAccount.getFirst("password").toString());
      try{
        String sql = "INSERT INTO account (username, password) VALUES (?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, account.getUsername());
        preparedStatement.setString(2, account.getPassword());
        preparedStatement.executeUpdate();
        ResultSet generatedKeysRS = preparedStatement.getGeneratedKeys();
        if(generatedKeysRS.next()){
            int newAccountId = (int) generatedKeysRS.getLong(1);
            Account newAccount = new Account(newAccountId, account.getUsername(), account.getPassword());
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonAccountString = objectMapper.writeValueAsString(newAccount);
            return jsonAccountString;
        }
      }catch(SQLException e){
         System.out.println(e.getMessage());
      }
      return null;
   }

}