package socialnetwork.toysocialnetwork.repository.database;

import socialnetwork.toysocialnetwork.domain.Friendship;
import socialnetwork.toysocialnetwork.domain.FriendshipSender;
import socialnetwork.toysocialnetwork.domain.FriendshipStatus;
import socialnetwork.toysocialnetwork.domain.dto.FriendshipDTO;
import socialnetwork.toysocialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.toysocialnetwork.domain.validators.Validator;
import socialnetwork.toysocialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class DBFriendshipRepository implements Repository<Long, Friendship> {
    private String databaseURL;
    private String username;
    private String password;

    private final Validator<Friendship> validator;

    public DBFriendshipRepository(FriendshipValidator validator, String databaseURL, String username, String password) {
        this.databaseURL = databaseURL;
        this.username = username;
        this.password = password;
        this.validator = validator;

    }

    public Friendship searchByValue(Friendship friendship)
    {
        if(friendship == null)
            throw new IllegalArgumentException();
        String sql = "SELECT * FROM friendships WHERE first_user_id = ? AND second_user_id = ?";
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        )
        {
            preparedStatement.setLong(1, friendship.getU1ID());
            preparedStatement.setLong(2, friendship.getU2ID());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                Long id = resultSet.getLong("id");
                Long firstUserID = resultSet.getLong("first_user_id");
                Long secondUserID = resultSet.getLong("second_user_id");
                Timestamp friendsFromTemp = resultSet.getTimestamp("friends_from");
                LocalDateTime friendsFrom = friendsFromTemp.toLocalDateTime();
                FriendshipSender friendshipSender = resultSet.getLong("requested_by") == 1 ? FriendshipSender.FIRST_USER : FriendshipSender.SECOND_USER;

                //String friendshipStatusString = resultSet.getString("friendship_status");
                FriendshipStatus friendshipStatus = FriendshipStatus.valueOf(resultSet.getString("friendship_status").toUpperCase());

                Friendship foundFriendship = new Friendship(firstUserID, secondUserID, friendsFrom, friendshipSender, friendshipStatus);
                foundFriendship.setId(id);
                return foundFriendship;
            }
        }
        catch (SQLException e)
        {
            return null;
        }
        return null;
    }

    @Override
    public Friendship save(Friendship newEntity) {
        if(newEntity == null)
        {
            return null;
        }

        validator.validate(newEntity);

        /* TODO: This is technically required, but the IDs are now managed by the DB, so it's always null by the time it gets here.

        User foundUser = findOne(newEntity.getId());

        if(foundUser != null)
        {
            return foundUser;
        }*/
        
        Friendship foundFriendship = searchByValue(newEntity);
        if(foundFriendship != null)
        {
            return foundFriendship;
        }

        String sql = "INSERT INTO friendships(first_user_id, second_user_id, friends_from, friendship_status, requested_by) VALUES(?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, newEntity.getU1ID());
            preparedStatement.setLong(2, newEntity.getU2ID());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(newEntity.getFriendsFrom()));
            preparedStatement.setString(4, newEntity.getStatus().name().toLowerCase());
            preparedStatement.setInt(5, newEntity.getSender() == FriendshipSender.FIRST_USER ? 1 : 2);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return newEntity;
        }

        return null;
    }

    @Override
    public Friendship findOne(Long entityID) {
        if (entityID == null)
            throw new IllegalArgumentException();

        String sql = "SELECT * FROM friendships WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        )
        {
            preparedStatement.setLong(1, entityID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                Long id = resultSet.getLong("id");
                Long firstUserID = resultSet.getLong("first_user_id");
                Long secondUserID = resultSet.getLong("second_user_id");


                Timestamp friendsFromTemp = resultSet.getTimestamp("friends_from");
                LocalDateTime friendsFrom = friendsFromTemp.toLocalDateTime();

                FriendshipSender friendshipSender = resultSet.getLong("requested_by") == 1 ? FriendshipSender.FIRST_USER : FriendshipSender.SECOND_USER;
                FriendshipStatus friendshipStatus = FriendshipStatus.valueOf(resultSet.getString("friendship_status").toUpperCase());


                Friendship friendship = new Friendship(firstUserID, secondUserID, friendsFrom, friendshipSender, friendshipStatus);
                friendship.setId(id);
                return friendship;
            }
        }
        catch (SQLException e)
        {
            return null;
        }


        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendshipsSet = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("select * from friendships");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long firstUserID = resultSet.getLong("first_user_id");
                Long secondUserID = resultSet.getLong("second_user_id");

                Timestamp friendsFromTemp = resultSet.getTimestamp("friends_from");
                LocalDateTime friendsFrom = friendsFromTemp.toLocalDateTime();

                FriendshipSender friendshipSender = resultSet.getLong("requested_by") == 1 ? FriendshipSender.FIRST_USER : FriendshipSender.SECOND_USER;
                FriendshipStatus friendshipStatus = FriendshipStatus.valueOf(resultSet.getString("friendship_status").toUpperCase());

                Friendship friendship = new Friendship(firstUserID, secondUserID, friendsFrom, friendshipSender, friendshipStatus);
                friendship.setId(id);
                friendshipsSet.add(friendship);
            }
        } catch (SQLException e)
        {
            return new HashSet<>();
        }

        return friendshipsSet;
    }

    public Iterable<FriendshipDTO> findAllWithNames()
    {
        Set<FriendshipDTO> friendshipsSet = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     """
                             select F.id, U1.id as u1id, U2.id as u2id, U1.first_name as fn1, U2.first_name as fn2, U1.last_name as ln1, U2.last_name as ln2, F.friends_from as frnds_from, F.friendship_status as frnds_status, F.requested_by
                             from friendships F
                             INNER JOIN users U1 ON U1.id = F.first_user_id
                             INNER JOIN users U2 ON U2.id = F.second_user_id
                         """);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long firstUserID = resultSet.getLong("u1id");
                Long secondUserID = resultSet.getLong("u2id");

                String firstUserFN = resultSet.getString("fn1");
                String firstUserLN = resultSet.getString("ln1");

                String secondUserFN = resultSet.getString("fn2");
                String secondUserLN = resultSet.getString("ln2");

                Timestamp friendsFromTemp = resultSet.getTimestamp("frnds_from");
                LocalDateTime friendsFrom = friendsFromTemp.toLocalDateTime();

                FriendshipSender friendshipSender = resultSet.getLong("requested_by") == 1 ? FriendshipSender.FIRST_USER : FriendshipSender.SECOND_USER;
                FriendshipStatus friendshipStatus = FriendshipStatus.valueOf(resultSet.getString("frnds_status").toUpperCase());


                FriendshipDTO friendship = new FriendshipDTO(id, firstUserID, secondUserID, firstUserFN, firstUserLN, secondUserFN, secondUserLN, friendsFrom, friendshipStatus, friendshipSender);
                friendshipsSet.add(friendship);
            }
        } catch (SQLException e)
        {
            return new HashSet<>();
        }

        return friendshipsSet;
    }

    @Override
    public Friendship update(Friendship newEntity) {
        if(newEntity == null)
            throw new IllegalArgumentException();

        validator.validate(newEntity);
        Friendship foundFriendship;
        try {
            foundFriendship = searchByValue(newEntity);
            if (foundFriendship == null)
                return newEntity;
        }catch(IllegalArgumentException iae)
        {
            return newEntity;
        }

        String sql = "UPDATE friendships SET friends_from = ?, requested_by = ?, friendship_status = ? WHERE id = ?";
        try(Connection connection = DriverManager.getConnection(databaseURL, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);)
        {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(newEntity.getFriendsFrom()));

            preparedStatement.setInt(2, newEntity.getSender() == FriendshipSender.FIRST_USER ? 1 : 2);
            preparedStatement.setString(3, newEntity.getStatus().name().toLowerCase());

            preparedStatement.setLong(4, foundFriendship.getId());

            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            return newEntity;
        }
        return null;
    }

    @Override
    public Friendship delete(Long entityID) {
        if(entityID == null)
            throw new IllegalArgumentException();


        Friendship foundFriendship = findOne(entityID);

        if(foundFriendship == null)
        {
            return null;
        }

        String sqlDeleteStatement = "DELETE FROM friendships WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(databaseURL, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteStatement))
        {
            preparedStatement.setLong(1, entityID);
            preparedStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            return null;
        }

        return foundFriendship;
    }
}

