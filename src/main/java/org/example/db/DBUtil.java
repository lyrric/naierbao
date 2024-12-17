package org.example.db;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.example.db.mapper.AppointHistoryMapper;
import org.example.model.AppointHistory;
import org.example.model.DBConfig;
import org.example.util.DBConfigUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class DBUtil {

    static SqlSessionFactory sqlSessionFactory;

    static{
        // 配置数据源
        DBConfig dbConfig = DBConfigUtils.getDBConfig();
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(dbConfig.getUrl());
        dataSource.setUsername(dbConfig.getUsername());
        dataSource.setPassword(dbConfig.getPassword());

        // 配置 MyBatis
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);

        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration(environment);
        mybatisConfiguration.addMapper(AppointHistoryMapper.class);

        MybatisSqlSessionFactoryBuilder factoryBuilder = new MybatisSqlSessionFactoryBuilder();
        sqlSessionFactory =  factoryBuilder.build(mybatisConfiguration);
    }

    static SqlSession getSession(){
        return sqlSessionFactory.openSession();
    }

    public static void main(String[] args) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AppointHistoryMapper mapper = session.getMapper(AppointHistoryMapper.class);
            // 执行数据库操作
            List<AppointHistory> list = mapper.selectList(null);
            list.forEach(System.out::println);
        }
    }




    private static String getToday(){
        Date now = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(now);
    }

}
