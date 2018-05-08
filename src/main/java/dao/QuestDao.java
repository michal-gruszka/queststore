package dao;

import exceptions.DataAccessException;
import model.QuestModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestDao extends ManipulationDao implements QuestDaoInterface{

    private final int QUEST_STATUS_ID = 2;

    @Override
    public void addQuest(QuestModel newQuest) throws DataAccessException {
        try{
            PreparedStatement ps = getConnection().prepareStatement(
                    "INSERT INTO quest (name, reward, description, category_id)" +
                            "SELECT ?,?,?, quest_category.id " +
                            "FROM quest_category " +
                            "WHERE quest_category.name = ?;");
            ps.setString(1, newQuest.getName());
            ps.setInt(2, newQuest.getReward());
            ps.setString(3, newQuest.getDescription());
            ps.setString(4, newQuest.getCategory());
            ps.executeUpdate();
        } catch (SQLException e){
            throw new DataAccessException("Fail to add Quest");
        }
    }

    @Override
    public void removeQuest(QuestModel removeQuest) throws DataAccessException {
        try{
            PreparedStatement ps = getConnection().prepareStatement("DELETE FROM quest WHERE id = ?");
            ps.setInt(1, removeQuest.getID());
            ps.executeUpdate();
        } catch (SQLException e){
            throw new DataAccessException("Fail to remove Quest");
        }
    }

    @Override
    public void updateQuest(QuestModel updateQuest) throws DataAccessException {
        try{
            PreparedStatement ps = getConnection().prepareStatement(
                    "UPDATE quest SET " +
                            "name=?," +
                            "reward=?," +
                            "description=?," +
                            "category_id = (SELECT quest_category.id FROM quest_category WHERE quest_category.name=?)" +
                            "WHERE quest.id=?;");
            ps.setString(1, updateQuest.getName());
            ps.setInt(2, updateQuest.getReward());
            ps.setString(3, updateQuest.getDescription());
            ps.setString(4, updateQuest.getCategory());
            ps.setInt(5, updateQuest.getID());
            ps.executeUpdate();
        } catch (SQLException e){
            throw new DataAccessException("Fail to update Quest");
        }
    }

    @Override
    public List<String> getCategoriesName() throws DataAccessException {
        try{
            PreparedStatement ps = getConnection().prepareStatement("SELECT name FROM quest_category;");
            List<String> categoryNameList = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String categoryName = rs.getString("name");
                categoryNameList.add(categoryName);
            }
            return  categoryNameList;
        } catch (SQLException e){
            throw new DataAccessException("Fail to get categories names");
        }
    }

    @Override
    public QuestModel getByID(int id) throws DataAccessException {
        try{
            PreparedStatement ps = getConnection().prepareStatement("SELECT quest.id, quest.name, quest.reward," +
                    "quest.description, quest_category.name as 'category_name' FROM quest JOIN quest_category " +
                    "ON quest.category_id = quest_category.id WHERE quest.id=?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return getQuestFromResultSet(rs);
        } catch (SQLException e){
            throw new DataAccessException("Fail to get Quest");
        }
    }

    @Override
    public void updateUserQuest(int user_id, int quest_id) throws DataAccessException {
        try{
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO user_quest (user_id, quest_id, status_id)"+
                    "VALUES (?, ?, ?);");
            ps.setInt(1, user_id);
            ps.setInt(2, quest_id);
            ps.setInt(3, QUEST_STATUS_ID);
            ps.executeUpdate();
        } catch (SQLException e){
            throw new DataAccessException("Fail to update user Quest");
        }
    }

    private QuestModel getQuestFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String categoryName = rs.getString("category_name");
        int reward = rs.getInt("reward");
        return new QuestModel(id, name, description, categoryName, reward);
    }
}
