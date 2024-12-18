package org.example.db;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.example.db.mapper.RunInfoMapper;

@Slf4j
public class RunInfoBiz {

    public static void updateLastRunTime() {
        try( SqlSession session = DBUtil.getSession()) {
            RunInfoMapper mapper = session.getMapper(RunInfoMapper.class);
            mapper.updateLastRunTime();
            session.commit();
        }
    }

}
