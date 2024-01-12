package com.wmy.study.DearIMProject.typeHandler;

import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
@MappedTypes({MessageEntityType.class})
@MappedJdbcTypes({JdbcType.INTEGER})
public class MessageEntityTypeHandler implements TypeHandler<MessageEntityType> {
    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, MessageEntityType messageEntityType, JdbcType jdbcType) throws SQLException {
        int entityType = messageEntityType.getValue();
        preparedStatement.setInt(i, entityType);
    }

    @Override
    public MessageEntityType getResult(ResultSet resultSet, String s) throws SQLException {
        int anInt = resultSet.getInt(s);
        return MessageEntityType.fromInt(anInt);
    }

    @Override
    public MessageEntityType getResult(ResultSet resultSet, int i) throws SQLException {
        int anInt = resultSet.getInt(i);
        return MessageEntityType.fromInt(anInt);
    }

    @Override
    public MessageEntityType getResult(CallableStatement callableStatement, int i) throws SQLException {
        int anInt = callableStatement.getInt(i);
        return MessageEntityType.fromInt(anInt);
    }
}
