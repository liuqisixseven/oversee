package com.chd.common.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.*;

public class BlobTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, String parameter, JdbcType jdbcType) throws SQLException {
        //声明一个输入流对象
        ByteArrayInputStream bis=null;
        try {
            //把字符串转为字节流
            byte[] buf=parameter.getBytes("utf-8");
            bis = new ByteArrayInputStream(buf);
            preparedStatement.setBinaryStream(i, bis, buf.length);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Blob Encoding Error!");
        }finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    throw new RuntimeException("Blob Encoding Error!");
                }
            }
        }

    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Blob blob = (Blob) rs.getBlob(columnName);

        if (null == blob) {
          return null;
        }
        byte[] returnValue = blob.getBytes(1, (int) blob.length());
        try {
            //将取出的流对象转为utf-8的字符串对象
            return new String(returnValue, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Blob Encoding Error!");
        }
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        Blob blob = (Blob) resultSet.getBlob(columnIndex);
        if (null == blob) {
            return null;
        }
        byte[] returnValue = blob.getBytes(1, (int) blob.length());
        try {
            //将取出的流对象转为utf-8的字符串对象
            return new String(returnValue, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Blob Encoding Error!");
        }
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        Blob blob = (Blob) callableStatement.getBlob(columnIndex);
        if (null == blob) {
            return null;
        }
        byte[] returnValue = blob.getBytes(1, (int) blob.length());
        try {
            //将取出的流对象转为utf-8的字符串对象
            return new String(returnValue, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Blob Encoding Error!");
        }
    }
}
