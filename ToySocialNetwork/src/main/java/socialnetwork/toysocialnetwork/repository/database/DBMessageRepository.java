package socialnetwork.toysocialnetwork.repository.database;

import socialnetwork.toysocialnetwork.domain.Message;
import socialnetwork.toysocialnetwork.domain.validators.Validator;
import socialnetwork.toysocialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBMessageRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;

    private Validator<Message> messageValidator;

    public DBMessageRepository(Validator<Message> messageValidator, String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.messageValidator = messageValidator;
    }

    @Override
    public Message save(Message newEntity) {
        if(newEntity == null)
            throw new IllegalArgumentException();

        messageValidator.validate(newEntity);

        String insertSqlStatement = "INSERT INTO Messages (sender_id, receiver_id, content, time_sent) VALUES (?, ?, ?, ?)";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSqlStatement);
        )
        {
            preparedStatement.setLong(1, newEntity.getSenderID());
            preparedStatement.setLong(2, newEntity.getReceiverID());
            preparedStatement.setString(3, newEntity.getContent());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(newEntity.getTimeSent()));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return newEntity;
        }

        return null;
    }

    @Override
    public Message findOne(Long entityID) {
        if(entityID == null)
            throw new IllegalArgumentException();

        String sqlQueryString = "SELECT sender_id, receiver_id, time_sent, content FROM Messages WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryString)) {
            preparedStatement.setLong(1, entityID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                Long senderID = resultSet.getLong("sender_id");
                Long receiverID = resultSet.getLong("receiver_id");
                String content = resultSet.getString("content");
                LocalDateTime timeSent = resultSet.getTimestamp("time_sent").toLocalDateTime();

                Message message = new Message(senderID, receiverID, content, timeSent);
                return message;
            }

        }
        catch (SQLException e) {
            return null;
        }

        return null;
    }

    public Iterable<Message> findAllBetweenTwoUsers(Long user1ID, Long user2ID) {
        if(user1ID == null || user2ID == null)
            throw new IllegalArgumentException();

        List<Message> messageSet = new ArrayList<>();

        String sqlQueryString = "SELECT sender_id, receiver_id, content, time_sent FROM Messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY time_sent";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryString)) {

            preparedStatement.setLong(1, user1ID);
            preparedStatement.setLong(2, user2ID);
            preparedStatement.setLong(3, user2ID);
            preparedStatement.setLong(4, user1ID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next())
            {
                Long senderID = resultSet.getLong("sender_id");
                Long receiverID = resultSet.getLong("receiver_id");
                String content = resultSet.getString("content");
                LocalDateTime timeSent = resultSet.getTimestamp("time_sent").toLocalDateTime();

                Message message = new Message(senderID, receiverID, content, timeSent);
                messageSet.add(message);
            }
        }
        catch (SQLException e) {
            return new HashSet<>();
        }
        return messageSet;
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messageSet = new HashSet<>();

        String sqlQueryString = "SELECT sender_id, receiver_id, content, time_sent FROM Messages";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryString)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next())
            {
                Long senderID = resultSet.getLong("sender_id");
                Long receiverID = resultSet.getLong("receiver_id");
                String content = resultSet.getString("content");
                LocalDateTime timeSent = resultSet.getTimestamp("time_sent").toLocalDateTime();

                Message message = new Message(senderID, receiverID, content, timeSent);
                messageSet.add(message);
            }
        }
        catch (SQLException e) {
            return new HashSet<>();
        }
        return messageSet;
    }

    @Override
    public Message update(Message newEntity) {
        if(newEntity == null)
            throw new IllegalArgumentException();

        messageValidator.validate(newEntity);

        Message foundMessage = findOne(newEntity.getId());

        if(foundMessage == null)
            return newEntity;

        String sqlUpdateStatement = "UPDATE Messages SET content = ? WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateStatement)
        )
        {
            preparedStatement.setString(1, newEntity.getContent());
            preparedStatement.setLong(2, newEntity.getId());

            preparedStatement.executeUpdate();
        }
        catch(SQLException e) {
            return foundMessage;
        }
        return null;
    }

    @Override
    public Message delete(Long entityID) {
        if(entityID == null)
            throw new IllegalArgumentException();

        Message foundMessage = findOne(entityID);

        if(foundMessage == null)
            return null;

        String sqlDeleteStatement = "DELETE FROM Messages WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteStatement)
        )
        {
            preparedStatement.setLong(1, entityID);
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            return null;
        }
        return foundMessage;
    }
}
