package com.wmy.study.DearIMProject.typeHandler;

import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageStatus;
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
@MappedTypes({MessageStatus.class})
@MappedJdbcTypes({JdbcType.INTEGER})
public class MessageStatusTypeHandler implements TypeHandler<MessageStatus> {
    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, MessageStatus messageStatus, JdbcType jdbcType) throws SQLException {
        int entityType = messageStatus.getValue();
        preparedStatement.setInt(i, entityType);
    }

    @Override
    public MessageStatus getResult(ResultSet resultSet, String s) throws SQLException {
        int anInt = resultSet.getInt(s);
        return MessageStatus.fromInt(anInt);
    }

    @Override
    public MessageStatus getResult(ResultSet resultSet, int i) throws SQLException {
        int anInt = resultSet.getInt(i);
        return MessageStatus.fromInt(anInt);
    }

    @Override
    public MessageStatus getResult(CallableStatement callableStatement, int i) throws SQLException {
        int anInt = callableStatement.getInt(i);
        return MessageStatus.fromInt(anInt);
    }
}
