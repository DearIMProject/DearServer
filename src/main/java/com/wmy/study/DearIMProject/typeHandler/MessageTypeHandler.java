package com.wmy.study.DearIMProject.typeHandler;

import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageType;
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

@Component
@Slf4j
@MappedTypes({MessageType.class})
@MappedJdbcTypes({JdbcType.INTEGER})
public class MessageTypeHandler implements TypeHandler<MessageType> {
    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, MessageType messageType, JdbcType jdbcType) throws SQLException {
        int entityType = messageType.getValue();
        preparedStatement.setInt(i, entityType);
    }

    @Override
    public MessageType getResult(ResultSet resultSet, String s) throws SQLException {
        int anInt = resultSet.getInt(s);
        return MessageType.fromInt(anInt);
    }

    @Override
    public MessageType getResult(ResultSet resultSet, int i) throws SQLException {
        int anInt = resultSet.getInt(i);
        return MessageType.fromInt(anInt);
    }

    @Override
    public MessageType getResult(CallableStatement callableStatement, int i) throws SQLException {
        int anInt = callableStatement.getInt(i);
        return MessageType.fromInt(anInt);
    }
}
