package socialnetwork.toysocialnetwork.repository.database;

import socialnetwork.toysocialnetwork.domain.User;
import socialnetwork.toysocialnetwork.domain.validators.UserValidator;
import socialnetwork.toysocialnetwork.domain.validators.Validator;
import socialnetwork.toysocialnetwork.repository.Repository;

import java.sql.*;
import java.util.*;

public class DBUsersRepository implements Repository<Long, User> {
    private String databaseURL;
    private String username;
    private String password;

    private final Validator<User> validator;

    public DBUsersRepository(UserValidator validator, String databaseURL, String username, String password) {
        this.databaseURL = databaseURL;
        this.username = username;
        this.password = password;
        this.validator = validator;

    }

    /**
     * Finds all the users that the given user is related to, either by an accepted friend request, or by a pending one.
     * @param userID The ID of the given user.
     * @return An iterable over the IDs of all the users respecting the requirement.
     */
    public Iterable<Long> findUsersRelatedTo(Long userID)
    {
        if(userID == null)
            throw new IllegalArgumentException();


        Set<Long> userSet = new HashSet<>();

        String userLookupSql = """
                SELECT F.second_user_id AS user_id FROM friendships F WHERE F.first_user_id = ?
                UNION
                SELECT F.first_user_id AS user_id FROM friendships F WHERE F.second_user_id = ?
                """;

        try(Connection connection = DriverManager.getConnection(databaseURL, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(userLookupSql);)
        {
            preparedStatement.setLong(1, userID);
            preparedStatement.setLong(2, userID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next())
            {
                Long id = resultSet.getLong("user_id");
                userSet.add(id);
            }

        } catch (SQLException e) {
            return new HashSet<>();
        }

        return userSet;
    }

    /**
     * Searches for a user in the repository, using their username as an identifier.
     * @param userName The user's username.
     * @return The user with the given username if it exists. null otherwise.
     */
    public User findByUsername(String userName)
    {
        if (userName == null) {
            throw new IllegalArgumentException();
        }
        String userLookupSql = "SELECT * FROM users WHERE user_name = ?";

        try(Connection connection = DriverManager.getConnection(databaseURL, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(userLookupSql);
        )
        {
            preparedStatement.setString(1, userName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                Long id = resultSet.getLong("id");
                return findOne(id);
            }

        } catch (SQLException e) {
            return null;
        }

        return null;
    }

    /**
     * Searches a user in the repository based on an incomplete user entity.
     * @param user - The incomplete user entity used for searching.
     * @return A user matching the incomplete user entity, if it is found. null, if nothing matching the incomplete user entity is found.
     */
    public User lookUp(User user) {
        if (user == null) {
            throw new IllegalArgumentException();
        }

        String userLookupSql = "SELECT * FROM users WHERE first_name = ? AND last_name = ?";
        String friendsLookupSql = "SELECT first_user_id, second_user_id FROM friendships WHERE (first_user_id = ? OR second_user_id = ?) AND friendship_status = 'accepted'";
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password);
             PreparedStatement userLookupPreparedStatement = connection.prepareStatement(userLookupSql);
             PreparedStatement friendsLookupPreparedStatement = connection.prepareStatement(friendsLookupSql);
        ) {
            userLookupPreparedStatement.setString(1, user.getFirstName());
            userLookupPreparedStatement.setString(2, user.getLastName());


            ResultSet resultSet = userLookupPreparedStatement.executeQuery();

            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userName = resultSet.getString("user_name");
                User foundUser = new User(firstName, lastName, userName);
                foundUser.setId(id);

                friendsLookupPreparedStatement.setLong(1, foundUser.getId());
                friendsLookupPreparedStatement.setLong(2, foundUser.getId());

                ResultSet friendLookupResult = friendsLookupPreparedStatement.executeQuery();

                long user1ID, user2ID;

                while(friendLookupResult.next())
                {
                    user1ID = friendLookupResult.getLong("first_user_id");
                    user2ID = friendLookupResult.getLong("second_user_id");

                    if(user1ID == foundUser.getId())
                    {
                        foundUser.addFriend(user2ID);
                    }
                    else
                    {
                        foundUser.addFriend(user1ID);
                    }
                }

                return foundUser;
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    @Override
    public User save(User newEntity) {
        if (newEntity == null) {
            throw new IllegalArgumentException();
        }

        validator.validate(newEntity);

        /* TODO: This is technically required, but the IDs are now managed by the DB, so it's always null by the time it gets here.

        User foundUser = findOne(newEntity.getId());

        if(foundUser != null)
        {
            return foundUser;
        }*/

        User foundUser = lookUp(newEntity);
        if (foundUser != null) {
            return foundUser;
        }

        String sql = "INSERT INTO users(first_name, last_name, user_name) VALUES(?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newEntity.getFirstName());
            preparedStatement.setString(2, newEntity.getLastName());
            preparedStatement.setString(3, newEntity.getUserName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return newEntity;
        }

        return null;
    }

    @Override
    public User findOne(Long entityID) {
        if (entityID == null)
            throw new IllegalArgumentException();

        String userLookupSql = "SELECT * FROM users WHERE id = ?";
        String friendsLookupSql = "SELECT first_user_id, second_user_id FROM friendships WHERE (first_user_id = ? OR second_user_id = ?) AND friendship_status = 'accepted'";

        try (Connection connection = DriverManager.getConnection(databaseURL, username, password);
             PreparedStatement userLookupPreparedStatement = connection.prepareStatement(userLookupSql);
             PreparedStatement friendsLookupPreparedStatement = connection.prepareStatement(friendsLookupSql);
        ) {

            friendsLookupPreparedStatement.setLong(1, entityID);
            friendsLookupPreparedStatement.setLong(2, entityID);

            userLookupPreparedStatement.setLong(1, entityID);
            ResultSet resultSet = userLookupPreparedStatement.executeQuery();

            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userName = resultSet.getString("user_name");


                User user = new User(firstName, lastName, userName);
                user.setId(id);

                ResultSet friendLookupResult = friendsLookupPreparedStatement.executeQuery();

                long user1ID, user2ID;

                while(friendLookupResult.next())
                {
                    user1ID = friendLookupResult.getLong("first_user_id");
                    user2ID = friendLookupResult.getLong("second_user_id");

                    if(user1ID == user.getId())
                    {
                        user.addFriend(user2ID);
                    }
                    else
                    {
                        user.addFriend(user1ID);
                    }
                }

                return user;
            }
        } catch (SQLException e) {
            return null;
        }


        return null;
    }

    @Override
    public Iterable<User> findAll() {

        String findAllUsersSql = "select * from users";
        String findAllFriendsSql = """
                SELECT first_user_id, second_user_id
                FROM friendships
                WHERE friendship_status = 'accepted'""";

        Set<User> userSet = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(databaseURL, username, password);
             PreparedStatement usersPreparedStatement = connection.prepareStatement(findAllUsersSql);
             PreparedStatement friendshipsPreparedStatement = connection.prepareStatement(findAllFriendsSql);
             ResultSet userResultSet = usersPreparedStatement.executeQuery();
             ResultSet friendshipsResultSet = friendshipsPreparedStatement.executeQuery();
        ) {
            HashMap<Long, List<Long>> friendsMap = new HashMap<>();

            Long id1, id2;

            while(friendshipsResultSet.next()) {
                id1 = friendshipsResultSet.getLong("first_user_id");
                id2 = friendshipsResultSet.getLong("second_user_id");

                friendsMap.computeIfAbsent(id1, k -> new ArrayList<>());
                friendsMap.get(id1).add(id2);

                friendsMap.computeIfAbsent(id2, k -> new ArrayList<>());
                friendsMap.get(id2).add(id1);
            }

            while (userResultSet.next()) {
                Long id = userResultSet.getLong("id");
                String firstName = userResultSet.getString("first_name");
                String lastName = userResultSet.getString("last_name");
                String userName = userResultSet.getString("user_name");
                User user = new User(firstName, lastName, userName);
                user.setId(id);

                List<Long> friendsList = friendsMap.get(id);

                if(friendsList != null)
                    friendsList.forEach(user::addFriend);

                userSet.add(user);
            }
        } catch (SQLException e) {
            return new HashSet<>();
        }

        return userSet;
    }

    @Override
    public User update(User newEntity) {
        if (newEntity == null)
            throw new IllegalArgumentException();

        validator.validate(newEntity);

        User foundUser = findOne(newEntity.getId());
        if (foundUser == null)
            return newEntity;

        String sql = "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(databaseURL, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, newEntity.getFirstName());
            preparedStatement.setString(2, newEntity.getLastName());
            preparedStatement.setLong(3, newEntity.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return newEntity;
        }
        return null;
    }

    @Override
    public User delete(Long entityID) {
        if (entityID == null)
            throw new IllegalArgumentException();

        User foundUser = findOne(entityID);

        if (foundUser == null) {
            return null;
        }

        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(databaseURL, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, entityID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return null;
        }

        return foundUser;
    }
}
