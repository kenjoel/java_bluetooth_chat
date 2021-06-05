package com.kenjoel.dbUtils;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface DAO {
    @Insert
    void create(UserInfo userInfo);

    @Query("SELECT * FROM USERINFO where username LIKE :name")
    UserInfo getUserInfo(String name);
}
