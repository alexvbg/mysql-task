package filehostingservice.persistence;

import filehostingservice.entities.Gender;
import filehostingservice.entities.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PersonDAO {

    private static String deleteHumanQuery = "";

    private static String insertNewPersonQuery =
            "insert into persons(firstName, secondName, patronymic, sex, birth)"
                    + " values (?, ?, ?, ?, ?)";

    private static String getHumansQuery = "SELECT * FROM persons";

    private static String executeFirstQuery = "SELECT DISTINCT * FROM persons GROUP BY concat(secondName, firstName, patronymic, birth) HAVING COUNT(*)>=1";

//    private static String executeSecondQuery = "SELECT * FROM users WHERE left(secondName, 1) = 'ф' AND sex = 'male'";

    private static String executeSecondQuery = "SELECT * FROM persons WHERE secondName like 'ф%' AND sex = '1'";

    private static String isIndexExistQuery = "SHOW INDEX FROM persons WHERE KEY_NAME = 'sex'";

    private static String createIndexQuery = "ALTER TABLE `persons` ADD INDEX `sex` (`sex`)";

    private static String dropIndexQuery = "ALTER TABLE persons DROP INDEX sex";

    private static String createDataBase = "CREATE DATABASE PTMKtest";

    private static String dropTableQuery = "DROP TABLE IF EXISTS `persons`";

    private static String createTableQuery = "CREATE TABLE PTMKtest.persons " +
            "(id int auto_increment primary key," +
            " firstName varchar(30)," +
            " secondName varchar(30)," +
            " patronymic varchar(30)," +
            " sex boolean," +
            " birth date) ENGINE = MEMORY";


    private final Connection connection;

    public PersonDAO() {
        connection = ConnectionManager.getConnection();
    }

    public List<Person> executeQuery_1() {
        LinkedList<Person> personList = new LinkedList<>();
        try {
            ResultSet resultSet = connection.prepareStatement(executeFirstQuery).executeQuery();
            while (resultSet.next()) {
                Person person = new Person();
                person.firstName.set(resultSet.getString("firstName"));
                person.secondName.set(resultSet.getString("secondName"));
                person.patronymic.set(resultSet.getString("patronymic"));
                person.gender.set(resultSet.getBoolean("sex"));
                person.birthDate.set(resultSet.getDate("birth").toString());
                personList.add(person);
            }
            return personList;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Person> executeQuery_2() {
        LinkedList<Person> personList = new LinkedList<>();
        try {
            ResultSet resultSet = connection.prepareStatement(executeSecondQuery).executeQuery();
            while (resultSet.next()) {
                Person person = new Person();
                person.firstName.set(resultSet.getString("firstName"));
                person.secondName.set(resultSet.getString("secondName"));
                person.patronymic.set(resultSet.getString("patronymic"));
//                person.gender.set(resultSet.getString("sex"));
                person.gender.set(resultSet.getBoolean("sex"));
                person.birthDate.set(resultSet.getDate("birth").toString());
                personList.add(person);
            }
            return personList;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Collections.emptyList();
        }
    }

    public void createDataBase() {
        try {
            PreparedStatement dropTablePreparedStatement = connection.prepareStatement(dropTableQuery);
            dropTablePreparedStatement.execute();

            PreparedStatement createTablePreparedStatement = connection.prepareStatement(createTableQuery);
            createTablePreparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void createIndex() {
        try {
            ResultSet resultSet = connection.prepareStatement(isIndexExistQuery).executeQuery();
            if (!resultSet.next()) {
                PreparedStatement preparedStatement = connection.prepareStatement(createIndexQuery);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        PreparedStatement preparedStatement = connection.prepareStatement(isIndexExistQuery);
    }

    public void deleteIndex() {
        try {
            ResultSet resultSet = connection.prepareStatement(isIndexExistQuery).executeQuery();
            if (resultSet.next()) {
                PreparedStatement preparedStatement = connection.prepareStatement(dropIndexQuery);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void generateOneMillionPersons() {

        String now = LocalDate.now().toString();

        String female = Gender.FEMALE.name();
        String male = Gender.MALE.name();

        ArrayList<Person> persons = new ArrayList<>(1_000_000);

        for (int i = 0; i <= 99; i++) {

            Person person = new Person();

            person.patronymic.set(generateRandomString(true)); // start with ф
            person.secondName.set(generateRandomString(false));
            person.firstName.set(generateRandomString(true));
            person.gender.set(male);
            person.birthDate.set(now);

            persons.add(person);
        }

        for (int i = 0; i <= 999_899; i++) {

            Person person = new Person();

            person.patronymic.set(generateRandomString(true));
            person.secondName.set(generateRandomString(true));
            person.firstName.set(generateRandomString(true));
            person.gender.set(female);
            person.birthDate.set(now);

            persons.add(person);
        }

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(insertNewPersonQuery);
            connection.setAutoCommit(false);
            for (int i = 0; i < persons.size(); i++) {
                Person person = persons.get(i);

                preparedStatement.setString(1, person.firstName.getValue());
                preparedStatement.setString(2, person.secondName.getValue());
                preparedStatement.setString(3, person.patronymic.getValue());
                preparedStatement.setString(4, person.gender.getBooleanValue());
                preparedStatement.setString(5, person.birthDate.getValue());

                preparedStatement.execute();
                if (i % 10000 == 0) {
                    System.out.println("i = " + i);
                    connection.commit();

                }
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deletePerson(Person person) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteHumanQuery);

            preparedStatement.execute();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    public boolean savePerson(Person person) {
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(insertNewPersonQuery);
            preparedStatement.setString(1, person.firstName.getValue());
            preparedStatement.setString(2, person.secondName.getValue());
            preparedStatement.setString(3, person.patronymic.getValue());
//            preparedStatement.setString(4, person.gender.getValue());
            preparedStatement.setString(4, person.gender.getBooleanValue());
            preparedStatement.setString(5, person.birthDate.getValue());

            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public List<Person> getAllPersons() {

        LinkedList<Person> personList = new LinkedList<>();
        try {
            ResultSet resultSet = connection.prepareStatement(getHumansQuery).executeQuery();
            while (resultSet.next()) {
                Person person = new Person();
                person.firstName.set(resultSet.getString("firstName"));
                person.secondName.set(resultSet.getString("secondName"));
                person.patronymic.set(resultSet.getString("patronymic"));
//                person.gender.set(resultSet.getString("sex"));
                person.gender.set(resultSet.getBoolean("sex"));
                person.birthDate.set(resultSet.getDate("birth").toString());
                personList.add(person);
            }
            return personList;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Collections.emptyList();
        }
    }

    private static String generateRandomString(boolean isRandomStartedCharacter) {

        String characters = "йцукенгшщывапромсчи"; // 19

        char[] text = new char[20];

        ThreadLocalRandom localRandom = ThreadLocalRandom.current();

        for (int i = 0; i < 20; i++) {
            text[i] = characters.charAt(localRandom.nextInt(characters.length()));
        }

        if (!isRandomStartedCharacter) text[0] = 'ф';

        return new String(text);
    }
}
