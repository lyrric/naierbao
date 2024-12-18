package org.example.db.mapper;

import org.apache.ibatis.annotations.Update;

public interface RunInfoMapper {

    @Update("update run_info set last_run_time = now() where id = 1")
    void updateLastRunTime();
}
