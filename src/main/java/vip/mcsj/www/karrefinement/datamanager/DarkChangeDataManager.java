package vip.mcsj.www.karrefinement.datamanager;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.core.Service;
import vip.mcsj.www.karrefinement.main.KarRefinement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DarkChangeDataManager implements Service {

    @Override
    public void initialize() {
        // 纯数据库操作类，无需初始化配置
    }

    public void changePlayerDarkChangeInfo(OfflinePlayer p, boolean isSuccess,int count){
        List<Object> objects = getPlayerDarkChangeData(p);
        if(objects == null){
            insertPlayerDarkChangeData(p,isSuccess,count);
            return;
        }
        String playerUUID = (String) objects.get(0);
        if(playerUUID.equals(p.getUniqueId().toString())){
            updatePlayerDarkChangeData(p,isSuccess,count);
        }else{
            insertPlayerDarkChangeData(p,isSuccess,count);
        }
    }

    public List<Object> getPlayerDarkChangeData(OfflinePlayer p){
        String sql = "SELECT * FROM refinementdarkchange_data WHERE player_uuid = ?";

        try(Connection conn = KarRefinement.dm.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, p.getUniqueId().toString());
            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    List<Object> list = new ArrayList<>();
                    list.add(p.getUniqueId().toString());
                    list.add(rs.getBoolean("refining_success"));
                    list.add(rs.getInt("count"));
                    return list;
                }
            }
            return null;
        }catch(SQLException e){
            KarRefinement.instance.getLogger().severe("查询玩家暗改数据失败:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void updatePlayerDarkChangeData(OfflinePlayer p,boolean isSuccess,int count) {
        String sql = "UPDATE refinementdarkchange_data SET refining_success = ?,count = ? WHERE player_uuid = ?;";

        try(Connection conn = KarRefinement.dm.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            String uuid = p.getUniqueId().toString();
            pstmt.setBoolean(1, isSuccess);
            pstmt.setInt(2, count);
            pstmt.setString(3, uuid);
            pstmt.execute();
        }catch (SQLException e){
            KarRefinement.instance.getLogger().severe("更新玩家暗改数据失败:" + e.getMessage());
            e.printStackTrace();
        }

    }
    public void insertPlayerDarkChangeData(OfflinePlayer p,boolean isSuccess,int count) {
        String sql = "INSERT INTO refinementdarkchange_data (player_uuid,refining_success,count) VALUES (?,?,?)";

        try(Connection conn = KarRefinement.dm.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            String uuid = p.getUniqueId().toString();
            pstmt.setString(1, uuid);
            pstmt.setBoolean(2, isSuccess);
            pstmt.setInt(3, count);
            pstmt.execute();
        }catch (SQLException e){
            KarRefinement.instance.getLogger().severe("插入玩家暗改数据失败:" + e.getMessage());
            e.printStackTrace();
        }

    }

    public int deletePlayerDarkChangeData(OfflinePlayer p){
        String sql = "DELETE FROM refinementdarkchange_data WHERE player_uuid = ?;";

        try(Connection conn = KarRefinement.dm.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, p.getUniqueId().toString());
            return pstmt.executeUpdate();
        }catch (SQLException e){
            KarRefinement.instance.getLogger().severe("删除玩家暗改数据失败:" + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public void deleteOudatedPlayerDarkChangeData(){
        String sql = "DELETE FROM refinementdarkchange_data WHERE count <= 0;";
        try(Connection conn = KarRefinement.dm.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.execute();
        }catch (SQLException e){
            KarRefinement.instance.getLogger().severe("删除玩家暗改数据失败:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
