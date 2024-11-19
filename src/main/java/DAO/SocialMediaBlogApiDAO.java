package DAO;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SocialMediaBlogApiDAO {

   public boolean isAccountUsernameExists(String username) {
      Connection connection = ConnectionUtil.getConnection();
      try {
         String sql = "SELECT * FROM account WHERE username=?";
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         preparedStatement.setString(1, username);
         ResultSet rs = preparedStatement.executeQuery();
         if (rs.next()) {
            return true;
         }
      } catch (SQLException e) {
         System.out.println("isAccountUsernameExists: " + e.getMessage());
         return true;
      }
      return false;
   }

   public boolean isMessageByIdExists(int message_id) {
      Connection connection = ConnectionUtil.getConnection();
      try {
         String sql = "SELECT * FROM message WHERE message_id=?";
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         preparedStatement.setInt(1, message_id);
         ResultSet rs = preparedStatement.executeQuery();
         if (rs.next()) {
            return true;
         }
      } catch (SQLException e) {
         System.out.println("isMessageByIdExists: " + e.getMessage());
      }
      return false;
   }

   public Account registerAccount(Account account) {
      Connection connection = ConnectionUtil.getConnection();
      // Account account = new Account(passedAccount.getFirst("username").toString(),
      // passedAccount.getFirst("password").toString());
      try {
         boolean isUsernameExists = isAccountUsernameExists(account.getUsername());
         if (!isUsernameExists) {
            String sql = "INSERT INTO account (username, password) VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            preparedStatement.executeUpdate();
            ResultSet generatedKeysRS = preparedStatement.getGeneratedKeys();
            if (generatedKeysRS.next()) {
               int newAccountId = (int) generatedKeysRS.getLong(1);
               Account newAccount = new Account(newAccountId, account.getUsername(), account.getPassword());
               return newAccount;
            }
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      return null;
   }

   public Account login(Account account) {
      Connection connection = ConnectionUtil.getConnection();
      try {
         String sql = "SELECT * FROM account WHERE username=? AND password=?";
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         preparedStatement.setString(1, account.getUsername());
         preparedStatement.setString(2, account.getPassword());
         ResultSet rs = preparedStatement.executeQuery();
         Account foundAccount = null;
         if (rs.next()) {
            foundAccount = new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            return foundAccount;
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      return null;
   }

   public Account getAccountById(int accountId) {
      Connection connection = ConnectionUtil.getConnection();
      Account foundAccount = null;
      try {
         String sql = "SELECT * FROM account WHERE account_id=?";
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         preparedStatement.setInt(1, accountId);
         ResultSet rs = preparedStatement.executeQuery();
         if (rs.next()) {
            foundAccount = new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      return foundAccount;
   }

   public String createMessage(Message message) {
      Connection connection = ConnectionUtil.getConnection();
      try {
         String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?,?,?)";
         PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
         preparedStatement.setInt(1, message.getPosted_by());
         preparedStatement.setString(2, message.getMessage_text());
         // int epochTimeSecs = (int) System.currentTimeMillis()/1000;
         preparedStatement.setLong(3, message.getTime_posted_epoch());
         preparedStatement.executeUpdate();
         ResultSet generatedKeysRS = preparedStatement.getGeneratedKeys();
         if (generatedKeysRS.next()) {
            int newMessageId = (int) generatedKeysRS.getLong(1);
            Message newMessage = new Message(newMessageId, message.getPosted_by(), message.getMessage_text(),
                  message.getTime_posted_epoch());
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMessageString = null;
            try {
               jsonMessageString = objectMapper.writeValueAsString(newMessage);
            } catch (Exception e) {
               System.out.println("Error getting create message JSON string: " + e.getMessage());
            }
            return jsonMessageString;
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      return null;
   }

   public List<Message> getAllMessages() {
      Connection connection = ConnectionUtil.getConnection();
      List<Message> messages = new ArrayList<>();
      try {
         String sql = "SELECT * FROM message ORDER BY time_posted_epoch DESC";
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         ResultSet rs = preparedStatement.executeQuery();
         if (rs.next()) {
            messages.add(new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"),
                  rs.getLong("time_posted_epoch")));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      return messages;
   }

   public Message getMessageById(int messageId) {
      Connection connection = ConnectionUtil.getConnection();
      Message foundMessage = null;
      try {
         String sql = "SELECT * FROM message WHERE message_id=?";
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         preparedStatement.setInt(1, messageId);
         ResultSet rs = preparedStatement.executeQuery();
         if (rs.next()) {
            foundMessage = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"),
                  rs.getLong("time_posted_epoch"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      return foundMessage;
   }

   public Message deleteMessageById(int messageId) {
      Connection connection = ConnectionUtil.getConnection();
      Message foundMessage = getMessageById(messageId);
      Message deleteMessage = null;
      if (foundMessage != null) {
         deleteMessage = foundMessage;
         try {
            String sql = "DELETE * FROM message WHERE message_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, messageId);
            int numRows = preparedStatement.executeUpdate();
            System.out.println("deleteMessageById numRows deleted = " + numRows);
         } catch (SQLException e) {
            System.out.println("deleteMessageById ERROR:" + e.getMessage());
         }
      }
      return deleteMessage;
   }

   public Message updateMessageById(int messageId, Message updatedMessage) {
      System.out.println("updateMessageById updatedMessage = " + updatedMessage);
      Connection connection = ConnectionUtil.getConnection();
      Message foundMessage = getMessageById(messageId);
      Message newUpdatedMessage = null;
      if (foundMessage != null) {
         newUpdatedMessage = new Message(messageId, foundMessage.getPosted_by(), updatedMessage.getMessage_text(),
         foundMessage.getTime_posted_epoch());
         try {
            String sql = "UPDATE message SET posted_by=?, message_text=?, time_posted_epoch=? WHERE message_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, newUpdatedMessage.getPosted_by());
            preparedStatement.setString(2, newUpdatedMessage.getMessage_text());
            preparedStatement.setLong(3, newUpdatedMessage.getTime_posted_epoch());
            preparedStatement.setInt(4, messageId);
            int numRows = preparedStatement.executeUpdate();
            System.out.println("updatedMessageById numRows updated = " + numRows);
         } catch (SQLException e) {
            System.out.println("updatedMessageById ERROR:" + e.getMessage());
            newUpdatedMessage = null;
         }
      }
      return newUpdatedMessage;
   }

   public List<Message> getMessagesByAccountId(int accountId) {
      Connection connection = ConnectionUtil.getConnection();
      List<Message> messages = new ArrayList<>();
      try {
         String sql = "SELECT * FROM message WHERE posted_by=? ORDER BY time_posted_epoch DESC";
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         preparedStatement.setInt(1, accountId);
         ResultSet rs = preparedStatement.executeQuery();
         if (rs.next()) {
            messages.add(new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"),
                  rs.getLong("time_posted_epoch")));
         }
      } catch (SQLException e) {
         System.out.println("getMessagesByAccountId ERROR: " + e.getMessage());
      }
      return messages;
   }

}